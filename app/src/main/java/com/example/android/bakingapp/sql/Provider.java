package com.example.android.bakingapp.sql;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

@SuppressWarnings("ConstantConditions")
public class Provider extends android.content.ContentProvider {
    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int RECIPES = 1;
    private final static int RECIPE_ID = 2;
    private static final int INGREDIENTS = 3;
    private final static int INGREDIENT_ID = 4;
    private static final int STEPS = 5;
    private final static int STEP_ID = 6;

    static {
        /*
         * The calls to addURI() go here, for all of the content URI patterns that the provider
         * should recognize.
         */

        //Calls for all the table
        uriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.RecipesEntry.TABLE_NAME, RECIPES);
        uriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.IngredientsEntry.TABLE_NAME, INGREDIENTS);
        uriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.StepsEntry.TABLE_NAME, STEPS);
        //Calls for a single row
        uriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.RecipesEntry.TABLE_NAME + "/#", RECIPE_ID);
        uriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.IngredientsEntry.TABLE_NAME + "/#", INGREDIENT_ID);
        uriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.StepsEntry.TABLE_NAME + "/#", STEP_ID);

    }


    public Provider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase recipeDb;
        Context context = getContext();
        RecipeDb recipeDbHelper = new RecipeDb(context);
        recipeDb = recipeDbHelper.getWritableDatabase();
        int rowsDeleted;
        String id;

        switch (uriType) {
            case RECIPES:
                rowsDeleted = recipeDb.delete(Contracts.RecipesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case INGREDIENTS:
                rowsDeleted = recipeDb.delete(Contracts.IngredientsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case STEPS:
                rowsDeleted = recipeDb.delete(Contracts.StepsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case RECIPE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = recipeDb.delete(Contracts.RecipesEntry.TABLE_NAME,
                            Contracts.RecipesEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = recipeDb.delete(Contracts.RecipesEntry.TABLE_NAME,
                            Contracts.RecipesEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case INGREDIENT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = recipeDb.delete(Contracts.IngredientsEntry.TABLE_NAME,
                            Contracts.IngredientsEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = recipeDb.delete(Contracts.IngredientsEntry.TABLE_NAME,
                            Contracts.IngredientsEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case STEP_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = recipeDb.delete(Contracts.StepsEntry.TABLE_NAME,
                            Contracts.StepsEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = recipeDb.delete(Contracts.StepsEntry.TABLE_NAME,
                            Contracts.StepsEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case RECIPES:
                return Contracts.RecipesEntry.CONTENT_TYPE;
            case INGREDIENTS:
                return Contracts.IngredientsEntry.CONTENT_TYPE;
            case STEPS:
                return Contracts.StepsEntry.CONTENT_TYPE;
            case RECIPE_ID:
                return Contracts.RecipesEntry.CONTENT_ITEM_TYPE;
            case INGREDIENT_ID:
                return Contracts.IngredientsEntry.CONTENT_ITEM_TYPE;
            case STEP_ID:
                return Contracts.StepsEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Context context = getContext();
        SQLiteDatabase recipeDb;
        RecipeDb recipeDbHelper = new RecipeDb(context);
        recipeDb = recipeDbHelper.getWritableDatabase();
        long rowID;
        switch (uriMatcher.match(uri)) {
            case RECIPES:
                //Add a new recipe
                rowID = recipeDb.insert(Contracts.RecipesEntry.TABLE_NAME, "", values);
                Log.e("ROW ", "recipes number :     "+ rowID);
                //If record is added successfully
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(Contracts.RecipesEntry.CONTENT_URI, rowID);
                    context.getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Failed to add a record into " + uri);
            case INGREDIENTS:
                //Add a new ingredient
                rowID = recipeDb.insert(Contracts.IngredientsEntry.TABLE_NAME, "", values);
                Log.e("ROW ", "ingredients number :     "+ rowID);
                //If record is added successfully
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(Contracts.IngredientsEntry.CONTENT_URI, rowID);
                    context.getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Failed to add a record into " + uri);
            case STEPS:
                //Add a new step
                rowID = recipeDb.insert(Contracts.StepsEntry.TABLE_NAME, "", values);
                Log.e("ROW ", "steps number :     "+ rowID);
                //If record is added successfully
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(Contracts.StepsEntry.CONTENT_URI, rowID);
                    context.getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Failed to add a record into " + uri);
            default:
                throw new SQLException("Failed to add a record into " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        SQLiteDatabase recipeDb;
        RecipeDb recipeDbHelper = new RecipeDb(context);

        /**
         * Create a writable database which will trigger its
         * creation if it doesn't already exist.
         */
        recipeDb = recipeDbHelper.getWritableDatabase();
        return recipeDb != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Context context = getContext();
        SQLiteDatabase recipeDb;
        RecipeDb recipeDbHelper = new RecipeDb(context);
        recipeDb = recipeDbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

          /*
         * Choose the table to query and a sort order based on the code returned for the incoming
         * URI.
         */
        switch (uriMatcher.match(uri)) {
            case RECIPES:
                queryBuilder.setTables(Contracts.RecipesEntry.TABLE_NAME);
                break;
            case RECIPE_ID:
                queryBuilder.setTables(Contracts.RecipesEntry.TABLE_NAME);
                /*
                 * Because this URI was for a single row, the _ID value part is
                 * present. Get the last path segment from the URI; this is the _ID value.
                 * Then, append the value to the WHERE clause for the query
                 */
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            case INGREDIENTS:
                queryBuilder.setTables(Contracts.IngredientsEntry.TABLE_NAME);
                break;
            case INGREDIENT_ID:
                queryBuilder.setTables(Contracts.IngredientsEntry.TABLE_NAME);
                /*
                 * Because this URI was for a single row, the _ID value part is
                 * present. Get the last path segment from the URI; this is the _ID value.
                 * Then, append the value to the WHERE clause for the query
                 */
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            case STEPS:
                queryBuilder.setTables(Contracts.StepsEntry.TABLE_NAME);
                break;
            case STEP_ID:
                queryBuilder.setTables(Contracts.StepsEntry.TABLE_NAME);
                /*
                 * Because this URI was for a single row, the _ID value part is
                 * present. Get the last path segment from the URI; this is the _ID value.
                 * Then, append the value to the WHERE clause for the query
                 */
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;

            default:
                // If the URI is not recognized, you should do some error handling here.
        }
        // call the code to actually do the query
        Cursor cursor = queryBuilder.query(recipeDb,
                projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                uri);
        return cursor;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Context context = getContext();
        SQLiteDatabase recipeDb;
        RecipeDb recipeDbHelper = new RecipeDb(context);
        recipeDb = recipeDbHelper.getWritableDatabase();
        int uriType = uriMatcher.match(uri);
        int rowsUpdated;
        String id;
        switch (uriType) {
            case RECIPES:
                rowsUpdated =
                        recipeDb.update(Contracts.RecipesEntry.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);
                break;
            case RECIPE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            recipeDb.update(Contracts.RecipesEntry.TABLE_NAME,
                                    values,
                                    Contracts.RecipesEntry._ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            recipeDb.update(Contracts.RecipesEntry.TABLE_NAME,
                                    values,
                                    Contracts.RecipesEntry._ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;
            case INGREDIENTS:
                rowsUpdated =
                        recipeDb.update(Contracts.IngredientsEntry.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);
                break;
            case INGREDIENT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            recipeDb.update(Contracts.IngredientsEntry.TABLE_NAME,
                                    values,
                                    Contracts.IngredientsEntry._ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            recipeDb.update(Contracts.IngredientsEntry.TABLE_NAME,
                                    values,
                                    Contracts.IngredientsEntry._ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;
            case STEPS:
                rowsUpdated =
                        recipeDb.update(Contracts.StepsEntry.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);
                break;
            case STEP_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            recipeDb.update(Contracts.StepsEntry.TABLE_NAME,
                                    values,
                                    Contracts.StepsEntry._ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            recipeDb.update(Contracts.StepsEntry.TABLE_NAME,
                                    values,
                                    Contracts.StepsEntry._ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " +
                        uri);
        }
        getContext().getContentResolver().notifyChange(uri,
                null);
        return rowsUpdated;
    }
}
