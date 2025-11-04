package com.attendancetracker.service;

import com.attendancetracker.dao.CourseDAO;
import com.attendancetracker.model.Course;
import com.attendancetracker.model.Department;
import com.attendancetracker.model.User;

import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO;
    private final DepartmentService departmentService;

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.departmentService = new DepartmentService();
    }

    public boolean createCourse(Course course) {
        try {
            // Validate department
            Department department = departmentService.getDepartmentById(course.getDepartment().getId());
            if (department == null) {
                return false;
            }

            return courseDAO.create(course) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCourse(Course course) {
        try {
            // Validate department
            Department department = departmentService.getDepartmentById(course.getDepartment().getId());
            if (department == null) {
                return false;
            }

            return courseDAO.update(course) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCourse(int courseId) {
        try {
            return courseDAO.delete(courseId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Course getCourseById(int courseId) {
        return courseDAO.findById(courseId);
    }

    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    public List<Course> getCoursesByDepartment(int departmentId) {
        return courseDAO.findByDepartment(departmentId);
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        return courseDAO.findByTeacher(teacherId);
    }
} 