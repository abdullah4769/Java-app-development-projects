package com.example.aibasedcareercounsellingapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import android.view.View;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aibasedcareercounsellingapp.api.ApiClient;
import com.example.aibasedcareercounsellingapp.databinding.ActivityResumeBuilderBinding;
import com.example.aibasedcareercounsellingapp.models.GeminiRequest;
import com.example.aibasedcareercounsellingapp.models.GeminiResponse;
import com.example.aibasedcareercounsellingapp.utils.Constants;
import com.example.aibasedcareercounsellingapp.utils.PdfGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumeBuilderActivity extends AppCompatActivity {

    private ActivityResumeBuilderBinding binding;
    private static final String TAG = "ResumeBuilder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResumeBuilderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnAiSummary.setOnClickListener(v -> generateAiSummary());
        binding.btnGeneratePdf.setOnClickListener(v -> generatePdf());
    }

    private void generateAiSummary() {
        String education = binding.etEducation.getText().toString().trim();
        String skills = binding.etSkills.getText().toString().trim();
        String experience = binding.etExperience.getText().toString().trim();

        if (TextUtils.isEmpty(education) || TextUtils.isEmpty(skills)) {
            Toast.makeText(this, "Please fill Education and Skills first", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnAiSummary.setEnabled(false);

        String prompt = "Write a professional resume summary (max 3-4 lines) for a candidate with:\n" +
                "Education: " + education + "\n" +
                "Skills: " + skills + "\n" +
                "Experience: " + experience + "\n" +
                "Highlight key achievements and career goals. Provide only the text, no markdown.";

        GeminiRequest geminiRequest = new GeminiRequest(prompt);

        ApiClient.getGeminiService()
                .getAnalysis(Constants.GEMINI_API_KEY, geminiRequest)
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnAiSummary.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String summaryText = response.body().getText();
                                binding.etSummary.setText(summaryText);
                                Toast.makeText(ResumeBuilderActivity.this, "Summary Generated!", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Parsing Error", e);
                                Toast.makeText(ResumeBuilderActivity.this, "Parsing Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                            Toast.makeText(ResumeBuilderActivity.this, "AI Generation Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnAiSummary.setEnabled(true);
                        Log.e(TAG, "Network Error", t);
                        Toast.makeText(ResumeBuilderActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void generatePdf() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String education = binding.etEducation.getText().toString().trim();
        String skills = binding.etSkills.getText().toString().trim();
        String experience = binding.etExperience.getText().toString().trim();
        String summary = binding.etSummary.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(education) || TextUtils.isEmpty(skills)) {
            Toast.makeText(this, "Please fill in Name, Education, and Skills", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfGenerator.generateResume(this, name, email, phone, education, skills, experience, summary);
        saveGeneratedResumeToFireStore(name);
    }

    private void saveGeneratedResumeToFireStore(String name) {
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null) return;
        
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("name", name);
        data.put("type", "Resume Generated");

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users").document(uid).collection("generated_resumes")
                .add(data)
                .addOnFailureListener(e -> Log.e(TAG, "Firestore Error", e));
    }
}
