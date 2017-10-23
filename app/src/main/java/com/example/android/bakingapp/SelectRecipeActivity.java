package com.example.android.bakingapp;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.bakingapp.databinding.ActivitySelectRecipeBinding;

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


    ActivitySelectRecipeBinding binding;
    private static final String TAG = "RecyclerViewExample";
    private List<Recipe> Recipes;
    private MyRecyclerViewAdapter adapter;
    private static final String RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_recipe);
        setLayoutManager();
        adapter = new MyRecyclerViewAdapter(SelectRecipeActivity.this, null);
        binding.recyclerView.setAdapter(adapter);
        new DownloadTask().execute(RECIPE_URL);
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

                adapter = new MyRecyclerViewAdapter(SelectRecipeActivity.this, Recipes);
                binding.recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(SelectRecipeActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String result) {
        Log.d(TAG, "PARSE RESULT");
        try {
            //JSONObject response = new JSONObject(result);
            JSONArray response = new JSONArray(result);
            //JSONArray recipes = response.optJSONArray("reci");
            Recipes = new ArrayList<>();

            for (int i = 0; i < response.length(); i++) {
                JSONObject recipe = response.optJSONObject(i);
                Recipe item = new Recipe();
                item.setId(recipe.optInt("id"));
                item.setName(recipe.optString("name"));
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                ArrayList<RecipeIngredient> RecipeIngredients = createIngredientsArrayList(ingredients);
                item.setIngredients(RecipeIngredients);
                JSONArray steps = recipe.getJSONArray("steps");
                ArrayList<RecipeStep> recipeSteps = createRecipeStepsArrayList(steps);
                item.setRecipeSteps(recipeSteps);
                item.setServings(recipe.optInt("servings"));
                item.setImage(recipe.optString("image"));
                Recipes.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<RecipeIngredient> createIngredientsArrayList(JSONArray ingredients) {
        ArrayList<RecipeIngredient> recipeIngredients = new ArrayList<>();
        try {
            for (int i = 0; i < ingredients.length(); i++) {
                JSONObject ingredient = ingredients.getJSONObject(i);
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setQuantity(ingredient.optInt("quantity"));
                recipeIngredient.setMeasure(ingredient.optString("measure"));
                recipeIngredient.setIngredient(ingredient.optString("ingredient"));
                recipeIngredients.add(recipeIngredient);
            }
            Log.d(TAG, "yes createIngredientRecipes");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "no createIngredientRecipe");
        }
        Log.e(TAG, recipeIngredients.size() + "");
        return recipeIngredients;
    }

    private ArrayList<RecipeStep> createRecipeStepsArrayList(JSONArray steps) {
        ArrayList<RecipeStep> recipeSteps = new ArrayList<>();
        try {
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                RecipeStep recipeStep = new RecipeStep();
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
        Log.e(TAG, recipeSteps.size() + "");
        return recipeSteps;
    }


}
