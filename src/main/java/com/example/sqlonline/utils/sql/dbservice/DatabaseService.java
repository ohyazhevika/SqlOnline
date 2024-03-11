package com.example.sqlonline.utils.sql.dbservice;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseService {
    DbUserCredentials createDatabase();
    void shutDownDatabase(String name);
    void dropDatabase(String name);

    Connection connectToDatabase(String databaseName, String userName, String password) throws SQLException;

}
