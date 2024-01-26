package com.example.sqlonline.controllers;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLRunner {
    private final String connectionURL;
    public SQLRunner(String connectionURL) {
        this.connectionURL = connectionURL;
    }
    public String execute(String query) {
        try {
            Connection connection = DriverManager.getConnection(connectionURL);
            Statement statement = connection.createStatement();
            statement.addBatch(query);
            statement.executeBatch();
            return "!";
        } catch (SQLException e) {
            return "";
        }
    }
}
