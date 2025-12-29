package com.example.careercounsellingapp.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.careercounsellingapp.data.dao.CareerBenchmarkDao;
import com.example.careercounsellingapp.data.dao.QuestionDao;
import com.example.careercounsellingapp.data.dao.UserResultDao;
import com.example.careercounsellingapp.data.entities.CareerBenchmark;
import com.example.careercounsellingapp.data.entities.UserResult;
import com.example.careercounsellingapp.data.relations.QuestionWithOptions;

import java.util.List;

public class CareerRepository {

    private final QuestionDao questionDao;
    private final UserResultDao userResultDao;
    private final CareerBenchmarkDao careerBenchmarkDao;

    public CareerRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        questionDao = db.questionDao();
        userResultDao = db.userResultDao();
        careerBenchmarkDao = db.careerBenchmarkDao();
    }

    // Question methods
    public LiveData<List<QuestionWithOptions>> getQuestionsByType(String type) {
        return questionDao.getQuestionsWithOptionsByType(type);
    }

    // Benchmark methods
    public List<CareerBenchmark> getAllBenchmarks() {
        return careerBenchmarkDao.getAllBenchmarks();
    }

    // Result methods
    public void insertResult(UserResult result) {
        AppDatabase.databaseWriteExecutor.execute(() -> userResultDao.insertResult(result));
    }

    public LiveData<List<UserResult>> getAllResults() {
        return userResultDao.getAllResults();
    }
}