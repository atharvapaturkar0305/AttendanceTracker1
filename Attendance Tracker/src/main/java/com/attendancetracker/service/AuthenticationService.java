package com.attendancetracker.service;

import com.attendancetracker.dao.UserDAO;
import com.attendancetracker.model.User;

public class AuthenticationService {
    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password, User.UserRole role) {
        User user = userDAO.findByUsername(username);
        
        if (user != null && user.getPassword().equals(password) && user.getRole() == role) {
            return user;
        }
        
        return null;
    }

    public boolean register(User user) {
        try {
            // Check if username already exists
            if (userDAO.findByUsername(user.getUsername()) != null) {
                return false;
            }

            // Create new user
            return userDAO.create(user) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDAO.findById(userId);
        
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            return userDAO.update(user) > 0;
        }
        
        return false;
    }

    public boolean deleteUser(int userId) {
        try {
            return userDAO.delete(userId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 