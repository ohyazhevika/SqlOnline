package com.example.sqlonline.utils.sql.query;

import java.sql.Connection;

public interface SqlQueryRunner {
    QueryResult execute(Connection connection,  String query);
}
