package com.example.android.bakingapp;


import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.sql.Contracts;

import java.util.ArrayList;

public class IngredientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String INGREDIENT_RECIPE_ID = "recipe_id";
    private static final int LOADER_INGREDIENTS = 2;
    private static final String RECIPE_ID="recipe_id";
    private RecyclerView ingredientRecyclerView;
    private int recipeId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IngredientsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        getDataFromBundle(arguments);

    }

    private void takeIngredientsFromDb() {
        startIngredientsLoader();
    }

    private void startIngredientsLoader() {
        getActivity().getSupportLoaderManager().initLoader(LOADER_INGREDIENTS, null, this);
    }

    private void getDataFromBundle(Bundle arguments) {
        if (arguments.containsKey(INGREDIENT_RECIPE_ID)) {
            recipeId=arguments.getInt(INGREDIENT_RECIPE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        initializations(rootView);
        takeIngredientsFromDb();
        return rootView;
    }

    private void initializations(View rootView) {
        ingredientRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_ingredients);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_INGREDIENTS) {
            return new CursorLoader(getContext(), ContentUris.withAppendedId(Contracts.IngredientsEntry.CONTENT_URI,recipeId),
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            for (int i = 0; i < data.getCount(); i++) {
                Ingredient ingredient = new Ingredient();
                String ingredientName = data.getString(data.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_NAME));
                int ingredientQuantity = data.getInt(data.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY));
                String ingredientMeasure = data.getString(data.getColumnIndex(Contracts.IngredientsEntry.COLUMN_INGREDIENT_MEASURE));

                ingredient.setQuantity(ingredientQuantity);
                ingredient.setMeasure(ingredientMeasure);
                ingredient.setName(ingredientName);
                ingredientArrayList.add(ingredient);
                data.moveToNext();
            }
            setupRecyclerView(ingredientArrayList);
        }
    }

    private void setupRecyclerView(ArrayList<Ingredient> ingredientArrayList) {
        ingredientRecyclerView.setAdapter(new IngredientsAdapter(ingredientArrayList));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
