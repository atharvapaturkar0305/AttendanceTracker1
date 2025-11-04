package com.attendancetracker.dao;

import com.attendancetracker.model.Course;
import com.attendancetracker.model.Department;
import com.attendancetracker.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CourseDAO extends BaseDAO<Course> {
    private static final String INSERT_COURSE = "INSERT INTO courses (name, department_id, teacher_id) VALUES (?, ?, ?)";
    private static final String UPDATE_COURSE = "UPDATE courses SET name = ?, department_id = ?, teacher_id = ? WHERE id = ?";
    private static final String DELETE_COURSE = "DELETE FROM courses WHERE id = ?";
    private static final String SELECT_COURSE_BY_ID = "SELECT c.*, d.id as dept_id, d.name as dept_name, " +
            "u.id as teacher_id, u.username as teacher_username, u.email as teacher_email " +
            "FROM courses c " +
            "LEFT JOIN departments d ON c.department_id = d.id " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE c.id = ?";
    private static final String SELECT_ALL_COURSES = "SELECT c.*, d.id as dept_id, d.name as dept_name, " +
            "u.id as teacher_id, u.username as teacher_username, u.email as teacher_email " +
            "FROM courses c " +
            "LEFT JOIN departments d ON c.department_id = d.id " +
            "LEFT JOIN users u ON c.teacher_id = u.id";
    private static final String SELECT_COURSES_BY_DEPARTMENT = "SELECT c.*, d.id as dept_id, d.name as dept_name, " +
            "u.id as teacher_id, u.username as teacher_username, u.email as teacher_email " +
            "FROM courses c " +
            "LEFT JOIN departments d ON c.department_id = d.id " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE c.department_id = ?";
    private static final String SELECT_COURSES_BY_TEACHER = "SELECT c.*, d.id as dept_id, d.name as dept_name, " +
            "u.id as teacher_id, u.username as teacher_username, u.email as teacher_email " +
            "FROM courses c " +
            "LEFT JOIN departments d ON c.department_id = d.id " +
            "LEFT JOIN users u ON c.teacher_id = u.id " +
            "WHERE c.teacher_id = ?";

    public int create(Course course) {
        return executeUpdate(INSERT_COURSE, new Object[]{
            course.getName(),
            course.getDepartment().getId(),
            course.getTeacher().getId()
        });
    }

    public int update(Course course) {
        return executeUpdate(UPDATE_COURSE, new Object[]{
            course.getName(),
            course.getDepartment().getId(),
            course.getTeacher().getId(),
            course.getId()
        });
    }

    public int delete(int courseId) {
        return executeUpdate(DELETE_COURSE, new Object[]{courseId});
    }

    public Course findById(int courseId) {
        return executeQueryForSingleResult(SELECT_COURSE_BY_ID, new Object[]{courseId}, this::mapResultSet);
    }

    public List<Course> findAll() {
        return executeQuery(SELECT_ALL_COURSES, null, this::mapResultSet);
    }

    public List<Course> findByDepartment(int departmentId) {
        return executeQuery(SELECT_COURSES_BY_DEPARTMENT, new Object[]{departmentId}, this::mapResultSet);
    }

    public List<Course> findByTeacher(int teacherId) {
        return executeQuery(SELECT_COURSES_BY_TEACHER, new Object[]{teacherId}, this::mapResultSet);
    }

    private Course mapResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setName(rs.getString("name"));

        Department department = new Department();
        department.setId(rs.getInt("dept_id"));
        department.setName(rs.getString("dept_name"));
        course.setDepartment(department);

        User teacher = new User();
        teacher.setId(rs.getInt("teacher_id"));
        teacher.setUsername(rs.getString("teacher_username"));
        teacher.setEmail(rs.getString("teacher_email"));
        course.setTeacher(teacher);

        return course;
    }
} 