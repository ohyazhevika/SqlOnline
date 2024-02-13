package com.example.sqlonline.utils.sql;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "service")
public class DriverJarInfos {
    private Map<String, DriverJarInfo> drivers;

    public Map<String, Driver> createTargetDrivers() {
        Map<String, Driver> result = new HashMap<>();
        drivers.forEach((key, value)-> {
            try {
                result.put(key, value.createDriver());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }
}
