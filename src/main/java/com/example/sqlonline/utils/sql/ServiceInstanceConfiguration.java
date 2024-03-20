package com.example.sqlonline.utils.sql;

import lombok.Data;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

@Data
public class ServiceInstanceConfiguration {
    public String driverClassName;
    public String driverJarPath;
    public String host;
    public String port;
    public Driver createDriver() throws Exception {
        URL jarURL = new File(driverJarPath).toURI().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[]{jarURL}, Thread.currentThread().getContextClassLoader());
        Class<? extends Driver> classToLoad = (Class<? extends Driver>) Class.forName(driverClassName, true, loader);
        return classToLoad.newInstance();
    }
    public String getPort() {
        return port;
    }
    public String getHost() {
        return host;
    }
}
