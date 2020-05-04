package com.projektarbeit.redis.controllers;

import com.projektarbeit.mysql.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsController {

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

    public static void setMaintenance(boolean maintenance) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'maintenance'");
            statement.setString(1, String.valueOf(maintenance));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static void setAlcoholAgeCheck(boolean check) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'alcohol_age-check'");
            statement.setString(1, String.valueOf(check));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static void setIdleLight(int red, int green, int blue) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'light_idle'");
            statement.setString(1, red + ":" + green + ":" + blue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static void setInProgressLight(int red, int green, int blue) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'light_in-progress'");
            statement.setString(1, red + ":" + green + ":" + blue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static void setSuccessLight(int red, int green, int blue) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'light_success'");
            statement.setString(1, red + ":" + green + ":" + blue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static void setErrorLight(int red, int green, int blue) {
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("UPDATE settings SET `value` = ? WHERE `key` = 'light_error'");
            statement.setString(1, red + ":" + green + ":" + blue);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int[] splitColor(String color) {
        int[] rgb = new int[3];

        String[] colors = color.split(":");
        rgb[0] = Integer.valueOf(colors[0]);
        rgb[1] = Integer.valueOf(colors[1]);
        rgb[2] = Integer.valueOf(colors[2]);

        return rgb;
    }
}
