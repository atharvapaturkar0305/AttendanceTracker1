package com.attendancetracker.dao;

import com.attendancetracker.model.Department;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DepartmentDAO extends BaseDAO<Department> {
    private static final String INSERT_DEPARTMENT = "INSERT INTO departments (name) VALUES (?)";
    private static final String UPDATE_DEPARTMENT = "UPDATE departments SET name = ? WHERE id = ?";
    private static final String DELETE_DEPARTMENT = "DELETE FROM departments WHERE id = ?";
    private static final String SELECT_DEPARTMENT_BY_ID = "SELECT * FROM departments WHERE id = ?";
    private static final String SELECT_DEPARTMENT_BY_NAME = "SELECT * FROM departments WHERE name = ?";
    private static final String SELECT_ALL_DEPARTMENTS = "SELECT * FROM departments";

    public int create(Department department) {
        return executeUpdate(INSERT_DEPARTMENT, new Object[]{department.getName()});
    }

    public int update(Department department) {
        return executeUpdate(UPDATE_DEPARTMENT, new Object[]{department.getName(), department.getId()});
    }

    public int delete(int departmentId) {
        return executeUpdate(DELETE_DEPARTMENT, new Object[]{departmentId});
    }

    public Department findById(int departmentId) {
        return executeQueryForSingleResult(SELECT_DEPARTMENT_BY_ID, new Object[]{departmentId}, this::mapResultSet);
    }

    public Department findByName(String name) {
        return executeQueryForSingleResult(SELECT_DEPARTMENT_BY_NAME, new Object[]{name}, this::mapResultSet);
    }

    public List<Department> findAll() {
        return executeQuery(SELECT_ALL_DEPARTMENTS, null, this::mapResultSet);
    }

    private Department mapResultSet(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("id"));
        department.setName(rs.getString("name"));
        return department;
    }
} 