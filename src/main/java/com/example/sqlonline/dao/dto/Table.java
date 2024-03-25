package com.example.sqlonline.dao.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Table implements Serializable {
    public final List<String[]> rowList;
    public final String [] columnNames;
    public Table(String[] columns) {
         columnNames = columns;
         rowList = new ArrayList<>();
    }

    public void addRow(String[] rowFields) {
        rowList.add(rowFields);
    }

    public static Table getEmptyTable() {
        String[] dummyColumns = new String[0];
        return new Table(dummyColumns);
    }
}
