package com.projektarbeit;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.projektarbeit.mysql.DatabaseManager;
import com.projektarbeit.objects.Ingredient;
import com.projektarbeit.redis.CommunicationManager;
import com.projektarbeit.redis.controllers.CocktailController;
import com.projektarbeit.redis.controllers.IngredientController;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class Main {

    public static final int I2C_ADDRESS = 0x2c;
    public static I2CDevice device;

    public static void main(String[] args) throws InterruptedException, I2CFactory.UnsupportedBusNumberException, IOException {
        DatabaseManager.connect();
        CommunicationManager.establishConnection();
        CommunicationManager.setupSubscriber();

        IngredientController.getIngredients();

        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
        device = i2c.getDevice(I2C_ADDRESS);

        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput cancelButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
        cancelButton.setShutdownOptions(true);

        cancelButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                System.out.println("INTERRUPT");
                if(CommunicationManager.activeActions.containsKey("make_cocktail")) {
                    UUID actionId = CommunicationManager.activeActions.get("make_cocktail");

                    CocktailController.cancelledCocktail(actionId);
                }
            }
        });

        /*new Thread(() -> {
            while(true) {
                try {
                    Random random = new Random();

                    String message = "rgb:" + random.nextInt((255 - 0) + 1) + ":" + random.nextInt((255 - 0) + 1) + ":" + random.nextInt((255 - 0) + 1);

                    Main.device.write(message.getBytes());

                    Thread.sleep(5000);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();*/
    }
}
