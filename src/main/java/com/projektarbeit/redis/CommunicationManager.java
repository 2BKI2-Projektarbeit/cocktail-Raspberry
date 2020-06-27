package com.projektarbeit.redis;

import com.projektarbeit.Main;
import com.projektarbeit.objects.Cocktail;
import com.projektarbeit.redis.controllers.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class CommunicationManager {

    public static UUID uuid = UUID.randomUUID();
    public static HashMap<String, UUID> activeActions = new HashMap<>();

    private static Jedis jedisPub;
    private static Jedis jedisSub;

    /**
     * Establishes connection to redis server.
     * @return boolean Returns true if connection was successful.
     */
    public static boolean establishConnection() {
        Main.debug("Establishing redis connection...");

        jedisPub = new Jedis("127.0.0.1");
        jedisSub = new Jedis("127.0.0.1");

        try {
            jedisPub.ping();
            jedisSub.ping();

            Main.debug("Successfully established connection to redis server!");

            return true;
        } catch (JedisConnectionException ex) {
            Main.debug("Error while establishing connection to redis server!");
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Publishes message to a specified redis client.
     * @param uuid ID to send message to.
     * @param object Message to publish.
     * @return Nothing.
     */
    public static void publishMessage(UUID uuid, JSONObject object) {
        JSONObject sender = new JSONObject();
        JSONObject to = new JSONObject();

        try {
            sender.put("uuid", JSONObject.NULL);
            sender.put("type", "controller");

            to.put("uuid", uuid);
            to.put("type", "app_android");

            object.put("sender", sender);
            object.put("to", to);
        } catch (JSONException ignored) {}

        jedisPub.publish("general", object.toString());
        System.out.println("[REDIS] Message sent: " + object.toString());
    }

    /**
     * Publishes message to all redis clients.
     * @param object Message to publish.
     * @return Nothing.
     */
    public static void publishMessage(JSONObject object) {
        JSONObject sender = new JSONObject();
        JSONObject to = new JSONObject();

        try {
            sender.put("uuid", JSONObject.NULL);
            sender.put("type", "controller");

            to.put("uuid", "broadcast");
            to.put("type", "app_android");

            object.put("sender", sender);
            object.put("to", to);
        } catch (JSONException ignored) {}

        jedisPub.publish("general", object.toString());
        System.out.println("[REDIS] Message sent: " + object.toString());
    }

    /**
     * Sets up subscriber for redis messages.
     * @return Nothing.
     */
    public static void setupSubscriber() {
        Main.debug("Setting up redis subscriber...");

        Thread t = new Thread(() -> jedisSub.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    JSONObject object = new JSONObject(message);

                    if(object.getJSONObject("to").getString("type").equalsIgnoreCase("controller") || (object.getJSONObject("to").getString("uuid").equalsIgnoreCase("broadcast") && !(object.getJSONObject("sender").getString("type").equalsIgnoreCase("controller")))) {
                        System.out.println("[REDIS] Message received: " + object.toString());

                        switch (object.getString("action")) {
                            case "make_cocktail_start":
                                CocktailController.start(object);
                                break;

                            case "machine_clean":
                                MachineController.startCleaning(object);
                                break;

                            case "admin_auth_start":
                                AdminAuthController.start(object);
                                break;

                            case "admin_auth_cancel":
                                AdminAuthController.cancelIn();
                                break;

                            case "admin_auth_finish":
                                AdminAuthController.finish();
                                break;

                            case "maintenance_auth_start":
                                MaintenanceAuthController.start(object);
                                break;

                            case "maintenance_auth_cancel":
                                MaintenanceAuthController.cancelIn();
                                break;

                            case "maintenance_auth_finish":
                                MaintenanceAuthController.finish();
                                break;

                            case "confirm_age_start":
                                ConfirmAgeController.start(object);
                                break;

                            case "confirm_age_cancel":
                                ConfirmAgeController.cancelIn();
                                break;

                            case "confirm_age_finish":
                                ConfirmAgeController.finish();
                                break;

                            case "user_add_auth_start":
                                UserAddAuthController.start(object);
                                break;

                            case "user_add_auth_cancel":
                                UserAddAuthController.cancelIn();
                                break;

                            case "user_add_auth_finish":
                                UserAddAuthController.finish();
                                break;

                            case "user_edit_auth_start":
                                UserEditAuthController.start(object);
                                break;

                            case "user_edit_auth_cancel":
                                UserEditAuthController.cancelIn();
                                break;

                            case "user_edit_auth_finish":
                                UserEditAuthController.finish();
                                break;

                        }
                    }
                } catch (JSONException ex) {
                    Main.debug("Error while processing received message!");
                    ex.printStackTrace();
                }
            }
        }, "general"), "subscriberThread");

        t.start();
    }

    /**
     * Sends message to arduino.
     * @param message Message to be sent.
     * @return Nothing.
     */
    public static void sendToArduino(String message) {
        try {
            Main.device.write(message.getBytes());
            System.out.println("[I2C] Message sent: " + message);
        } catch (IOException ex) {
            Main.debug("Error while sending I2C message!");
            ex.printStackTrace();
        }
    }
}
