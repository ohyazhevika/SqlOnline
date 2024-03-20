package com.example.sqlonline.controllers;


import com.example.sqlonline.utils.sql.dbservice.DatabaseService;
import com.example.sqlonline.utils.sql.dbservice.DatabaseServiceManager;
import com.example.sqlonline.utils.sql.dbservice.DbUserCredentials;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class GetStartedController {
    private final DatabaseServiceManager databaseServiceManager;

    @Autowired
    public GetStartedController(DatabaseServiceManager databaseServiceManager) {
        this.databaseServiceManager = databaseServiceManager;
    }

    @GetMapping("/ShowVersions")
    public String showVersions(Model model) {
        model.addAttribute("versions", databaseServiceManager.getServicesIds());
        return "versions";
    }

    @PostMapping("/ChooseVersion")
    public String chooseVersion(@RequestParam("version") String version, HttpSession session) {
        if (!userCredentialsAreCached(session, version)) {
            DatabaseService dbService = databaseServiceManager.getDatabaseService(version);
            DbUserCredentials userCredentials = dbService.createDatabase();
            session.setAttribute("VERSION_ID", version);
            session.setAttribute("DB_NAME", userCredentials.dbName);
            session.setAttribute("USERNAME", userCredentials.userName);
            session.setAttribute("PASSWORD", userCredentials.userPassword);
        }
        return "redirect:/ShowSql";
    }

    private boolean userCredentialsAreCached(HttpSession session, String version) {
        String cachedVersion = (String) session.getAttribute("VERSION_ID");
        return version.equals(cachedVersion);
    }
}
