package com.attendancetracker.service;

import com.attendancetracker.dao.StudentDAO;
import com.attendancetracker.model.Department;
import com.attendancetracker.model.Student;
import com.attendancetracker.model.User;

import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;
    private final DepartmentService departmentService;
    private final AuthenticationService authService;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.departmentService = new DepartmentService();
        this.authService = new AuthenticationService();
    }

    public boolean createStudent(Student student) {
        try {
            // Validate department
            Department department = departmentService.getDepartmentById(student.getDepartment().getId());
            if (department == null) {
                return false;
            }

            // Create user account first
            if (!authService.register(student.getUser())) {
                return false;
            }

            // Get the created user
            User createdUser = authService.login(
                student.getUser().getUsername(),
                student.getUser().getPassword(),
                User.UserRole.STUDENT
            );

            if (createdUser == null) {
                return false;
            }

            // Set the created user to student
            student.setUser(createdUser);

            return studentDAO.create(student) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        try {
            // Validate department
            Department department = departmentService.getDepartmentById(student.getDepartment().getId());
            if (department == null) {
                return false;
            }

            return studentDAO.update(student) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        try {
            Student student = studentDAO.findById(studentId);
            if (student == null) {
                return false;
            }

            // Delete student record
            if (studentDAO.delete(studentId) <= 0) {
                return false;
            }

            // Delete user account
            return authService.deleteUser(student.getUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Student getStudentById(int studentId) {
        return studentDAO.findById(studentId);
    }

    public Student getStudentByUserId(int userId) {
        return studentDAO.findByUserId(userId);
    }

    public Student getStudentByRollNumber(String rollNumber) {
        return studentDAO.findByRollNumber(rollNumber);
    }

    public List<Student> getStudentsByDepartment(int departmentId) {
        return studentDAO.findByDepartment(departmentId);
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }
} 