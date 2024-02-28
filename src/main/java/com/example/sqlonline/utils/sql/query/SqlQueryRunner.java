package com.example.sqlonline.utils.sql.query;

import java.sql.Connection;
import java.util.List;

public interface SqlQueryRunner {
    List<QueryResult> execute(Connection connection, String query);
}
