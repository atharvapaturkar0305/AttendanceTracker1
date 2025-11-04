package com.attendancetracker.model;

public class Student {
    private int id;
    private User user;
    private Department department;
    private String rollNumber;

    public Student() {}

    public Student(User user, Department department, String rollNumber) {
        this.user = user;
        this.department = department;
        this.rollNumber = rollNumber;
    }

    public Student(int id, User user, Department department, String rollNumber) {
        this.id = id;
        this.user = user;
        this.department = department;
        this.rollNumber = rollNumber;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", user=" + user +
                ", department=" + department +
                ", rollNumber='" + rollNumber + '\'' +
                '}';
    }
} 