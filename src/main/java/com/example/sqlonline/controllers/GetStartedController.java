package com.example.sqlonline.controllers;


import com.example.sqlonline.utils.sql.dbservice.DatabaseService;
import com.example.sqlonline.utils.sql.dbservice.DatabaseServiceManager;
import com.example.sqlonline.utils.sql.dbservice.DbUserCredentials;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
public class GetStartedController {
    private final DatabaseServiceManager databaseServiceManager;

    @Autowired
    public GetStartedController(DatabaseServiceManager databaseServiceManager) {
        this.databaseServiceManager = databaseServiceManager;
    }

    @GetMapping("/versions")
    public List<String> showVersions() {
        //model.addAttribute("versions", databaseServiceManager.getServicesIds());
        return databaseServiceManager.getServicesIds();
    }

    @PostMapping("/ChooseVersion")
    public void chooseVersion(@RequestParam("version") String version, HttpSession session, HttpServletResponse response) throws IOException {
        if (!userCredentialsAreCached(session, version)) {
            DatabaseService dbService = databaseServiceManager.getDatabaseService(version);
            DbUserCredentials userCredentials = dbService.createDatabase();
            session.setAttribute("VERSION_ID", version);
            session.setAttribute("DB_NAME", userCredentials.dbName);
            session.setAttribute("USERNAME", userCredentials.userName);
            session.setAttribute("PASSWORD", userCredentials.userPassword);
        }
        response.sendRedirect("/ShowSql");
    }

    private boolean userCredentialsAreCached(HttpSession session, String version) {
        String cachedVersion = (String) session.getAttribute("VERSION_ID");
        return version.equals(cachedVersion);
    }
}
