package com.example.sqlonline.utils.sql;

public interface SQLRunner {
    String execute(String connectionUrl, String query) throws Exception;
}
