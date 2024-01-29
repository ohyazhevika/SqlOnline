package com.example.sqlonline.controllers;


import java.sql.*;

public class SQLRunner {
    private final String connectionURL;
    public SQLRunner(String connectionURL) {
        this.connectionURL = connectionURL;
    }
    public String execute(String query) {
        try {
            Connection connection = DriverManager.getConnection(connectionURL);
            Statement statement = connection.createStatement();
            boolean hasMoreResults = statement.execute(query);
            StringBuilder sb = new StringBuilder();
            while (hasMoreResults) {
                ResultSet rs = statement.getResultSet();
                sb.append(rs);
                hasMoreResults = statement.getMoreResults();
            }
            return sb.toString();
        } catch (SQLException e) {
            return e.getSQLState();
        }
    }
}
