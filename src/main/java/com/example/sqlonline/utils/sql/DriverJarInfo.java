package com.example.sqlonline.utils.sql;

public class DriverJarInfo {
    public String id;
    public String className;
    public String jarPath;
    public DriverJarInfo (String id, String className, String jarPath) {
        this.id = id;
        this.className = className;
        this.jarPath = jarPath;
    }
}
