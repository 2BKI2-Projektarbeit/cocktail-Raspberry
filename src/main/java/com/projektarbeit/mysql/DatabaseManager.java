package com.projektarbeit.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static Connection connection;

    public static boolean connect() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/projektarbeit", "projektarbeit", "test123");

            createTables();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void createTables() {
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `cocktails` (`cocktailId` VARCHAR(36) PRIMARY KEY, `name` VARCHAR(50) NOT NULL, `description` TEXT, `image` VARCHAR(200), `ingredients` TEXT, `enabled` BOOLEAN DEFAULT true, `createdAt` TIMESTAMP NOT NULL DEFAULT NOW());").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `ingredients` (`ingredientId` VARCHAR(36) PRIMARY KEY, `name` VARCHAR(50) NOT NULL, `containsAlcohol` BOOLEAN DEFAULT false, `pump` INT NOT NULL, `fillLevel` INT NOT NULL, `fillCapacity` INT NOT NULL);").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `users` (`userId` VARCHAR(45) PRIMARY KEY, `isAdult` BOOLEAN DEFAULT false);").execute();
        } catch (SQLException ignored) {}
    }
}
