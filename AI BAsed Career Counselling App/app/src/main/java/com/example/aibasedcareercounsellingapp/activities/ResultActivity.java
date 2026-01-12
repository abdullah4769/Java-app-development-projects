package com.example.aibasedcareercounsellingapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aibasedcareercounsellingapp.R;
import com.example.aibasedcareercounsellingapp.api.ApiClient;
import com.example.aibasedcareercounsellingapp.databinding.ActivityResultBinding;
import com.example.aibasedcareercounsellingapp.models.GeminiRequest;
import com.example.aibasedcareercounsellingapp.models.GeminiResponse;
import com.example.aibasedcareercounsellingapp.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {
    
    private static final String TAG = "ResultActivity";
    private ActivityResultBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    
    private ArrayList<String> selectedSkills;
    private ArrayList<String> careerNames;
    private ArrayList<Integer> matchPercentages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Get data from intent
        selectedSkills = getIntent().getStringArrayListExtra("selectedSkills");
        careerNames = getIntent().getStringArrayListExtra("careerNames");
        matchPercentages = getIntent().getIntegerArrayListExtra("matchPercentages");
        
        // Display results
        displayResults();
        
        // Set up button listeners
        binding.btnSaveHistory.setOnClickListener(v -> saveToHistory());
        binding.btnAiAnalysis.setOnClickListener(v -> showAiAnalysis());
    }
    
    /**
     * Display top 3 career predictions
     */
    private void displayResults() {
        if (careerNames == null || matchPercentages == null || careerNames.size() < 3) {
            Toast.makeText(this, "Invalid prediction data", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Display Career 1 (Top Match)
        binding.tvCareer1Name.setText(careerNames.get(0));
        binding.tvCareer1Match.setText(matchPercentages.get(0) + "% Match");
        binding.progressCareer1.setProgress(matchPercentages.get(0));
        
        // Display Career 2
        binding.tvCareer2Name.setText(careerNames.get(1));
        binding.tvCareer2Match.setText(matchPercentages.get(1) + "% Match");
        binding.progressCareer2.setProgress(matchPercentages.get(1));
        
        // Display Career 3
        binding.tvCareer3Name.setText(careerNames.get(2));
        binding.tvCareer3Match.setText(matchPercentages.get(2) + "% Match");
        binding.progressCareer3.setProgress(matchPercentages.get(2));
    }
    
    /**
     * Save prediction result to Firestore history
     */
    private void saveToHistory() {
        String userId = mAuth.getCurrentUser().getUid();
        
        // Create history document
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("selectedSkills", selectedSkills);
        
        // Store predictions as list of maps
        ArrayList<Map<String, Object>> predictions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("career", careerNames.get(i));
            prediction.put("matchScore", matchPercentages.get(i));
            predictions.add(prediction);
        }
        historyData.put("predictions", predictions);
        historyData.put("timestamp", System.currentTimeMillis());
        
        // Save to Firestore: users/{userId}/history/{autoId}
        db.collection("users").document(userId)
                .collection("history")
                .add(historyData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "✅ Saved to History!", Toast.LENGTH_SHORT).show();
                    binding.btnSaveHistory.setEnabled(false);
                    binding.btnSaveHistory.setText("✓ Saved");
                    Log.d(TAG, "History saved with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error saving history", e);
                });
    }
    
    /**
     * Show AI analysis using Gemini API
     */
    private void showAiAnalysis() {
        // Create BottomSheet
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_ai_insights, null);
        bottomSheet.setContentView(sheetView);
        
        TextView tvAiResponse = sheetView.findViewById(R.id.tvAiResponse);
        ProgressBar progressBar = sheetView.findViewById(R.id.progressBar);
        
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        tvAiResponse.setVisibility(View.GONE);
        
        // Clean input
        String skillsList = String.join(", ", selectedSkills).replaceAll("[^a-zA-Z0-9, ]", "");
        String topCareer = careerNames.get(0).replaceAll("[^a-zA-Z0-9, ]", "");
        
        String prompt = String.format(
                "Based on these skills: %s, why is '%s' a good career fit in the context of the Pakistan job market? " +
                "Provide 3 specific growth steps to excel in this career. Keep the response concise and actionable.",
                skillsList, topCareer
        );
        
        // Call Gemini API
        GeminiRequest geminiRequest = new GeminiRequest(prompt);
        
        // Create JSon Body
        String jsonBody = new com.google.gson.Gson().toJson(geminiRequest);
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonBody);
        
        ApiClient.getGeminiService().getAnalysis(Constants.GEMINI_API_KEY, requestBody)
                .enqueue(new Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Response<okhttp3.ResponseBody> response) {
                        Log.e("GEMINI_TRACE", "URL: " + call.request().url());
                        Log.d("GEMINI_DEBUG", "URL Called: " + call.request().url().toString());
                        
                        progressBar.setVisibility(View.GONE);
                        tvAiResponse.setVisibility(View.VISIBLE);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseString = response.body().string();
                                
                                // Manual Parsing
                                GeminiResponse geminiResponse = 
                                        new com.google.gson.Gson().fromJson(responseString, GeminiResponse.class);
                                        
                                String aiText = geminiResponse.getText();
                                tvAiResponse.setText(aiText);
                                Log.d(TAG, "AI Response received");
                                
                                // Optionally save AI insight to history
                                saveAiInsightToLastHistory(aiText);
                            } catch (Exception e) {
                                e.printStackTrace();
                                tvAiResponse.setText("❌ Failed to parse response");
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e("GEMINI_TRACE", "Error Body: " + errorBody);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                            tvAiResponse.setText("❌ Failed to get AI response. Error: " + response.code() + 
                                    "\n\nPlease check your API key in Constants.java");
                            Log.e(TAG, "API Error: " + response.code());
                        }
                    }
                    
                    @Override
                    public void onFailure(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Throwable t) {
                        Log.e("GEMINI_TRACE", "URL: " + call.request().url());
                        Log.e("GEMINI_TRACE", "Failure: " + t.getMessage());
                        
                        progressBar.setVisibility(View.GONE);
                        tvAiResponse.setVisibility(View.VISIBLE);
                        tvAiResponse.setText("❌ Network error: " + t.getMessage() + 
                                "\n\nPlease check your internet connection.");
                        Log.e(TAG, "Network error", t);
                    }
                });
        
        // Close button
        sheetView.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheet.dismiss());
        
        bottomSheet.show();
    }
    
    /**
     * Save AI insight to the most recent history entry
     */
    private void saveAiInsightToLastHistory(String aiInsight) {
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
                .collection("history")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("users").document(userId)
                                .collection("history").document(docId)
                                .update("aiInsight", aiInsight)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "AI insight saved to history"))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to save AI insight", e));
                    }
                });
    }
}
