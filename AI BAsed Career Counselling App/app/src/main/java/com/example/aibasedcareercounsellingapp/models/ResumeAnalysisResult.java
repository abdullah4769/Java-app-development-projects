package com.example.aibasedcareercounsellingapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for storing resume analysis results from Gemini AI
 */
public class ResumeAnalysisResult {
    
    private int matchScore;
    private List<String> missingSkills;
    private List<String> formattingTips;
    private String targetJob;
    
    public ResumeAnalysisResult() {
        this.missingSkills = new ArrayList<>();
        this.formattingTips = new ArrayList<>();
    }
    
    public ResumeAnalysisResult(int matchScore, List<String> missingSkills, List<String> formattingTips, String targetJob) {
        this.matchScore = matchScore;
        this.missingSkills = missingSkills != null ? missingSkills : new ArrayList<>();
        this.formattingTips = formattingTips != null ? formattingTips : new ArrayList<>();
        this.targetJob = targetJob;
    }
    
    // Getters and Setters
    public int getMatchScore() {
        return matchScore;
    }
    
    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }
    
    public List<String> getMissingSkills() {
        return missingSkills;
    }
    
    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
    
    public List<String> getFormattingTips() {
        return formattingTips;
    }
    
    public void setFormattingTips(List<String> formattingTips) {
        this.formattingTips = formattingTips;
    }
    
    public String getTargetJob() {
        return targetJob;
    }
    
    public void setTargetJob(String targetJob) {
        this.targetJob = targetJob;
    }
}
