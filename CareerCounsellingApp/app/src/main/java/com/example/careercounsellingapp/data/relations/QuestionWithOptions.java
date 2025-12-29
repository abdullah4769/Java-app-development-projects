package com.example.careercounsellingapp.data.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.careercounsellingapp.data.entities.Question;
import com.example.careercounsellingapp.data.entities.Option;

import java.util.List;

public class QuestionWithOptions {
    @Embedded
    public Question question;

    @Relation(
            parentColumn = "id",
            entityColumn = "questionId"
    )
    public List<Option> options;
}