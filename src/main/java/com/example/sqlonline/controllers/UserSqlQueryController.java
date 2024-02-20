package com.example.sqlonline.controllers;

import com.example.sqlonline.utils.sql.dbservice.DatabaseServiceManager;
import com.example.sqlonline.utils.sql.query.QueryResult;
import com.example.sqlonline.utils.sql.query.SqlQueryRunner;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;

@Controller
public class UserSqlQueryController {
    private final DatabaseServiceManager databaseServiceManager;
    private final SqlQueryRunner sqlQueryRunner;
    @Autowired
    public UserSqlQueryController(SqlQueryRunner sqlQueryRunner, DatabaseServiceManager databaseServiceManager) {
        this.sqlQueryRunner = sqlQueryRunner;
        this.databaseServiceManager = databaseServiceManager;
    }
    @PostMapping("/RunSql")
    public String runQuery(@RequestParam("query") String query, HttpSession session) {
        QueryResult queryResult;

        Connection connection = databaseServiceManager.getConnection(session);
        queryResult = sqlQueryRunner.execute(connection, query);

        session.setAttribute("ERROR_MESSAGE", queryResult.errorMessage);
        session.setAttribute("ROWS", queryResult.rows);
        //todo: query result should be updated on page refresh
        session.setAttribute("QUERY", query);
        return "redirect:/ShowSql";
    }

    @GetMapping("/ShowSql")
    public String show(Model model, HttpSession session) {
        //todo: add some nicer presentation of query results
        model.addAttribute("query", session.getAttribute("QUERY"));
        model.addAttribute("errorMessage", session.getAttribute("ERROR_MESSAGE"));
        model.addAttribute("rows", session.getAttribute("ROWS"));

        return "showSql";
    }
}
