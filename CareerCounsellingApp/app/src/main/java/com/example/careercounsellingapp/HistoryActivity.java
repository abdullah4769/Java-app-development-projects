package com.example.careercounsellingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.careercounsellingapp.viewmodel.AssessmentViewModel;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvNoHistory;
    private AssessmentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistory = findViewById(R.id.rvHistory);
        tvNoHistory = findViewById(R.id.tvNoHistory);
        
        // Update the 'No History' message to be more encouraging
        tvNoHistory.setText("No career history found. Take a quiz to begin!");

        try {
            rvHistory.setLayoutManager(new LinearLayoutManager(this));
            
            // Use ViewModel for centralized data fetching
            viewModel = new ViewModelProvider(this).get(AssessmentViewModel.class);

            // Observe the results list
            viewModel.getAllResults().observe(this, results -> {
                if (results != null && !results.isEmpty()) {
                    Log.d("HISTORY_CHECK", "Results found: " + results.size());
                    HistoryAdapter adapter = new HistoryAdapter(results);
                    rvHistory.setAdapter(adapter);
                    
                    // UI State: Data found
                    rvHistory.setVisibility(View.VISIBLE);
                    tvNoHistory.setVisibility(View.GONE);
                } else {
                    Log.d("HISTORY_CHECK", "No results in database.");
                    // UI State: No data
                    rvHistory.setVisibility(View.GONE);
                    tvNoHistory.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            Log.e("HISTORY_ACTIVITY", "Error during history initialization", e);
            Toast.makeText(this, "A connection error occurred.", Toast.LENGTH_SHORT).show();
        }
    }
}