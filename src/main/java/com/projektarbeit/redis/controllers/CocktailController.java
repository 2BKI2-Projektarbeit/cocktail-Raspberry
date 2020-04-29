package com.projektarbeit.redis.controllers;

import com.projektarbeit.Main;
import com.projektarbeit.enums.CocktailSize;
import com.projektarbeit.mysql.DatabaseManager;
import com.projektarbeit.objects.Cocktail;
import com.projektarbeit.objects.Ingredient;
import com.projektarbeit.redis.CommunicationManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class CocktailController {

    public static boolean ready = true;
    public static Thread thread;

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

    public static void start(JSONObject object) {
        if(ready) {
            Cocktail cocktail = fetchCocktail(UUID.fromString(object.getString("cocktail_id")));
            CocktailSize size = CocktailSize.valueOf(object.getString("cocktail_size"));

            thread = new Thread(() -> {
                ready = false;
                CommunicationManager.activeActions.put("make_cocktail", UUID.fromString(object.getString("action_id")));

                confirmStart(UUID.fromString(object.getString("action_id")));

                try {
                    Thread.sleep(2000);
                } catch (Exception ignored) {}

                cocktail.getIngredients().forEach((ingredient, ml) -> {
                    try {
                        int milliseconds = MachineController.berechneMillisekunden(ml);

                        if(size == CocktailSize.HALF)
                            milliseconds /= 2;

                        String message = "p:" + ingredient.getPump() + ":" + milliseconds;

                        Main.device.write(message.getBytes());
                        Thread.sleep(milliseconds + 1000);

                        DatabaseManager.getConnection().prepareStatement("UPDATE `ingredients` SET `fillLevel` = `fillLevel` -" + ml + " WHERE `ingredientId`='" + ingredient.getIngredientId() + "'").execute();
                    } catch (Exception ignored) {}
                });

                finishedCocktail(UUID.fromString(object.getString("action_id")));

            });
            thread.start();
        }
    }

    public static void confirmStart(UUID actionId) {
        JSONObject message = new JSONObject();

        try {
            message.put("action", "make_cocktail_confirmation");
            message.put("action_id", actionId.toString());
        } catch(JSONException ignored) {}

        CommunicationManager.publishMessage(message);
    }

    public static void finishedCocktail(UUID actionId) {
        JSONObject message = new JSONObject();

        try {
            message.put("action", "make_cocktail_finished");
            message.put("action_id", actionId.toString());
        } catch (JSONException ignored) {}

        CommunicationManager.publishMessage(message);
        ready = true;
        CommunicationManager.activeActions.remove("make_cocktail");
    }

    public static void cancelledCocktail(UUID actionId) {
        thread.stop();

        JSONObject message = new JSONObject();

        try {
            message.put("action", "make_cocktail_cancelled");
            message.put("action_id", actionId.toString());
        } catch (JSONException ignored) {}

        CommunicationManager.publishMessage(message);
        ready = true;
        CommunicationManager.activeActions.remove("make_cocktail");
    }
}
