package com.example.sqlonline.utils.sql.dbservice;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "db")
public class DbManagementServiceConfigurations {
    private Map<String, DbManagementServiceConfiguration> configurations = new HashMap<>();

    // get/set ommitted

    public Map<Object, Object> createTargetDataSources() {
        Map<Object, Object> result = new HashMap<>();
        configurations.forEach((key, value) ->  result.put(key, value.createDataSource()));
        return result;
    }
}
