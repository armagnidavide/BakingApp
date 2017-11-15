package com.example.android.bakingapp.sql;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract Class establishes a contract between the content provider and other applications,
 * also ensures that our content provider can be accessed correctly even if there are changes
 * to the actual values of URIs, column names
 */
public class Contracts {

    public static final String CONTENT_AUTHORITY =
            "com.example.android.bakingapp";
    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPES = "recipes";
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_STEPS = "steps";

    /**
     * Inner class that defines the table contents of the Recipes-table.
     */
    public static final class RecipesEntry implements BaseColumns {

        public static final String TABLE_NAME = "recipes";

        public static final String COLUMN_RECIPE_NAME = "name";
        public static final String COLUMN_RECIPE_INGREDIENTS = "ingredients";
        public static final String COLUMN_RECIPE_STEPS = "steps";
        public static final String COLUMN_RECIPE_SERVINGS = "servings";
        public static final String COLUMN_RECIPE_THUMBNAIL = "thumbnail";
        public static final String COLUMN_RECIPE_LAST_TIME_USED = "last_time";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_RECIPES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_RECIPES;

        // Helper method.
        public static Uri buildRecipeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /**
     * Inner class that defines the table contents of the Ingredients-table.
     */
    public static final class IngredientsEntry implements BaseColumns {

        public static final String TABLE_NAME = "ingredients";

        public static final String COLUMN_INGREDIENT_RECIPE_ID = "recipe_id";
        public static final String COLUMN_INGREDIENT_NAME = "name";
        public static final String COLUMN_INGREDIENT_QUANTITY = "quantity";
        public static final String COLUMN_INGREDIENT_MEASURE = "measure";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_INGREDIENTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_INGREDIENTS;


    }

    /**
     * Inner class that defines the table contents of the Steps-table.
     */
    public static final class StepsEntry implements BaseColumns {

        public static final String TABLE_NAME = "steps";

        public static final String COLUMN_STEP_RECIPE_ID = "recipe_id";
        public static final String COLUMN_STEP_SHORT_DESCRIPTION = "short_description";
        public static final String COLUMN_STEP_DESCRIPTION = "description";
        public static final String COLUMN_STEP_VIDEO_URL = "video_url";
        public static final String COLUMN_STEP_THUMBNAIL_URL = "thumbnail_url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_STEPS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_STEPS;

        // Helper method.
        public static Uri buildStepUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}

