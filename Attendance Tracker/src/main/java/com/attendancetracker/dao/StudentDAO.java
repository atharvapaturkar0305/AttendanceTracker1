package com.attendancetracker.dao;

import com.attendancetracker.model.Department;
import com.attendancetracker.model.Student;
import com.attendancetracker.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StudentDAO extends BaseDAO<Student> {
    private static final String INSERT_STUDENT = "INSERT INTO students (user_id, department_id, roll_number) VALUES (?, ?, ?)";
    private static final String UPDATE_STUDENT = "UPDATE students SET department_id = ?, roll_number = ? WHERE id = ?";
    private static final String DELETE_STUDENT = "DELETE FROM students WHERE id = ?";
    private static final String SELECT_STUDENT_BY_ID = "SELECT s.*, u.id as user_id, u.username, u.email, " +
            "d.id as dept_id, d.name as dept_name " +
            "FROM students s " +
            "LEFT JOIN users u ON s.user_id = u.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "WHERE s.id = ?";
    private static final String SELECT_STUDENT_BY_USER_ID = "SELECT s.*, u.id as user_id, u.username, u.email, " +
            "d.id as dept_id, d.name as dept_name " +
            "FROM students s " +
            "LEFT JOIN users u ON s.user_id = u.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "WHERE s.user_id = ?";
    private static final String SELECT_STUDENT_BY_ROLL_NUMBER = "SELECT s.*, u.id as user_id, u.username, u.email, " +
            "d.id as dept_id, d.name as dept_name " +
            "FROM students s " +
            "LEFT JOIN users u ON s.user_id = u.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "WHERE s.roll_number = ?";
    private static final String SELECT_STUDENTS_BY_DEPARTMENT = "SELECT s.*, u.id as user_id, u.username, u.email, " +
            "d.id as dept_id, d.name as dept_name " +
            "FROM students s " +
            "LEFT JOIN users u ON s.user_id = u.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "WHERE s.department_id = ?";
    private static final String SELECT_ALL_STUDENTS = "SELECT s.*, u.id as user_id, u.username, u.email, " +
            "d.id as dept_id, d.name as dept_name " +
            "FROM students s " +
            "LEFT JOIN users u ON s.user_id = u.id " +
            "LEFT JOIN departments d ON s.department_id = d.id";

    public int create(Student student) {
        return executeUpdate(INSERT_STUDENT, new Object[]{
            student.getUser().getId(),
            student.getDepartment().getId(),
            student.getRollNumber()
        });
    }

    public int update(Student student) {
        return executeUpdate(UPDATE_STUDENT, new Object[]{
            student.getDepartment().getId(),
            student.getRollNumber(),
            student.getId()
        });
    }

    public int delete(int studentId) {
        return executeUpdate(DELETE_STUDENT, new Object[]{studentId});
    }

    public Student findById(int studentId) {
        return executeQueryForSingleResult(SELECT_STUDENT_BY_ID, new Object[]{studentId}, this::mapResultSet);
    }

    public Student findByUserId(int userId) {
        return executeQueryForSingleResult(SELECT_STUDENT_BY_USER_ID, new Object[]{userId}, this::mapResultSet);
    }

    public Student findByRollNumber(String rollNumber) {
        return executeQueryForSingleResult(SELECT_STUDENT_BY_ROLL_NUMBER, new Object[]{rollNumber}, this::mapResultSet);
    }

    public List<Student> findByDepartment(int departmentId) {
        return executeQuery(SELECT_STUDENTS_BY_DEPARTMENT, new Object[]{departmentId}, this::mapResultSet);
    }

    public List<Student> findAll() {
        return executeQuery(SELECT_ALL_STUDENTS, null, this::mapResultSet);
    }

    private Student mapResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setRollNumber(rs.getString("roll_number"));

        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        student.setUser(user);

        Department department = new Department();
        department.setId(rs.getInt("dept_id"));
        department.setName(rs.getString("dept_name"));
        student.setDepartment(department);

        return student;
    }
} 