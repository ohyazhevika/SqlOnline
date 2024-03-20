package com.example.sqlonline.controllers;

import com.example.sqlonline.utils.sql.dbservice.DatabaseServiceManager;
import com.example.sqlonline.utils.sql.query.QueryResult;
import com.example.sqlonline.utils.sql.query.SqlScriptRunner;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserSqlQueryController {
    private final DatabaseServiceManager databaseServiceManager;
    private final SqlScriptRunner sqlScriptRunner;
    @Autowired
    public UserSqlQueryController(SqlScriptRunner sqlScriptRunner, DatabaseServiceManager databaseServiceManager) {
        this.sqlScriptRunner = sqlScriptRunner;
        this.databaseServiceManager = databaseServiceManager;
    }
    @PostMapping("/RunSql")
    public String runQuery(@RequestParam("query") String query, HttpSession session) {
        List<QueryResult> queryResults;

        Connection connection = databaseServiceManager.getConnection(session);
        queryResults = sqlScriptRunner.execute(connection, query);

        //todo: query result should be updated on page refresh
        session.setAttribute("QUERY", query);
        session.setAttribute("QUERY_RESULT", queryResults);
        return "redirect:/ShowSql";
    }

    @GetMapping("/ShowSql")
    public String show(Model model, HttpSession session) {
        //todo: add some nicer presentation of query results
        model.addAttribute("query", session.getAttribute("QUERY"));
        Object q = session.getAttribute("QUERY_RESULT");
        if (q == null)
            q = new ArrayList<QueryResult>();
        model.addAttribute("queryResults", q);

        return "showSql";
    }
}
