package org.rishbootdev.hiresenseaiservice.dto;

import lombok.Data;

@Data
public class EducationItem {
    private String institution;
    private String degree;
    private String major;
    private String startDate;
    private String endDate;
    private String text;
}
