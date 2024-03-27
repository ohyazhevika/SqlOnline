package com.example.sqlonline.utils.sql.dbservice.impl;


import com.example.sqlonline.utils.sql.dbservice.DatabaseService;
import com.example.sqlonline.dao.dto.DbUserCredentials;
import com.example.sqlonline.utils.sql.dbservice.DbUserCredentialsGenerator;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SoqolDatabaseService implements DatabaseService {

    DbUserCredentialsGenerator userCredentialsGenerator;
    private final String host;
    private final Driver driver;
    private final String serviceUrl;

    public SoqolDatabaseService(String host, Driver driver) {
        this.host = host;
        this.driver = driver;
        userCredentialsGenerator = new DbUserCredentialsGenerator();
        serviceUrl = "jdbc:soqol://SOQOL:SOQOL@" + host;
    }

    @Override
    public DbUserCredentials createDatabase() {
        try (Connection c = driver.connect(serviceUrl, new Properties())) {
            Statement s = c.createStatement();
            DbUserCredentials duc = userCredentialsGenerator.generate();
            s.execute("CREATE DATABASE " + duc.dbName);
            setUpDbUser(duc);
            return duc;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutDownDatabase(String name) {
        try (Connection c = driver.connect(serviceUrl, new Properties())) {
            Statement s = c.createStatement();
            s.execute("SHUTDOWN DATABASE " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropDatabase(String name) {
        shutDownDatabase(name);
        try (Connection c = driver.connect(serviceUrl, new Properties())) {
            Statement s = c.createStatement();
            s.execute("DROP DATABASE " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection connectToDatabase(DbUserCredentials dbUserCredentials) throws SQLException {
        String url = "jdbc:soqol://" + dbUserCredentials.userName + ":" + dbUserCredentials.password + "@" + host + "/" + dbUserCredentials.dbName;
        return driver.connect(url, new Properties());
    }

    private void setUpDbUser(DbUserCredentials userCredentials) {
        String url = serviceUrl + "/" + userCredentials.dbName;
        try (Connection c = driver.connect(url, new Properties())) {
            Statement s = c.createStatement();
            s.execute("CREATE USER " + userCredentials.userName + " IDENTIFIED BY '" + userCredentials.password + "'");
            s.execute("GRANT DBA TO " + userCredentials.userName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
