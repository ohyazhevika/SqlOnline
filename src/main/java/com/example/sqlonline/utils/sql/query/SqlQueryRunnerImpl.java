package com.example.sqlonline.utils.sql.query;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SqlQueryRunnerImpl implements SqlQueryRunner {
    @Override
    public QueryResult execute(Connection c, String query) {
        QueryResult queryResult = new QueryResult();
        try (
                Statement statement = c.createStatement()
        ) {
            statement.execute(query);
            ResultSet rs = statement.getResultSet();
            queryResult.rows.addAll(processResultSet(rs));

        } catch (SQLException e) {
            queryResult.errorCode = 1;
            queryResult.errorMessage = e.getMessage();
        }
        return queryResult;
    }

    private List<String> processResultSet(ResultSet rs) throws SQLException {
        List<String> rows = new ArrayList<>();
        if (rs == null) return rows;
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= columnsNumber; i++) {
            sb.append(rsmd.getColumnName(i)).append("    ");
        }
        rows.add(sb.toString());
        while(rs.next()) {
            sb = new StringBuilder();
            for (int i = 1; i <= columnsNumber; i++) {
                sb.append(rs.getString(i)).append("    ");
            }
            rows.add(sb.toString());
        }
        return rows;
    }
}
