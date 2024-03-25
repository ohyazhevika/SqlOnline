package com.example.sqlonline.controllers;

import com.example.sqlonline.utils.sql.dbservice.DatabaseServiceManager;
import com.example.sqlonline.dao.dto.QueryResult;
import com.example.sqlonline.utils.sql.query.SqlScriptRunner;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
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
    @PostMapping("/RunSql")
    public void runQuery(@RequestParam("query") String query, HttpSession session, HttpServletResponse response) throws IOException {
        List<QueryResult> queryResults;

        Connection connection = databaseServiceManager.getConnection(session);
        queryResults = sqlScriptRunner.execute(connection, query);

        //todo: query result should be updated on page refresh
        session.setAttribute("QUERY", query);
        session.setAttribute("QUERY_RESULT", queryResults);
        response.sendRedirect("/ShowSql");
    }

    @GetMapping("/ShowSql")
    public List<QueryResult> show(HttpSession session) {
        //todo: add some nicer presentation of query results
        //model.addAttribute("query", session.getAttribute("QUERY"));
        ArrayList<QueryResult> q = (ArrayList<QueryResult>) session.getAttribute("QUERY_RESULT");
        if (q == null)
            q = new ArrayList<>();
       // model.addAttribute("queryResults", q);

        return q;
    }
}
