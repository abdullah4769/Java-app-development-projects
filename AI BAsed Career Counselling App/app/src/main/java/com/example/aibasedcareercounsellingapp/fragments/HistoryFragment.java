package com.example.aibasedcareercounsellingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aibasedcareercounsellingapp.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {
    
    private FragmentHistoryBinding binding;
    private com.example.aibasedcareercounsellingapp.adapters.HistoryAdapter adapter;
    private java.util.List<com.example.aibasedcareercounsellingapp.models.HistoryItem> historyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        historyList = new java.util.ArrayList<>();
        adapter = new com.example.aibasedcareercounsellingapp.adapters.HistoryAdapter(historyList);
        binding.recyclerView.setAdapter(adapter);
        
        loadHistory();
    }

    private void loadHistory() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

        // Fetch Career Predictions
        db.collection("users").document(uid).collection("history")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            // Safely extract predictions list
                            java.util.List<java.util.Map<String, Object>> predictions = 
                                    (java.util.List<java.util.Map<String, Object>>) doc.get("predictions");
                            
                            String details = "Result: ";
                            if (predictions != null && !predictions.isEmpty()) {
                                details += predictions.get(0).get("career"); // Top career
                            } else {
                                details += "Unknown";
                            }
                            
                            long timestamp = doc.getLong("timestamp");
                            historyList.add(new com.example.aibasedcareercounsellingapp.models.HistoryItem("Career Prediction", details, timestamp));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    // Fetch Resume Analysis (Chained)
                    fetchGeneratedResumes(db, uid);
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    android.widget.Toast.makeText(getContext(), "Error loading history", android.widget.Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchGeneratedResumes(com.google.firebase.firestore.FirebaseFirestore db, String uid) {
        db.collection("users").document(uid).collection("generated_resumes")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        long timestamp = doc.getLong("timestamp");
                        historyList.add(new com.example.aibasedcareercounsellingapp.models.HistoryItem("Resume Generated", "For: " + name, timestamp));
                    }
                    // Fetch Quiz Results (Chained)
                    fetchQuizResults(db, uid);
                })
                .addOnFailureListener(e -> fetchQuizResults(db, uid));
    }

    private void fetchQuizResults(com.google.firebase.firestore.FirebaseFirestore db, String uid) {
        db.collection("users").document(uid).collection("history") // Assuming Quiz results are stored here as 'Career Prediction' originally
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Logic already handled in first batch, but this function is conceptually for 'quiz_results' if you separate them. 
                    // In current implementation, 'Career Prediction' comes from 'history' collection.
                    // User asked for 'quiz_results', let's check if we saved anything there. 
                    // If AdaptiveQuizActivity actually saved to 'history', then we are covered by loadHistory(). 
                    // Let's assume we want to pull 'resume_analysis' now.
                    fetchResumeAnalysis(db, uid);
                })
                .addOnFailureListener(e -> fetchResumeAnalysis(db, uid));
    }

    private void fetchResumeAnalysis(com.google.firebase.firestore.FirebaseFirestore db, String uid) {
        db.collection("users").document(uid).collection("resume_analysis")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        String analysis = doc.getString("analysis");
                        if (analysis != null && analysis.length() > 50) {
                            analysis = analysis.substring(0, 50) + "..."; // Truncate
                        }
                        long timestamp = doc.getLong("timestamp");
                        historyList.add(new com.example.aibasedcareercounsellingapp.models.HistoryItem("Resume Analysis", analysis, timestamp));
                    }
                    finalizeList();
                })
                .addOnFailureListener(e -> finalizeList());
    }

    private void finalizeList() {
        // Sort combined list by timestamp
        java.util.Collections.sort(historyList, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
        updateUI();
    }

    private void updateUI() {
        binding.progressBar.setVisibility(View.GONE);
        if (historyList.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
