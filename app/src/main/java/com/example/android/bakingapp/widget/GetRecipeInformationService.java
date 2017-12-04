package com.example.android.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.android.bakingapp.sql.Contracts;


public class GetRecipeInformationService extends IntentService {

    public static final String ACTION_SHOW_LAST_USED_RECIPE = "show_last_used_recipe";
    public static final String ACTION_SHOW_NEXT_RECIPE = "show_next_recipe";
    public static final String ACTION_SHOW_PREVIOUS_RECIPE = "show_previous_recipe";

    private int cursorCount;//the number of recipes inside the db

    public GetRecipeInformationService() {
        super("GetRecipeInformationService");
    }

    public static void showRecipeDataIntoTheWidget(Context context, String action) {
        Intent intent = new Intent(context, GetRecipeInformationService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void showRecipeDataIntoTheWidget(Context context, String action, int recipeId) {
        Intent intent = new Intent(context, GetRecipeInformationService.class);
        intent.setAction(action);
        intent.putExtra("recipeId", recipeId);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        int recipeId;
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SHOW_LAST_USED_RECIPE.equals(action)) {
                recipeId = intent.getIntExtra("recipeId", 1);
                handleActionShowRecipeDataIntoTheWidget(ACTION_SHOW_LAST_USED_RECIPE, recipeId);
            } else if (ACTION_SHOW_NEXT_RECIPE.equals(action)) {
                recipeId = intent.getIntExtra("recipeId", 1);
                handleActionShowRecipeDataIntoTheWidget(ACTION_SHOW_NEXT_RECIPE, recipeId);
            } else if (ACTION_SHOW_PREVIOUS_RECIPE.equals(action)) {
                recipeId = intent.getIntExtra("recipeId", 1);
                handleActionShowRecipeDataIntoTheWidget(ACTION_SHOW_PREVIOUS_RECIPE, recipeId);

            }
        }
    }

    private void handleActionShowRecipeDataIntoTheWidget(String action, int recipeId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        String recipeName;
        int recipeIngredientsNumber;
        int recipeStepsNumber;
        String recipeThumbnail;

        Cursor cursor = getCursor(action, recipeId);
        if (cursor != null&&cursor.getCount()>0) {
            cursor.moveToFirst();
            recipeName = cursor.getString(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_NAME));
            recipeIngredientsNumber = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_INGREDIENTS));
            recipeStepsNumber = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_STEPS));
            recipeId = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry._ID));
            recipeThumbnail = cursor.getString(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_THUMBNAIL));
            cursor.close();

            //update all widgets
            RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds,
                    recipeId,
                    recipeName,
                    recipeIngredientsNumber,
                    recipeStepsNumber,
                    recipeThumbnail);
        }
    }

    private Cursor getCursor(String action, int recipeId) {
        Cursor cursor;
        cursor = getContentResolver().query(
                Contracts.RecipesEntry.CONTENT_URI,
                null,
                null,
                null,
                Contracts.RecipesEntry.COLUMN_RECIPE_LAST_TIME_USED + " DESC ");
        if (cursor != null) cursorCount = cursor.getCount();
        if (action != null && (action.equals(ACTION_SHOW_PREVIOUS_RECIPE) || action.equals(ACTION_SHOW_NEXT_RECIPE))) {

            if (action.equals(GetRecipeInformationService.ACTION_SHOW_NEXT_RECIPE)) {
                if (recipeId == cursorCount) {
                    recipeId = 1;
                } else {
                    recipeId = recipeId + 1;
                }
            } else if (action.equals(GetRecipeInformationService.ACTION_SHOW_PREVIOUS_RECIPE)) {
                if (recipeId == 1) {
                    recipeId = cursorCount;
                } else {
                    recipeId = recipeId - 1;
                }
            }

            switch (action) {
                case ACTION_SHOW_NEXT_RECIPE:
                    cursor = getContentResolver().query(
                            ContentUris.withAppendedId(Contracts.RecipesEntry.CONTENT_URI, recipeId),
                            null,
                            null,
                            null,
                            null);
                    break;
                case ACTION_SHOW_PREVIOUS_RECIPE:
                    cursor = getContentResolver().query(
                            ContentUris.withAppendedId(Contracts.RecipesEntry.CONTENT_URI, recipeId),
                            null,
                            null,
                            null,
                            null);
                    break;
                default:
                    cursor = null;
            }
        }
        return cursor;

    }


}
