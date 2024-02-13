package com.example.sqlonline.utils.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Driver;

import java.util.List;
import java.util.Map;

@Component
public class DriversMapper {

    //todo: there's not only driver jar info that we need to create connection from driver shim: specify url pattern, host+port
    private final Map<String, Driver> drivers;
    @Autowired
    public DriversMapper(DriverJarInfos d) {
        drivers = d.createTargetDrivers();
    }

    public Driver getDriver(String id) {
        return drivers.get(id);
    }

    public List<String> getVersions() {
        return drivers.keySet().stream().toList();
    }

}