package com.example.android.bakingapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.android.bakingapp.sql.Contracts;

import java.util.ArrayList;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link .....}.
 */
public class StepDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final int FRAGMENT_STEP = 0;
    public static final int FRAGMENT_INGREDIENT = 1;
    public static final String STEP_ADAPTER_POSITION = "step_position";
    public static final String RECIPE_ID = "recipe_id";
    public static final int LOADER_RECIPE_STEPS = 5;
    private Button btnNextStep;
    private Button btnPreviousStep;
    private String stepDescription;
    private String stepVideoURL;
    private String stepImageURL;
    private int recipeId;
    private int fragmentType;
    private int stepAdapterPosition;
    private ArrayList<String> stepDescriptionArrayList;
    private ArrayList<String> stepVideoUrlArrayList;
    private ArrayList<String> stepImageUrlArrayList;
    private int numberOfSteps;

    private static final String INSTANCE_STATE_STEP_DESCRIPTION_ARRAYLIST="step_description_arraylist";
    private static final String INSTANCE_STATE_STEP_VIDEOURL_ARRAYLIST="step_videourl_arraylist";
    private static final String INSTANCE_STATE_STEP_IMAGEURL_ARRAYLIST="step_imageurl_arraylist";
    private static final String INSTANCE_STATE_NUMBER_OF_STEPS="number_of_steps";
    private static final String INSTANCE_STATE_STEP_ADAPTER_POSITION ="step_adapter_position";
    private static final String INSTANCE_STATE_RECIPE_ID="recipe_id";
    private static final String INSTANCE_STATE_FRAGMENT_TYPE ="fragment_type";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        initializations();
        Intent intent = getIntent();
        getDataFromIntent(intent);
        if (savedInstanceState==null||!savedInstanceState.containsKey(INSTANCE_STATE_RECIPE_ID)) {
            if (fragmentType == FRAGMENT_STEP) {
                disableButtons();
                startStepsLoader();
                setClickListeners();
            } else {
                makeButtonsDisappear();
            }
        } else {
            disableButtons();
            restoreFragmentInformation(savedInstanceState);
            setClickListeners();
            enableButtons();
        }
        openFragment();
    }

    private void restoreFragmentInformation(Bundle savedInstanceState) {
        fragmentType=savedInstanceState.getInt(INSTANCE_STATE_FRAGMENT_TYPE);
        recipeId=savedInstanceState.getInt(INSTANCE_STATE_RECIPE_ID);
        numberOfSteps=savedInstanceState.getInt(INSTANCE_STATE_NUMBER_OF_STEPS);
        stepAdapterPosition=savedInstanceState.getInt(INSTANCE_STATE_STEP_ADAPTER_POSITION);
        stepDescriptionArrayList=savedInstanceState.getStringArrayList(INSTANCE_STATE_STEP_DESCRIPTION_ARRAYLIST);
        stepVideoUrlArrayList=savedInstanceState.getStringArrayList(INSTANCE_STATE_STEP_VIDEOURL_ARRAYLIST);
        stepImageUrlArrayList=savedInstanceState.getStringArrayList(INSTANCE_STATE_STEP_IMAGEURL_ARRAYLIST);
        setPreviousValues();

    }

    private void setPreviousValues() {
        stepDescription=stepDescriptionArrayList.get(stepAdapterPosition);
        stepVideoURL=stepVideoUrlArrayList.get(stepAdapterPosition);
        stepImageURL=stepImageUrlArrayList.get(stepAdapterPosition);
    }

    private void startStepsLoader() {
        getSupportLoaderManager().initLoader(LOADER_RECIPE_STEPS, null, this);
    }

    private void setClickListeners() {
        btnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNextStep();
            }
        });
        btnPreviousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreviousStep();
            }
        });
    }

    private void disableButtons() {
        btnNextStep.setEnabled(false);
        btnPreviousStep.setEnabled(false);
    }

    private void initializations() {
        btnNextStep = (Button) findViewById(R.id.btn_next_step_detail_activity);
        btnPreviousStep = (Button) findViewById(R.id.btn_previous_step_detail_activity);
        stepDescriptionArrayList = new ArrayList<>();
        stepVideoUrlArrayList = new ArrayList<>();
        stepImageUrlArrayList = new ArrayList<>();
    }

    private void makeButtonsDisappear() {
        LinearLayout btnsNextPreviousContainer = (LinearLayout) findViewById(R.id.linearLayout_btns_container);
        btnsNextPreviousContainer.setVisibility(View.GONE);
    }

    private void openNextStep() {
        if (stepAdapterPosition < numberOfSteps - 1) {
            ++stepAdapterPosition;
        } else {
            stepAdapterPosition = 0;
        }
        stepDescription = stepDescriptionArrayList.get(stepAdapterPosition);
        stepVideoURL = stepVideoUrlArrayList.get(stepAdapterPosition);
        stepImageURL = stepImageUrlArrayList.get(stepAdapterPosition);
        Bundle arguments = new Bundle();
        arguments.putString(StepDetailFragment.STEP_DESCRIPTION, stepDescription);
        arguments.putString(StepDetailFragment.STEP_VIDEO_URL, stepVideoURL);
        arguments.putString(StepDetailFragment.STEP_IMAGE_URL, stepImageURL);
        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(arguments);
        launchFragment(fragment, arguments);
    }

    private void openPreviousStep() {
        if (stepAdapterPosition > 0) {
            --stepAdapterPosition;
        } else {
            stepAdapterPosition = numberOfSteps - 1;
        }
        stepDescription = stepDescriptionArrayList.get(stepAdapterPosition);
        stepVideoURL = stepVideoUrlArrayList.get(stepAdapterPosition);
        stepImageURL = stepImageUrlArrayList.get(stepAdapterPosition);
        Bundle arguments = new Bundle();
        arguments.putString(StepDetailFragment.STEP_DESCRIPTION, stepDescription);
        arguments.putString(StepDetailFragment.STEP_VIDEO_URL, stepVideoURL);
        arguments.putString(StepDetailFragment.STEP_IMAGE_URL, stepImageURL);
        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(arguments);
        launchFragment(fragment, arguments);
    }

    private void openFragment() {
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



    private void launchFragment(Fragment fragment, Bundle arguments) {
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
    }


    private void getDataFromIntent(Intent intent) {
        if (intent.getIntExtra(FRAGMENT_TYPE, -1) == FRAGMENT_STEP) {
            stepDescription = intent.getStringExtra(StepDetailFragment.STEP_DESCRIPTION);
            stepVideoURL = intent.getStringExtra(StepDetailFragment.STEP_VIDEO_URL);
            stepImageURL = intent.getStringExtra(StepDetailFragment.STEP_IMAGE_URL);
            stepAdapterPosition = intent.getIntExtra(STEP_ADAPTER_POSITION, 1);
            recipeId = intent.getIntExtra(RECIPE_ID, 1);
            fragmentType = FRAGMENT_STEP;

        } else {
            recipeId = intent.getIntExtra(IngredientsFragment.INGREDIENT_RECIPE_ID, 0);
            fragmentType = FRAGMENT_INGREDIENT;
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_RECIPE_STEPS) {
            return new CursorLoader(this, ContentUris.withAppendedId(Contracts.StepsEntry.CONTENT_URI, recipeId),
                    null,
                    null,
                    null,
                    null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            numberOfSteps = data.getCount();
            data.moveToFirst();
            for (int i = 0; i < data.getCount(); i++) {
                stepDescriptionArrayList.add(data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_DESCRIPTION)));
                stepVideoUrlArrayList.add(data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_VIDEO_URL)));
                stepImageUrlArrayList.add(data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_THUMBNAIL_URL)));
                data.moveToNext();
            }
            data.close();
            enableButtons();

        }


    }

    private void enableButtons() {
        btnNextStep.setEnabled(true);
        btnPreviousStep.setEnabled(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList(INSTANCE_STATE_STEP_DESCRIPTION_ARRAYLIST,stepDescriptionArrayList);
        savedInstanceState.putStringArrayList(INSTANCE_STATE_STEP_VIDEOURL_ARRAYLIST,stepVideoUrlArrayList);
        savedInstanceState.putStringArrayList(INSTANCE_STATE_STEP_IMAGEURL_ARRAYLIST,stepImageUrlArrayList);
        savedInstanceState.putInt(INSTANCE_STATE_NUMBER_OF_STEPS,numberOfSteps);
        savedInstanceState.putInt(INSTANCE_STATE_STEP_ADAPTER_POSITION,stepAdapterPosition);
        savedInstanceState.putInt(INSTANCE_STATE_RECIPE_ID,recipeId);
        savedInstanceState.putInt(INSTANCE_STATE_FRAGMENT_TYPE,fragmentType);


    }
}
