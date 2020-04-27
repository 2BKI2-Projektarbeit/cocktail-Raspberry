package com.projektarbeit.redis.controllers;

import com.projektarbeit.Main;
import com.projektarbeit.redis.CommunicationManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AdminAuthController {

    private static Thread thread;

    public static void start(JSONObject object) {
        if(CommunicationManager.activeActions.containsKey("admin_auth")) {
            cancelOut(CommunicationManager.activeActions.get("admin_auth"));
        }

        CommunicationManager.activeActions.put("admin_auth", UUID.fromString(object.getString("action_id")));

        try {
            Main.device.write("clr:rfid".getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        thread = new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            while(true) {
                try {
                    byte[] bytes = new byte[4];
                    Main.device.read(0x2c, bytes, 0, 4);

                    long value = 0;
                    for (int i = 0; i < bytes.length; i++)
                    {
                        value = (value << 8) + (bytes[i] & 0xff);
                    }

                    if(value != 16777215L) {
                        JSONObject message = new JSONObject();
                        UUID actionId = CommunicationManager.activeActions.get("admin_auth");

                        try {
                            message.put("action", "admin_auth_response");
                            message.put("action_id", actionId.toString());
                            message.put("response", JSONObject.valueToString(value));
                        } catch(JSONException ignored) {}

                        CommunicationManager.publishMessage(message);
                    }

                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void cancelIn() {
        if(CommunicationManager.activeActions.containsKey("admin_auth")) {
            thread.stop();
            CommunicationManager.activeActions.remove("admin_auth");
        }
    }

    public static void cancelOut(UUID actionId) {
        if(CommunicationManager.activeActions.containsValue(actionId)) {
            thread.stop();

            JSONObject message = new JSONObject();

            try {
                message.put("action", "admin_auth_response");
                message.put("action_id", actionId.toString());
                message.put("response", "cancel");
            } catch(JSONException ignored) {}

            CommunicationManager.publishMessage(message);
            CommunicationManager.activeActions.remove("admin_auth");
        }
    }

    public static void finish() {
        thread.stop();
        CommunicationManager.activeActions.remove("admin_auth");
    }
}
