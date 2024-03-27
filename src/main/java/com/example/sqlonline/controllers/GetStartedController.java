package com.example.sqlonline.controllers;


import com.example.sqlonline.utils.sql.dbservice.DatabaseService;
import com.example.sqlonline.utils.sql.dbservice.DatabaseServiceManager;
import com.example.sqlonline.dao.dto.DbUserCredentials;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return databaseServiceManager.getServicesIds();
    }

    @GetMapping("/versions/{version}")
    public DbUserCredentials chooseVersion(@PathVariable("version") String version) {
        DatabaseService dbService = databaseServiceManager.getDatabaseService(version);
        return dbService.createDatabase();
    }

    private boolean userCredentialsAreCached(HttpSession session, String version) {
        String cachedVersion = (String) session.getAttribute("VERSION_ID");
        return version.equals(cachedVersion);
    }
}
