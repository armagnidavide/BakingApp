package com.example.android.bakingapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Recipe;
import com.example.android.bakingapp.activities.SelectStepActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The Adapter used for the RecyclerView in SelectRecipeActivity.
 */
public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.CustomViewHolder> {
    private List<Recipe> recipeList;
    private Context mContext;


    public RecipesAdapter(Context context, List<Recipe> recipeList) {
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
        //Setting text view ingredients number
        customViewHolder.txtVwRecipeIngredientsNumber.setText(String.valueOf(recipe.getIngredientsNumber()));
        //Setting text view steps number
        customViewHolder.txtVwRecipeStepsNumber.setText(String.valueOf(recipe.getStepsNumber()));
        //Setting text view steps number
        customViewHolder.txtVwRecipeServing.setText(String.valueOf(recipe.getServings()));
    }

    @Override
    public int getItemCount() {
        return (null != recipeList ? recipeList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgVwRecipeImage;
        TextView txtVwRecipeName;
        TextView txtVwRecipeIngredientsNumber;
        TextView txtVwRecipeStepsNumber;
        TextView txtVwRecipeServing;


        CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.imgVwRecipeImage = (ImageView) view.findViewById(R.id.imgVw_recipe_image);
            this.txtVwRecipeName = (TextView) view.findViewById(R.id.recipeItem_txtVw_recipe_name);
            this.txtVwRecipeIngredientsNumber = (TextView) view.findViewById(R.id.txtVw_recipe_ingredients_number);
            this.txtVwRecipeStepsNumber = (TextView) view.findViewById(R.id.txtVw_recipe_steps_number);
            this.txtVwRecipeServing = (TextView) view.findViewById(R.id.selectStepActivity_txtVw_recipe_serving);

        }
        /**
         * When the user clicks a recipe, SelectRecipeActivity is opened
         */
        @Override
        public void onClick(View view) {
            Recipe recipe = recipeList.get(getAdapterPosition());
            Intent intent = new Intent(mContext, SelectStepActivity.class);
            intent.setAction(SelectStepActivity.ACTION_GET_RECIPE_DATA_FROM_THIS_INTENT);
            intent.putExtra(SelectStepActivity.RECIPE_ID, recipe.getId());
            intent.putExtra(SelectStepActivity.RECIPE_NAME, recipe.getName());
            intent.putExtra(SelectStepActivity.RECIPE_SERVING, recipe.getServings());
            intent.putExtra(SelectStepActivity.RECIPE_LAST_TIME_USED, recipe.getLastTimeUsed());
            mContext.startActivity(intent);
        }
    }


}

