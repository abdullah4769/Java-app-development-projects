package com.example.aibasedcareercounsellingapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aibasedcareercounsellingapp.databinding.ActivityResumeBuilderBinding;
import com.example.aibasedcareercounsellingapp.utils.PdfGenerator;

public class ResumeBuilderActivity extends AppCompatActivity {

    private ActivityResumeBuilderBinding binding;

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

        if (android.text.TextUtils.isEmpty(education) || android.text.TextUtils.isEmpty(skills)) {
            Toast.makeText(this, "Please fill Education and Skills first", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(android.view.View.VISIBLE);
        binding.btnAiSummary.setEnabled(false);

        String prompt = "Write a professional resume summary (max 3-4 lines) for a candidate with:\n" +
                "Education: " + education + "\n" +
                "Skills: " + skills + "\n" +
                "Experience: " + experience + "\n" +
                "Highlight key achievements and career goals. Provide only the text, no markdown.";

        com.example.aibasedcareercounsellingapp.models.GeminiRequest geminiRequest = 
                new com.example.aibasedcareercounsellingapp.models.GeminiRequest(prompt);

        // Create JSon Body
        String jsonBody = new com.google.gson.Gson().toJson(geminiRequest);
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonBody);

        com.example.aibasedcareercounsellingapp.api.ApiClient.getGeminiService()
                .getAnalysis(com.example.aibasedcareercounsellingapp.utils.Constants.GEMINI_API_KEY, requestBody)
                .enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, 
                                         retrofit2.Response<okhttp3.ResponseBody> response) {
                        
                        android.util.Log.d("GEMINI_RESUME", "Response Code: " + response.code());
                        Toast.makeText(ResumeBuilderActivity.this, "AI Response Code: " + response.code(), Toast.LENGTH_LONG).show();

                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.btnAiSummary.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseString = response.body().string();
                                android.util.Log.d("GEMINI_RESUME", "Raw Response: " + responseString);
                                
                                // Manual Parsing
                                com.example.aibasedcareercounsellingapp.models.GeminiResponse geminiResponse = 
                                        new com.google.gson.Gson().fromJson(responseString, com.example.aibasedcareercounsellingapp.models.GeminiResponse.class);
                                
                                String summaryText = geminiResponse.getText();
                                binding.etSummary.setText(summaryText);
                                Toast.makeText(ResumeBuilderActivity.this, "Summary Generated!", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ResumeBuilderActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                android.util.Log.e("GEMINI_RESUME", "Error Body: " + errorBody);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(ResumeBuilderActivity.this, "AI Generation Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.btnAiSummary.setEnabled(true);
                        android.util.Log.e("GEMINI_RESUME", "Failure: " + t.getMessage());
                        Toast.makeText(ResumeBuilderActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSummaryFromResponse(com.example.aibasedcareercounsellingapp.models.GeminiResponse response) {
         // Assuming GeminiResponse structure is compatible or we add a helper
         // But checking usages, GeminiResponse might need a helper method if getCandidates() is used.
         // Let's rely on the existing .getText() method if it abstracts the candidates.
         // The previous code used response.body().getText(), so we assume that exists.
         return response.getText();
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
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("name", name);
        data.put("type", "Resume Generated");

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users").document(uid).collection("generated_resumes")
                .add(data)
                .addOnFailureListener(e -> android.util.Log.e("Firestore", "Error saving resume", e));
    }
}
