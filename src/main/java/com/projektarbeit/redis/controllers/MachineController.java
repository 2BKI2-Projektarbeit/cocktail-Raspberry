package com.projektarbeit.redis.controllers;

import com.projektarbeit.redis.CommunicationManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MachineController {

    public static final int pumpleistung = 2000;

    /**
     * Calculates milliseconds by pump power and wanted milliliters.
     * @param ml Wanted milliliters.
     * @return int Amount of milliseconds pump has to be turned on.
     */
    public static int berechneMillisekunden(int ml) {
        return (60000 / pumpleistung) * ml;
    }

    /**
     * Starts cleaning process.
     * @param object Incoming message object.
     * @return Nothing.
     */
    public static void startCleaning(JSONObject object) {
        if(CocktailController.ready) {
            CocktailController.thread = new Thread(() -> {
                CocktailController.ready = false;
                CommunicationManager.activeActions.put("machine_clean", UUID.fromString(object.getString("action_id")));
                LightController.sendRGB(SettingsController.splitColor(SettingsController.getInProgressLight()));

                confirmCleaning(UUID.fromString(object.getString("action_id")));

                try {
                    Thread.sleep(2000);
                } catch (Exception ignored) {}

                int milliseconds = MachineController.berechneMillisekunden(100);

                for(int i = 1; i <= 6; i++) {
                    try {
                        CommunicationManager.sendToArduino("p:" + i + ":" + milliseconds);
                        Thread.sleep(milliseconds + 1000);
                    } catch (Exception ex) {}
                }

                cleaningFinished(UUID.fromString(object.getString("action_id")));

            });
            CocktailController.thread.start();
        }
    }

    /**
     * Confirms start of cleaning process to app.
     * @param actionId Action ID to be confirmed.
     * @return Nothing.
     */
    public static void confirmCleaning(UUID actionId) {
        JSONObject message = new JSONObject();

        try {
            message.put("action", "machine_clean_confirmation");
            message.put("action_id", actionId.toString());
        } catch(JSONException ignored) {}

        CommunicationManager.publishMessage(message);
    }

    /**
     * Notifies app that cleaning is finished
     * @param actionId Action ID to be finished.
     * @return Nothing.
     */
    public static void cleaningFinished(UUID actionId) {
        JSONObject message = new JSONObject();

        try {
            message.put("action", "machine_clean_finished");
            message.put("action_id", actionId.toString());
        } catch (JSONException ignored) {}

        CommunicationManager.publishMessage(message);
        CocktailController.ready = true;
        CommunicationManager.activeActions.remove("machine_clean");
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
     * Notifies app that cleaning is cancelled.
     * @param actionId Action ID to be cancelled.
     * @return Nothing.
     */
    public static void cleaningCancelled(UUID actionId) {
        CocktailController.thread.stop();

        JSONObject message = new JSONObject();

        try {
            message.put("action", "machine_clean_cancelled");
            message.put("action_id", actionId.toString());
        } catch (JSONException ignored) {}

        CommunicationManager.publishMessage(message);
        CocktailController.ready = true;
        CommunicationManager.activeActions.remove("machine_clean");
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
