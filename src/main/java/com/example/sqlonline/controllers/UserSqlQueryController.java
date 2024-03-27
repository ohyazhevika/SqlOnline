package com.example.sqlonline.controllers;

import com.example.sqlonline.dao.dto.UserSqlQuery;
import com.example.sqlonline.utils.sql.dbservice.DatabaseServiceManager;
import com.example.sqlonline.dao.dto.QueryResult;
import com.example.sqlonline.utils.sql.query.SqlScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.util.List;

@RestController
public class UserSqlQueryController {
    private final DatabaseServiceManager databaseServiceManager;
    private final SqlScriptRunner sqlScriptRunner;
    @Autowired
    public UserSqlQueryController(SqlScriptRunner sqlScriptRunner, DatabaseServiceManager databaseServiceManager) {
        this.sqlScriptRunner = sqlScriptRunner;
        this.databaseServiceManager = databaseServiceManager;
    }

    @PostMapping("/sql/{version}")
    public List<QueryResult> runQuery(@PathVariable(value="version") String version,
                         @RequestBody UserSqlQuery userSqlQuery) {

        // todo: there has to be a different way of passing users' credentials to DatabaseServiceManager.
        // todo: Connections shouldn't be handled by controller. There's to be some service for that.
        Connection connection = databaseServiceManager.getConnection(version, userSqlQuery.userCredentials);

        return sqlScriptRunner.execute(connection, userSqlQuery.sql);
    }
}
