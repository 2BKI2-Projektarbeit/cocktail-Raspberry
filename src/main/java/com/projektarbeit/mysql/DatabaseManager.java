package com.projektarbeit.mysql;

import com.projektarbeit.Main;

import java.sql.*;

public class DatabaseManager {

    private static Connection connection;

    /**
     * Establishes connection to MySQL server.
     * @return boolean Returns true if connection was successful.
     */
    public static boolean connect() {
        try {
            Main.debug("Connecting to MySQL database...");

            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/projektarbeit", "projektarbeit", "test123");

            createTables();

            Main.debug("Successfully connected to database!");
            return true;
        } catch (Exception ex) {
            Main.debug("Failed connecting to database!");
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Returns connection to database.
     * @return Connection
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Creates needed tables for application.
     * @return Nothing.
     */
    public static void createTables() {
        try {
            Main.debug("Checking and createing tables if needed...");

            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `cocktails` (`cocktailId` VARCHAR(36) PRIMARY KEY, `name` VARCHAR(50) NOT NULL, `description` TEXT, `ingredients` TEXT, `enabled` BOOLEAN DEFAULT true, `createdAt` TIMESTAMP NOT NULL DEFAULT NOW());").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `ingredients` (`ingredientId` VARCHAR(36) PRIMARY KEY, `name` VARCHAR(50) NOT NULL, `containsAlcohol` BOOLEAN DEFAULT false, `pump` INT NOT NULL, `fillLevel` INT UNSIGNED NOT NULL, `fillCapacity` INT UNSIGNED NOT NULL);").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `users` (`userId` VARCHAR(45) PRIMARY KEY, `isAdult` BOOLEAN DEFAULT false, `isAdmin` BOOLEAN DEFAULT false);").execute();

            ResultSet resultSet = connection.getMetaData().getTables(null, null, "settings", null);

            if(!resultSet.next()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS `settings` (`key` VARCHAR(100), `value` VARCHAR(100));").execute();

                final PreparedStatement statement = connection.prepareStatement("INSERT INTO settings (`key`, `value`) VALUES (?, ?)");

                statement.setString(1, "maintenance");
                statement.setString(2, "false");
                statement.execute();

                statement.setString(1, "alcohol_age-check");
                statement.setString(2, "false");
                statement.execute();

                statement.setString(1, "light_idle");
                statement.setString(2, "66:144:245");
                statement.execute();

                statement.setString(1, "light_in-progress");
                statement.setString(2, "217:195:30");
                statement.execute();

                statement.setString(1, "light_success");
                statement.setString(2, "80:217:30");
                statement.execute();

                statement.setString(1, "light_error");
                statement.setString(2, "217:30:30");
                statement.execute();
            }

            Main.debug("Successfully checked tables!");
        } catch (SQLException ex) {
            Main.debug("An error occurred while checking and creating tables!");
            ex.printStackTrace();
        }
    }
}
