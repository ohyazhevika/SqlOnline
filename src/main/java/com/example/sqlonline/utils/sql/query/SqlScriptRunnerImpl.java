package com.example.sqlonline.utils.sql.query;

import com.example.sqlonline.dao.dto.QueryResult;
import com.example.sqlonline.dao.dto.Table;
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
        Statement statement = c.createStatement()
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
                    queryResult.table = getTableFromResultSet(rs);
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

    private Table getTableFromResultSet (ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        String[] columnNames = getColumnNamesArray(rsmd);
        Table table = new Table(columnNames);
        int columnsCnt = columnNames.length;
        if (columnsCnt > 0) {
            while(resultSet.next()) {
                String[] rowValues = new String[columnsCnt];
                for (int i = 0; i < columnsCnt; i++) {
                   rowValues[i] = resultSet.getString(i + 1);
                }
                table.addRow(rowValues);
            }
        }
        return table;
    }

    private String[] getColumnNamesArray(ResultSetMetaData rsmd) throws SQLException {
        int columnsNumber = rsmd.getColumnCount();
        String[] columnNames = new String[columnsNumber];
        for (int i = 0; i < columnsNumber; i++) {
            columnNames[i] = rsmd.getColumnName(i + 1);
        }
        return columnNames;
    }

}
