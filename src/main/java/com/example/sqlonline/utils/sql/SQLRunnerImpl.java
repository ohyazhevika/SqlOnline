package com.example.sqlonline.utils.sql;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Properties;

@Component
public class SQLRunnerImpl implements SQLRunner {
    //private final String connectionURL =  "jdbc:soqol://SOQOL:SOQOL@localhost:2060/DB";
    private final DriverShim driverShim;

    @Autowired
    public SQLRunnerImpl(DriverShim driverShim) {
        this.driverShim = driverShim;
    }

    @Override
    public String execute(String connectionUrl, String query) throws Exception {
        try (Connection connection = driverShim.connect(connectionUrl, new Properties())) {
            Statement statement = connection.createStatement();
            boolean hasMoreResults = statement.execute(query);
            StringBuilder sb = new StringBuilder();
            while (hasMoreResults) {
                ResultSet rs = statement.getResultSet();
                sb.append(rs);
                hasMoreResults = statement.getMoreResults();
            }
            return sb.toString();
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }
}
