package com.example.sqlonline.utils.sql.query;

import com.example.sqlonline.utils.sql.parser.SqlScriptParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SqlScriptRunnerImpl implements SqlScriptRunner {
    private final SqlScriptParser parser;
    @Autowired
    public SqlScriptRunnerImpl(SqlScriptParser parser) {
        this.parser = parser;
    }

    @Override
    public List<QueryResult> execute(Connection c, String sqlScript) {
        try (
        Statement statement = c.createStatement();
        ) {
            List<String> queries = parser.parse(sqlScript);
            return executeQueries(statement, queries);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<QueryResult> executeQueries(Statement statement, List<String> queries) {
        List<QueryResult> queryResults = new ArrayList<>();
        for (String query: queries) {
            QueryResult queryResult = new QueryResult();
            try {
                statement.execute(query);
                ResultSet rs = statement.getResultSet();
                if (rs != null) {
                    queryResult.rows.addAll(processResultSet(rs));
                    rs.close();
                }
                queryResult.errorMessage = "Query OK";
            } catch (SQLException e) {
                queryResult.errorMessage = e.getMessage();
            }
            queryResults.add(queryResult);
        }
        return queryResults;
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
