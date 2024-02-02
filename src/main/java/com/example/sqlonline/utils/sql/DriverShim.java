package com.example.sqlonline.utils.sql;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DriverShim implements Driver {
    private final DriversMapper driverMapper;

    public DriverShim(DriversMapper driverMapper) {
        this.driverMapper = driverMapper;
    }

    private Map.Entry<Driver, String> getDriverUrlEntry(String url) throws Exception {
        String reg ="\\?driverId=([^&]+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url);
        String driverId;
        String baseUrl;
        if (matcher.find()) {
            driverId = matcher.group(1);
            baseUrl = url.replaceAll(reg, "");
            return Map.entry(driverMapper.getDriver(driverId), baseUrl);
        }
        throw new Exception("No driver found");
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        try {
            Map.Entry<Driver, String> driverEntry = getDriverUrlEntry(url);
            return driverEntry.getKey().connect(driverEntry.getValue(), info);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        try {
            Map.Entry<Driver, String> driverEntry = getDriverUrlEntry(url);
            return driverEntry.getKey().acceptsURL(driverEntry.getValue());
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        try {
            Map.Entry<Driver, String> driverEntry = getDriverUrlEntry(url);
            return driverEntry.getKey().getPropertyInfo(driverEntry.getValue(), info);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() {
        return null;
    }
}
