package com.attendancetracker.service;

import com.attendancetracker.dao.DepartmentDAO;
import com.attendancetracker.model.Department;

import java.util.List;

public class DepartmentService {
    private final DepartmentDAO departmentDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
    }

    public boolean createDepartment(Department department) {
        try {
            // Check if department name already exists
            if (departmentDAO.findByName(department.getName()) != null) {
                return false;
            }

            return departmentDAO.create(department) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDepartment(Department department) {
        try {
            Department existingDepartment = departmentDAO.findByName(department.getName());
            if (existingDepartment != null && existingDepartment.getId() != department.getId()) {
                return false; // Department name already exists
            }

            return departmentDAO.update(department) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDepartment(int departmentId) {
        try {
            return departmentDAO.delete(departmentId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Department getDepartmentById(int departmentId) {
        return departmentDAO.findById(departmentId);
    }

    public Department getDepartmentByName(String name) {
        return departmentDAO.findByName(name);
    }

    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }
} 