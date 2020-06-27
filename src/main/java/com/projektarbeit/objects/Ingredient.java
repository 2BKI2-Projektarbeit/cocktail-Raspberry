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

    /**
     * Returns ingredient id.
     * @return UUID.
     */
    public UUID getIngredientId() {
        return ingredientId;
    }

    /**
     * Returns ingredient name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns if ingredient contains alcohol.
     * @return boolean Returns true if ingredient contains alcohol.
     */
    public boolean containsAlcohol() {
        return containsAlcohol;
    }

    /**
     * Returns ingredient pump id.
     * @return int
     */
    public int getPump() {
        return pump;
    }

    /**
     * Returns ingredient fill level.
     * @return int Returns level in ml.
     */
    public int getFillLevel() {
        return fillLevel;
    }

    /**
     * Returns ingredient fill capacity.
     * @return int Returns capacity in ml.
     */
    public int getFillCapacity() {
        return fillCapacity;
    }
}