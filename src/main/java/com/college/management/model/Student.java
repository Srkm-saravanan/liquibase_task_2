package com.college.management.model;

public class Student {
    private int studentId;
    private String rollNumber; // New Field
    private String firstName;
    private String lastName;
    private String email;
    private int classId;
    private Class studentClass;

    public Student() {}

    // Constructor without ID (for insertion)
    public Student(String rollNumber, String firstName, String lastName, String email, int classId) {
        this.rollNumber = rollNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.classId = classId;
    }

    // Constructor with ID (for retrieval)
    public Student(int studentId, String rollNumber, String firstName, String lastName, String email, int classId) {
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.classId = classId;
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    
    public Class getStudentClass() { return studentClass; }
    public void setStudentClass(Class studentClass) { this.studentClass = studentClass; }
}