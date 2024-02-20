package com.example.sqlonline.utils.sql.query;

import com.example.sqlonline.utils.sql.query.SqlQueryRunner;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlQueryRunnerImpl implements SqlQueryRunner {
    private DataSource dataSource;
    public SqlQueryRunnerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public QueryResult execute(String query) {
        QueryResult queryResult = new QueryResult();
        try (Connection c = dataSource.getConnection();
             Statement statement = c.createStatement()
        ) {
            statement.execute(query);
            ResultSet rs = statement.getResultSet();
            while (rs != null) {
                queryResult.rows.addAll(processResultSet(rs));
                rs = statement.getResultSet();
            }

        } catch (SQLException e) {
            queryResult.errorCode = 1;
            queryResult.errorMessage = e.getMessage();
        }
        return queryResult;
    }

    private List<String> processResultSet(ResultSet rs) throws SQLException {
        List<String> rows = new ArrayList<>();
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
