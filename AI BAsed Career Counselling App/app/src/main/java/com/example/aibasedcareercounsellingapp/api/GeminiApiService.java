package com.example.aibasedcareercounsellingapp.api;

import com.example.aibasedcareercounsellingapp.models.GeminiRequest;
import com.example.aibasedcareercounsellingapp.models.GeminiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit service interface for Gemini API
 */
public interface GeminiApiService {
    
    @retrofit2.http.Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    Call<GeminiResponse> getAnalysis(
        @Query("key") String apiKey,
        @Body GeminiRequest body
    );
}
