package com.example.sqlonline.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserSqlQueryController {
    private final SQLRunner sqlRunner = new SQLRunner("jdbc:soqol://SOQOL:SOQOL@localhost:2060/DB");
    @PostMapping("/RunSql")
    public String runQuery(@RequestParam("query") String query, HttpServletRequest request) {
        String queryResult = sqlRunner.execute(query);
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
