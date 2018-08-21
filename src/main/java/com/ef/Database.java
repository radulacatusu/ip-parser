package com.ef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/parser?createDatabaseIfNotExist=true&useSSL=false" +
            "&rewriteBatchedStatements=true";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String MAX_POOL = "250";
    private static Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private Connection connection;
    private Properties properties;

    public Database() throws SQLException {
        init();
    }

    private void init() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }

        String sqlCreateLogTable = "CREATE TABLE IF NOT EXISTS " + "LOGS"
                + "  (id int auto_increment not null,"
                + "   start_date           TIMESTAMP NOT NULL,"
                + "   IP            VARCHAR(45) NOT NULL,"
                + "   request          VARCHAR(100) NOT NULL,"
                + "   status           INTEGER NOT NULL,"
                + "   user_agent           VARCHAR(500),"
                + "   primary key (id),"
                + "   INDEX idx_log_start_date_IP(start_date, IP))";

        String sqlCreateBlockedIpsTable = "CREATE TABLE IF NOT EXISTS " + "BLOCKED_IPS"
                + "  (id int auto_increment not null,"
                + "   IP            VARCHAR(45) NOT NULL,"
                + "   reason          VARCHAR(1000) NOT NULL,"
                + "   insert_time     TIMESTAMP NOT NULL,"
                + "   primary key (id))";

        connect().createStatement().execute(sqlCreateLogTable);
        connect().createStatement().execute(sqlCreateBlockedIpsTable);
    }

    private Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("MaxPooledStatements", MAX_POOL);
        }
        return properties;
    }

    public Connection connect() {
        if (connection == null) {
            try {
                Class.forName(DATABASE_DRIVER);
                connection = DriverManager.getConnection(DATABASE_URL, getProperties());
            } catch (ClassNotFoundException | SQLException e) {
                LOGGER.error("Cannot connect the database", e.getMessage());
                throw new IllegalStateException("Cannot connect the database!", e);
            }
        }
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                LOGGER.error("Cannot disconnect the database", e.getMessage());
            }
        }
    }
}