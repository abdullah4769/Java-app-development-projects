# PathFinder 🧭
### Your Personal Offline Career Compass

> **Status:** ✅ Complete & Offline Ready
> **Platform:** Android (Java)

---

## 👋 What is PathFinder?
PathFinder is a smart Android app that helps you discover the best career for your personality. 

Most career quizzes require the internet or ask random questions. PathFinder is different—it works **100% Offline** and uses a mathematical engine to compare your traits against real-world job requirements. Whether you are a student or a professional, this app helps you find your direction.

---

## ✨ Features for Users

### 1. Two Ways to Test Yourself
* **Situational Approach (Real Life):** The app gives you real-world scenarios (e.g., "A deadline is approaching...") and analyzes how you react.
* **Preference Approach (Direct):** A straightforward test that asks about your likes, dislikes, and working style.

### 2. Smart Results & Coaching
* **Instant Match:** See exactly which career fits you best (e.g., "You are a 92% match for Data Scientist").
* **"Why?" Breakdown:** Tap on a result to see a chart comparing *your* score vs. the *job's* requirements.
* **Pro Tips:** The app tells you exactly which skills you need to improve to get that dream job.

### 3. Multi-User History
* **Play with Friends:** The app asks for a Name before every test.
* **Save Everything:** It saves every result locally. You can scroll back through History to see "Ahmad's Result" or "Ali's Result" separately.
* **Privacy First:** No data leaves your phone. Everything is stored in a secure offline database.

---

## 🛠️ Technical Details (For Developers)

This project was built to demonstrate advanced Android development skills, focusing on clean architecture and algorithm efficiency.

### 🏗️ Architecture: MVVM (Model-View-ViewModel)
The app is built using industry-standard patterns to ensure the code is clean and scalable:
* **Model:** Handles the data (User answers, Career benchmarks).
* **View:** The UI (Activities, XML Layouts) that observes data changes.
* **ViewModel:** The "Brain" that processes logic without freezing the UI.

### 💾 Database: Room (SQLite)
* Implements a fully offline **Room Database**.
* Uses **DAOs (Data Access Objects)** to handle complex queries (saving history, fetching questions).
* Includes **Data Persistence** so users never lose their history, even if they close the app.

### 📐 The Algorithm: Euclidean Distance
Instead of simple "If/Else" statements, PathFinder uses vector mathematics.
1.  The User is treated as a vector of traits (e.g., Leadership, Logic, Creativity).
2.  Each Career is a target vector.
3.  The app calculates the **Euclidean Distance** between the User and the Career.
    * *Shorter Distance = Higher Match Percentage.*

### 🎨 UI & Design
* **Material Design:** Uses a custom "Midnight Blue & Gold" theme for a professional academic look.
* **Vector Assets:** Custom XML-drawn Compass icon (Resolution independent).
* **MPAndroidChart:** Integrated for generating dynamic Radar and Bar charts.

---

## 🚀 How to Run This Project

1.  **Clone the Repo:** Download this project to your computer.
2.  **Open in Android Studio:** Open the root folder.
3.  **Sync:** Let Gradle download the necessary libraries (Room, Material Design).
4.  **Run:** Connect your mobile via USB or use an Emulator and hit **Play**.

---

## 👨‍💻 Author
**Developed by [Abdullah Abid]**
*A project demonstrating Offline-First Architecture, Mathematical Logic, and Android UI/UX Design.*