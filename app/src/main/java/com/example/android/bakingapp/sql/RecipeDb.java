package com.example.android.bakingapp.sql;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDb extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "recipesDb";
    public static final int DATABASE_VERSION = 1;

    public RecipeDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + Contracts.RecipesEntry.TABLE_NAME + "("
                + Contracts.RecipesEntry._ID + " INTEGER PRIMARY KEY " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_NAME + " TEXT " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_INGREDIENTS + " INTEGER" + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_STEPS + " INTEGER " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_SERVINGS + " INTEGER " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_LAST_TIME_USED + " INTEGER " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_THUMBNAIL + " TEXT "
                + ")";
        String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + Contracts.IngredientsEntry.TABLE_NAME + "("
                + Contracts.IngredientsEntry._ID + " INTEGER PRIMARY KEY " + ","
                + Contracts.IngredientsEntry.COLUMN_INGREDIENT_RECIPE_ID + " INTEGER " + ","
                + Contracts.IngredientsEntry.COLUMN_INGREDIENT_NAME + " TEXT" + ","
                + Contracts.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY + " INTEGER " + ","
                + Contracts.IngredientsEntry.COLUMN_INGREDIENT_MEASURE + " TEXT "
                + ")";
        String CREATE_STEPS_TABLE = "CREATE TABLE " + Contracts.StepsEntry.TABLE_NAME + "("
                + Contracts.StepsEntry._ID + " INTEGER PRIMARY KEY " + ","
                + Contracts.StepsEntry.COLUMN_STEP_RECIPE_ID + " INTEGER " + ","
                + Contracts.StepsEntry.COLUMN_STEP_SHORT_DESCRIPTION + " TEXT" + ","
                + Contracts.StepsEntry.COLUMN_STEP_DESCRIPTION + " TEXT " + ","
                + Contracts.StepsEntry.COLUMN_STEP_VIDEO_URL + " TEXT " + ","
                + Contracts.StepsEntry.COLUMN_STEP_THUMBNAIL_URL + " TEXT "
                + ")";
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if exist
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.RecipesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.IngredientsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.StepsEntry.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
}
