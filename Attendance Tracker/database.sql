-- Create database
CREATE DATABASE IF NOT EXISTS attendance_tracker;
USE attendance_tracker;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Departments table
CREATE TABLE IF NOT EXISTS departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE NOT NULL,
    department_id INT,
    teacher_id INT,
    credits INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

-- Students table
CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    department_id INT,
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    admission_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Student_Courses table (for course enrollment)
CREATE TABLE IF NOT EXISTS student_courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    course_id INT,
    semester VARCHAR(20) NOT NULL,
    enrollment_date DATE NOT NULL,
    status ENUM('ENROLLED', 'COMPLETED', 'DROPPED') DEFAULT 'ENROLLED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE KEY unique_enrollment (student_id, course_id, semester)
);

-- Attendance table
CREATE TABLE IF NOT EXISTS attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    course_id INT,
    date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LATE') NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE KEY unique_attendance (student_id, course_id, date)
);

-- Insert sample data

-- Insert departments
INSERT INTO departments (name, code, description) VALUES
('Computer Science', 'CS', 'Department of Computer Science'),
('Electrical Engineering', 'EE', 'Department of Electrical Engineering'),
('Mechanical Engineering', 'ME', 'Department of Mechanical Engineering'),
('Business Administration', 'BA', 'Department of Business Administration');

-- Insert admin user
INSERT INTO users (username, password, email, role) VALUES
('admin', 'admin123', 'admin@example.com', 'ADMIN');

-- Insert sample teachers
INSERT INTO users (username, password, email, role) VALUES
('teacher1', 'teacher123', 'teacher1@example.com', 'TEACHER'),
('teacher2', 'teacher123', 'teacher2@example.com', 'TEACHER');

-- Insert sample courses
INSERT INTO courses (name, code, department_id, teacher_id, credits, description) VALUES
('Introduction to Programming', 'CS101', 1, 2, 3, 'Basic programming concepts and practices'),
('Database Systems', 'CS201', 1, 2, 3, 'Database design and management'),
('Circuit Analysis', 'EE101', 2, 3, 4, 'Basic electrical circuit analysis'),
('Thermodynamics', 'ME101', 3, 3, 4, 'Principles of thermodynamics');

-- Insert sample students
INSERT INTO users (username, password, email, role) VALUES
('student1', 'student123', 'student1@example.com', 'STUDENT'),
('student2', 'student123', 'student2@example.com', 'STUDENT');

INSERT INTO students (user_id, department_id, roll_number, admission_date) VALUES
(4, 1, 'CS2023001', '2023-01-01'),
(5, 1, 'CS2023002', '2023-01-01');

-- Enroll students in courses
INSERT INTO student_courses (student_id, course_id, semester, enrollment_date) VALUES
(1, 1, 'Spring 2023', '2023-01-15'),
(1, 2, 'Spring 2023', '2023-01-15'),
(2, 1, 'Spring 2023', '2023-01-15'),
(2, 2, 'Spring 2023', '2023-01-15');

-- Insert sample attendance records
INSERT INTO attendance (student_id, course_id, date, status, remarks) VALUES
(1, 1, '2023-02-01', 'PRESENT', 'On time'),
(1, 1, '2023-02-02', 'ABSENT', 'Sick leave'),
(2, 1, '2023-02-01', 'PRESENT', 'On time'),
(2, 1, '2023-02-02', 'LATE', 'Late by 10 minutes'); 