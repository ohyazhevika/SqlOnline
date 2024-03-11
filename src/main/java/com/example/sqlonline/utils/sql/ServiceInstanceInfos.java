package com.example.sqlonline.utils.sql;

import com.example.sqlonline.utils.sql.dbservice.DatabaseService;
import com.example.sqlonline.utils.sql.dbservice.impl.SoqolDatabaseService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "service")
public class ServiceInstanceInfos {
    private Map<String, ServiceInstanceConfiguration> configurations;

    public Map <String, DatabaseService> createServiceInstances() {
        Map<String, DatabaseService> serviceMap = new HashMap<>();
        configurations.forEach((key, value) -> {
            try {
                String host = value.host + ":" + value.port;
                DatabaseService databaseService = new SoqolDatabaseService(host, value.createDriver());
                serviceMap.put(key, databaseService);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });
        return serviceMap;
    }
}
