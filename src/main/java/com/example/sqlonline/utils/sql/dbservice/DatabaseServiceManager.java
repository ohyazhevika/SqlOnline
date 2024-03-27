package com.example.sqlonline.utils.sql.dbservice;

import com.example.sqlonline.dao.dto.DbUserCredentials;
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
    public Connection getConnection(String versionId, DbUserCredentials userCredentials) {
        DatabaseService databaseService = services.get(versionId);
        Connection connection;
        try {
            connection = databaseService.connectToDatabase(userCredentials);
            return connection;
        } catch (SQLException e) {
            String connectionDetails = "Connection [version: " + versionId + ", database: "
                    + userCredentials.dbName + ", username: " + userCredentials.userName + ", userPassword: " + userCredentials.password + "]";
            throw new RuntimeException(e.getMessage() + connectionDetails);
        }
    }

    public List<String> getServicesIds() {
        return services.keySet().stream().toList();
    }

    public DatabaseService getDatabaseService(String serviceId) {
        return services.get(serviceId);
    }

}
