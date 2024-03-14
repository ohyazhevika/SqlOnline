package com.example.sqlonline.config;

import com.example.sqlonline.utils.sql.ServiceInstanceInfos;
import com.example.sqlonline.utils.sql.dbservice.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class DatabaseServicesConfig {
    private final ServiceInstanceInfos serviceInstanceInfos;

    @Autowired
    public DatabaseServicesConfig(ServiceInstanceInfos serviceInstanceInfos) {
        this.serviceInstanceInfos = serviceInstanceInfos;
    }

    @Bean
    public Map<String, DatabaseService> getServices() {
        return serviceInstanceInfos.createServiceInstances();
    }
}
