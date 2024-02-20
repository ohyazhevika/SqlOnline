package com.example.sqlonline.utils.sql.dbservice;


import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class SoqolDatabaseService implements DatabaseService {

    private String host;
    private Driver driver;

    public SoqolDatabaseService(String host, Driver driver) {
        this.host = host;
        this.driver = driver;
    }
    @Override
    public void startup() {

    }

    @Override
    public void createDatabase(String name, String path) {

    }

    @Override
    public void shutDownDatabase(String name) {

    }

    @Override
    public void dropDatabase(String name) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public Connection connectToDatabase(String databaseName, String userName, String password) throws SQLException {
        String url = "jdbc:soqol://" + userName + ":" + password + "@" + host + "/" + databaseName;
        return driver.connect(url, new Properties());
    }
}
