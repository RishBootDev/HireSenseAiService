package org.rishbootdev.hiresenseaiservice.dto;


import lombok.Data;

import java.util.List;

@Data
public class MatchScoreResult {
    private int score;
    private List<String> matchedSkills;
    private List<String> missingSkills;
}
