package com.attendancetracker.service;

import com.attendancetracker.dao.AttendanceDAO;
import com.attendancetracker.model.Attendance;
import com.attendancetracker.model.Course;
import com.attendancetracker.model.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttendanceService {
    private final AttendanceDAO attendanceDAO;
    private final StudentService studentService;
    private final CourseService courseService;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
        this.studentService = new StudentService();
        this.courseService = new CourseService();
    }

    public boolean markAttendance(Attendance attendance) {
        try {
            // Validate student and course
            Student student = studentService.getStudentById(attendance.getStudent().getId());
            Course course = courseService.getCourseById(attendance.getCourse().getId());

            if (student == null || course == null) {
                return false;
            }

            return attendanceDAO.create(attendance) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAttendance(Attendance attendance) {
        try {
            return attendanceDAO.update(attendance) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAttendance(int attendanceId) {
        try {
            return attendanceDAO.delete(attendanceId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Attendance> getAttendanceByStudent(int studentId) {
        return attendanceDAO.findByStudent(studentId);
    }

    public List<Attendance> getAttendanceByCourse(int courseId) {
        return attendanceDAO.findByCourse(courseId);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceDAO.findByDate(date);
    }

    public List<Attendance> getAttendanceByStudentAndCourse(int studentId, int courseId) {
        return attendanceDAO.findByStudentAndCourse(studentId, courseId);
    }

    public Map<LocalDate, List<Attendance>> getAttendanceByStudentGroupedByDate(int studentId) {
        return getAttendanceByStudent(studentId).stream()
                .collect(Collectors.groupingBy(Attendance::getDate));
    }

    public Map<Course, List<Attendance>> getAttendanceByStudentGroupedByCourse(int studentId) {
        return getAttendanceByStudent(studentId).stream()
                .collect(Collectors.groupingBy(Attendance::getCourse));
    }

    public Map<Student, List<Attendance>> getAttendanceByCourseGroupedByStudent(int courseId) {
        return getAttendanceByCourse(courseId).stream()
                .collect(Collectors.groupingBy(Attendance::getStudent));
    }

    public double calculateAttendancePercentage(int studentId, int courseId) {
        List<Attendance> attendances = getAttendanceByStudentAndCourse(studentId, courseId);
        if (attendances.isEmpty()) {
            return 0.0;
        }

        long presentCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT)
                .count();

        return (double) presentCount / attendances.size() * 100;
    }
} 