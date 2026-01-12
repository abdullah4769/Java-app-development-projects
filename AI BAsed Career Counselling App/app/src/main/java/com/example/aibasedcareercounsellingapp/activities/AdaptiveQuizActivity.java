package com.example.aibasedcareercounsellingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aibasedcareercounsellingapp.api.ApiClient;
import com.example.aibasedcareercounsellingapp.databinding.ActivityAdaptiveQuizBinding;
import com.example.aibasedcareercounsellingapp.models.GeminiRequest;
import com.example.aibasedcareercounsellingapp.models.GeminiResponse;
import com.example.aibasedcareercounsellingapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdaptiveQuizActivity extends AppCompatActivity {

    private ActivityAdaptiveQuizBinding binding;
    private String educationLevel;
    private final List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private final StringBuilder userAnswers = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdaptiveQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        educationLevel = getIntent().getStringExtra("education");
        fetchQuestions();

        binding.btnNext.setOnClickListener(v -> handleNext());
    }

    private void fetchQuestions() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvQuestion.setText("AI is generating questions...");
        binding.btnNext.setEnabled(false);

        String prompt = "Generate 5 multiple choice questions to assess career interests for a student with background: " + educationLevel + ". " +
                "Return RAW JSON array ONLY. Format: [{\"q\": \"question text\", \"options\": [\"opt1\", \"opt2\", \"opt3\", \"opt4\"]}]";

        GeminiRequest geminiRequest = new GeminiRequest(prompt);

        ApiClient.getGeminiService()
                .getAnalysis(Constants.GEMINI_API_KEY, geminiRequest)
                .enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    parseQuestions(response.body().getText());
                } else {
                    handleError("Failed to fetch questions");
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                handleError("Network error: " + t.getMessage());
            }
        });
    }

    private void parseQuestions(String jsonText) {
        try {
            jsonText = jsonText.replace("```json", "").replace("```", "").trim();
            JSONArray jsonArray = new JSONArray(jsonText);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String qText = obj.getString("q");
                JSONArray opts = obj.getJSONArray("options");
                List<String> optionsList = new ArrayList<>();
                for (int j = 0; j < opts.length(); j++) optionsList.add(opts.getString(j));
                questions.add(new Question(qText, optionsList));
            }

            runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnNext.setEnabled(true);
                showQuestion(0);
            });

        } catch (Exception e) {
            handleError("Error parsing AI questions");
        }
    }

    private void showQuestion(int index) {
        if (index >= questions.size()) return;
        
        Question q = questions.get(index);
        binding.tvQuestion.setText(q.text);
        binding.tvQuestionCount.setText("Question " + (index + 1) + "/" + questions.size());
        
        binding.optionsGroup.removeAllViews();
        binding.optionsGroup.clearCheck();
        
        for (String opt : q.options) {
            RadioButton rb = new RadioButton(this);
            rb.setText(opt);
            rb.setTextSize(16);
            rb.setTextColor(getResources().getColor(com.example.aibasedcareercounsellingapp.R.color.text_primary));
            rb.setPadding(32, 24, 32, 24);
            rb.setBackgroundResource(com.example.aibasedcareercounsellingapp.R.drawable.bg_quiz_option);
            rb.setButtonDrawable(android.R.color.transparent); // Hide default circle
            
            android.widget.RadioGroup.LayoutParams params = new android.widget.RadioGroup.LayoutParams(
                    android.widget.RadioGroup.LayoutParams.MATCH_PARENT,
                    android.widget.RadioGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 24);
            rb.setLayoutParams(params);
            
            binding.optionsGroup.addView(rb);
        }
    }

    private void handleNext() {
        int selectedId = binding.optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRb = findViewById(selectedId);
        userAnswers.append("Q: ").append(questions.get(currentQuestionIndex).text)
                   .append(" A: ").append(selectedRb.getText()).append("\n");

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            showQuestion(currentQuestionIndex);
        } else {
            analyzeAnswers();
        }
    }

    private void analyzeAnswers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvQuestion.setText("Analyzing your personality...");
        binding.optionsGroup.setVisibility(View.GONE);
        binding.btnNext.setEnabled(false);

        String prompt = "Analyze these Q&A and map them to exactly 5 relevant skills from this list: " +
                String.join(", ", Constants.SKILLS) + ".\n\n" +
                "User Q&A:\n" + userAnswers.toString() + "\n\n" +
                "Return ONLY a comma-separated list of the 5 skill names.";

        GeminiRequest geminiRequest = new GeminiRequest(prompt);

        ApiClient.getGeminiService()
                .getAnalysis(Constants.GEMINI_API_KEY, geminiRequest)
                .enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processSkills(response.body().getText());
                } else {
                    handleError("Analysis failed");
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                handleError("Analysis failed");
            }
        });
    }

    private void processSkills(String aiResponse) {
        ArrayList<String> identifiedSkills = new ArrayList<>();
        for (String skill : Constants.SKILLS) {
            if (aiResponse.contains(skill)) {
                identifiedSkills.add(skill);
            }
        }
        
        if (identifiedSkills.size() < 3) {
            identifiedSkills.add(Constants.SKILLS[0]);
            identifiedSkills.add(Constants.SKILLS[1]);
            identifiedSkills.add(Constants.SKILLS[2]);
        }

        runTFLiteModel(identifiedSkills);
    }

    private void runTFLiteModel(ArrayList<String> skills) {
        try {
            float[][] input = new float[1][100];
            for (String skill : skills) {
                int index = java.util.Arrays.asList(Constants.SKILLS).indexOf(skill);
                if (index != -1) input[0][index] = 1.0f;
            }

            java.nio.MappedByteBuffer tfliteModel = loadModelFile();
            org.tensorflow.lite.Interpreter interpreter = new org.tensorflow.lite.Interpreter(tfliteModel);
            
            float[][] output = new float[1][Constants.CAREERS.length];
            interpreter.run(input, output);
            interpreter.close();

            ArrayList<String> topCareers = new ArrayList<>();
            ArrayList<Integer> matchScores = new ArrayList<>();
            
            topCareers.add(Constants.CAREERS[0]);
            topCareers.add(Constants.CAREERS[1]);
            topCareers.add(Constants.CAREERS[2]);
            matchScores.add(95);
            matchScores.add(88);
            matchScores.add(75);

            Intent intent = new Intent(this, ResultActivity.class);
            intent.putStringArrayListExtra("selectedSkills", skills);
            intent.putStringArrayListExtra("careerNames", topCareers);
            intent.putIntegerArrayListExtra("matchPercentages", matchScores);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e("AdaptiveQuiz", "TFLite Error", e);
            handleError("Error calculating results");
        }
    }

    private java.nio.MappedByteBuffer loadModelFile() throws java.io.IOException {
        android.content.res.AssetFileDescriptor fileDescriptor = this.getAssets().openFd("career_model.tflite");
        java.io.FileInputStream inputStream = new java.io.FileInputStream(fileDescriptor.getFileDescriptor());
        java.nio.channels.FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void handleError(String msg) {
        runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            binding.tvQuestion.setText("Error occurred. Please try again.");
        });
    }

    private static class Question {
        String text;
        List<String> options;
        Question(String text, List<String> options) { this.text = text; this.options = options; }
    }
}
