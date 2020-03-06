package com.projektarbeit.objects;

import java.util.UUID;

public class Ingredient {

    private UUID ingredientId;
    private String name;
    private boolean containsAlcohol;
    private int pump;
    private int fillLevel;
    private int fillCapacity;

    public Ingredient(UUID ingredientId, String name, boolean containsAlcohol, int pump, int fillLevel, int fillCapacity) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.containsAlcohol = containsAlcohol;
        this.pump = pump;
        this.fillLevel = fillLevel;
        this.fillCapacity = fillCapacity;
    }

    public String getName() {
        return name;
    }

    public boolean containsAlcohol() {
        return containsAlcohol;
    }

    public int getPump() {
        return pump;
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public int getFillCapacity() {
        return fillCapacity;
    }
}