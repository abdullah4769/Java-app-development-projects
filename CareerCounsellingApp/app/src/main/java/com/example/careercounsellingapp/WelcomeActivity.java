package com.example.careercounsellingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class WelcomeActivity extends AppCompatActivity {

    public static final String QUIZ_MODE_EXTRA = "QUIZ_MODE";
    public static final String USER_NAME_EXTRA = "USER_NAME";
    public static final String MODE_SJT = "SJT";
    public static final String MODE_PREFERENCE = "Preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        MaterialCardView cardSituational = findViewById(R.id.cardSituational);
        MaterialCardView cardPreference = findViewById(R.id.cardPreference);
        ImageButton btnHistory = findViewById(R.id.btnHistory);

        cardSituational.setOnClickListener(v -> showNameDialog(MODE_SJT));
        cardPreference.setOnClickListener(v -> showNameDialog(MODE_PREFERENCE));
        
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void showNameDialog(String mode) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_name, null);
        TextInputEditText etUserName = dialogView.findViewById(R.id.etUserName);
        TextInputLayout nameInputLayout = dialogView.findViewById(R.id.nameInputLayout);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.CustomDialogTheme)
                .setView(dialogView)
                .setPositiveButton("Start Quiz", null) // Set to null first to override dismiss behavior
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String userName = etUserName.getText().toString().trim();
                if (userName.isEmpty()) {
                    nameInputLayout.setError("Please enter your name to begin");
                } else {
                    nameInputLayout.setError(null);
                    dialog.dismiss();
                    startQuiz(mode, userName);
                }
            });
        });

        dialog.show();
    }

    private void startQuiz(String mode, String userName) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(QUIZ_MODE_EXTRA, mode);
        intent.putExtra(USER_NAME_EXTRA, userName);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}