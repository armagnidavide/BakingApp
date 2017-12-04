package com.example.android.bakingapp.fragments;


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

import com.example.android.bakingapp.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.IngredientsAdapter;
import com.example.android.bakingapp.sql.Contracts;

import java.util.ArrayList;

public class IngredientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //constant to retrieve the recipeId from the bundle
    public static final String INGREDIENT_RECIPE_ID = "recipe_id";
    //loader Id
    private static final int LOADER_INGREDIENTS = 10;
    //recyclerView to display the ingredients
    private RecyclerView ingredientRecyclerView;


    private int recipeId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        getDataFromBundle(arguments);
    }

    private void getDataFromBundle(Bundle arguments) {
        if (arguments.containsKey(INGREDIENT_RECIPE_ID)) {
            recipeId = arguments.getInt(INGREDIENT_RECIPE_ID);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        initializations(rootView);
        takeIngredientsFromDb();
        return rootView;
    }
    private void initializations(View rootView) {
        ingredientRecyclerView = (RecyclerView) rootView.findViewById(R.id.ingredientsFragment_recyclerView_ingredients);
    }

    private void takeIngredientsFromDb() {
        getActivity().getSupportLoaderManager().initLoader(LOADER_INGREDIENTS, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_INGREDIENTS) {
            return new CursorLoader(getContext(), ContentUris.withAppendedId(Contracts.IngredientsEntry.CONTENT_URI, recipeId),
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
