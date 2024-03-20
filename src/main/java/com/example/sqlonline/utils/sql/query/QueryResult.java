package com.example.sqlonline.utils.sql.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryResult implements Serializable {
    public String errorMessage;
    public List<String> rows;

    public QueryResult() {
        errorMessage = "";
        rows = new ArrayList<>();
    }
}
