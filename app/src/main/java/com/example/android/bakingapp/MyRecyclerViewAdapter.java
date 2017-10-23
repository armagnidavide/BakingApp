package com.example.android.bakingapp;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder> {
    private List<Recipe> recipeList;
    private Context mContext;
    private static String TAG = "MyRecyclerViewAdapter";


    public MyRecyclerViewAdapter(Context context, List<Recipe> recipeList) {
        this.recipeList = recipeList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_item, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Recipe recipe = recipeList.get(i);

        //Render image using Picasso library
        if (!TextUtils.isEmpty(recipe.getImage())) {
            Picasso.with(mContext).load(recipe.getImage())
                    .error(R.drawable.recipe_placeholder)
                    .placeholder(R.drawable.recipe_placeholder)
                    .into(customViewHolder.imgVwRecipeImage);
        }

        //Setting text view title
        customViewHolder.txtVwRecipeName.setText(recipe.getName());
        //Setting text view steps number
        customViewHolder.txtVwRecipeStepsNumber.setText(recipe.getRecipeSteps().size() + "");
        //Setting text view ingredients number
        customViewHolder.txtVwRecipeIngredientsNumber.setText(recipe.getIngredients().size() + "");
    }

    @Override
    public int getItemCount() {
        return (null != recipeList ? recipeList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgVwRecipeImage;
        TextView txtVwRecipeName;
        TextView txtVwRecipeStepsNumber;
        TextView txtVwRecipeIngredientsNumber;


        CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.imgVwRecipeImage = (ImageView) view.findViewById(R.id.imgVw_recipe_image);
            this.txtVwRecipeName = (TextView) view.findViewById(R.id.txtVw_recipe_name);
            this.txtVwRecipeStepsNumber = (TextView) view.findViewById(R.id.txtVw_recipe_steps_number);
            this.txtVwRecipeIngredientsNumber = (TextView) view.findViewById(R.id.txtVw_recipe_ingredients_number);
        }

        @Override
        public void onClick(View view) {
            Recipe recipe = recipeList.get(getAdapterPosition());
            Intent intent = new Intent(mContext, SelectRecipeStepActivity.class);
            intent.putExtra("name", recipe.getName());
            intent.putExtra("recipeSteps", recipe.getRecipeSteps().size());
            intent.putExtra("recipeIngredients", recipe.getIngredients().size());
            mContext.startActivity(intent);
        }
    }


}

