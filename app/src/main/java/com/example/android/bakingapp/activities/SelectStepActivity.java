package com.example.android.bakingapp.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Step;
import com.example.android.bakingapp.Utils;
import com.example.android.bakingapp.adapters.StepsAdapter;
import com.example.android.bakingapp.fragments.IngredientsFragment;
import com.example.android.bakingapp.sql.Contracts;
import com.example.android.bakingapp.widget.GetRecipeInformationService;

import java.util.ArrayList;

/**
 * In this Activity the user can see all the steps of the chosen recipe.
 * If the devise is large enough, step's details and recipe'
 * ingredients are shown on the right side of the stepList.
 */
public class SelectStepActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //constants that we use to retrieve recipe's data from the intent.
    public static final String RECIPE_ID = "recipe_id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_SERVING = "recipe_serving";
    public static final String RECIPE_LAST_TIME_USED = "last_time_used";
    public static final String ACTION_GET_RECIPE_DATA_FROM_CURSOR = "get_recipe_data_from_cursor";
    public static final String ACTION_GET_RECIPE_DATA_FROM_THIS_INTENT = "get_recipe_data_from_this_intent";

    //Ids for the loaders
    private static final int LOADER_STEP = 1;
    private static final int LOADER_RECIPE = 2;

    //Whether or not the activity is in two-pane mode
    private boolean mTwoPane;

    //layout components
    private RecyclerView recyclerView;//recyclerView to display the steps
    private TextView txtVwRecipeName;
    private TextView txtVwRecipeServing;

    //recipe's data
    private int recipeId ;
    private String recipeName;
    private int recipeServing;

    private String intentAction;

    private long currentTime;
    //indicates if we have to take the recipe's data from the intent(so it comes from SelectRecipeActivity)
    //or from a Cursor because te intent contains only the recipeId(so it comes from the widget)
    private boolean gettingRecipeFromCursor;

    private ArrayList<Step> stepsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_step);
        initializations();
        checkDetailContainerViewInsideLayout();
        getIntentAction();
        getDataFromIntent(intentAction);
        if (gettingRecipeFromCursor) startLoader(LOADER_RECIPE);
        updateTime();
        if (!gettingRecipeFromCursor) startLoader(LOADER_STEP);


    }

    private void initializations() {
        currentTime = System.currentTimeMillis();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_steps);
        txtVwRecipeName = (TextView) findViewById(R.id.selectStepActivity_txtVw_recipe_name);
        txtVwRecipeServing = (TextView) findViewById(R.id.selectStepActivity_txtVw_recipe_serving);
    }

    /**
     * Checks if the layout is for TwoPane mode or not
     */
    private void checkDetailContainerViewInsideLayout() {
        if (findViewById(R.id.stepList_frameLayout_fragment_container) != null) {
            mTwoPane = true;
        }
    }
    private void getIntentAction() {
        Intent intent = getIntent();
        intentAction=intent.getAction();
    }

    /**
     *Retrieve data from the intent
     */
    private void getDataFromIntent(String intentAction) {
        Intent intent = getIntent();
        recipeId = intent.getIntExtra(RECIPE_ID, 0);
        if(intentAction.equals(ACTION_GET_RECIPE_DATA_FROM_THIS_INTENT)){
            gettingRecipeFromCursor = false;
            recipeName = intent.getStringExtra(RECIPE_NAME);
            recipeServing = intent.getIntExtra(RECIPE_SERVING, 0);
            setRecipeNameAndServingTxtViews();}
            else {
            gettingRecipeFromCursor = true;
        }
    }

    private void startLoader(int loaderId) {
        getSupportLoaderManager().restartLoader(loaderId, null, this);
    }

    /**
     * Start an UpdateTimeAsyncTask
     */
    private void updateTime() {
        new UpdateTimeAsyncTask().execute();
    }

    /**
     * Displays recipe's name and recipe's servings
     */
    private void setRecipeNameAndServingTxtViews() {
        txtVwRecipeName.setText(recipeName);
        txtVwRecipeServing.setText(String.valueOf(recipeServing));
    }

    /**
     *if in twoPane mode shows the ingredients in a Fragment,
     *otherwise open DetailsActivity
     * @param v the button("show ingredients)
     */
    public void displayIngredients(View v) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(IngredientsFragment.INGREDIENT_RECIPE_ID, recipeId);
            IngredientsFragment fragment = new IngredientsFragment();
            Utils.openFragment(fragment, arguments, getSupportFragmentManager(),R.id.stepList_frameLayout_fragment_container);
        } else {
            Context context = v.getContext();
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra(IngredientsFragment.INGREDIENT_RECIPE_ID, recipeId);
            Utils.openActivity(context, intent);
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     * @param id  loader's Id
     * @param args
     * @return CursorLoader
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //retrieve the steps of a recipe with an id=recipeId
        if (id == LOADER_STEP) {
            return new CursorLoader(this, ContentUris.withAppendedId(Contracts.StepsEntry.CONTENT_URI, recipeId),
                    null,
                    null,
                    null,
                    null);
        } else if (id == LOADER_RECIPE) {
            //retrieve a recipe with an id=recipeId
            return new CursorLoader(this, ContentUris.withAppendedId(Contracts.RecipesEntry.CONTENT_URI, recipeId),
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            if (loader.getId() == LOADER_STEP) {
                stepsArrayList=createStepArrayList(data);
                setupRecyclerView(stepsArrayList);
            } else if (loader.getId() == LOADER_RECIPE) {
                recipeName = data.getString(data.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_NAME));
                recipeServing = data.getInt(data.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_SERVINGS));
                setRecipeNameAndServingTxtViews();
                startLoader(LOADER_STEP);
            }
            data.close();
        }
    }

    private ArrayList<Step> createStepArrayList(Cursor data) {
        ArrayList<Step> stepsArrayList = new ArrayList<>();
        for (int i = 0; i < data.getCount(); i++) {
            Step step = new Step();
            String stepDescription = data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_DESCRIPTION));
            String stepImageUrl = data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_THUMBNAIL_URL));
            String stepShortDescription = data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_SHORT_DESCRIPTION));
            String stepVideoURL = data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_VIDEO_URL));

            step.setDescription(stepDescription);
            step.setThumbnailURL(stepImageUrl);
            step.setShortDescription(stepShortDescription);
            step.setVideoURL(stepVideoURL);
            stepsArrayList.add(step);
            data.moveToNext();
        }
        return stepsArrayList;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setupRecyclerView(ArrayList<Step> steps) {

        recyclerView.setAdapter(new StepsAdapter(this, steps, mTwoPane, recipeId, getSupportFragmentManager()));
    }

    /**
     * This AsyncTask updates the COLUMN_RECIPE_LAST_TIME_USED
     * in this way we always know which is the last used recipe:
     */
    private class UpdateTimeAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues cv = new ContentValues();
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_LAST_TIME_USED, currentTime);
            getContentResolver().update(ContentUris.withAppendedId(Contracts.RecipesEntry.CONTENT_URI, recipeId),
                    cv,
                    null,
                    null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            GetRecipeInformationService.showRecipeDataIntoTheWidget(getApplicationContext(), GetRecipeInformationService.ACTION_SHOW_LAST_USED_RECIPE, recipeId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_go_to_catalog:
                Intent intent=new Intent(this,SelectRecipeActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
