package com.example.careercounsellingapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.careercounsellingapp.data.entities.Option;
import com.example.careercounsellingapp.data.relations.QuestionWithOptions;
import com.example.careercounsellingapp.viewmodel.AssessmentViewModel;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private AssessmentViewModel viewModel;
    private ProgressBar progressBar;
    private TextView tvProgress, tvRole, tvContext, tvQuestionText;
    private LinearLayout optionsContainer;
    private List<QuestionWithOptions> questions = new ArrayList<>();
    private String quizMode;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        viewModel = new ViewModelProvider(this).get(AssessmentViewModel.class);
        
        quizMode = getIntent().getStringExtra(WelcomeActivity.QUIZ_MODE_EXTRA);
        userName = getIntent().getStringExtra(WelcomeActivity.USER_NAME_EXTRA);
        if (quizMode == null) quizMode = WelcomeActivity.MODE_SJT;
        if (userName == null) userName = "Guest User";

        initViews();
        observeViewModel();
        setupBackPressed();
        
        viewModel.loadQuestions(quizMode).observe(this, fetchedQuestions -> {
            if (fetchedQuestions != null && !fetchedQuestions.isEmpty()) {
                Log.d("QUIZ_ACTIVITY", "Questions fetched: " + fetchedQuestions.size());
                this.questions = fetchedQuestions;
                Integer currentIndex = viewModel.getCurrentQuestionIndex().getValue();
                if (currentIndex != null && currentIndex == 0) {
                    displayQuestion(questions.get(0));
                    updateProgress(0);
                }
            } else {
                Toast.makeText(this, "Database Loading or Empty...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Your progress will be lost.")
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .setNegativeButton("Stay", null)
                .show();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
        tvRole = findViewById(R.id.tvRole);
        tvContext = findViewById(R.id.tvContext);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        optionsContainer = findViewById(R.id.optionsContainer);
        tvQuestionText.setPadding(24, 24, 24, 24);
    }

    private void observeViewModel() {
        viewModel.getCurrentQuestionIndex().observe(this, index -> {
            if (index != null && !questions.isEmpty()) {
                if (index < questions.size()) {
                    displayQuestion(questions.get(index));
                    updateProgress(index);
                }
            }
        });

        viewModel.isQuizFinished().observe(this, isFinished -> {
            if (isFinished != null && isFinished) {
                finishAssessment();
            }
        });
    }

    private void displayQuestion(QuestionWithOptions questionData) {
        if (questionData == null || questionData.question == null) return;

        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                tvQuestionText.setText(questionData.question.getDilemma());
                
                if (quizMode.equals(WelcomeActivity.MODE_SJT)) {
                    tvRole.setVisibility(View.VISIBLE);
                    tvContext.setVisibility(View.VISIBLE);
                    tvRole.setText("Role: " + questionData.question.getRole());
                    tvContext.setText(questionData.question.getContext());
                    setupSJTOptions(questionData.options);
                } else {
                    tvRole.setVisibility(View.GONE);
                    tvContext.setVisibility(View.GONE);
                    setupPreferenceOptions(questionData.question.getRole()); // Role stores the Trait
                }

                findViewById(R.id.cardQuestion).startAnimation(fadeIn);
                optionsContainer.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        findViewById(R.id.cardQuestion).startAnimation(fadeOut);
        optionsContainer.startAnimation(fadeOut);
    }

    private void setupSJTOptions(List<Option> options) {
        optionsContainer.removeAllViews();
        if (options == null || options.isEmpty()) return;

        for (Option option : options) {
            View optionView = LayoutInflater.from(this).inflate(R.layout.item_option, optionsContainer, false);
            TextView tvOption = optionView.findViewById(R.id.tvOptionText);
            tvOption.setText(option.getText());

            optionView.setOnClickListener(v -> {
                viewModel.processSelection(option);
                viewModel.getNextQuestion(questions.size()); 
            });

            optionsContainer.addView(optionView);
        }
    }

    private void setupPreferenceOptions(String trait) {
        optionsContainer.removeAllViews();
        String[] labels = {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"};
        String[] colors = {"#81C784", "#C5E1A5", "#B0BEC5", "#FFAB91", "#E57373"};
        int[] scores = {2, 1, 0, -1, -2};

        for (int i = 0; i < labels.length; i++) {
            MaterialButton button = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle);
            button.setText(labels[i]);
            button.setTextColor(Color.parseColor("#001F3F")); // Midnight Blue for contrast
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors[i])));
            button.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#D4AF37"))); // Gold stroke
            button.setStrokeWidth(2);
            button.setCornerRadius(24);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 16);
            button.setLayoutParams(params);

            final int score = scores[i];
            button.setOnClickListener(v -> {
                viewModel.processPreferenceScore(trait, score);
                viewModel.getNextQuestion(questions.size());
            });

            optionsContainer.addView(button);
        }
    }

    private void updateProgress(int index) {
        int total = questions.size();
        if (total > 0) {
            progressBar.setMax(total);
            progressBar.setProgress(index + 1);
            tvProgress.setText("Question " + (index + 1) + " of " + total);
        }
    }

    private void finishAssessment() {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("SCORES", (Serializable) viewModel.getTraitScores());
        intent.putExtra(WelcomeActivity.USER_NAME_EXTRA, userName);
        String displayType = quizMode.equals(WelcomeActivity.MODE_SJT) ? "Situational Approach" : "Preference Approach";
        intent.putExtra("ASSESSMENT_TYPE", displayType);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}