package com.example.careercounsellingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.careercounsellingapp.data.AppDatabase;
import com.example.careercounsellingapp.data.entities.CareerBenchmark;
import com.example.careercounsellingapp.logic.ResultCalculator;
import com.example.careercounsellingapp.viewmodel.AssessmentViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {

    private RadarChart radarChart;
    private LinearLayout resultsContainer;
    private Map<String, Integer> userRawScores;
    private AssessmentViewModel viewModel;
    private String quizMode;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        if (getIntent().getExtras() == null) {
            Toast.makeText(this, "No scores found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AssessmentViewModel.class);
        userName = getIntent().getStringExtra(WelcomeActivity.USER_NAME_EXTRA);
        if (userName == null) userName = "Guest User";
        
        quizMode = getIntent().getStringExtra("ASSESSMENT_TYPE");
        if (quizMode == null) quizMode = "Standard Assessment";

        radarChart = findViewById(R.id.radarChart);
        resultsContainer = findViewById(R.id.resultsContainer);
        Button btnRestart = findViewById(R.id.btnRestart);

        try {
            userRawScores = (Map<String, Integer>) getIntent().getSerializableExtra("SCORES");
            if (userRawScores == null) userRawScores = new HashMap<>();

            setupRadarChart();
            loadAndDisplayResults();
        } catch (Exception e) {
            Log.e("RESULTS_ACTIVITY", "Error initializing chart or results", e);
            Toast.makeText(this, "Error displaying results.", Toast.LENGTH_SHORT).show();
        }

        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });
    }

    private void setupRadarChart() {
        Map<String, Double> normalizedScores = ResultCalculator.normalizeScores(userRawScores);
        
        List<RadarEntry> entries = new ArrayList<>();
        String[] labels = {
            AssessmentViewModel.TRAIT_ANALYTICAL, 
            AssessmentViewModel.TRAIT_SOCIAL, 
            AssessmentViewModel.TRAIT_CREATIVE, 
            AssessmentViewModel.TRAIT_LEADERSHIP, 
            AssessmentViewModel.TRAIT_STRUCTURED
        };

        for (String label : labels) {
            float val = normalizedScores.getOrDefault(label, 0.0).floatValue();
            entries.add(new RadarEntry(val));
        }

        RadarDataSet dataSet = new RadarDataSet(entries, "Professional Traits");
        dataSet.setColor(Color.parseColor("#D4AF37")); // Gold
        dataSet.setFillColor(Color.parseColor("#D4AF37"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(130);
        dataSet.setLineWidth(2.5f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);

        RadarData data = new RadarData(dataSet);
        radarChart.setData(data);
        
        radarChart.setBackgroundColor(Color.parseColor("#001326"));
        radarChart.getDescription().setEnabled(false);
        radarChart.getLegend().setEnabled(false);
        
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);
        
        radarChart.getYAxis().setAxisMinimum(0f);
        radarChart.getYAxis().setAxisMaximum(100f);
        radarChart.getYAxis().setEnabled(false);
        
        radarChart.animateXY(1400, 1400);
        radarChart.invalidate();
    }

    private void loadAndDisplayResults() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<CareerBenchmark> benchmarks = AppDatabase.getDatabase(this).careerBenchmarkDao().getAllBenchmarks();
            Map<String, Double> normalizedScores = ResultCalculator.normalizeScores(userRawScores);
            List<ResultCalculator.CareerMatch> matches = ResultCalculator.calculateMatches(normalizedScores, benchmarks);

            runOnUiThread(() -> displayMatches(matches, benchmarks));
        });
    }

    private void displayMatches(List<ResultCalculator.CareerMatch> matches, List<CareerBenchmark> benchmarks) {
        resultsContainer.removeAllViews();
        int displayCount = Math.min(3, matches.size());
        
        for (int i = 0; i < displayCount; i++) {
            ResultCalculator.CareerMatch match = matches.get(i);
            CareerBenchmark fullData = findBenchmarkByName(match.getCareerName(), benchmarks);
            
            if (fullData != null) {
                if (i == 0) {
                    double maxPossibleDistance = 500.0;
                    int matchPercent = (int) (100 - (match.getDistance() / maxPossibleDistance * 100));
                    viewModel.saveResult(userName, fullData.getProfileName(), matchPercent, quizMode);
                    Toast.makeText(this, "Result saved to history", Toast.LENGTH_SHORT).show();
                }

                View card = LayoutInflater.from(this).inflate(R.layout.item_career_result, resultsContainer, false);
                
                TextView tvName = card.findViewById(R.id.tvCareerName);
                TextView tvPercent = card.findViewById(R.id.tvMatchPercentage);
                TextView tvDesc = card.findViewById(R.id.tvCareerDescription);

                double maxPossibleDistance = 500.0;
                int matchPercent = (int) (100 - (match.getDistance() / maxPossibleDistance * 100));
                
                tvName.setText((i + 1) + ". " + fullData.getProfileName());
                tvPercent.setText(matchPercent + "% Match");
                tvDesc.setText(fullData.getDescription());

                card.setOnClickListener(v -> showBreakdownDialog(fullData));

                resultsContainer.addView(card);
            }
        }
    }

    private void showBreakdownDialog(CareerBenchmark benchmark) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_career_breakdown, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        BarChart barChart = dialogView.findViewById(R.id.comparisonChart);
        TextView tvNextSteps = dialogView.findViewById(R.id.tvNextStepsContent);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        setupComparisonChart(barChart, benchmark);

        // Find weakest trait
        Map<String, Double> normalizedScores = ResultCalculator.normalizeScores(userRawScores);
        String weakestTrait = "";
        double maxDiff = -1.0;
        
        String[] traits = {
            AssessmentViewModel.TRAIT_ANALYTICAL, 
            AssessmentViewModel.TRAIT_SOCIAL, 
            AssessmentViewModel.TRAIT_CREATIVE, 
            AssessmentViewModel.TRAIT_LEADERSHIP, 
            AssessmentViewModel.TRAIT_STRUCTURED
        };

        for (String trait : traits) {
            double benchVal = getBenchmarkTraitValue(benchmark, trait);
            double userVal = normalizedScores.getOrDefault(trait, 0.0);
            double diff = benchVal - userVal;
            if (diff > maxDiff) {
                maxDiff = diff;
                weakestTrait = trait;
            }
        }

        tvNextSteps.setText("Pro Tip: Since this career requires high " + weakestTrait + ", try engaging in " + getGrowthActivity(weakestTrait) + " to grow.");

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private String getGrowthActivity(String trait) {
        switch (trait) {
            case AssessmentViewModel.TRAIT_ANALYTICAL: return "data-driven projects or logic puzzles";
            case AssessmentViewModel.TRAIT_SOCIAL: return "team collaborations or volunteer work";
            case AssessmentViewModel.TRAIT_CREATIVE: return "brainstorming sessions or artistic hobbies";
            case AssessmentViewModel.TRAIT_LEADERSHIP: return "taking initiative in small group projects";
            case AssessmentViewModel.TRAIT_STRUCTURED: return "organizing workflows or systematic planning";
            default: return "targeted skill-building exercises";
        }
    }

    private void setupComparisonChart(BarChart chart, CareerBenchmark benchmark) {
        Map<String, Double> normalizedScores = ResultCalculator.normalizeScores(userRawScores);
        String[] traits = {"Analyt.", "Social", "Creat.", "Lead.", "Struct."};
        String[] traitKeys = {
            AssessmentViewModel.TRAIT_ANALYTICAL, 
            AssessmentViewModel.TRAIT_SOCIAL, 
            AssessmentViewModel.TRAIT_CREATIVE, 
            AssessmentViewModel.TRAIT_LEADERSHIP, 
            AssessmentViewModel.TRAIT_STRUCTURED
        };

        List<BarEntry> userEntries = new ArrayList<>();
        List<BarEntry> industryEntries = new ArrayList<>();

        for (int i = 0; i < traitKeys.length; i++) {
            userEntries.add(new BarEntry(i, normalizedScores.getOrDefault(traitKeys[i], 0.0).floatValue()));
            industryEntries.add(new BarEntry(i, (float) getBenchmarkTraitValue(benchmark, traitKeys[i])));
        }

        BarDataSet userDataSet = new BarDataSet(userEntries, "Your Score");
        userDataSet.setColor(Color.parseColor("#D4AF37")); // Gold
        userDataSet.setValueTextColor(Color.WHITE);

        BarDataSet industryDataSet = new BarDataSet(industryEntries, "Industry Standard");
        industryDataSet.setColor(Color.argb(100, 255, 255, 255)); // Semi-transparent White
        industryDataSet.setValueTextColor(Color.WHITE);

        float groupSpace = 0.08f;
        float barSpace = 0.03f;
        float barWidth = 0.43f;

        BarData data = new BarData(userDataSet, industryDataSet);
        data.setBarWidth(barWidth);
        chart.setData(data);
        chart.groupBars(0, groupSpace, barSpace);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(traits));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(chart.getBarData().getGroupWidth(groupSpace, barSpace) * 5);

        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(100f);
        chart.getAxisRight().setEnabled(false);
        
        chart.animateY(1500);
        chart.invalidate();
    }

    private double getBenchmarkTraitValue(CareerBenchmark b, String trait) {
        switch (trait) {
            case AssessmentViewModel.TRAIT_ANALYTICAL: return b.getAnalyticalScore();
            case AssessmentViewModel.TRAIT_SOCIAL: return b.getSocialScore();
            case AssessmentViewModel.TRAIT_CREATIVE: return b.getCreativeScore();
            case AssessmentViewModel.TRAIT_LEADERSHIP: return b.getLeadershipScore();
            case AssessmentViewModel.TRAIT_STRUCTURED: return b.getStructuredScore();
            default: return 0.0;
        }
    }

    private CareerBenchmark findBenchmarkByName(String name, List<CareerBenchmark> benchmarks) {
        for (CareerBenchmark b : benchmarks) {
            if (b.getProfileName().equals(name)) return b;
        }
        return null;
    }
}