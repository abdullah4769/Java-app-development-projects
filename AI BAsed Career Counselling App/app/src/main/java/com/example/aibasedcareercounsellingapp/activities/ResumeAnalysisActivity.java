package com.example.aibasedcareercounsellingapp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aibasedcareercounsellingapp.R;
import com.example.aibasedcareercounsellingapp.databinding.ActivityResumeAnalysisBinding;
// import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

public class ResumeAnalysisActivity extends AppCompatActivity {

    private ActivityResumeAnalysisBinding binding;
    private StringBuilder extractedText = new StringBuilder();
    private static final String TAG = "ResumeAnalysis";

    // Activity Result Launcher for picking PDF
    private final androidx.activity.result.ActivityResultLauncher<String> pickPdfLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    extractTextFromPdf(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(getApplicationContext());
        
        binding = ActivityResumeAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        binding.btnUploadPdf.setOnClickListener(v -> pickPdfLauncher.launch("application/pdf"));
        
        binding.btnAnalyze.setOnClickListener(v -> analyzeResume());
    }

    private void extractTextFromPdf(android.net.Uri uri) {
        new Thread(() -> {
            try {
                java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
                com.tom_roush.pdfbox.pdmodel.PDDocument document = com.tom_roush.pdfbox.pdmodel.PDDocument.load(inputStream);
                com.tom_roush.pdfbox.text.PDFTextStripper stripper = new com.tom_roush.pdfbox.text.PDFTextStripper();
                String text = stripper.getText(document);
                document.close();
                inputStream.close();

                runOnUiThread(() -> {
                    extractedText.setLength(0);
                    extractedText.append(text);
                    binding.tvPlaceholder.setText("PDF Loaded: " + text.length() + " chars extracted");
                    binding.tvPlaceholder.setTextColor(getColor(R.color.success_green));
                    binding.btnAnalyze.setVisibility(android.view.View.VISIBLE);
                    binding.tvResults.setText("Resume loaded. Click 'Analyze Resume' to get AI insights.");
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error parsing PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.tvPlaceholder.setText("Error loading PDF");
                    binding.tvPlaceholder.setTextColor(getColor(R.color.error_red));
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void analyzeResume() {
        if (extractedText.length() == 0) return;

        binding.progressBar.setVisibility(android.view.View.VISIBLE);
        binding.btnAnalyze.setEnabled(false);
        binding.tvResults.setText("Analyzing with Gemini AI...");

        String prompt = "Act as a professional Resume Reviewer. Analyze the following resume text. " +
                "Provide a structured report with: \n" +
                "1. Overall Score (out of 100)\n" +
                "2. Top 3 Strengths\n" +
                "3. Top 3 Weaknesses/Missing Skills for a general tech role\n" +
                "4. 3 Actionable Improvements\n\n" +
                "Resume Text:\n" + extractedText.toString();

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
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.btnAnalyze.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseString = response.body().string();
                                
                                // Manual Parsing
                                com.example.aibasedcareercounsellingapp.models.GeminiResponse geminiResponse = 
                                        new com.google.gson.Gson().fromJson(responseString, com.example.aibasedcareercounsellingapp.models.GeminiResponse.class);
                                
                                String analysis = geminiResponse.getText();
                                binding.tvResults.setText(analysis);
                                saveAnalysisToFirestore(analysis);
                            } catch (Exception e) {
                                e.printStackTrace();
                                binding.tvResults.setText("Failed to parse response");
                            }
                        } else {
                            try {
                                android.util.Log.e("API_ERROR", "Response error: " + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            binding.tvResults.setText("Analysis failed. Error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.btnAnalyze.setEnabled(true);
                        binding.tvResults.setText("Network error: " + t.getMessage());
                    }
                });
    }

    private void saveAnalysisToFirestore(String analysis) {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("analysis", analysis);
        data.put("resumeLength", extractedText.length());

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users").document(uid).collection("resume_analysis")
                .add(data)
                .addOnSuccessListener(ref -> Toast.makeText(this, "Analysis saved to history", Toast.LENGTH_SHORT).show());
    }
}
