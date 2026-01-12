package com.example.aibasedcareercounsellingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aibasedcareercounsellingapp.activities.ResumeAnalysisActivity;
import com.example.aibasedcareercounsellingapp.activities.ResumeBuilderActivity;
import com.example.aibasedcareercounsellingapp.databinding.FragmentResumeBinding;

public class ResumeFragment extends Fragment {
    
    private FragmentResumeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResumeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Resume Analyzer button
        binding.btnAnalyzeResume.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResumeAnalysisActivity.class);
            startActivity(intent);
        });
        
        // Resume Builder button
        binding.btnBuildResume.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResumeBuilderActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
