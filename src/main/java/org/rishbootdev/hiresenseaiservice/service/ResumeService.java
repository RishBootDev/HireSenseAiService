package org.rishbootdev.hiresenseaiservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rishbootdev.hiresenseaiservice.dto.MatchScoreResult;
import org.rishbootdev.hiresenseaiservice.dto.ResumeData;
import org.rishbootdev.hiresenseaiservice.dto.ResumeResponse;
import org.rishbootdev.hiresenseaiservice.dto.ResumeSection;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ResumeService {

    private final PdfService pdfService;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BeanOutputConverter<ResumeResponse> converter = new BeanOutputConverter<>(ResumeResponse.class);

    private static final int MAX_TEXT_LENGTH = 120_000;

    public ResumeResponse processResume(MultipartFile file) {
        try {
            String resumeText = pdfService.getTextFromFile(file);
            if (resumeText == null) resumeText = "";

            if (resumeText.length() > MAX_TEXT_LENGTH) {
                resumeText = resumeText.substring(0, MAX_TEXT_LENGTH);
            }
            String format = converter.getFormat();

            String prompt = """
                Extract structured resume information from the text below.
                Return JSON strictly matching the ResumeResponse DTO shape.
                Use empty arrays for lists when nothing is found and null for absent objects.
                %s
                Resume text:
                ~~~
                %s
                ~~~
                """.formatted(format, resumeText);

            String llmOutput = getModelOutput(prompt);
            ResumeResponse resumeResponse = null;
            try {
                resumeResponse = converter.convert(llmOutput);
            } catch (Exception convEx) {
                log.warn("Bean conversion failed, falling back. converter error: {}", convEx.getMessage(), convEx);
                return buildFallbackResponse(resumeText);
            }

            if (resumeResponse == null) return buildFallbackResponse(resumeText);

            ResumeData data = resumeResponse.getData();
            if (data == null) {
                resumeResponse.setData(buildFallbackResponse(resumeText).getData());
                return resumeResponse;
            }

            if (data.getSections() == null) data.setSections(new ArrayList<>());
            if (data.getSkills() == null) data.setSkills(new ArrayList<>());
            if (data.getWorkExperience() == null) data.setWorkExperience(new ArrayList<>());
            if (data.getEducation() == null) data.setEducation(new ArrayList<>());
            if (data.getEmails() == null) data.setEmails(new ArrayList<>());
            if (data.getPhoneNumbers() == null) data.setPhoneNumbers(new ArrayList<>());

            System.out.println(resumeResponse.toString());
            return resumeResponse;

        } catch (Exception e) {
            log.error("Failed to process resume: {}",e.getMessage(),e);
            return buildFallbackResponse(pdfService.getTextFromFile(file));
        }
    }

    private String getModelOutput(String prompt) {
        var response = chatClient.prompt().user(prompt).call();
        try {
            return response.content();
        } catch (NoSuchMethodError | AbstractMethodError ignored) { }
        try {
            return response.content();
        } catch (Exception ignored) { }

        return response.toString();
    }

    private ResumeResponse buildFallbackResponse(String rawText) {
        ResumeResponse fallback = new ResumeResponse();
        ResumeData data = new ResumeData();

        List<ResumeSection> sections = new ArrayList<>();
        ResumeSection summary = new ResumeSection();
        summary.setSectionType("Summary");
        summary.setText(rawText == null ? "" : rawText);
        sections.add(summary);

        data.setSections(sections);
        data.setSkills(new ArrayList<>());
        data.setWorkExperience(new ArrayList<>());
        data.setEducation(new ArrayList<>());
        data.setEmails(new ArrayList<>());
        data.setPhoneNumbers(new ArrayList<>());
        data.setName(null);

        fallback.setData(data);
        return fallback;
    }


    public int calculateMatchScoreUsingLLM(String jobSkillsCsv, List<String> resumeSkills) {
        try {
            BeanOutputConverter<MatchScoreResult> converter =
                    new BeanOutputConverter<>(MatchScoreResult.class);

            String format = converter.getFormat();

            String prompt = """
                You are an expert resume evaluator.
                Task:
                Compare the job-required skills with the candidate's resume skills.
                Required Output Format (IMPORTANT):
                %s
                Rules:
                - `score` must be an integer between 0 and 100.
                - `matchedSkills` = skills appearing in both lists.
                - `missingSkills` = skills from jobSkillsCsv that are NOT in resumeSkills.
                - Treat skill names case-insensitively.
                - Do NOT hallucinate skills that are not provided.
                Job Skills (CSV):
                %s
                Resume Skills (List):
                %s
                """.formatted(
                    format,
                    jobSkillsCsv,
                    objectMapper.writeValueAsString(resumeSkills)
            );

            String llmOutput = chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            MatchScoreResult result = converter.convert(llmOutput);
            System.out.println("the score is "+result.getScore());
            return result.getScore();

        } catch (Exception e) {
            log.error("LLM Match Score Failed: {}", e.getMessage(), e);
            return 0;
        }
    }
}
