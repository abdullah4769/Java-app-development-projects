package com.example.aibasedcareercounsellingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aibasedcareercounsellingapp.databinding.FragmentQuizBinding;

public class QuizFragment extends Fragment {
    
    private FragmentQuizBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Spinner
        String[] educationLevels = {"Matric (Science)", "Matric (Arts)", "FSc Pre-Engineering", 
                                    "FSc Pre-Medical", "ICS (Computer Science)", "I.Com (Commerce)", "FA (Arts)"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, educationLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEducation.setAdapter(adapter);

        binding.btnStartQuiz.setOnClickListener(v -> {
            String selectedEducation = binding.spinnerEducation.getSelectedItem().toString();
            android.content.Intent intent = new android.content.Intent(getActivity(), 
                    com.example.aibasedcareercounsellingapp.activities.AdaptiveQuizActivity.class);
            intent.putExtra("education", selectedEducation);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
