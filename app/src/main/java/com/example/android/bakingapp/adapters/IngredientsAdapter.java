package com.example.android.bakingapp.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.Ingredient;
import com.example.android.bakingapp.R;

import java.util.List;

/**
 * The adapter used for the recyclerView inside IngredientFragment
 */
public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.CustomViewHolder> {
    private List<Ingredient> ingredientList;


    public IngredientsAdapter(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ingredient_item, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Ingredient ingredient = ingredientList.get(i);

        customViewHolder.txtVwIngredientName.setText(ingredient.getName());
        customViewHolder.txtVwIngredientQuantity.setText(String.valueOf(ingredient.getQuantity()));
        customViewHolder.txtVwIngredientMeasure.setText(String.valueOf(ingredient.getMeasure()));

    }

    @Override
    public int getItemCount() {
        return (null != ingredientList ? ingredientList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView txtVwIngredientName;
        TextView txtVwIngredientQuantity;
        TextView txtVwIngredientMeasure;


        CustomViewHolder(View view) {
            super(view);
            this.txtVwIngredientName = (TextView) view.findViewById(R.id.ingredientItem_txtVw_ingredient_name);
            this.txtVwIngredientQuantity = (TextView) view.findViewById(R.id.ingredientItem_txtVw_ingredient_quantity);
            this.txtVwIngredientMeasure = (TextView) view.findViewById(R.id.ingredientItem_txtVw_recipe_ingredient_measure);

        }


    }


}

