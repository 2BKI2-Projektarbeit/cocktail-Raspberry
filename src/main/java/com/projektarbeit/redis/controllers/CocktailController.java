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

    /**
     * Loads information of a specified cocktail from database.
     * @param cocktailId Cocktail ID to be loaded.
     * @return Cocktail Object with information.
     */
    public static Cocktail fetchCocktail(UUID cocktailId) {
        Cocktail cocktail;

        Main.debug("Fetching information of cocktail " + cocktailId + "...");

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
                Main.debug("Successfully fetched cocktail data!");
                return cocktail;
            }
        } catch (SQLException ignored) {
            Main.debug("An error occurred while fetching cocktail data!");
        }

        return null;
    }

    /**
     * Starts making a cocktail.
     * @param object Content of incoming message.
     * @return Nothing.
     */
    public static void start(JSONObject object) {
        if(ready) {
            Cocktail cocktail = fetchCocktail(UUID.fromString(object.getString("cocktail_id")));
            CocktailSize size = CocktailSize.valueOf(object.getString("cocktail_size"));

            Main.debug("Starting making cocktail " + cocktail.getCocktailId() + " in size " + size.toString() + "...");

            thread = new Thread(() -> {
                ready = false;
                CommunicationManager.activeActions.put("make_cocktail", UUID.fromString(object.getString("action_id")));
                LightController.sendRGB(SettingsController.splitColor(SettingsController.getInProgressLight()));

                confirmStart(UUID.fromString(object.getString("action_id")));

                try {
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    Main.debug("An error occurred while making cocktail!");
                    ex.printStackTrace();
                }

                cocktail.getIngredients().forEach((ingredient, ml) -> {
                    try {
                        int milliseconds = MachineController.berechneMillisekunden(ml);

                        if(size == CocktailSize.HALF)
                            milliseconds /= 2;

                        CommunicationManager.sendToArduino("p:" + ingredient.getPump() + ":" + milliseconds);
                        Thread.sleep(milliseconds + 1000);

                        DatabaseManager.getConnection().prepareStatement("UPDATE `ingredients` SET `fillLevel` = `fillLevel` -" + ml + " WHERE `ingredientId`='" + ingredient.getIngredientId() + "'").execute();
                    } catch (Exception ex) {
                        Main.debug("An error occurred while making cocktail!");
                        ex.printStackTrace();
                    }
                });

                Main.debug("Successfully finished cocktail making!");
                finishedCocktail(UUID.fromString(object.getString("action_id")));
            });
            thread.start();
        }
    }

    /**
     * Confirms the start to app.
     * @param actionId Action to be confirmed.
     * @return Nothing.
     */
    public static void confirmStart(UUID actionId) {
        JSONObject message = new JSONObject();

        Main.debug("Confirming start of cocktail making process...");

        try {
            message.put("action", "make_cocktail_confirmation");
            message.put("action_id", actionId.toString());
        } catch(JSONException ignored) {
            Main.debug("An error occurred while confirming cocktail making process!");
        }

        CommunicationManager.publishMessage(message);
        Main.debug("Successfully confirmed cocktail making process!");
    }

    /**
     * Notifies app that cocktail is finished
     * @param actionId Action to be finished.
     * @return Nothing.
     */
    public static void finishedCocktail(UUID actionId) {
        JSONObject message = new JSONObject();

        try {
            message.put("action", "make_cocktail_finished");
            message.put("action_id", actionId.toString());
        } catch (JSONException ignored) {}

        CommunicationManager.publishMessage(message);
        ready = true;
        CommunicationManager.activeActions.remove("make_cocktail");
        LightController.sendRGB(SettingsController.splitColor(SettingsController.getSuccessLight()));

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                LightController.sendRGB(SettingsController.splitColor(SettingsController.getIdleLight()));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Notifies app that cocktail is cancelled
     * @param actionId Action to be cancelled.
     * @return Nothing.
     */
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
        LightController.sendRGB(SettingsController.splitColor(SettingsController.getErrorLight()));

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                LightController.sendRGB(SettingsController.splitColor(SettingsController.getIdleLight()));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
