# 🚀 AI-Powered Career Counselling & Smart Resume Analyzer
### *Transforming Career Guidance with Generative AI (Google Gemini)*

This repository contains the full source code for a production-grade Android application that leverages Large Language Models (LLMs) to provide automated resume auditing, career path mapping, and intelligent professional coaching.

---

## 📖 Table of Contents
1. [Executive Summary](#-executive-summary)
2. [Key Features](#-key-features)
3. [Technical Architecture](#-technical-architecture)
4. [Tech Stack](#-tech-stack)
5. [UI/UX Aesthetics](#-uiux-aesthetics)
6. [API Implementation & 404 Resolution](#-api-implementation--404-resolution)
7. [Installation Guide](#-installation-guide)
8. [Project Roadmap](#-project-roadmap)

---

## 🌟 Executive Summary
In the modern job market, the first hurdle is passing the **ATS (Applicant Tracking System)**. Many students possess the skills but fail to present them effectively. This project solves this by providing an AI-driven "Second Opinion" that critiques resumes with the precision of a professional recruiter.

---

## ✨ Key Features

### 🔍 **1. Deep Resume Audit**
- **ATS Score Calculation:** A data-driven score (0-100) based on industry standards.
- **Skill Gap Analysis:** Identifies missing technical and soft skills for specific job roles.
- **Formatting Engine:** Highlights issues with font consistency, layout, and readability.

### 🤖 **2. Smart AI Career Coach**
- **Interactive Chat:** A chatbot built on Gemini 1.5 Flash that maintains conversation context.
- **Personalized Advice:** Tailored suggestions based on the user's uploaded resume and quiz performance.

### 📝 **3. Adaptive Assessment Quiz**
- Dynamic questioning to gauge the user's technical aptitude.
- Career path suggestions (e.g., suggesting "Full Stack Development" vs "Data Science" based on user input).

---

## 🏗 Technical Architecture

The application follows the **MVVM (Model-View-ViewModel)** pattern combined with **Clean Architecture** principles to ensure the code is modular, testable, and maintainable.



### **Data Flow:**
1. **Input:** User uploads a PDF.
2. **Extraction:** Text is parsed using `Apache PDFBox` or `IText7`.
3. **Processing:** The extracted text is sent to the **Gemini API** via **Retrofit**.
4. **Output:** Structured JSON response is parsed and rendered in a high-contrast Material 3 UI.

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 17 / XML |
| **Framework** | Android SDK (Min API 24) |
| **AI Engine** | Google Gemini 1.5 Flash (Generative Language API) |
| **Networking** | Retrofit 2.9.0 + OkHttp 4.x |
| **Backend** | Firebase Auth, Firestore, Cloud Storage |
| **UI Library** | Material Design 3 (M3) |

---

## 🎨 UI/UX Aesthetics
The app features a **"Midnight Obsidian"** theme designed for the modern professional.
- **Contrast:** Deep Charcoal backgrounds (#121212) with Neon Blue accents (#4D96FF).
- **Interactivity:** Every button utilizes `selectableItemBackground` for tactile feedback.
- **Layout:** Glassmorphic card designs with 16dp corner radius for a premium feel.



---

## 🔧 API Implementation & 404 Resolution

During development, a critical **404 Model Not Found** error was identified and resolved. This documented fix ensures the robustness of the networking layer.

### **The Correct Retrofit Interface:**
```java
@Headers("Content-Type: application/json")
@POST("v1beta/models/gemini-1.5-flash:generateContent")
Call<GeminiResponse> getAnalysis(
    @Query("key") String apiKey, 
    @Body GeminiRequest request
);
