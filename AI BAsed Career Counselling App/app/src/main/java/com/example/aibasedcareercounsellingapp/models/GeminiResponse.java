package com.example.aibasedcareercounsellingapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeminiResponse {
    
    @SerializedName("candidates")
    private List<Candidate> candidates;
    
    public String getText() {
        if (candidates != null && !candidates.isEmpty() &&
            candidates.get(0).content != null && 
            candidates.get(0).content.parts != null &&
            !candidates.get(0).content.parts.isEmpty()) {
            return candidates.get(0).content.parts.get(0).text;
        }
        return "No response from AI";
    }
    
    public static class Candidate {
        @SerializedName("content")
        private Content content;
    }
    
    public static class Content {
        @SerializedName("parts")
        private List<Part> parts;
    }
    
    public static class Part {
        @SerializedName("text")
        private String text;
    }
}
