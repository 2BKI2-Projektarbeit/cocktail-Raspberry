package com.projektarbeit.redis.controllers;

import com.projektarbeit.redis.CommunicationManager;

public class LightController {

    public static void sendRGB(int r, int g, int b) {
        CommunicationManager.sendToArduino("rgb:" + r + ":" + g + ":" + b);
    }

    public static void sendRGB(int[] rgb) {
        sendRGB(rgb[0], rgb[1], rgb[2]);
    }
}
