package com.attendancetracker.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/attendance_tracker";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // Create tables if they don't exist
            String[] createTables = {
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL)",

                "CREATE TABLE IF NOT EXISTS departments (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL UNIQUE)",

                "CREATE TABLE IF NOT EXISTS courses (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "department_id INT, " +
                "teacher_id INT, " +
                "FOREIGN KEY (department_id) REFERENCES departments(id), " +
                "FOREIGN KEY (teacher_id) REFERENCES users(id))",

                "CREATE TABLE IF NOT EXISTS students (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "user_id INT, " +
                "department_id INT, " +
                "roll_number VARCHAR(20) UNIQUE NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (department_id) REFERENCES departments(id))",

                "CREATE TABLE IF NOT EXISTS attendance (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "student_id INT, " +
                "course_id INT, " +
                "date DATE NOT NULL, " +
                "status ENUM('PRESENT', 'ABSENT') NOT NULL, " +
                "FOREIGN KEY (student_id) REFERENCES students(id), " +
                "FOREIGN KEY (course_id) REFERENCES courses(id))"
            };

            for (String sql : createTables) {
                conn.createStatement().execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 