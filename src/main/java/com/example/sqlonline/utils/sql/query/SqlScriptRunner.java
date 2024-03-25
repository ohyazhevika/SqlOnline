package com.example.sqlonline.utils.sql.query;

import com.example.sqlonline.dao.dto.QueryResult;

import java.sql.Connection;
import java.util.List;

public interface SqlScriptRunner {
    List<QueryResult> execute(Connection connection, String query);
}
