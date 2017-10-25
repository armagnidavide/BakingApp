package com.example.android.bakingapp;

import java.util.ArrayList;

public class Recipe {

    private int id;
    private String name;
    private ArrayList<Ingredient> ingredients;

    public int getIngredientsNumber() {
        return ingredientsNumber;
    }

    public void setIngredientsNumber(int ingredientsNumber) {
        this.ingredientsNumber = ingredientsNumber;
    }

    public int getStepsNumber() {
        return stepsNumber;
    }

    public void setStepsNumber(int stepsNumber) {
        this.stepsNumber = stepsNumber;
    }

    private int ingredientsNumber;
    private int stepsNumber;
    private ArrayList<Step> steps;
    private int servings;
    private String thumbnail;

    public Recipe() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return thumbnail;
    }

    public void setImage(String thumbnail) {
        this.thumbnail = thumbnail;
    }


}

