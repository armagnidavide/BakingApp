package com.example.android.bakingapp;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.sql.Contracts;

//An interface for an adapter between a remote collection view (ListView, GridView, etc) and the underlying data for that view.
// The implementor is responsible for making a RemoteView for each item in the data set. This interface is a thin wrapper around Adapter.
public class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory  {
    //context is necessary to access the ContentResolver
    private Context context;
    private Cursor recipesCursor;
    //to get ingredeints data from db
    private Cursor ingredientsCursor;

     private int recipeId;

    //default constructor to register as a receiver
    public ListRemoteViewFactory(){}

    public ListRemoteViewFactory(Context applicationContext, Intent intent) {
        context=applicationContext;
        recipeId=intent.getIntExtra("recipeId",4);
        Log.e("BakingAppDebug", "GridRemoteViewsFactory constructor, recipeId= "+recipeId );


    }

    @Override
    public void onCreate() {
    }




    private void getIngredientsFromDesiredRecipe() {
            ingredientsCursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(Contracts.IngredientsEntry.CONTENT_URI, recipeId),
                    null,
                    null,
                    null,
                    null
            );
    }

    // called on start and whe notifyAppWidgetViewDataChanged
    @Override
    public void onDataSetChanged() {
        Log.e("BakingAppDebug","GridRemoteViewsFactory onDataSetChange() recipeId = "+ recipeId);
        getIngredientsFromDesiredRecipe();
    }

    @Override
    public void onDestroy() {
        ingredientsCursor.close();

    }

    @Override
    public int getCount() {
        if (ingredientsCursor == null) return 0;
        return ingredientsCursor.getCount();
    }
    //Get a View that displays the data at the specified position in the data set.
    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredientsCursor == null || ingredientsCursor.getCount() == 0) {
            return null;
        }
        ingredientsCursor.moveToPosition(position);
        String ingredientName;
        int ingredientQuantity;
        String ingredientMeasure;
        ingredientName = ingredientsCursor.getString(ingredientsCursor.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_NAME));
        ingredientQuantity = ingredientsCursor.getInt(ingredientsCursor.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY));
        ingredientMeasure = ingredientsCursor.getString(ingredientsCursor.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_MEASURE));

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient);
        remoteViews.setTextViewText(R.id.txtVw_ingredient_name, ingredientName);
        remoteViews.setTextViewText(R.id.txtVw_ingredient_quantity, String.valueOf(ingredientQuantity));
        remoteViews.setTextViewText(R.id.txtVw_ingredient_measure, ingredientMeasure);
        //create the fillIntent for the GridView' items, in this way it will be possible to go to selectStepActivity when the user clicks on them
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

