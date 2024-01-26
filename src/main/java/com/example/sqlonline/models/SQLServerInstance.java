package com.example.sqlonline.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SQLServerInstance {
    @Id
    private String versionName;
    private String connectionUrl;

    public SQLServerInstance(String versionName, String connectionUrl) {
        this.versionName = versionName;
        this.connectionUrl = connectionUrl;
    }

    public SQLServerInstance() {
        versionName = "";
        connectionUrl = "";
    }

}
