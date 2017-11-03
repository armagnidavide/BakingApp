package com.example.android.bakingapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.bakingapp.databinding.ActivitySelectRecipeBinding;
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

public class SelectRecipeActivity extends AppCompatActivity {


    private static final String TAG = "RecyclerViewExample";
    private static final String RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    ActivitySelectRecipeBinding binding;
    private List<Recipe> recipes;
    private RecipesAdapter adapter;

    /**
     * public static String strSeparator = "__,__";
     * <p>
     * public static String convertArrayToString(String[] array) {
     * String str = "";
     * for (int i = 0; i < array.length; i++) {
     * str = str + array[i];
     * // Do not append comma at the end of last element
     * if (i < array.length - 1) {
     * str = str + strSeparator;
     * }
     * }
     * return str;
     * }
     * <p>
     * public static String[] convertStringToArray(String str) {
     * String[] arr = str.split(strSeparator);
     * return arr;
     * }
     **/
    public static Uri insert(Context context, ContentResolver resolver,
                             Uri uri, ContentValues values) {
        try {
            return resolver.insert(uri, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Catch a SQLiteException when insert: ", e);
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_recipe);
        setLayoutManager();
        adapter = new RecipesAdapter(SelectRecipeActivity.this, null);
        binding.recyclerView.setAdapter(adapter);
        if (!databaseContainsData()) {
            new DownloadTask().execute(RECIPE_URL);
        } else {
            displayRecipesFromDb();
        }
    }

    private void setLayoutManager() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                break;
            default:
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private boolean databaseContainsData() {
        boolean containsRecipes;
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(Contracts.RecipesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        containsRecipes = cursor.getCount() > 0;
        cursor.close();
        return containsRecipes;
    }


    /**
     * private static boolean doesDatabaseExist(Context context, String dbName) {
     * File dbFile = context.getDatabasePath(dbName);
     * return dbFile.exists();}
     **/


    private void parseResult(String result) {
        Log.d(TAG, "PARSE RESULT");
        try {
            //JSONObject response = new JSONObject(result);
            JSONArray response = new JSONArray(result);
            //JSONArray recipes = response.optJSONArray("reci");
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
                recipes.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
            Log.d(TAG, "yes createIngredientRecipes");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "no createIngredientRecipe");
        }
        return recipeIngredients;
    }

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
            Log.d(TAG, "yes createStepsRecipe");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "no createStepRecipes");
        }
        Log.e(TAG, "recipe steps  are : " + recipeSteps.size() + "");
        return recipeSteps;
    }

    private void putRecipesIntoDb() {
        for (int i = 0; i < recipes.size(); i++) {
            insertIngredients(recipes.get(i).getIngredients(), recipes.get(i).getId());
            insertSteps(recipes.get(i).getSteps(), recipes.get(i).getId());
            ContentValues cv = new ContentValues();
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_NAME, recipes.get(i).getName());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_INGREDIENTS, recipes.get(i).getIngredientsNumber());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_STEPS, recipes.get(i).getStepsNumber());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_SERVINGS, recipes.get(i).getServings());
            cv.put(Contracts.RecipesEntry.COLUMN_RECIPE_THUMBNAIL, recipes.get(i).getImage());
            insert(this, getContentResolver(), Contracts.RecipesEntry.CONTENT_URI, cv);
        }

    }

    private void insertIngredients(ArrayList<Ingredient> ingredients, int recipeId) {
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient currentIngredient = ingredients.get(i);
            ContentValues cv = new ContentValues();
            cv.put(Contracts.IngredientsEntry.COLUMN_INGREDIENT_RECIPE_ID, recipeId);
            cv.put(Contracts.IngredientsEntry.COLUMN_INGREDIENT_NAME, currentIngredient.getName());
            cv.put(Contracts.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY, currentIngredient.getQuantity());
            cv.put(Contracts.IngredientsEntry.COLUMN_INGREDIENT_MEASURE, currentIngredient.getMeasure());
            insert(this, getContentResolver(), Contracts.IngredientsEntry.CONTENT_URI, cv);

        }
    }

    private void insertSteps(ArrayList<Step> steps, int recipeId) {
        for (int i = 0; i < steps.size(); i++) {
            Step currentStep = steps.get(i);
            ContentValues cv = new ContentValues();
            cv.put(Contracts.StepsEntry.COLUMN_STEP_RECIPE_ID, recipeId);
            cv.put(Contracts.StepsEntry.COLUMN_STEP_DESCRIPTION, currentStep.getDescription());
            cv.put(Contracts.StepsEntry.COLUMN_STEP_SHORT_DESCRIPTION, currentStep.getShortDescription());
            cv.put(Contracts.StepsEntry.COLUMN_STEP_VIDEO_URL, currentStep.getVideoURL());
            cv.put(Contracts.StepsEntry.COLUMN_STEP_THUMBNAIL_URL, currentStep.getThumbnailURL());
            insert(this, getContentResolver(), Contracts.StepsEntry.CONTENT_URI, cv);
        }
    }

    private void displayRecipesFromDb() {
        recipes = new ArrayList<Recipe>();
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(Contracts.RecipesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Recipe recipe = new Recipe();
                int recipeId = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry._ID));
                String name = cursor.getString(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_NAME));
                int ingredientsNumber = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_INGREDIENTS));
                int stepsNumber = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_STEPS));
                int servings = cursor.getInt(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_SERVINGS));
                String thumbnail = cursor.getString(cursor.getColumnIndex(Contracts.RecipesEntry.COLUMN_RECIPE_THUMBNAIL));
                recipe.setId(recipeId);
                recipe.setName(name);
                recipe.setIngredientsNumber(ingredientsNumber);
                recipe.setStepsNumber(stepsNumber);
                recipe.setServings(servings);
                recipe.setImage(thumbnail);
                recipes.add(recipe);
                cursor.moveToNext();
            }
            cursor.close();
            adapter = new RecipesAdapter(SelectRecipeActivity.this, recipes);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.recyclerView.setAdapter(adapter);
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            binding.progressBar.setVisibility(View.VISIBLE);
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
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            binding.progressBar.setVisibility(View.GONE);

            if (result == 1) {
                putRecipesIntoDb();
                adapter = new RecipesAdapter(SelectRecipeActivity.this, recipes);
                binding.recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(SelectRecipeActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
