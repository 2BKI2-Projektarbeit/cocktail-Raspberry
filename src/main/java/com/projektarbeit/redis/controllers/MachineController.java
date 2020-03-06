package com.projektarbeit.redis.controllers;

public class MachineController {

    public static final int pumpleistung = 2000;

    public static int berechneMillisekunden(int ml) {
        return (60000 / pumpleistung) * ml;
    }
}
