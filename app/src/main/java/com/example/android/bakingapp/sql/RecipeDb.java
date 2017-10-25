package com.example.android.bakingapp.sql;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RecipeDb extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "recipesDb";

    public RecipeDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("Provider","database is created");
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + Contracts.RecipesEntry.TABLE_NAME + "("
                + Contracts.RecipesEntry._ID + " INTEGER PRIMARY KEY " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_NAME + " TEXT " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_INGREDIENTS + " INTEGER" + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_STEPS + " INTEGER " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_SERVINGS + " INTEGER " + ","
                + Contracts.RecipesEntry.COLUMN_RECIPE_THUMBNAIL + " TEXT "
                + ")";
        String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + Contracts.IngredientsEntry.TABLE_NAME + "("
                + Contracts.IngredientsEntry._ID + " INTEGER PRIMARY KEY " + ","
                + Contracts.IngredientsEntry.COLUMN_INGREDIENTS_RECIPE_ID + " INTEGER " + ","
                + Contracts.IngredientsEntry.COLUMN_INGREDIENTS_NAME + " TEXT" + ","
                + Contracts.IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY + " INTEGER " + ","
                + Contracts.IngredientsEntry.COLUMN_INGREDIENTS_MEASURE + " TEXT "
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

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.RecipesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.IngredientsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.StepsEntry.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
}
