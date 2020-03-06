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

    public UUID getCocktailId() {
        return cocktailId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<Ingredient, Integer> getIngredients() {
        return ingredients;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}