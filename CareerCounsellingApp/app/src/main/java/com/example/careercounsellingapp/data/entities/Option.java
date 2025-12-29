package com.example.careercounsellingapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "options",
        foreignKeys = @ForeignKey(entity = Question.class,
                parentColumns = "id",
                childColumns = "questionId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("questionId")})
public class Option {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int questionId;
    private String text;
    private String traitWeightsJson; // JSON string mapping traits to scores

    public Option(int questionId, String text, String traitWeightsJson) {
        this.questionId = questionId;
        this.text = text;
        this.traitWeightsJson = traitWeightsJson;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTraitWeightsJson() { return traitWeightsJson; }
    public void setTraitWeightsJson(String traitWeightsJson) { this.traitWeightsJson = traitWeightsJson; }
}