package com.attendancetracker.dao;

import com.attendancetracker.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO extends BaseDAO<User> {
    private static final String INSERT_USER = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SELECT_USERS_BY_ROLE = "SELECT * FROM users WHERE role = ?";

    public int create(User user) {
        return executeUpdate(INSERT_USER, new Object[]{
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            user.getRole().name()
        });
    }

    public int update(User user) {
        return executeUpdate(UPDATE_USER, new Object[]{
            user.getUsername(),
            user.getPassword(),
            user.getEmail(),
            user.getRole().name(),
            user.getId()
        });
    }

    public int delete(int userId) {
        return executeUpdate(DELETE_USER, new Object[]{userId});
    }

    public User findById(int userId) {
        return executeQueryForSingleResult(SELECT_USER_BY_ID, new Object[]{userId}, this::mapResultSet);
    }

    public User findByUsername(String username) {
        return executeQueryForSingleResult(SELECT_USER_BY_USERNAME, new Object[]{username}, this::mapResultSet);
    }

    public List<User> findAll() {
        return executeQuery(SELECT_ALL_USERS, null, this::mapResultSet);
    }

    public List<User> findByRole(User.UserRole role) {
        return executeQuery(SELECT_USERS_BY_ROLE, new Object[]{role.name()}, this::mapResultSet);
    }

    private User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        return user;
    }
} 