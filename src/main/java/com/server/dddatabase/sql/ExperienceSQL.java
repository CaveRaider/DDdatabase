package com.server.dddatabase.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ExperienceSQL {

    private String host = "172.18.0.1";
    private String port = "3306";
    private String database = "s7_LevelSystem";
    private String username = "u7_Hetc8WT1BS";
    private String password = "CYJZC0C0+.XegySPDghqYjkM";

    private Connection connection;

    public boolean isConnected() {
        return connection != null;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (isConnected()) return;
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
    }

    public void disconnect() {
        if (!isConnected()) return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
