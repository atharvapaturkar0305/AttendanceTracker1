package com.attendancetracker.dao;

import com.attendancetracker.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> {
    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    protected void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void closeResources(Connection conn, PreparedStatement stmt) {
        closeResources(conn, stmt, null);
    }

    protected List<T> executeQuery(String sql, Object[] params, ResultSetMapper<T> mapper) {
        List<T> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return results;
    }

    protected T executeQueryForSingleResult(String sql, Object[] params, ResultSetMapper<T> mapper) {
        List<T> results = executeQuery(sql, params, mapper);
        return results.isEmpty() ? null : results.get(0);
    }

    protected int executeUpdate(String sql, Object[] params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int result = 0;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            
            result = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt);
        }

        return result;
    }

    protected interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
} 