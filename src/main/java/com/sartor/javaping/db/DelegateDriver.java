package com.sartor.javaping.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

public class DelegateDriver implements Driver {
    private Driver realDriver;

    public DelegateDriver(Driver realDriver) {
        this.realDriver = realDriver;
    }

    public Connection connect(String url, Properties info) throws SQLException {
        return realDriver.connect(url, info);
    }

    public boolean acceptsURL(String url) throws SQLException {
        return realDriver.acceptsURL(url);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return realDriver.getPropertyInfo(url, info);
    }

    public int getMajorVersion() {
        return realDriver.getMajorVersion();
    }

    public int getMinorVersion() {
        return realDriver.getMinorVersion();
    }

    public boolean jdbcCompliant() {
        return realDriver.jdbcCompliant();
    }

    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
