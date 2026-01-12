package com.example.aibasedcareercounsellingapp.utils;

public class Constants {
    
    // ==================== SKILLS (100 Items) ====================
    // 50 Soft Skills + 50 Hard Skills
    public static final String[] SKILLS = {
            // Soft Skills (50)
            "Communication", "Leadership", "Teamwork", "Problem Solving", "Critical Thinking",
            "Creativity", "Adaptability", "Time Management", "Conflict Resolution", "Negotiation",
            "Emotional Intelligence", "Presentation Skills", "Decision Making", "Collaboration", "Networking",
            "Research Skills", "Observation", "Patience", "Self-motivation", "Stress Management",
            "Active Listening", "Mentoring", "Coaching", "Public Speaking", "Planning",
            "Goal Setting", "Initiative", "Empathy", "Analytical Thinking", "Logical Reasoning",
            "Organization", "Delegation", "Strategic Thinking", "Confidence", "Persuasion",
            "Interpersonal Skills", "Motivation", "Problem Analysis", "Learning Agility", "Storytelling",
            "Team Leadership", "Conflict Management", "Innovation", "Brainstorming", "Collaboration Online",
            "Adaptation to Change", "Prioritization", "Decision Analysis", "Ethical Reasoning", "Accountability",
            
            // Hard Skills (50)
            "Python", "Java", "C++", "SQL", "Machine Learning",
            "Web Development", "Mobile App Development", "Graphic Design", "Data Analysis", "Statistical Analysis",
            "Cloud Computing", "Cybersecurity", "Network Management", "Hardware Knowledge", "Financial Analysis",
            "Accounting", "Legal Knowledge", "Drafting", "Medical Knowledge", "Nursing Skills",
            "Pharmacology", "Research Methodology", "Lab Techniques", "Project Management", "Agile/Scrum",
            "CAD/Design Software", "Cooking", "Baking", "Physical Fitness", "Martial Arts",
            "Strategy Planning", "SEO/Digital Marketing", "Social Media Management", "Content Writing", "Editing",
            "Video Editing", "Animation", "Photography", "Translation", "Foreign Languages",
            "Customer Service", "Sales Techniques", "Inventory Management", "Logistics/Supply Chain", "Entrepreneurship",
            "Market Research", "Teaching", "Online Course Creation", "Public Policy Knowledge", "Event Planning"
    };
    
    // ==================== CAREERS (100 Items) ====================
    public static final String[] CAREERS = {
            "Software Engineer", "Data Scientist", "AI Engineer", "ML Engineer", "Web Developer",
            "Mobile App Developer", "UI/UX Designer", "Graphic Designer", "Product Manager", "Project Manager",
            "Digital Marketing Specialist", "Content Writer", "Journalist", "Editor/Media Producer", "Teacher",
            "Research Scientist", "Doctor", "Nurse", "Pharmacist", "Surgeon",
            "Lawyer", "Legal Advisor", "Civil Engineer", "Mechanical Engineer", "Electrical Engineer",
            "Architect", "Business Analyst", "Entrepreneur/Startup Founder", "Accountant", "Finance Analyst",
            "Investment Banker", "Pilot", "Aviation Engineer", "Military Officer", "Police Officer",
            "Security Analyst", "Chef", "Baker", "Nutritionist", "Dietitian",
            "Interior Designer", "Fashion Designer", "Photographer", "Videographer", "Animator",
            "Translator", "Event Manager", "Public Speaker/Coach", "Online Educator", "UX Researcher",
            "Statistician", "Data Engineer", "Cloud Engineer", "DevOps Engineer", "Cybersecurity Analyst",
            "Network Engineer", "Supply Chain Manager", "Logistics Officer", "Research Analyst", "Biologist",
            "Chemist", "Physicist", "Mathematician", "Economist", "Marketing Manager",
            "Sales Manager", "Human Resource Manager", "Trainer/Mentor", "Leadership Coach", "NGO Worker",
            "Social Worker", "Politician/Public Servant", "Policy Analyst", "Event Coordinator", "Travel Consultant",
            "Tour Guide", "Adventure Instructor", "Sports Coach", "Fitness Trainer", "Yoga Instructor",
            "Meditation Coach", "E-commerce Manager", "App Product Owner", "Game Designer", "Game Developer",
            "Robotics Engineer", "Hardware Engineer", "AI Researcher", "Biomedical Engineer", "Clinical Researcher",
            "Pharmacologist", "Diet Coach", "Fashion Stylist", "Makeup Artist", "Musician",
            "Composer", "Sound Engineer", "Voice-over Artist", "Podcast Creator", "Film Director"
    };
    
    // ==================== API KEYS ====================
    // TODO: Replace with your actual Gemini API key
    public static final String GEMINI_API_KEY = "AIzaSyDOAtlkXf2iYiP9eFwnw_gbBVI6s8h-2Io";
    public static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/";
//    public static final String MODEL_NAME = "gemini-1.5-flash";
    // ==================== HELPER METHODS ====================
    
    /**
     * Get skill name by index
     * @param index Index (0-99)
     * @return Skill name or "Unknown Skill" if invalid
     */
    public static String getSkillName(int index) {
        if (index >= 0 && index < SKILLS.length) {
            return SKILLS[index];
        }
        return "Unknown Skill";
    }
    
    /**
     * Get career name by index
     * @param index Index (0-99)
     * @return Career name or "Unknown Career" if invalid
     */
    public static String getCareerName(int index) {
        if (index >= 0 && index < CAREERS.length) {
            return CAREERS[index];
        }
        return "Unknown Career";
    }
    
    /**
     * Get skill index by name (case-insensitive)
     * @param skillName Skill name
     * @return Index (0-99) or -1 if not found
     */
    public static int getSkillIndex(String skillName) {
        for (int i = 0; i < SKILLS.length; i++) {
            if (SKILLS[i].equalsIgnoreCase(skillName)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Get career index by name (case-insensitive)
     * @param careerName Career name
     * @return Index (0-99) or -1 if not found
     */
    public static int getCareerIndex(String careerName) {
        for (int i = 0; i < CAREERS.length; i++) {
            if (CAREERS[i].equalsIgnoreCase(careerName)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Get total number of skills
     */
    public static int getTotalSkills() {
        return SKILLS.length;
    }
    
    /**
     * Get total number of careers
     */
    public static int getTotalCareers() {
        return CAREERS.length;
    }
}
