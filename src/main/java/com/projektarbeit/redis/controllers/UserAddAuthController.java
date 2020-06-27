package com.projektarbeit.redis.controllers;

import com.projektarbeit.Main;
import com.projektarbeit.redis.CommunicationManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class UserAddAuthController {

    private static Thread thread;

    /**
     * Starts scanning for RFID-Tag to add.
     * @param object Content of message.
     * @return Nothing.
     */
    public static void start(JSONObject object) {
        if(CommunicationManager.activeActions.containsKey("admin_auth")) {
            AdminAuthController.cancelOut(CommunicationManager.activeActions.get("admin_auth"));
        }

        if(CommunicationManager.activeActions.containsKey("maintenance_auth")) {
            MaintenanceAuthController.cancelOut(CommunicationManager.activeActions.get("maintenance_auth"));
        }

        if(CommunicationManager.activeActions.containsKey("confirm_age")) {
            ConfirmAgeController.cancelOut(CommunicationManager.activeActions.get("confirm_age"));
        }

        if(CommunicationManager.activeActions.containsKey("user_add_auth")) {
            cancelOut(CommunicationManager.activeActions.get("user_add_auth"));
        }

        if(CommunicationManager.activeActions.containsKey("user_edit_auth")) {
            UserEditAuthController.cancelOut(CommunicationManager.activeActions.get("user_edit_auth"));
        }

        CommunicationManager.activeActions.put("user_add_auth", UUID.fromString(object.getString("action_id")));

        try {
            Main.device.write("clr:rfid".getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        thread = new Thread(() -> {
            Main.debug("Starting RFID Identification");

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
                        UUID actionId = CommunicationManager.activeActions.get("user_add_auth");

                        try {
                            message.put("action", "user_add_auth_response");
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

    /**
     * Handles cancel message.
     * @return Nothing.
     */
    public static void cancelIn() {
        if(CommunicationManager.activeActions.containsKey("user_add_auth")) {
            thread.stop();
            CommunicationManager.activeActions.remove("user_add_auth");
        }
    }

    /**
     * Notifies all apps that authentication has been cancelled.
     * @param actionId Action ID to be cancelled.
     * @return Nothing.
     */
    public static void cancelOut(UUID actionId) {
        if(CommunicationManager.activeActions.containsValue(actionId)) {
            thread.stop();

            JSONObject message = new JSONObject();

            try {
                message.put("action", "user_add_auth_response");
                message.put("action_id", actionId.toString());
                message.put("response", "cancel");
            } catch(JSONException ignored) {}

            CommunicationManager.publishMessage(message);
            CommunicationManager.activeActions.remove("user_add_auth");
        }
    }

    /**
     * Stops authentication thread.
     * @return Nothing.
     */
    public static void finish() {
        thread.stop();
        CommunicationManager.activeActions.remove("user_add_auth");
    }
}
