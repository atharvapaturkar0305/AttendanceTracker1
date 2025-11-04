package com.attendancetracker.model;

import java.time.LocalDate;

public class Attendance {
    private int id;
    private Student student;
    private Course course;
    private LocalDate date;
    private AttendanceStatus status;

    public enum AttendanceStatus {
        PRESENT, ABSENT
    }

    public Attendance() {}

    public Attendance(Student student, Course course, LocalDate date, AttendanceStatus status) {
        this.student = student;
        this.course = course;
        this.date = date;
        this.status = status;
    }

    public Attendance(int id, Student student, Course course, LocalDate date, AttendanceStatus status) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", student=" + student +
                ", course=" + course +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
} 