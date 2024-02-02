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
            queryResult = sqlRunner.execute(query);
        } catch (Exception e) {
            queryResult = e.getMessage();
        }
        request.getSession().setAttribute("QUERY_RESULT", queryResult);
        request.getSession().setAttribute("QUERY", query);
        return "redirect:/ShowSql";
    }

    @GetMapping("/ShowSql")
    public String show(Model model, HttpSession session) {
        model.addAttribute("query", session.getAttribute("QUERY"));
        model.addAttribute("queryResult", session.getAttribute("QUERY_RESULT"));
        return "showSql";
    }
}
