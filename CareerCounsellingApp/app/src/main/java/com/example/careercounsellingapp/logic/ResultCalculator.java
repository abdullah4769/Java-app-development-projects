package com.example.careercounsellingapp.logic;

import com.example.careercounsellingapp.data.entities.CareerBenchmark;
import com.example.careercounsellingapp.viewmodel.AssessmentViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultCalculator {

    private static final double MAX_TRAIT_SCORE = 40.0;

    public static Map<String, Double> normalizeScores(Map<String, Integer> rawScores) {
        Map<String, Double> normalized = new HashMap<>();
        for (Map.Entry<String, Integer> entry : rawScores.entrySet()) {
            double score = (entry.getValue() / MAX_TRAIT_SCORE) * 100.0;
            normalized.put(entry.getKey(), Math.min(100.0, Math.max(0.0, score)));
        }
        return normalized;
    }

    public static List<CareerMatch> calculateMatches(Map<String, Double> userScores, List<CareerBenchmark> benchmarks) {
        List<CareerMatch> matches = new ArrayList<>();

        for (CareerBenchmark benchmark : benchmarks) {
            double distance = 0;
            distance += Math.abs(userScores.getOrDefault(AssessmentViewModel.TRAIT_ANALYTICAL, 0.0) - benchmark.getAnalyticalScore());
            distance += Math.abs(userScores.getOrDefault(AssessmentViewModel.TRAIT_SOCIAL, 0.0) - benchmark.getSocialScore());
            distance += Math.abs(userScores.getOrDefault(AssessmentViewModel.TRAIT_CREATIVE, 0.0) - benchmark.getCreativeScore());
            distance += Math.abs(userScores.getOrDefault(AssessmentViewModel.TRAIT_LEADERSHIP, 0.0) - benchmark.getLeadershipScore());
            distance += Math.abs(userScores.getOrDefault(AssessmentViewModel.TRAIT_STRUCTURED, 0.0) - benchmark.getStructuredScore());

            matches.add(new CareerMatch(benchmark.getProfileName(), distance));
        }

        Collections.sort(matches, Comparator.comparingDouble(CareerMatch::getDistance));
        return matches;
    }

    public static class CareerMatch {
        private final String careerName;
        private final double distance;

        public CareerMatch(String careerName, double distance) {
            this.careerName = careerName;
            this.distance = distance;
        }

        public String getCareerName() { return careerName; }
        public double getDistance() { return distance; }
    }
}