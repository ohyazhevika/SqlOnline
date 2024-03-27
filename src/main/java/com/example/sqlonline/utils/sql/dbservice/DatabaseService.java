package com.example.sqlonline.utils.sql.dbservice;

import com.example.sqlonline.dao.dto.DbUserCredentials;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseService {
    DbUserCredentials createDatabase();
    void shutDownDatabase(String name);
    void dropDatabase(String name);

    Connection connectToDatabase(DbUserCredentials dbUserCredentials) throws SQLException;

}
