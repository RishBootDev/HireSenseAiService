package org.rishbootdev.hiresenseaiservice.controller;

import lombok.AllArgsConstructor;
import org.rishbootdev.hiresenseaiservice.dto.ResumeResponse;
import org.rishbootdev.hiresenseaiservice.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/HireSenseAi")
@AllArgsConstructor
public class AiController {

    private final ResumeService resumeService;

    @PostMapping("/analyze")
    public ResponseEntity<ResumeResponse> analyzeResume(@RequestParam("file") MultipartFile file) {
        ResumeResponse response = resumeService.processResume(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/score")
    public ResponseEntity<Integer> getMatchScore(
            @RequestParam String jobSkills,
            @RequestBody List<String> resumeSkills
    ) {
        int score = resumeService.calculateMatchScoreUsingLLM(jobSkills, resumeSkills);
        return ResponseEntity.ok(score);
    }

}
