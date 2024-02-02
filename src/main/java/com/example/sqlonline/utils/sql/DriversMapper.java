package com.example.sqlonline.utils.sql;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

public class DriversMapper {
    private final Map<String, Driver> drivers = new HashMap<>();
    public DriversMapper(DriverJarInfo ... driversInfo) throws Exception {
        for (DriverJarInfo dji: driversInfo) {
            put(dji);
        }
    }

    public Driver getDriver(String id) {
        return drivers.get(id);
    }

    private void put (DriverJarInfo jarInfo) throws Exception {
        URL jarURL = new File(jarInfo.jarPath).toURI().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[]{jarURL}, ClassLoader.getSystemClassLoader());
        Class<? extends Driver> classToLoad = (Class<? extends Driver>) Class.forName(jarInfo.className, true, loader);
        Driver driver = classToLoad.newInstance();
        drivers.put(jarInfo.id, driver);
    }
}