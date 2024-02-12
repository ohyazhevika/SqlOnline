package com.example.sqlonline.utils.sql.dbservice;

public interface DatabaseService {
    void startup();
    void createDatabase(String name, String path);
    void shutdown();
}
