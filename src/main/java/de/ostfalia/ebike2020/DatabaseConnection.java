package de.ostfalia.ebike2020;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static final String URL = "jdbc:mysql://localhost:3306/e_bike_2020";
    public static final String USER = "root";
    public static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
