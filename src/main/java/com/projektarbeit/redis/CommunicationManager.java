package com.projektarbeit.redis;

import com.projektarbeit.objects.Cocktail;
import com.projektarbeit.redis.controllers.CocktailController;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static boolean establishConnection() {
        jedisPub = new Jedis("127.0.0.1");
        jedisSub = new Jedis("127.0.0.1");

        try {
            jedisPub.ping();
            jedisSub.ping();

            return true;
        } catch (JedisConnectionException ex) {
            return false;
        }
    }

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

    public static void setupSubscriber() {
        Thread t = new Thread(() -> jedisSub.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    JSONObject object = new JSONObject(message);
                    System.out.println("[REDIS] Message received: " + object.toString());

                    if(object.getJSONObject("to").getString("type").equalsIgnoreCase("controller")) {
                        if(object.getString("action").equalsIgnoreCase("make_cocktail_start")) {
                            UUID cocktailId = UUID.fromString(object.getString("cocktail_id"));
                            if(CocktailController.ready)
                                CocktailController.start(cocktailId);
                        }
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }, "general"), "subscriberThread");

        t.start();
    }
}
