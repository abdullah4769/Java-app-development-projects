package com.example.aibasedcareercounsellingapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for storing resume data for PDF generation
 */
public class ResumeData {
    
    // Personal Info
    private String fullName;
    private String email;
    private String phone;
    private String address;
    
    // Education
    private String degree;
    private String institution;
    private String graduationYear;
    
    // Experience
    private String company;
    private String role;
    private String experienceDescription;
    
    // Skills
    private List<String> skills;
    
    public ResumeData() {
        this.skills = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String degree) {
        this.degree = degree;
    }
    
    public String getInstitution() {
        return institution;
    }
    
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public String getGraduationYear() {
        return graduationYear;
    }
    
    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getExperienceDescription() {
        return experienceDescription;
    }
    
    public void setExperienceDescription(String experienceDescription) {
        this.experienceDescription = experienceDescription;
    }
    
    public List<String> getSkills() {
        return skills;
    }
    
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
