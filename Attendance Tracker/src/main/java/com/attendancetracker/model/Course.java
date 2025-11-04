package com.attendancetracker.model;

public class Course {
    private int id;
    private String name;
    private Department department;
    private User teacher;

    public Course() {}

    public Course(String name, Department department, User teacher) {
        this.name = name;
        this.department = department;
        this.teacher = teacher;
    }

    public Course(int id, String name, Department department, User teacher) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.teacher = teacher;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department=" + department +
                ", teacher=" + teacher +
                '}';
    }
} 