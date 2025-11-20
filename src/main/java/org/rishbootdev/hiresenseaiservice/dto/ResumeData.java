package org.rishbootdev.hiresenseaiservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class ResumeData {
    private List<ResumeSection> sections;
    private Name name;
    private List<String> emails;
    private List<String> phoneNumbers;
    private List<ResumeSkill> skills;
    private List<WorkExperience> workExperience;
    private List<EducationItem> education;
}
