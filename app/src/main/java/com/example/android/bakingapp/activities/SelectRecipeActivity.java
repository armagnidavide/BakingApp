package com.example.android.bakingapp.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bakingapp.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Recipe;
import com.example.android.bakingapp.Step;
import com.example.android.bakingapp.adapters.RecipesAdapter;
import com.example.android.bakingapp.sql.Contracts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * In this Activity a List or Grid(depending on the width of the screen)
 * of recipes is shown to the user.
 * If the user selects one SelectStepActivity is opened with the details.
 */
public class SelectRecipeActivity extends AppCompatActivity {


    //tag used for the Logcat
    private static final String TAG = SelectRecipeActivity.class.getSimpleName();
    //the Url where the app finds the recipes
    private static final String RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    //the List contains all the recipes
    private List<Recipe> recipes;
    //recyclerView and Adapter used to display the recipes
    private RecyclerView recyclerView;
    private RecipesAdapter adapter;
    //progressBar appears while the recipes data is not ready yet.
    private ProgressBar progressBar;
    //The View shown when there is no data available for the RecyclerView
    LinearLayout emptyView;
    //the button inside emptyView
    Button btnEmptyView;

    /**
     * Inserts data into the Db
     *
     * @param resolver the ContentResolvr
     * @param uri      the Uri
     * @param values   the Values to insert
     */
    public static void insert(ContentResolver resolver,
                              Uri uri, ContentValues values) {
        try {
            resolver.insert(uri, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Catch a SQLiteException when insert: ", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recipe);
        initializations();
        setRecyclerView();
        setClickListener();
    }


    /**
     * Initializes what we need from the beginning
     */
    private void initializations() {
        recyclerView = (RecyclerView) findViewById(R.id.selectRecipeActivity_recyclerView_recipes);
        adapter = new RecipesAdapter(SelectRecipeActivity.this, null);
        progressBar = (ProgressBar) findViewById(R.id.selectRecipeActivity_progressBar);
        emptyView=(LinearLayout)findViewById(R.id.selectRecipeActivity_linearLayout_empty_view);
        btnEmptyView=(Button)findViewById(R.id.selectRecipeActivity_btn_tryAgain);

    }

    /**
     * If the recipes are not in the Db the DownloadTask is executed,
     * otherwise displayRecipeFromDb() is called.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!databaseContainsData()) {
            new DownloadTask().execute(RECIPE_URL);
        } else {
            displayRecipesFromDb();
        }
    }

    private void setRecyclerView() {
        setLayoutManager();
        recyclerView.setAdapter(adapter);
    }

    /**
     * Set the LayoutManager as a GridLayoutManager if the configuration is XLARGE,
     * otherwise as a LinearLayoutManager.
     */
    private void setLayoutManager() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                break;
            default:
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setClickListener() {
        btnEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadTask().execute(RECIPE_URL);
            }
        });
    }

    /**
     * Checks if there are already recipes inside the Db
     *
     * @return true if the recipes are already inside the Db
     */
    private boolean databaseContainsData() {
        boolean dbContainsRecipesAlready = false;
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(Contracts.RecipesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        if (cursor != null) {
            dbContainsRecipesAlready = cursor.getCount() > 0;
            cursor.close();
        }
        return dbContainsRecipesAlready;
    }

    /**
     * Parse the JsonResponse and extract all the recipes from it and put them inside a List ( recipes ).
     *
     * @param JsonResult the JsonResponse from the Network
     */
    private void parseResult(String JsonResult) {
        try {
            JSONArray response = new JSONArray(JsonResult);
            recipes = new ArrayList<>();

            for (int i = 0; i < response.length(); i++) {
                JSONObject recipe = response.optJSONObject(i);
                Recipe item = new Recipe();
                item.setId(recipe.optInt("id"));
                item.setName(recipe.optString("name"));
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                ArrayList<Ingredient> recipeIngredients = createIngredientsArrayList(ingredients);
                item.setIngredients(recipeIngredients);
                item.setIngredientsNumber(recipeIngredients.size());
                JSONArray steps = recipe.getJSONArray("steps");
                ArrayList<Step> recipeSteps = createRecipeStepsArrayList(steps);
                item.setSteps(recipeSteps);
                item.setStepsNumber(recipeSteps.size());
                item.setServings(recipe.optInt("servings"));
                item.setImage(recipe.optString("image"));
                item.setLastTimeUsed(getCurrentTime());
                recipes.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the currentTime .
     *
     * @return the currentTime
     */
    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Create an ArrayList of ingredients from a JsonArray
     *
     * @param ingredients the JsonArray
     * @return the ArrayList of ingredients
     */
    private ArrayList<Ingredient> createIngredientsArrayList(JSONArray ingredients) {
        ArrayList<Ingredient> recipeIngredients = new ArrayList<>();
        try {
            for (int i = 0; i < ingredients.length(); i++) {
                JSONObject ingredient = ingredients.getJSONObject(i);
                Ingredient recipeIngredient = new Ingredient();
                recipeIngredient.setQuantity(ingredient.optInt("quantity"));
                recipeIngredient.setMeasure(ingredient.optString("measure"));
                recipeIngredient.setName(ingredient.optString("ingredient"));
                recipeIngredients.add(recipeIngredient);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipeIngredients;
    }

    /**
     * Creates an ArrayList of Steps from a JsonArray
     *
     * @param steps the JsonArray
     * @return the ArrayList of steps
     */
    private ArrayList<Step> createRecipeStepsArrayList(JSONArray steps) {
        ArrayList<Step> recipeSteps = new ArrayList<>();
        try {
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                Step recipeStep = new Step();
                recipeStep.setId(step.optInt("id"));
                recipeStep.setShortDescription(step.optString("shortDescription"));
                recipeStep.setDescription(step.optString("description"));
                recipeStep.setVideoURL(step.getString("videoURL"));
                recipeStep.setThumbnailURL(step.getString("thumbnailURL"));
                recipeSteps.add(recipeStep);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipeSteps;
    }

    /**
     * Inserts an ArrayList of Recipes inside the Db.
     */
    private void insertRecipesIntoDb() {
        for (int i = 0; i < recipes.size(); i++) {
            insertIngredientsIntoDb(recipes.get(i).getIngredients(), recipes.get(i).getId());
            insertStepsIntoDb(recipes.get(i).getSteps(), recipes.get(i).getId());
            ContentValues cv = new ContentValues();
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_NAME, recipes.get(i).getName());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_INGREDIENTS, recipes.get(i).getIngredientsNumber());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_STEPS, recipes.get(i).getStepsNumber());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_SERVINGS, recipes.get(i).getServings());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_THUMBNAIL, recipes.get(i).getImage());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_LAST_TIME_USED, recipes.get(i).getLastTimeUsed());
            insert(getContentResolver(), Contracts.RecipesEntry.CONTENT_URI, cv);
        }

    }

    /**
     * Inserts an ArrayList of Ingredients inside the Db.
     */
    private void insertIngredientsIntoDb(ArrayList<Ingredient> ingredients, int recipeId) {
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient currentIngredient = ingredients.get(i);
            ContentValues cv = new ContentValues();
            cv.put(Contracts.IngredientsEntry.COLUMN_INGREDIENT_RECIPE_ID, recipeId);
            cv.put(Contracts.IngredientsEntry.COLUMN_INGREDIENT_NAME, currentIngredient.getName());
            cv.put(Contracts.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY, currentIngredient.getQuantity());
            cv.put(Contracts.IngredientsEntry.COLUMN_INGREDIENT_MEASURE, currentIngredient.getMeasure());
            insert(getContentResolver(), Contracts.IngredientsEntry.CONTENT_URI, cv);

        }
    }

    /**
     * Inserts an ArrayList of Steps inside the Db.
     */
    private void insertStepsIntoDb(ArrayList<Step> steps, int recipeId) {
        for (int i = 0; i < steps.size(); i++) {
            Step currentStep = steps.get(i);
            ContentValues cv = new ContentValues();
            cv.put(Contracts.StepsEntry.COLUMN_STEP_RECIPE_ID, recipeId);
            cv.put(Contracts.StepsEntry.COLUMN_STEP_DESCRIPTION, currentStep.getDescription());
            cv.put(Contracts.StepsEntry.COLUMN_STEP_SHORT_DESCRIPTION, currentStep.getShortDescription());
            cv.put(Contracts.StepsEntry.COLUMN_STEP_VIDEO_URL, currentStep.getVideoURL());
            cv.put(Contracts.StepsEntry.COLUMN_STEP_THUMBNAIL_URL, currentStep.getThumbnailURL());
            insert(getContentResolver(), Contracts.StepsEntry.CONTENT_URI, cv);
        }
    }

    /**
     * Retrieves the recipes from the Db and put them inside an ArrayList
     */
    private void displayRecipesFromDb() {
        recipes = new ArrayList<>();
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(Contracts.RecipesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        if (cursor != null && cursor.getCount() > 0) {
            showLayoutForTheData();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Recipe recipe = new Recipe();
                int recipeId = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry._ID));
                String name = cursor.getString(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_NAME));
                int ingredientsNumber = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_INGREDIENTS));
                int stepsNumber = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_STEPS));
                int servings = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_SERVINGS));
                String thumbnail = cursor.getString(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_THUMBNAIL));
                long lastTimeUsed = cursor.getLong(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_LAST_TIME_USED));
                recipe.setId(recipeId);
                recipe.setName(name);
                recipe.setIngredientsNumber(ingredientsNumber);
                recipe.setStepsNumber(stepsNumber);
                recipe.setServings(servings);
                recipe.setImage(thumbnail);
                recipe.setLastTimeUsed(lastTimeUsed);
                recipes.add(recipe);
                cursor.moveToNext();
            }
            cursor.close();
            adapter = new RecipesAdapter(SelectRecipeActivity.this, recipes);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(adapter);
        }else{
            showEmptyView();
        }

    }

    /**
     * This AsyncTask retrieves recipes data from the Network
     */
    public class DownloadTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            //A progressBar is shown while the data cannot be shown to the User
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1;
                } else {
                    result = 0;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                showLayoutForTheData();
                insertRecipesIntoDb();
                adapter = new RecipesAdapter(SelectRecipeActivity.this, recipes);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(SelectRecipeActivity.this, R.string.activity_select_recipe_toast_failed_to_fetch_data, Toast.LENGTH_SHORT).show();
                showEmptyView();
            }
        }
    }

    public void showLayoutForTheData(){
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }
    public void showEmptyView(){
        recyclerView.setVisibility(View.GONE);
        LinearLayout emptyView=(LinearLayout)findViewById(R.id.selectRecipeActivity_linearLayout_empty_view);
        emptyView.setVisibility(View.VISIBLE);
    }


}
