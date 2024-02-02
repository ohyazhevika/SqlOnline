package com.example.sqlonline.utils.sql;


import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Properties;

@Component
public class SQLRunnerImpl implements SQLRunner {
    private final String connectionURL;
    private final DriverShim driverShim;

    public SQLRunnerImpl() throws Exception {
        this.connectionURL = "jdbc:soqol://SOQOL:SOQOL@localhost:2060/DB";
        this.driverShim = new DriverShim(
           new DriversMapper(
                   new DriverJarInfo(
                           "s1", "ru.relex.soqol.jdbc.Driver", "D:\\soqol\\bin\\soqol-jdbc-2.0.1.jar"
                   )
           )
        );
    }
    @Override
    public String execute(String query) throws Exception {
        try (Connection connection = driverShim.connect(connectionURL, new Properties())) {
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
