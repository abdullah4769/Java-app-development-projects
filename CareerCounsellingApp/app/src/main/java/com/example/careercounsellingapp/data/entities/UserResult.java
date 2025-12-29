package com.example.careercounsellingapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_results")
public class UserResult {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private long timestamp;
    private String userName;
    private String topCareerPath;
    private String assessmentType; // "Situational Approach" or "Preference Approach"
    private double analyticalScore;
    private double socialScore;
    private double creativeScore;
    private double leadershipScore;
    private double structuredScore;

    public UserResult(long timestamp, String userName, String topCareerPath, String assessmentType, double analyticalScore, 
                      double socialScore, double creativeScore, double leadershipScore, double structuredScore) {
        this.timestamp = timestamp;
        this.userName = userName;
        this.topCareerPath = topCareerPath;
        this.assessmentType = assessmentType;
        this.analyticalScore = analyticalScore;
        this.socialScore = socialScore;
        this.creativeScore = creativeScore;
        this.leadershipScore = leadershipScore;
        this.structuredScore = structuredScore;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getTopCareerPath() { return topCareerPath; }
    public void setTopCareerPath(String topCareerPath) { this.topCareerPath = topCareerPath; }
    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }
    public double getAnalyticalScore() { return analyticalScore; }
    public void setAnalyticalScore(double analyticalScore) { this.analyticalScore = analyticalScore; }
    public double getSocialScore() { return socialScore; }
    public void setSocialScore(double socialScore) { this.socialScore = socialScore; }
    public double getCreativeScore() { return creativeScore; }
    public void setCreativeScore(double creativeScore) { this.creativeScore = creativeScore; }
    public double getLeadershipScore() { return leadershipScore; }
    public void setLeadershipScore(double leadershipScore) { this.leadershipScore = leadershipScore; }
    public double getStructuredScore() { return structuredScore; }
    public void setStructuredScore(double structuredScore) { this.structuredScore = structuredScore; }
}