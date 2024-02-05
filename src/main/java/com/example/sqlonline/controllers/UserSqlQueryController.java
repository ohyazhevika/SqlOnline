package com.example.sqlonline.controllers;

import com.example.sqlonline.utils.sql.SQLRunner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserSqlQueryController {
    private final SQLRunner sqlRunner;
    @Autowired
    public UserSqlQueryController(SQLRunner sqlRunner) {
        this.sqlRunner = sqlRunner;
    }
    @PostMapping("/RunSql")
    public String runQuery(@RequestParam("query") String query, HttpServletRequest request) {
        String queryResult;
        try {
            String connectionUrl = getConnectionUrl((request.getSession()));
            queryResult = sqlRunner.execute(connectionUrl, query);
        } catch (Exception e) {
            queryResult = e.getMessage();
        }
        request.getSession().setAttribute("QUERY_RESULT", queryResult);
        //todo: query result should be update with page refresh
        request.getSession().setAttribute("QUERY", query);
        return "redirect:/ShowSql";
    }

    @GetMapping("/ShowSql")
    public String show(Model model, HttpSession session) {
        //todo: add some nicer presentation of query results
        model.addAttribute("query", session.getAttribute("QUERY"));
        model.addAttribute("queryResult", session.getAttribute("QUERY_RESULT"));
        return "showSql";
    }

    private String getConnectionUrl(HttpSession session) {
        //todo: add more flexible mechanism of url creation, make it possible to work with other drivers
        StringBuilder sb = new StringBuilder("jdbc:soqol://");
        String version = (String) session.getAttribute("VERSION_ID");
        String database = (String) session.getAttribute("DB_NAME");
        String username = (String) session.getAttribute("USERNAME");
        String password = (String) session.getAttribute("PASSWORD");
        sb.append(username)
                .append(":")
                .append(password)
                .append("@")
                .append("localhost:2060/")
                .append(database)
                .append("?driverId=")
                .append(version);
        return sb.toString();
    }
}
