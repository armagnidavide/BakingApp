package com.example.android.bakingapp.widget;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.sql.Contracts;

import java.util.ArrayList;

/**
 * An interface for an adapter between a remote collection view (our ListView) and the underlying data for that view.
 * The implementor is responsible for making a RemoteView for each item in the data set.
 * This interface is a thin wrapper around Adapter.
 */
public class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
    //context to access the ContentResolver
    private Context context;
    //to get ingredients data from db
    private Cursor ingredientsCursor;
    private ArrayList<Ingredient> ingredients;
    private int recipeId;


    public ListRemoteViewFactory(Context applicationContext, Intent intent) {
        context = applicationContext;
        recipeId = intent.getIntExtra("recipeId", 4);

    }

    @Override
    public void onCreate() {
    }

    private void getIngredientsFromDesiredRecipe() {
        ingredients = new ArrayList<>();
        ingredientsCursor = context.getContentResolver().query(
                ContentUris.withAppendedId(Contracts.IngredientsEntry.CONTENT_URI, recipeId),
                null,
                null,
                null,
                null
        );
        if (ingredientsCursor != null && ingredientsCursor.getCount() > 0) {
            ingredientsCursor.moveToFirst();
            for (int i = 0; i < ingredientsCursor.getCount(); i++) {
                String ingredientName;
                int ingredientQuantity;
                String ingredientMeasure;
                Ingredient ingredient = new Ingredient();
                ingredientName = ingredientsCursor.getString(ingredientsCursor.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_NAME));
                ingredientQuantity = ingredientsCursor.getInt(ingredientsCursor.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY));
                ingredientMeasure = ingredientsCursor.getString(ingredientsCursor.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_MEASURE));
                ingredient.setName(ingredientName);
                ingredient.setQuantity(ingredientQuantity);
                ingredient.setMeasure(ingredientMeasure);
                ingredients.add(ingredient);
                ingredientsCursor.moveToNext();
            }
            ingredientsCursor.close();
        }


    }

    // called on start and whe notifyAppWidgetViewDataChanged
    @Override
    public void onDataSetChanged() {
        getIngredientsFromDesiredRecipe();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (ingredientsCursor == null) return 0;
        return ingredientsCursor.getCount();
    }

    //Get a View that displays the data at the specified position in the data set.
    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredients == null || ingredients.size() == 0) {
            return null;
        }
        Ingredient ingredient = ingredients.get(position);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient);
        remoteViews.setTextViewText(R.id.ingredientItem_txtVw_ingredient_name, ingredient.getName());
        remoteViews.setTextViewText(R.id.ingredientItem_txtVw_ingredient_quantity, String.valueOf(ingredient.getQuantity()));
        remoteViews.setTextViewText(R.id.txtVw_ingredient_measure, ingredient.getMeasure());

        Intent fillInIntent = new Intent();
        remoteViews.setOnClickFillInIntent(R.id.appwidget_layout_ingredient, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    //Returns the number of types of Views that will be created by getView(int, View, ViewGroup).
    // Each type represents a set of views that can be converted in getView(int, View, ViewGroup).
    // If the adapter always returns the same type of View for all items, this method should return 1.
    //Treat all items in the GridView the same
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    //Get the row id associated with the specified position in the list.
    @Override
    public long getItemId(int i) {
        return i;
    }

    //Indicates whether the item ids are stable across changes to the underlying data.
    @Override
    public boolean hasStableIds() {
        return false;
    }


}

