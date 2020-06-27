package com.projektarbeit.objects;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Cocktail {
    private UUID cocktailId;
    private String name;
    private String description;
    private HashMap<Ingredient, Integer> ingredients;
    private boolean enabled;
    private Date createdAt;

    public Cocktail(UUID cocktailId, String name, String description, HashMap<Ingredient, Integer> ingredients, boolean enabled, Date createdAt) {
        this.cocktailId = cocktailId;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    /**
     * Returns cocktail id.
     * @return UUID
     */
    public UUID getCocktailId() {
        return cocktailId;
    }

    /**
     * Returns cocktail name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns cocktail description.
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns list of ingredients.
     * @return HashMap<Ingredient, Integer> Returns ingredient and amount.
     */
    public HashMap<Ingredient, Integer> getIngredients() {
        return ingredients;
    }

    /**
     * Returns if cocktail is enabled.
     * @return boolean Returns true if cocktail is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns cocktail creation date.
     * @return Date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
}