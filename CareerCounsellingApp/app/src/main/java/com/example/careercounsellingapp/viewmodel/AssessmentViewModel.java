package com.example.careercounsellingapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.careercounsellingapp.data.CareerRepository;
import com.example.careercounsellingapp.data.entities.Option;
import com.example.careercounsellingapp.data.entities.UserResult;
import com.example.careercounsellingapp.data.relations.QuestionWithOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AssessmentViewModel extends AndroidViewModel {

    private final CareerRepository repository;
    private final HashMap<String, Integer> traitScores = new HashMap<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isQuizFinished = new MutableLiveData<>(false);
    private LiveData<List<QuestionWithOptions>> questions;

    // Trait Constants
    public static final String TRAIT_ANALYTICAL = "Analytical";
    public static final String TRAIT_SOCIAL = "Social";
    public static final String TRAIT_CREATIVE = "Creative";
    public static final String TRAIT_LEADERSHIP = "Leadership";
    public static final String TRAIT_STRUCTURED = "Structured";

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        repository = new CareerRepository(application);
        // Initialize scores to zero
        traitScores.put(TRAIT_ANALYTICAL, 0);
        traitScores.put(TRAIT_SOCIAL, 0);
        traitScores.put(TRAIT_CREATIVE, 0);
        traitScores.put(TRAIT_LEADERSHIP, 0);
        traitScores.put(TRAIT_STRUCTURED, 0);
    }

    public LiveData<List<QuestionWithOptions>> loadQuestions(String type) {
        questions = repository.getQuestionsByType(type);
        return questions;
    }

    public void processSelection(Option selectedOption) {
        if (selectedOption == null || selectedOption.getTraitWeightsJson() == null) return;

        try {
            JSONObject weights = new JSONObject(selectedOption.getTraitWeightsJson());
            Iterator<String> keys = weights.keys();

            while (keys.hasNext()) {
                String traitKey = keys.next();
                int weight = weights.getInt(traitKey);
                
                if (traitScores.containsKey(traitKey)) {
                    Integer currentScore = traitScores.get(traitKey);
                    traitScores.put(traitKey, (currentScore != null ? currentScore : 0) + weight);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processPreferenceScore(String trait, int score) {
        String key = traitKey(trait);
        if (key != null && traitScores.containsKey(key)) {
            Integer currentScore = traitScores.get(key);
            traitScores.put(key, (currentScore != null ? currentScore : 0) + score);
        }
    }
    
    private String traitKey(String rawTrait) {
        if (rawTrait == null) return null;
        if (rawTrait.equalsIgnoreCase("Analytical")) return TRAIT_ANALYTICAL;
        if (rawTrait.equalsIgnoreCase("Social")) return TRAIT_SOCIAL;
        if (rawTrait.equalsIgnoreCase("Creative")) return TRAIT_CREATIVE;
        if (rawTrait.equalsIgnoreCase("Leadership")) return TRAIT_LEADERSHIP;
        if (rawTrait.equalsIgnoreCase("Structured")) return TRAIT_STRUCTURED;
        return rawTrait;
    }

    public LiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public LiveData<Boolean> isQuizFinished() {
        return isQuizFinished;
    }

    public void getNextQuestion(int totalSize) {
        Integer current = currentQuestionIndex.getValue();
        if (current != null) {
            if (current + 1 >= totalSize) {
                isQuizFinished.setValue(true);
            } else {
                currentQuestionIndex.setValue(current + 1);
            }
        }
    }

    public Map<String, Integer> getTraitScores() {
        return traitScores;
    }

    public void saveResult(String userName, String topCareer, int matchPercentage, String quizMode) {
        UserResult result = new UserResult(
                System.currentTimeMillis(),
                userName,
                topCareer,
                quizMode,
                (double) matchPercentage, // Using analyticalScore as proxy for match percentage
                0.0, 0.0, 0.0, 0.0
        );
        repository.insertResult(result);
    }

    /**
     * Senior Developer Enhancement: Centralized Result Fetching with Chronological Sorting
     */
    public LiveData<List<UserResult>> getAllResults() {
        return repository.getAllResults();
    }
}