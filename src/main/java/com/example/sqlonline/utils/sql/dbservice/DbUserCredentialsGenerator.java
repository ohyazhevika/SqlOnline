package com.example.sqlonline.utils.sql.dbservice;

public class DbUserCredentialsGenerator {
    private final static String userNamePrefix = "U";
    private final static String dbNamePrefix = "DB";
    private long userCnt = 0L;
    private long dbCnt = 0L;

    public DbUserCredentials generate() {
        userCnt ++;
        dbCnt ++;
        DbUserCredentials duc =  new DbUserCredentials();
        duc.userName = userNamePrefix + userCnt;
        duc.userPassword = duc.userName;
        duc.dbName = dbNamePrefix + dbCnt;
        return duc;
    }
}
