package com.example.sqlonline.dao.dto;

import java.io.Serializable;

public class QueryResult implements Serializable {
    public String errorMessage;
    public Table table;

    public QueryResult() {
        errorMessage = "";
        table = Table.getEmptyTable();
    }
}
