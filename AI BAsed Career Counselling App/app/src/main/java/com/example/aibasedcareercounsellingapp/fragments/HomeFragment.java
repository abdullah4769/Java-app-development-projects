package com.example.aibasedcareercounsellingapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aibasedcareercounsellingapp.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {
    
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Load user data
        loadUserData();
        
        // Setup Explore Careers button
        binding.btnExploreCareers.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), com.example.aibasedcareercounsellingapp.activities.SkillSelectionActivity.class);
            startActivity(intent);
        });
    }
    
    /**
     * Load user data from Firestore
     */
    private void loadUserData() {
        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            Log.w(TAG, "No user logged in");
            binding.tvUserName.setText("Guest");
            binding.tvEmail.setText("Please log in");
            binding.tvAge.setText("--");
            binding.tvQualification.setText("--");
            return;
        }
        
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get user data
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        Long age = documentSnapshot.getLong("age");
                        String qualification = documentSnapshot.getString("qualification");
                        
                        // Update UI
                        binding.tvUserName.setText(name != null ? name : "User");
                        binding.tvEmail.setText(email != null ? email : "--");
                        binding.tvAge.setText(age != null ? String.valueOf(age) : "--");
                        binding.tvQualification.setText(qualification != null ? qualification : "--");
                        
                        Log.d(TAG, "User data loaded successfully");
                    } else {
                        Log.w(TAG, "User document does not exist");
                        binding.tvUserName.setText("User");
                        Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user data", e);
                    binding.tvUserName.setText("User");
                    Toast.makeText(getContext(), "Failed to load profile: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
