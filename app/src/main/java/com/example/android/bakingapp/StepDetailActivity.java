package com.example.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link .....}.
 */
public class StepDetailActivity extends AppCompatActivity {
    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final int FRAGMENT_STEP = 0;
    public static final int FRAGMENT_INGREDIENT = 1;
    private String stepDescription;
    private String stepVideoURL;
    private String stepImageURL;
    private int recipeId;
    private int fragmentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail);
        Intent intent = getIntent();
        getDataFromIntent(intent);
        openFragment(savedInstanceState);
    }

    private void openFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            if (fragmentType == FRAGMENT_STEP) {
                arguments.putString(StepDetailFragment.STEP_DESCRIPTION, stepDescription);
                arguments.putString(StepDetailFragment.STEP_VIDEO_URL, stepVideoURL);
                arguments.putString(StepDetailFragment.STEP_IMAGE_URL, stepImageURL);
                StepDetailFragment fragment = new StepDetailFragment();
                fragment.setArguments(arguments);
                launchFragment(fragment, arguments);
            } else {
                arguments.putInt(IngredientsFragment.INGREDIENT_RECIPE_ID, recipeId);
                IngredientsFragment fragment = new IngredientsFragment();
                launchFragment(fragment, arguments);
            }

        }
    }

    private void launchFragment(Fragment fragment, Bundle arguments) {
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.item_detail_container, fragment)
                .commit();
    }


    private void getDataFromIntent(Intent intent) {
        if (intent.getIntExtra(FRAGMENT_TYPE, -1) == FRAGMENT_STEP) {
            stepDescription = intent.getStringExtra(StepDetailFragment.STEP_DESCRIPTION);
            stepVideoURL = intent.getStringExtra(StepDetailFragment.STEP_VIDEO_URL);
            stepImageURL = intent.getStringExtra(StepDetailFragment.STEP_IMAGE_URL);
            fragmentType = FRAGMENT_STEP;
        } else {
            recipeId = intent.getIntExtra(IngredientsFragment.INGREDIENT_RECIPE_ID, 0);
            fragmentType = FRAGMENT_INGREDIENT;
        }

    }

}
