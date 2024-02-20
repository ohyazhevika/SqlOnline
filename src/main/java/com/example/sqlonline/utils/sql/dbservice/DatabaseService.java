package com.example.sqlonline.utils.sql.dbservice;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseService {
    void startup();
    void createDatabase(String name, String path);
    void shutDownDatabase(String name);
    void dropDatabase(String name);
    void shutdown();

    Connection connectToDatabase(String databaseName, String userName, String password) throws SQLException;
}
