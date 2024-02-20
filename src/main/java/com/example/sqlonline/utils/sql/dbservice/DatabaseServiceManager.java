package com.example.sqlonline.utils.sql.dbservice;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseServiceManager {
    private final Map<String, DatabaseService> services;

    @Autowired
    public DatabaseServiceManager(Map<String, DatabaseService> services) {
        this.services = services;
    }
    public Connection getConnection(HttpSession session) {
        String versionId = (String) session.getAttribute("VERSION_ID");
        if (versionId == null) throw new RuntimeException("No service version was chosen!");
        DatabaseService databaseService = services.get(versionId);

        String databaseName = (String) session.getAttribute("DB_NAME");
        String username = (String) session.getAttribute("USERNAME");
        String password = (String) session.getAttribute("PASSWORD");
        Connection connection;
        try {
            connection = databaseService.connectToDatabase(databaseName, username, password);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getServicesIds() {
        return services.keySet().stream().toList();
    }

}
