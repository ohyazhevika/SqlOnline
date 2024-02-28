package com.example.sqlonline.config;

import com.example.sqlonline.utils.sql.DriverJarInfos;
import com.example.sqlonline.utils.sql.dbservice.DatabaseService;
import com.example.sqlonline.utils.sql.dbservice.impl.SoqolDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseServicesConfig {
    DriverJarInfos driverJarInfos;

    @Autowired
    public DatabaseServicesConfig(DriverJarInfos driverJarInfos) {
        this.driverJarInfos = driverJarInfos;
    }

    @Bean
    public Map<String, DatabaseService> getServices() {
        Map<String, DatabaseService> services = new HashMap<>();
        Map<String, Driver> drivers = driverJarInfos.createTargetDrivers();
        for (Map.Entry<String, Driver> d: drivers.entrySet()) {
            String version = d.getKey();
            Driver driver = d.getValue();
            DatabaseService s = new SoqolDatabaseService("localhost:2060", driver);
            services.put(version, s);
        }
        return services;
    }
}
