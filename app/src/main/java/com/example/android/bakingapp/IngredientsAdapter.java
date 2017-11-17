package com.example.android.bakingapp;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.CustomViewHolder> {
    private List<Ingredient> ingredientList;


    public IngredientsAdapter(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ingredient_item, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Ingredient ingredient = ingredientList.get(i);

        //Setting ingredient's name
        customViewHolder.txtVwIngredientName.setText(ingredient.getName());
        //Setting ingredient's quantity
        customViewHolder.txtVwIngredientQuantity.setText(String.valueOf(ingredient.getQuantity()));
        //Setting ingredient's measure
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
            this.txtVwIngredientName = (TextView) view.findViewById(R.id.txtVw_ingredient_name);
            this.txtVwIngredientQuantity = (TextView) view.findViewById(R.id.txtVw_ingredient_quantity);
            this.txtVwIngredientMeasure = (TextView) view.findViewById(R.id.txtVw_recipe_ingredient_measure);

        }


    }


}

