package com.example.aibasedcareercounsellingapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeminiRequest {
    
    @SerializedName("contents")
    private List<Content> contents;
    
    public GeminiRequest(String text) {
        this.contents = java.util.Collections.singletonList(new Content(text));
    }
    
    public static class Content {
        @SerializedName("parts")
        private List<Part> parts;
        
        public Content(String text) {
            this.parts = java.util.Collections.singletonList(new Part(text));
        }
    }
    
    public static class Part {
        @SerializedName("text")
        private String text;
        
        public Part(String text) {
            this.text = text;
        }
    }
}
