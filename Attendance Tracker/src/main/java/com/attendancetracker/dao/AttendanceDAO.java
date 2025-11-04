package com.attendancetracker.dao;

import com.attendancetracker.model.Attendance;
import com.attendancetracker.model.Course;
import com.attendancetracker.model.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AttendanceDAO extends BaseDAO<Attendance> {
    private static final String INSERT_ATTENDANCE = "INSERT INTO attendance (student_id, course_id, date, status) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_ATTENDANCE = "UPDATE attendance SET status = ? WHERE id = ?";
    private static final String DELETE_ATTENDANCE = "DELETE FROM attendance WHERE id = ?";
    private static final String SELECT_ATTENDANCE_BY_ID = "SELECT a.*, s.id as student_id, s.roll_number, " +
            "c.id as course_id, c.name as course_name " +
            "FROM attendance a " +
            "LEFT JOIN students s ON a.student_id = s.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.id = ?";
    private static final String SELECT_ATTENDANCE_BY_STUDENT_AND_COURSE = "SELECT a.*, s.id as student_id, s.roll_number, " +
            "c.id as course_id, c.name as course_name " +
            "FROM attendance a " +
            "LEFT JOIN students s ON a.student_id = s.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.student_id = ? AND a.course_id = ?";
    private static final String SELECT_ATTENDANCE_BY_STUDENT = "SELECT a.*, s.id as student_id, s.roll_number, " +
            "c.id as course_id, c.name as course_name " +
            "FROM attendance a " +
            "LEFT JOIN students s ON a.student_id = s.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.student_id = ?";
    private static final String SELECT_ATTENDANCE_BY_COURSE = "SELECT a.*, s.id as student_id, s.roll_number, " +
            "c.id as course_id, c.name as course_name " +
            "FROM attendance a " +
            "LEFT JOIN students s ON a.student_id = s.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.course_id = ?";
    private static final String SELECT_ATTENDANCE_BY_DATE = "SELECT a.*, s.id as student_id, s.roll_number, " +
            "c.id as course_id, c.name as course_name " +
            "FROM attendance a " +
            "LEFT JOIN students s ON a.student_id = s.id " +
            "LEFT JOIN courses c ON a.course_id = c.id " +
            "WHERE a.date = ?";

    public int create(Attendance attendance) {
        return executeUpdate(INSERT_ATTENDANCE, new Object[]{
            attendance.getStudent().getId(),
            attendance.getCourse().getId(),
            attendance.getDate(),
            attendance.getStatus().name()
        });
    }

    public int update(Attendance attendance) {
        return executeUpdate(UPDATE_ATTENDANCE, new Object[]{
            attendance.getStatus().name(),
            attendance.getId()
        });
    }

    public int delete(int attendanceId) {
        return executeUpdate(DELETE_ATTENDANCE, new Object[]{attendanceId});
    }

    public Attendance findById(int attendanceId) {
        return executeQueryForSingleResult(SELECT_ATTENDANCE_BY_ID, new Object[]{attendanceId}, this::mapResultSet);
    }

    public List<Attendance> findByStudentAndCourse(int studentId, int courseId) {
        return executeQuery(SELECT_ATTENDANCE_BY_STUDENT_AND_COURSE, new Object[]{studentId, courseId}, this::mapResultSet);
    }

    public List<Attendance> findByStudent(int studentId) {
        return executeQuery(SELECT_ATTENDANCE_BY_STUDENT, new Object[]{studentId}, this::mapResultSet);
    }

    public List<Attendance> findByCourse(int courseId) {
        return executeQuery(SELECT_ATTENDANCE_BY_COURSE, new Object[]{courseId}, this::mapResultSet);
    }

    public List<Attendance> findByDate(LocalDate date) {
        return executeQuery(SELECT_ATTENDANCE_BY_DATE, new Object[]{date}, this::mapResultSet);
    }

    private Attendance mapResultSet(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setId(rs.getInt("id"));
        attendance.setDate(rs.getDate("date").toLocalDate());
        attendance.setStatus(Attendance.AttendanceStatus.valueOf(rs.getString("status")));

        Student student = new Student();
        student.setId(rs.getInt("student_id"));
        student.setRollNumber(rs.getString("roll_number"));
        attendance.setStudent(student);

        Course course = new Course();
        course.setId(rs.getInt("course_id"));
        course.setName(rs.getString("course_name"));
        attendance.setCourse(course);

        return attendance;
    }
} 