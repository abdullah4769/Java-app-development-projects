package com.example.aibasedcareercounsellingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.aibasedcareercounsellingapp.adapters.SkillAdapter;
import com.example.aibasedcareercounsellingapp.databinding.ActivityManualSkillSelectionBinding;
import com.example.aibasedcareercounsellingapp.ml.CareerClassifier;
import com.example.aibasedcareercounsellingapp.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManualSkillSelectionActivity extends AppCompatActivity {

    private ActivityManualSkillSelectionBinding binding;
    private SkillAdapter adapter;
    private CareerClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManualSkillSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Classifier
        classifier = new CareerClassifier(this);

        setUpRecyclerView();

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnPredict.setOnClickListener(v -> predictCareer());
    }

    private void setUpRecyclerView() {
        List<String> allSkills = java.util.Arrays.asList(Constants.SKILLS);
        // Listener just updates a toast or log if needed, or we can leave it empty if we just pull data on button click
        adapter = new SkillAdapter(allSkills, count -> {
            // Optional: Update a text view with "Selected: " + count
        });
        binding.rvSkills.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSkills.setAdapter(adapter);
    }

    private void predictCareer() {
        java.util.Set<String> selectedSet = adapter.getSelectedSkills();
        if (selectedSet.size() < 5) {
            Toast.makeText(this, "Please select at least 5 skills", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnPredict.setEnabled(false);

        // Create float[100] vector
        float[] inputVector = new float[100];
        
        // Fill vector: 1.0 if skill is present in selectedSet, else 0.0
        for (int i = 0; i < Constants.SKILLS.length; i++) {
            if (selectedSet.contains(Constants.SKILLS[i])) {
                inputVector[i] = 1.0f;
            } else {
                inputVector[i] = 0.0f;
            }
        }

        // Perform Prediction using TFLite
        new Thread(() -> {
            int predictedIndex = classifier.predictCareer(inputVector);
            
            runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnPredict.setEnabled(true);

                if (predictedIndex != -1) {
                    String predictedCareer = Constants.getCareerName(predictedIndex);
                    navigateToResult(predictedCareer);
                } else {
                    Toast.makeText(this, "Prediction Failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void navigateToResult(String career) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("predicted_career", career);
        intent.putStringArrayListExtra("selected_skills", new ArrayList<>(adapter.getSelectedSkills()));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (classifier != null) {
            classifier.close();
        }
    }
}
