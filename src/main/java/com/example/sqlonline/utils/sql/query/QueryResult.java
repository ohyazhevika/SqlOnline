package com.example.sqlonline.utils.sql.query;

import java.util.ArrayList;
import java.util.List;

public class QueryResult {
    public int errorCode;
    public String errorMessage;
    public List<String> rows;

    public QueryResult() {
        errorCode = 0;
        errorMessage = "Query OK";
        rows = new ArrayList<>();
    }
}
