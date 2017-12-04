package com.example.android.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.activities.SelectStepActivity;
import com.squareup.picasso.Picasso;

/**
 * Implementation of App Widget functionality.
 * Defines the basic methods that allow us to programmatically interface with the App Widget,
 * based on broadcast events.
 * Through it, we will receive broadcasts when the App Widget is updated, enabled, disabled and deleted
 */
public class RecipeWidgetProvider extends AppWidgetProvider {
    private static final String SHOW_NEXT = "showNext";
    private static final String SHOW_PREVIOUS = "showPrevious";

    /**
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (SHOW_NEXT.equals(intent.getAction())) {
            GetRecipeInformationService.showRecipeDataIntoTheWidget
                    (context, GetRecipeInformationService.ACTION_SHOW_NEXT_RECIPE, intent.getIntExtra("recipeId", 1));
        } else if (SHOW_PREVIOUS.equals(intent.getAction())) {
            GetRecipeInformationService.showRecipeDataIntoTheWidget
                    (context, GetRecipeInformationService.ACTION_SHOW_PREVIOUS_RECIPE, intent.getIntExtra("recipeId", 1));
        }
    }


    //Update the Widgets which ids are in appWidgetIds[].
    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,
                                           int recipeId, String recipeName, int ingredientsNumber, int stepsNumber,
                                           String recipeThumbnail) {
        for (int appWidgetId : appWidgetIds) {
            Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
            int width = options.getInt(appWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

            RemoteViews rv;
            if (width < 200) {
                rv = setSmallRemoteView(context, recipeId, recipeName, ingredientsNumber, stepsNumber, appWidgetIds, recipeThumbnail);
            } else {
                rv = setListRemoteView(context, recipeId, recipeName, appWidgetIds, recipeThumbnail);
            }
            //Set the RemoteViews to use for the specified appWidgetId.
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }

    /**
     *
     * Creates and return the RemoteViews for the Widget.
     * @param context GetRecipeInformationService
     * @param recipeId the id of the recipe
     * @param recipeName the name of the recipe
     * @param ingredientsNumber the number of ingredients
     * @param stepsNumber the number of steps
     * @param appWidgetIds the ids of the widgets
     * @param recipeThumbnail the image of the recipe
     * @return the RemoteViews for the widgets
     */
    private static RemoteViews setSmallRemoteView(Context context, int recipeId, String recipeName, int ingredientsNumber,
                                                  int stepsNumber, int[] appWidgetIds, String recipeThumbnail) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_small);
        views.setTextViewText(R.id.appwidget_recipe_name, recipeName);
        views.setTextViewText(R.id.appwidget_recipe_ingredients_number, String.valueOf(ingredientsNumber));
        views.setTextViewText(R.id.appwidget_recipe_steps_number, String.valueOf(stepsNumber));
        views.setOnClickPendingIntent(R.id.widget_small_btn_appwidget_next,
                getPendingSelfIntent(context, SHOW_NEXT, recipeId));
        views.setOnClickPendingIntent(R.id.widget_small_btn_appwidget_previous,
                getPendingSelfIntent(context, SHOW_PREVIOUS, recipeId));
        // Load image for all appWidgetIds.
        //Render image using Picasso library
        if (!TextUtils.isEmpty(recipeThumbnail)) {
            Picasso.with(context).load(recipeThumbnail)
                    .error(R.drawable.recipe_placeholder)
                    .placeholder(R.drawable.recipe_placeholder)
                    .into(views, R.id.appwidget_recipe_image, appWidgetIds);
        } else {
            views.setImageViewResource(R.id.appwidget_recipe_image, R.drawable.recipe_placeholder);
        }
        Intent goToSelectStepActivityIntent = new Intent(context, SelectStepActivity.class);
        goToSelectStepActivityIntent.setAction(SelectStepActivity.ACTION_GET_RECIPE_DATA_FROM_CURSOR);
        goToSelectStepActivityIntent.putExtra(SelectStepActivity.RECIPE_ID, recipeId);
        PendingIntent goToSelectStepActivityPendingIntent = PendingIntent.getActivity(
                context,
                0,
                goToSelectStepActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_layout_small, goToSelectStepActivityPendingIntent);
        return views;
    }

    /**
     * Creates and return the RemoteViews for the Widget.
     * The widget contains a List that displays  the ingredients of the recipes.
     * @param context GetRecipeInformationService
     * @param recipeId the id of the recipe
     * @param recipeName the name of the recipe
     * @param appWidgetIds the ids of the widgets
     * @param recipeThumbnail the image of the recipe
     * @return the RemoteViews for the widgets
     */
    private static RemoteViews setListRemoteView(Context context, int recipeId, String recipeName
            , int[] appWidgetIds, String recipeThumbnail) {
        PendingIntent goToSelectStepActivityPendingIntent = getSelectStepActivityPendingIntent(context, recipeId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list);
        // Load image for all appWidgetIds.
        //Render image using Picasso library
        if (!TextUtils.isEmpty(recipeThumbnail)) {
            Picasso.with(context).load(recipeThumbnail)
                    .error(R.drawable.recipe_placeholder)
                    .placeholder(R.drawable.recipe_placeholder)
                    .into(views, R.id.appwidget_recipe_image, appWidgetIds);
        } else {
            views.setImageViewResource(R.id.appwidget_recipe_image, R.drawable.recipe_placeholder);
        }
        views.setOnClickPendingIntent(R.id.lnrLyt_for_pendingIntent, goToSelectStepActivityPendingIntent);
        views.setPendingIntentTemplate(R.id.widget_list_view, goToSelectStepActivityPendingIntent);
        views.setTextViewText(R.id.appwidget_recipe_name, recipeName);
        views.setOnClickPendingIntent(R.id.widget_small_btn_appwidget_next,
                getPendingSelfIntent(context, SHOW_NEXT, recipeId));
        views.setOnClickPendingIntent(R.id.widget_small_btn_appwidget_previous,
                getPendingSelfIntent(context, SHOW_PREVIOUS, recipeId));
        //set the ListWidgetService intent to act as the adapter for the ListView
        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra("recipeId", recipeId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widget_list_view, intent);
        //handle empty recipe
        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);

        return views;
    }

    //Called in response to the ACTION_APPWIDGET_OPTIONS_CHANGED broadcast when this widget has been layed out at a new size.
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        GetRecipeInformationService.showRecipeDataIntoTheWidget(context, GetRecipeInformationService.ACTION_SHOW_LAST_USED_RECIPE);
    }

    //Called in response to the ACTION_APPWIDGET_UPDATE and ACTION_APPWIDGET_RESTORED
    // broadcasts when this AppWidget provider is being asked to provide RemoteViews for a set of AppWidgets
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        GetRecipeInformationService.showRecipeDataIntoTheWidget(context, GetRecipeInformationService.ACTION_SHOW_LAST_USED_RECIPE);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    //helper method to create a PendingIntent that will be sent to RecipeWidgetProvider.class
    protected static PendingIntent getPendingSelfIntent(Context context, String action, int recipeId) {
        Intent intent = new Intent(context, RecipeWidgetProvider.class);
        intent.setAction(action);
        intent.putExtra("recipeId", recipeId);
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //helper method to create a PendingIntent that will start SelectStepActivity
    private static PendingIntent getSelectStepActivityPendingIntent(Context context, int recipeId) {
        Intent goToSelectStepActivityIntent = new Intent(context, SelectStepActivity.class);
        goToSelectStepActivityIntent.setAction(SelectStepActivity.ACTION_GET_RECIPE_DATA_FROM_CURSOR);
        goToSelectStepActivityIntent.putExtra(SelectStepActivity.RECIPE_ID, recipeId);
        return PendingIntent.getActivity(
                context,
                0,
                goToSelectStepActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }


}

