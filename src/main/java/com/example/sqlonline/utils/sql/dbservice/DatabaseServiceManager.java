package com.example.sqlonline.utils.sql.dbservice;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Map;

@Service
public class DatabaseServiceManager {
    private Map<String, DatabaseService> services;
    public Connection getConnection(String sessionId) {

    }

}
