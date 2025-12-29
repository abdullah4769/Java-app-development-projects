package com.example.careercounsellingapp.data;

import com.example.careercounsellingapp.data.dao.CareerBenchmarkDao;
import com.example.careercounsellingapp.data.dao.QuestionDao;
import com.example.careercounsellingapp.data.entities.CareerBenchmark;
import com.example.careercounsellingapp.data.entities.Option;
import com.example.careercounsellingapp.data.entities.Question;
import com.example.careercounsellingapp.viewmodel.AssessmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {

    public static void populateAsync(AppDatabase db) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            QuestionDao questionDao = db.questionDao();
            CareerBenchmarkDao benchmarkDao = db.careerBenchmarkDao();

            if (questionDao.getAllQuestions().isEmpty()) {
                insertSJTQuestions(questionDao);
                insertPreferenceQuestions(questionDao);
                insertBenchmarks(benchmarkDao);
            }
        });
    }

    private static void insertSJTQuestions(QuestionDao dao) {
        // Humanized SJT Questions
        insertSJT(dao, 1, "Software Lead", "Your team is 2 hours away from a major release and the main server fails.", "How do you handle the pressure?",
                new String[]{"Analyze server logs immediately to find the root cause.", "Gather the team to keep morale high and brainstorm a fix.", "Take executive command and delegate specific recovery tasks."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Leadership\":2}"});

        insertSJT(dao, 2, "Marketing Director", "A viral campaign suddenly receives unexpected negative backlash.", "What is your strategic response?",
                new String[]{"Evaluate the sentiment data to adjust the campaign targeting.", "Reach out to influencers to rebuild positive brand image.", "Halt the campaign and issue a firm public statement."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Leadership\":2}"});

        insertSJT(dao, 3, "Operations Manager", "The main supply chain is disrupted by a global logistics strike.", "How do you maintain efficiency?",
                new String[]{"Audit alternative routes and cost-impact scenarios.", "Negotiate with local partners to share resources.", "Implement a new emergency contingency plan immediately."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 4, "Senior Architect", "The client suddenly requests a major structural change mid-construction.", "How do you resolve this conflict?",
                new String[]{"Analyze the technical feasibility and safety impact.", "Meet with the client to understand their underlying needs.", "Present a creative compromise that minimizes delays."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Creative\":2}"});

        insertSJT(dao, 5, "Product Head", "A competitor launches a similar feature just before your own launch.", "How do you pivot?",
                new String[]{"Review the competitor's feature for technical weaknesses.", "Discuss with users to find what they are still missing.", "Innovate a unique value proposition that sets you apart."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Creative\":2}"});

        insertSJT(dao, 6, "Financial Director", "An internal audit reveals a significant budgeting error from last year.", "What is your priority?",
                new String[]{"Manually re-calculate all accounts to ensure precision.", "Inform the board and take full responsibility.", "Establish stricter reporting protocols for the future."},
                new String[]{"{\"Analytical\":2}", "{\"Leadership\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 7, "HR Manager", "Two top performers are in a heated dispute over project credit.", "How do you mediate?",
                new String[]{"Review the project logs to verify individual contributions.", "Hold a joint session to rebuild their working relationship.", "Reassign them to separate teams to maintain productivity."},
                new String[]{"{\"Structured\":2}", "{\"Social\":2}", "{\"Leadership\":2}"});

        insertSJT(dao, 8, "Creative Director", "The design team is struggling to find a vision for a prestige brand.", "How do you inspire them?",
                new String[]{"Research current artistic trends and market aesthetics.", "Encourage an open brainstorming session with no limits.", "Set clear milestones and a strict conceptual framework."},
                new String[]{"{\"Analytical\":2}", "{\"Creative\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 9, "Customer Success Lead", "A high-value client threatens to leave due to a technical delay.", "How do you retain them?",
                new String[]{"Prepare a detailed technical report on the resolution steps.", "Invite the client for a personal meeting to empathize.", "Offer a customized, high-tier solution as a gesture."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Creative\":2}"});

        insertSJT(dao, 10, "R&D Scientist", "An experiment yields results that contradict your initial hypothesis.", "What is your next move?",
                new String[]{"Critically examine the methodology for any bias.", "Present the data to peers for collaborative critique.", "Pivot the research toward this new, unexpected discovery."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Creative\":2}"});

        // Adding 10 more to reach 20
        insertSJT(dao, 11, "Project Coordinator", "Resources are suddenly cut mid-project due to budget shifts.", "How do you adapt?",
                new String[]{"Audit all tasks to prioritize high-impact deliverables.", "Meet with the team to manage workload expectations.", "Restructure the project timeline around remaining assets."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 12, "PR Specialist", "A minor internal document is leaked and misinterpreted online.", "How do you respond?",
                new String[]{"Fact-check the leak and prepare a detailed FAQ.", "Draft an empathetic response addressing public concerns.", "Take charge of the narrative with a live press briefing."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Leadership\":2}"});

        insertSJT(dao, 13, "Data Architect", "System performance drops significantly during a high-traffic event.", "What is your focus?",
                new String[]{"Execute stress tests to identify the bottleneck.", "Coordinate with dev-ops for a temporary server scaling.", "Enforce strict query optimization rules immediately."},
                new String[]{"{\"Analytical\":2}", "{\"Leadership\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 14, "Startup Founder", "An investor pulls out at the last minute before a funding round.", "What do you do?",
                new String[]{"Analyze the pitch deck to see where confidence was lost.", "Reach out to your network for alternative introductions.", "Pivot the business model to be more capital-efficient."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Creative\":2}"});

        insertSJT(dao, 15, "Logistics Manager", "A natural disaster shuts down your primary distribution hub.", "How to react?",
                new String[]{"Map out all available secondary routes and costs.", "Work with partners to utilize their warehouse space.", "Standardize emergency rerouting procedures for the team."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 16, "UX Lead", "User data shows that a key feature is being ignored by users.", "What is the fix?",
                new String[]{"Conduct a deep dive into user interaction heatmaps.", "Interview users to understand their friction points.", "Propose a bold, creative redesign of the interface."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Creative\":2}"});

        insertSJT(dao, 17, "Chief of Staff", "A department head is consistently missing deadlines.", "How to handle?",
                new String[]{"Audit their department's workflow for inefficiencies.", "Mentor them privately on time management and stress.", "Implement strict weekly reporting and accountability."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 18, "Editorial Head", "A published article contains a small but sensitive factual error.", "Action?",
                new String[]{"Trace the error back to the original source.", "Contact the subjects of the article to apologize.", "Update the style guide to prevent future oversights."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 19, "Compliance Officer", "New regulations are passed that make your current process illegal.", "What now?",
                new String[]{"Read the full legislation to find every requirement.", "Organize a workshop to train the company on changes.", "Design a completely new, fully compliant workflow."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Structured\":2}"});

        insertSJT(dao, 20, "Sales Manager", "The team is demoralized after losing a major contract.", "How to lead?",
                new String[]{"Review the lost bid for specific technical failures.", "Host a team building session to rebuild confidence.", "Set new, aggressive targets to regain momentum."},
                new String[]{"{\"Analytical\":2}", "{\"Social\":2}", "{\"Leadership\":2}"});
    }

    private static void insertSJT(QuestionDao dao, int id, String role, String context, String dilemma, String[] optTexts, String[] weights) {
        Question q = new Question(role, context, dilemma, "SJT");
        dao.insertQuestion(q);
        List<Option> options = new ArrayList<>();
        // Use auto-generated IDs in real scenario, here we assume incremental IDs for simplicity in demonstration
        for (int i = 0; i < optTexts.length; i++) {
            options.add(new Option(id, optTexts[i], weights[i]));
        }
        dao.insertOptions(options);
    }

    private static void insertPreferenceQuestions(QuestionDao dao) {
        // Updated to use 'role' field as the target trait for programmatic handling in Preference mode
        String[][] prefData = {
            {"Analytical", "I enjoy breaking down complex problems into smaller parts."},
            {"Social", "I find it rewarding to help others reach their potential."},
            {"Creative", "I often think of unconventional solutions to problems."},
            {"Leadership", "I enjoy taking charge and guiding a team toward a goal."},
            {"Structured", "I prefer having a clear set of rules and guidelines to follow."},
            {"Analytical", "I am comfortable working with large amounts of data and logic."},
            {"Social", "Connecting with people is my favorite part of any project."},
            {"Creative", "I am constantly looking for ways to innovate and improve."},
            {"Leadership", "I am willing to take risks to achieve a significant reward."},
            {"Structured", "I work best when my tasks are well-defined and scheduled."},
            {"Analytical", "I prioritize logic over intuition when making decisions."},
            {"Social", "I am good at resolving conflicts and building harmony."},
            {"Creative", "I would rather create something new than follow a guide."},
            {"Leadership", "I feel confident representing a team's interests to others."},
            {"Structured", "I enjoy organizing information and files systematically."},
            {"Analytical", "I like analyzing patterns and trends to predict outcomes."},
            {"Social", "I am a good listener and empathetic to others' feelings."},
            {"Creative", "I find beauty in abstract ideas and conceptual thinking."},
            {"Leadership", "I am motivated by achieving collective milestones."},
            {"Structured", "I value precision and accuracy above all else in my work."}
        };

        for (int i = 0; i < prefData.length; i++) {
            // Using 'role' to store the trait for Preference mode
            Question q = new Question(prefData[i][0], "Personal Interest", prefData[i][1], "Preference");
            dao.insertQuestion(q);
        }
    }

    private static void insertBenchmarks(CareerBenchmarkDao dao) {
        List<CareerBenchmark> benchmarks = new ArrayList<>();
        benchmarks.add(new CareerBenchmark("Surgeon", "Precision-driven medical professional specialized in invasive procedures.", 95, 70, 30, 85, 90));
        benchmarks.add(new CareerBenchmark("Data Scientist", "Analytical expert who extracts insights from complex datasets.", 98, 40, 60, 45, 85));
        benchmarks.add(new CareerBenchmark("Software Engineer", "Systems architect focused on building scalable digital solutions.", 92, 50, 80, 40, 75));
        benchmarks.add(new CareerBenchmark("UX Designer", "Creative problem solver focused on human-centered digital experiences.", 45, 65, 98, 50, 60));
        benchmarks.add(new CareerBenchmark("Psychologist", "Empathetic scientist studying human behavior and mental health.", 50, 98, 55, 60, 45));
        benchmarks.add(new CareerBenchmark("Entrepreneur", "Innovative risk-taker building new business ventures from scratch.", 70, 75, 90, 98, 35));
        benchmarks.add(new CareerBenchmark("Financial Analyst", "Data-driven professional providing strategic investment guidance.", 94, 40, 25, 50, 95));
        benchmarks.add(new CareerBenchmark("Creative Director", "Artistic visionary leading the creative strategy for brands.", 40, 80, 99, 85, 45));
        benchmarks.add(new CareerBenchmark("Project Manager", "Structured leader ensuring projects are delivered on time and budget.", 65, 85, 45, 90, 95));
        benchmarks.add(new CareerBenchmark("Research Scientist", "Methodical analyst pushing the boundaries of scientific knowledge.", 99, 35, 80, 25, 88));
        benchmarks.add(new CareerBenchmark("Human Resources", "People-focused professional managing talent and company culture.", 35, 95, 55, 70, 82));
        benchmarks.add(new CareerBenchmark("Nurse Practitioner", "Advanced clinical provider combining empathy with medical expertise.", 65, 95, 45, 75, 88));

        dao.insertBenchmarks(benchmarks);
    }
}