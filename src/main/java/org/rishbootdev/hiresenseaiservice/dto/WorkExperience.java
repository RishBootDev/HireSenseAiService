package org.rishbootdev.hiresenseaiservice.dto;

import lombok.Data;

@Data
public class WorkExperience {
    private String jobTitle;
    private String organization;
    private String location;
    private DateRange dateRange;
    private String description;
}
