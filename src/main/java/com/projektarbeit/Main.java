package com.projektarbeit;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.projektarbeit.mysql.DatabaseManager;
import com.projektarbeit.redis.CommunicationManager;
import com.projektarbeit.redis.controllers.*;

import java.io.IOException;
import java.util.UUID;

public class Main {

    public static final int I2C_ADDRESS = 0x2c;
    public static I2CDevice device;

    public static void main(String[] args) throws I2CFactory.UnsupportedBusNumberException, IOException {
        DatabaseManager.connect();
        CommunicationManager.establishConnection();
        CommunicationManager.setupSubscriber();

        IngredientController.getIngredients();

        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
        device = i2c.getDevice(I2C_ADDRESS);

        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput cancelButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
        cancelButton.setShutdownOptions(true);

        cancelButton.addListener((GpioPinListenerDigital) event -> {
            debug("Triggered cancel interrupt...");

            if(CommunicationManager.activeActions.containsKey("make_cocktail")) {
                UUID actionId;

                if(CommunicationManager.activeActions.containsKey("")) {
                    actionId = CommunicationManager.activeActions.get("make_cocktail");
                    CocktailController.cancelledCocktail(actionId);
                } else if(CommunicationManager.activeActions.containsKey("machine_clean")) {
                    actionId = CommunicationManager.activeActions.get("machine_clean");
                    MachineController.cleaningCancelled(actionId);
                }
            }
        });

        LightController.sendRGB(SettingsController.splitColor(SettingsController.getIdleLight()));
    }

    public static void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }
}
