package com.projektarbeit.redis.controllers;

import com.projektarbeit.Main;
import com.projektarbeit.redis.CommunicationManager;

public class LightController {

    /**
     * Tells arduino led strip color.
     * @param r Red value between 0 and 255.
     * @param g Green value between 0 and 255.
     * @param b Blue value between 0 and 255.
     * @return Nothing.
     */
    public static void sendRGB(int r, int g, int b) {
        Main.debug("Changing ambient light color to " + r + ":" + g + ":" + b + "...");
        CommunicationManager.sendToArduino("rgb:" + r + ":" + g + ":" + b);
    }

    /**
     * Tells arduino led strip color.
     * @param rgb Array of red, green and blue value between 0 and 255.
     * @return Nothing.
     */
    public static void sendRGB(int[] rgb) {
        sendRGB(rgb[0], rgb[1], rgb[2]);
    }
}
