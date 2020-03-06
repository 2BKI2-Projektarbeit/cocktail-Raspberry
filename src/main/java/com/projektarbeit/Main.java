package com.projektarbeit;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.projektarbeit.mysql.DatabaseManager;
import com.projektarbeit.objects.Ingredient;
import com.projektarbeit.redis.CommunicationManager;
import com.projektarbeit.redis.controllers.IngredientController;

import java.io.IOException;

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
    }
}
