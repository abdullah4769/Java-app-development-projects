package com.example.aibasedcareercounsellingapp.models;

import com.example.aibasedcareercounsellingapp.utils.Constants;

/**
 * Model class representing a career prediction with its probability score
 */
public class CareerPrediction implements Comparable<CareerPrediction> {
    
    private int careerIndex;
    private String careerName;
    private float probability;
    private int matchPercentage;
    
    public CareerPrediction(int careerIndex, float probability) {
        this.careerIndex = careerIndex;
        this.probability = probability;
        this.careerName = Constants.getCareerName(careerIndex);
        this.matchPercentage = Math.round(probability * 100);
    }
    
    // Getters
    public int getCareerIndex() {
        return careerIndex;
    }
    
    public String getCareerName() {
        return careerName;
    }
    
    public float getProbability() {
        return probability;
    }
    
    public int getMatchPercentage() {
        return matchPercentage;
    }
    
    // Setters
    public void setCareerIndex(int careerIndex) {
        this.careerIndex = careerIndex;
    }
    
    public void setCareerName(String careerName) {
        this.careerName = careerName;
    }
    
    public void setProbability(float probability) {
        this.probability = probability;
        this.matchPercentage = Math.round(probability * 100);
    }
    
    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }
    
    /**
     * Compare by probability (descending order)
     */
    @Override
    public int compareTo(CareerPrediction other) {
        return Float.compare(other.probability, this.probability);
    }
    
    @Override
    public String toString() {
        return careerName + " (" + matchPercentage + "% match)";
    }
}
