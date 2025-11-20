package org.rishbootdev.hiresenseaiservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResumeSection {
    private String sectionType;
    private String text;

    private Integer pageIndex;
    private List<Double> bbox;
}
