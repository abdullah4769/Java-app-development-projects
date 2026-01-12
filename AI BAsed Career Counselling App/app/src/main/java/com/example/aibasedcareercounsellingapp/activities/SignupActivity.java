package com.example.aibasedcareercounsellingapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aibasedcareercounsellingapp.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    
    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Set up click listeners
        binding.btnSignUp.setOnClickListener(v -> registerUser());
        
        binding.tvLogin.setOnClickListener(v -> finish());
    }
    
    private void registerUser() {
        String name = binding.etName.getText().toString().trim();
        String age = binding.etAge.getText().toString().trim();
        String qualification = binding.etQualification.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        
        // Validation
        if (TextUtils.isEmpty(name)) {
            binding.etName.setError("Name is required");
            return;
        }
        
        if (TextUtils.isEmpty(age)) {
            binding.etAge.setError("Age is required");
            return;
        }
        
        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 15 || ageValue > 100) {
                binding.etAge.setError("Please enter a valid age (15-100)");
                return;
            }
        } catch (NumberFormatException e) {
            binding.etAge.setError("Please enter a valid number");
            return;
        }
        
        if (TextUtils.isEmpty(qualification)) {
            binding.etQualification.setError("Qualification is required");
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            return;
        }
        
        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            return;
        }
        
        // Show loading state
        binding.btnSignUp.setEnabled(false);
        binding.btnSignUp.setText("Creating Account...");
        
        // Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Save user data to Firestore 'users' collection
                        String userId = mAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("age", Integer.parseInt(age));
                        user.put("qualification", qualification);
                        user.put("createdAt", System.currentTimeMillis());
                        
                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignupActivity.this, 
                                            "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignupActivity.this, 
                                            "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    binding.btnSignUp.setEnabled(true);
                                    binding.btnSignUp.setText("Sign Up");
                                });
                    } else {
                        Toast.makeText(SignupActivity.this, 
                                "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        binding.btnSignUp.setEnabled(true);
                        binding.btnSignUp.setText("Sign Up");
                    }
                });
    }
}
