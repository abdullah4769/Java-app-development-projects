package com.example.careercounsellingapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "career_benchmarks")
public class CareerBenchmark {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String profileName;
    private String description;
    private double analyticalScore;
    private double socialScore;
    private double creativeScore;
    private double leadershipScore;
    private double structuredScore;

    public CareerBenchmark(String profileName, String description, double analyticalScore, double socialScore, 
                           double creativeScore, double leadershipScore, double structuredScore) {
        this.profileName = profileName;
        this.description = description;
        this.analyticalScore = analyticalScore;
        this.socialScore = socialScore;
        this.creativeScore = creativeScore;
        this.leadershipScore = leadershipScore;
        this.structuredScore = structuredScore;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
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