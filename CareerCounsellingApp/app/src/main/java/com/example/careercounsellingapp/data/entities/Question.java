package com.example.careercounsellingapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class Question {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String role;
    private String context;
    private String dilemma;
    private String questionType; // "SJT" or "Preference"

    public Question(String role, String context, String dilemma, String questionType) {
        this.role = role;
        this.context = context;
        this.dilemma = dilemma;
        this.questionType = questionType;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public String getDilemma() { return dilemma; }
    public void setDilemma(String dilemma) { this.dilemma = dilemma; }
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
}