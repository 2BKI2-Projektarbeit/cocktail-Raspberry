package com.projektarbeit.redis.controllers;

import com.projektarbeit.mysql.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsController {

    /**
     * Checks if app is in maintenance mode.
     * @return boolean Returns true if maintenance mode is enabled.
     */
    public static boolean isMaintenance() {
        try {
            ResultSet result = DatabaseManager.getConnection().prepareStatement("SELECT * FROM settings WHERE `key` = 'maintenance'").executeQuery();

            if(result.next())
                return result.getBoolean("value");
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set maintenance mode state.
     * @param maintenance True if maintenance mode should be enabled.
     * @return Nothing.
     */
    public static void setMaintenance(boolean maintenance) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'maintenance'");
            statement.setString(1, String.valueOf(maintenance));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns if age should be checked on alcoholic cocktails.
     * @return boolean True if should be checked.
     */
    public static boolean alcoholAgeCheck() {
        try {
            ResultSet result = DatabaseManager.getConnection().prepareStatement("SELECT * FROM settings WHERE `key` = 'alcohol_age-check'").executeQuery();

            if(result.next())
                return result.getBoolean("value");
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sets alcoholic age check.
     * @param check True if check should be enabled.
     * @return Nothing.
     */
    public static void setAlcoholAgeCheck(boolean check) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'alcohol_age-check'");
            statement.setString(1, String.valueOf(check));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns idle light string.
     * @return string
     */
    public static String getIdleLight() {
        try {
            ResultSet result = DatabaseManager.getConnection().prepareStatement("SELECT * FROM settings WHERE `key` = 'light_idle'").executeQuery();

            if(result.next())
                return result.getString("value");
            else
                return "255:255:255";
        } catch (SQLException e) {
            e.printStackTrace();
            return "255:255:255";
        }
    }

    /**
     * Sets color for idle light.
     * @param red Value for red color between 0 and 255.
     * @param green Value for green color between 0 and 255.
     * @param blue Value for blue color between 0 and 255.
     * @return Nothing.
     */
    public static void setIdleLight(int red, int green, int blue) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'light_idle'");
            statement.setString(1, red + ":" + green + ":" + blue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns in progess light string.
     * @return string
     */
    public static String getInProgressLight() {
        try {
            ResultSet result = DatabaseManager.getConnection().prepareStatement("SELECT * FROM settings WHERE `key` = 'light_in-progress'").executeQuery();

            if(result.next())
                return result.getString("value");
            else
                return "255:255:255";
        } catch (SQLException e) {
            e.printStackTrace();
            return "255:255:255";
        }
    }

    /**
     * Sets color for in progess light.
     * @param red Value for red color between 0 and 255.
     * @param green Value for green color between 0 and 255.
     * @param blue Value for blue color between 0 and 255.
     * @return Nothing.
     */
    public static void setInProgressLight(int red, int green, int blue) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'light_in-progress'");
            statement.setString(1, red + ":" + green + ":" + blue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns success light string.
     * @return string
     */
    public static String getSuccessLight() {
        try {
            ResultSet result = DatabaseManager.getConnection().prepareStatement("SELECT * FROM settings WHERE `key` = 'light_success'").executeQuery();

            if(result.next())
                return result.getString("value");
            else
                return "255:255:255";
        } catch (SQLException e) {
            e.printStackTrace();
            return "255:255:255";
        }
    }

    /**
     * Sets color for success light.
     * @param red Value for red color between 0 and 255.
     * @param green Value for green color between 0 and 255.
     * @param blue Value for blue color between 0 and 255.
     * @return Nothing.
     */
    public static void setSuccessLight(int red, int green, int blue) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'light_success'");
            statement.setString(1, red + ":" + green + ":" + blue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns error light string.
     * @return string
     */
    public static String getErrorLight() {
        try {
            ResultSet result = DatabaseManager.getConnection().prepareStatement("SELECT * FROM settings WHERE `key` = 'light_error'").executeQuery();

            if(result.next())
                return result.getString("value");
            else
                return "255:255:255";
        } catch (SQLException e) {
            e.printStackTrace();
            return "255:255:255";
        }
    }

    /**
     * Sets color for error light.
     * @param red Value for red color between 0 and 255.
     * @param green Value for green color between 0 and 255.
     * @param blue Value for blue color between 0 and 255.
     * @return Nothing.
     */
    public static void setErrorLight(int red, int green, int blue) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'light_error'");
            statement.setString(1, red + ":" + green + ":" + blue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Splits color string to int array.
     * @param color string
     * @return int array
     */
    public static int[] splitColor(String color) {
        int[] rgb = new int[3];

        String[] colors = color.split(":");
        rgb[0] = Integer.valueOf(colors[0]);
        rgb[1] = Integer.valueOf(colors[1]);
        rgb[2] = Integer.valueOf(colors[2]);

        return rgb;
    }
}
