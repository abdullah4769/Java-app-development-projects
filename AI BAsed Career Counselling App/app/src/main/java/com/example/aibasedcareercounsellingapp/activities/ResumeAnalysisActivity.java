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
        binding.tvResults.setText("Thinking... (This might take a few seconds)");

        String prompt = "You are an elitist, top-tier tech recruiter. Analyze this resume strictly and critically.\n" +
                "Return a structured report in the following format:\n\n" +
                "**RESUME SCORE:** [0-100]\n\n" +
                "**STRENGTHS:**\n" +
                "- [Point 1]\n" +
                "- [Point 2]\n\n" +
                "**WEAKNESSES:**\n" +
                "- [Point 1]\n" +
                "- [Point 2]\n\n" +
                "**ACTION PLAN:**\n" +
                "- [Specific Action 1]\n" +
                "- [Specific Action 2]\n\n" +
                "Resume Content:\n" + extractedText.toString();

        com.example.aibasedcareercounsellingapp.models.GeminiRequest geminiRequest = 
                new com.example.aibasedcareercounsellingapp.models.GeminiRequest(prompt);

        com.example.aibasedcareercounsellingapp.api.ApiClient.getGeminiService()
                .getAnalysis(com.example.aibasedcareercounsellingapp.utils.Constants.GEMINI_API_KEY, geminiRequest)
                .enqueue(new retrofit2.Callback<com.example.aibasedcareercounsellingapp.models.GeminiResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.aibasedcareercounsellingapp.models.GeminiResponse> call, 
                                         retrofit2.Response<com.example.aibasedcareercounsellingapp.models.GeminiResponse> response) {
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.btnAnalyze.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            String analysis = response.body().getText();
                            binding.tvResults.setText(analysis);
                            saveAnalysisToFirestore(analysis);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                                android.util.Log.e("GEMINI_ERROR", "Full Error Body: " + errorBody);
                                binding.tvResults.setText("Analysis Failed. Server Error: " + response.code() + "\nDetails: " + errorBody);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.aibasedcareercounsellingapp.models.GeminiResponse> call, Throwable t) {
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.btnAnalyze.setEnabled(true);
                        binding.tvResults.setText("Network Failure: " + t.getMessage());
                        android.util.Log.e("GEMINI_FAILURE", "Network Error", t);
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
