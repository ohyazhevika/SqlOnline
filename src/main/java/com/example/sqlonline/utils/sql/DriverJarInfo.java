package com.example.sqlonline.utils.sql;

import lombok.Data;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

@Data
public class DriverJarInfo {
    public String classname;
    public String jarpath;
    public Driver createDriver() throws Exception {
        URL jarURL = new File(jarpath).toURI().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[]{jarURL}, ClassLoader.getSystemClassLoader());
        Class<? extends Driver> classToLoad = (Class<? extends Driver>) Class.forName(classname, true, loader);
        return classToLoad.newInstance();
    }
}
