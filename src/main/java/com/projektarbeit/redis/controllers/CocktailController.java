package com.projektarbeit.redis.controllers;

import com.projektarbeit.Main;
import com.projektarbeit.mysql.DatabaseManager;
import com.projektarbeit.objects.Cocktail;
import com.projektarbeit.objects.Ingredient;
import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.server.ExportException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class CocktailController {

    public static boolean ready = true;

    public static Cocktail fetchCocktail(UUID cocktailId) {
        Cocktail cocktail;

        try {
            ResultSet resultSet = DatabaseManager.getConnection().prepareStatement("SELECT * FROM `cocktails` WHERE `cocktailId`='" + cocktailId.toString() + "'").executeQuery();

            while(resultSet.next()) {
                JSONArray ingredients = new JSONArray(resultSet.getString("ingredients"));
                HashMap<Ingredient, Integer> ingredientsList = new HashMap<>();

                for (int i = 0; i < ingredients.length(); i++) {
                    JSONObject ingredient = ingredients.getJSONObject(i);

                    ingredientsList.put(IngredientController.ingredients.get(UUID.fromString(ingredient.getString("ingredientId"))), ingredient.getInt("amount"));
                }

                cocktail = new Cocktail(cocktailId, resultSet.getString("name"), resultSet.getString("description"), ingredientsList, resultSet.getBoolean("enabled"), resultSet.getDate("createdAt"));
                return cocktail;
            }
        } catch (SQLException ignored) {}

        return null;
    }

    public static void start(UUID cocktailId) {
        Cocktail cocktail = fetchCocktail(cocktailId);

        new Thread(() -> {
            ready = false;

            cocktail.getIngredients().forEach((ingredient, ml) -> {
                try {
                    int milliseconds = MachineController.berechneMillisekunden(ml);

                    String message = "p:" + ingredient.getPump() + ":" + milliseconds;

                    Main.device.write(message.getBytes());
                    Thread.sleep(milliseconds + 1000);
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            });

            ready = true;
        }).start();
    }
}
