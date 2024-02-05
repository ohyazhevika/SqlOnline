package com.example.sqlonline.controllers;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GetStartedController {
    private final List<String> versions;

    public GetStartedController() {
        versions = new ArrayList<>();
        versions.add("soqol1");
    }

    @GetMapping("/ShowVersions")
    public String showVersions(Model model, HttpSession session) {
        model.addAttribute("versions", versions);
        return "versions";
    }

    @PostMapping("/ChooseVersion")
    public String chooseVersion(@RequestParam("version") String version, HttpSession session) {
        session.setAttribute("VERSION_ID", version);
        session.setAttribute("DB_NAME", "DB");
        session.setAttribute("USERNAME", "SOQOL");
        session.setAttribute("PASSWORD", "SOQOL");
        return "redirect:/ShowSql";
    }


}
