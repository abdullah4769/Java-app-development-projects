package com.example.aibasedcareercounsellingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.aibasedcareercounsellingapp.adapters.SkillAdapter;
import com.example.aibasedcareercounsellingapp.databinding.ActivitySkillSelectionBinding;
import com.example.aibasedcareercounsellingapp.ml.CareerClassifier;
import com.example.aibasedcareercounsellingapp.models.CareerPrediction;
import com.example.aibasedcareercounsellingapp.utils.Constants;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SkillSelectionActivity extends AppCompatActivity {
    
    private ActivitySkillSelectionBinding binding;
    private SkillAdapter adapter;
    private CareerClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivitySkillSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize ML classifier
        classifier = new CareerClassifier(this);
        
        // Set up RecyclerView with FlexboxLayoutManager for chip-style layout
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        binding.skillsRecyclerView.setLayoutManager(layoutManager);
        
        // Set up adapter with all 100 skills
        List<String> allSkills = Arrays.asList(Constants.SKILLS);
        adapter = new SkillAdapter(allSkills, selectedCount -> {
            binding.tvSelectedCount.setText(selectedCount + " selected");
        });
        binding.skillsRecyclerView.setAdapter(adapter);
        
        // Set up search functionality
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        
        // Set up FAB click listener
        binding.fabPredict.setOnClickListener(v -> predictCareers());
    }
    
    /**
     * Predict careers based on selected skills
     */
    private void predictCareers() {
        Set<String> selectedSkills = adapter.getSelectedSkills();
        
        // Validation
        if (selectedSkills.isEmpty()) {
            Toast.makeText(this, "Please select at least one skill", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!classifier.isModelLoaded()) {
            Toast.makeText(this, "ML model not loaded. Please restart the app.", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Show loading
        binding.fabPredict.setEnabled(false);
        Toast.makeText(this, "Analyzing your skills...", Toast.LENGTH_SHORT).show();
        
        // Create skill vector (100 elements: 1.0 if selected, 0.0 otherwise)
        float[] skillVector = new float[100];
        for (int i = 0; i < Constants.SKILLS.length; i++) {
            if (selectedSkills.contains(Constants.SKILLS[i])) {
                skillVector[i] = 1.0f;
            } else {
                skillVector[i] = 0.0f;
            }
        }
        
        // Get top 3 career predictions
        List<CareerPrediction> predictions = classifier.predictTopNCareers(skillVector, 3);
        
        binding.fabPredict.setEnabled(true);
        
        if (predictions == null || predictions.isEmpty()) {
            Toast.makeText(this, "Prediction failed. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Navigate to ResultActivity with predictions
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putStringArrayListExtra("selectedSkills", new ArrayList<>(selectedSkills));
        intent.putStringArrayListExtra("careerNames", getCareerNames(predictions));
        intent.putIntegerArrayListExtra("matchPercentages", getMatchPercentages(predictions));
        startActivity(intent);
    }
    
    private ArrayList<String> getCareerNames(List<CareerPrediction> predictions) {
        ArrayList<String> names = new ArrayList<>();
        for (CareerPrediction pred : predictions) {
            names.add(pred.getCareerName());
        }
        return names;
    }
    
    private ArrayList<Integer> getMatchPercentages(List<CareerPrediction> predictions) {
        ArrayList<Integer> percentages = new ArrayList<>();
        for (CareerPrediction pred : predictions) {
            percentages.add(pred.getMatchPercentage());
        }
        return percentages;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (classifier != null) {
            classifier.close();
        }
    }
}
