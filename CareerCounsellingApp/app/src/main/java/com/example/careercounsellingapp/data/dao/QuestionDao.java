package com.example.careercounsellingapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.careercounsellingapp.data.entities.Question;
import com.example.careercounsellingapp.data.entities.Option;
import com.example.careercounsellingapp.data.relations.QuestionWithOptions;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert
    void insertQuestion(Question question);

    @Insert
    void insertOptions(List<Option> options);

    @Transaction
    @Query("SELECT * FROM questions WHERE questionType = :type")
    LiveData<List<QuestionWithOptions>> getQuestionsWithOptionsByType(String type);

    @Query("SELECT * FROM questions")
    List<Question> getAllQuestions();
}