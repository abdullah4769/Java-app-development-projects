package com.example.aibasedcareercounsellingapp.ml;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * TensorFlow Lite model wrapper for Career Prediction
 * Loads career_model.tflite and provides prediction capabilities
 */
public class CareerClassifier {
    
    private static final String TAG = "CareerClassifier";
    private static final String MODEL_FILE = "career_model.tflite";
    private static final int INPUT_SIZE = 100;  // 100 skills
    private static final int OUTPUT_SIZE = 100; // 100 careers
    
    private Interpreter tflite;
    private boolean isModelLoaded = false;
    
    /**
     * Constructor - Loads the TFLite model from assets
     * @param context Application context
     */
    public CareerClassifier(Context context) {
        try {
            MappedByteBuffer modelBuffer = loadModelFile(context);
            tflite = new Interpreter(modelBuffer);
            isModelLoaded = true;
            Log.d(TAG, "TFLite model loaded successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error loading TFLite model: " + e.getMessage(), e);
            isModelLoaded = false;
        }
    }
    
    /**
     * Load the TFLite model file from assets
     * @param context Application context
     * @return MappedByteBuffer containing the model
     * @throws IOException if model file not found or cannot be read
     */
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    
    /**
     * Predict career based on skill vector
     * @param inputSkills Float array of length 100 (1.0 for present skill, 0.0 for absent)
     * @return Index of predicted career (0-99) or -1 if error
     */
    public int predictCareer(float[] inputSkills) {
        if (!isModelLoaded) {
            Log.e(TAG, "Cannot predict: Model not loaded");
            return -1;
        }
        
        if (inputSkills == null || inputSkills.length != INPUT_SIZE) {
            Log.e(TAG, "Invalid input: Expected array of length " + INPUT_SIZE);
            return -1;
        }
        
        try {
            // Prepare input tensor (1, 100)
            float[][] input = new float[1][INPUT_SIZE];
            input[0] = inputSkills;
            
            // Prepare output tensor (1, 100)
            float[][] output = new float[1][OUTPUT_SIZE];
            
            // Run inference
            tflite.run(input, output);
            
            // Find index with highest probability (Softmax output)
            int predictedIndex = 0;
            float maxProbability = output[0][0];
            
            for (int i = 1; i < OUTPUT_SIZE; i++) {
                if (output[0][i] > maxProbability) {
                    maxProbability = output[0][i];
                    predictedIndex = i;
                }
            }
            
            Log.d(TAG, "Prediction successful - Index: " + predictedIndex + 
                    ", Confidence: " + String.format("%.2f%%", maxProbability * 100));
            
            return predictedIndex;
            
        } catch (Exception e) {
            Log.e(TAG, "Error during prediction: " + e.getMessage(), e);
            return -1;
        }
    }
    
    /**
     * Predict career and get the full output probabilities
     * @param inputSkills Float array of length 100
     * @return Float array of probabilities for all careers, or null if error
     */
    public float[] predictCareerProbabilities(float[] inputSkills) {
        if (!isModelLoaded) {
            Log.e(TAG, "Cannot predict: Model not loaded");
            return null;
        }
        
        if (inputSkills == null || inputSkills.length != INPUT_SIZE) {
            Log.e(TAG, "Invalid input: Expected array of length " + INPUT_SIZE);
            return null;
        }
        
        try {
            // Prepare input tensor (1, 100)
            float[][] input = new float[1][INPUT_SIZE];
            input[0] = inputSkills;
            
            // Prepare output tensor (1, 100)
            float[][] output = new float[1][OUTPUT_SIZE];
            
            // Run inference
            tflite.run(input, output);
            
            return output[0];
            
        } catch (Exception e) {
            Log.e(TAG, "Error during prediction: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Predict top N careers based on skill vector
     * @param inputSkills Float array of length 100
     * @param topN Number of top careers to return
     * @return List of CareerPrediction sorted by probability (descending), or null if error
     */
    public java.util.List<com.example.aibasedcareercounsellingapp.models.CareerPrediction> predictTopNCareers(float[] inputSkills, int topN) {
        if (!isModelLoaded) {
            Log.e(TAG, "Cannot predict: Model not loaded");
            return null;
        }
        
        if (inputSkills == null || inputSkills.length != INPUT_SIZE) {
            Log.e(TAG, "Invalid input: Expected array of length " + INPUT_SIZE);
            return null;
        }
        
        if (topN < 1 || topN > OUTPUT_SIZE) {
            Log.e(TAG, "Invalid topN: Must be between 1 and " + OUTPUT_SIZE);
            return null;
        }
        
        try {
            // Get all probabilities
            float[] probabilities = predictCareerProbabilities(inputSkills);
            if (probabilities == null) {
                return null;
            }
            
            // Create CareerPrediction objects for all careers
            java.util.List<com.example.aibasedcareercounsellingapp.models.CareerPrediction> predictions = 
                    new java.util.ArrayList<>();
            for (int i = 0; i < OUTPUT_SIZE; i++) {
                predictions.add(new com.example.aibasedcareercounsellingapp.models.CareerPrediction(i, probabilities[i]));
            }
            
            // Sort by probability (descending)
            java.util.Collections.sort(predictions);
            
            // Get top N
            java.util.List<com.example.aibasedcareercounsellingapp.models.CareerPrediction> topPredictions = 
                    predictions.subList(0, Math.min(topN, predictions.size()));
            
            Log.d(TAG, "Top " + topN + " predictions generated successfully");
            for (int i = 0; i < topPredictions.size(); i++) {
                com.example.aibasedcareercounsellingapp.models.CareerPrediction pred = topPredictions.get(i);
                Log.d(TAG, (i + 1) + ". " + pred.getCareerName() + " - " + pred.getMatchPercentage() + "%");
            }
            
            return topPredictions;
            
        } catch (Exception e) {
            Log.e(TAG, "Error during top-N prediction: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Check if model is loaded successfully
     * @return true if model is loaded, false otherwise
     */
    public boolean isModelLoaded() {
        return isModelLoaded;
    }
    
    /**
     * Close the interpreter and release resources
     */
    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
            isModelLoaded = false;
            Log.d(TAG, "TFLite model closed");
        }
    }
    
    /**
     * Get input size (number of skills)
     */
    public static int getInputSize() {
        return INPUT_SIZE;
    }
    
    /**
     * Get output size (number of careers)
     */
    public static int getOutputSize() {
        return OUTPUT_SIZE;
    }
}
