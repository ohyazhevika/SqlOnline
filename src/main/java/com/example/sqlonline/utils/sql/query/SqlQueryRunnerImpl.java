package com.example.sqlonline.utils.sql.query;

import com.example.sqlonline.utils.sql.parser.SqlScriptParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SqlQueryRunnerImpl implements SqlQueryRunner {
    private final SqlScriptParser parser;
    @Autowired
    public SqlQueryRunnerImpl(SqlScriptParser parser) {
        this.parser = parser;
    }

    @Override
    public List<QueryResult> execute(Connection c, String sql) {
        List<QueryResult> queryResults = new ArrayList<>();
        try (
                Statement statement = c.createStatement()
        ) {
            List<String> queries = parser.parse(sql);
            for (String query: queries) {
                ResultSet rs = statement.executeQuery(query);
                QueryResult queryResult = new QueryResult();
                if (rs != null) {
                    queryResult.rows.addAll(processResultSet(rs));
                    rs.close();
                }
                queryResults.add(queryResult);
            }

        } catch (SQLException e) {
            QueryResult qr = new QueryResult();
            qr.errorCode = 1;
            qr.errorMessage = e.getMessage();
            queryResults.add(qr);
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
