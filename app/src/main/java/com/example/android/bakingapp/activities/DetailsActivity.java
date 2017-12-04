package com.example.android.bakingapp.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Utils;
import com.example.android.bakingapp.fragments.IngredientsFragment;
import com.example.android.bakingapp.fragments.StepDetailFragment;
import com.example.android.bakingapp.sql.Contracts;

import java.util.ArrayList;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link .....}.
 */
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //for  intents
    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String STEP_ADAPTER_POSITION = "step_position";
    public static final String RECIPE_ID = "recipe_id";
    //Constants to differentiate  StepDetailFragment and IntentFragment
    public static final int FRAGMENT_STEP = 0;
    public static final int FRAGMENT_INGREDIENTS = 1;

    //loader id
    public static final int LOADER_RECIPE_STEPS = 5;

    //Ui buttons
    private Button btnNextStep;
    private Button btnPreviousStep;
    //step's data
    private String stepDescription;
    private String stepVideoURL;
    private String stepImageURL;

    private int recipeId;
    private int stepAdapterPosition;
    //if it's a StepDetailFragment or an IngredientsFragment
    private int fragmentType;

    //ArrayList to store data of all the Steps of the Recipe
    private ArrayList<String> stepDescriptionArrayList;
    private ArrayList<String> stepVideoUrlArrayList;
    private ArrayList<String> stepImageUrlArrayList;
    //number of steps for the current recipe
    private int numberOfSteps;

    //for the savedInstanceState
    private static final String INSTANCE_STATE_STEP_DESCRIPTION_ARRAYLIST = "step_description_arraylist";
    private static final String INSTANCE_STATE_STEP_VIDEOURL_ARRAYLIST = "step_videourl_arraylist";
    private static final String INSTANCE_STATE_STEP_IMAGEURL_ARRAYLIST = "step_imageurl_arraylist";
    private static final String INSTANCE_STATE_NUMBER_OF_STEPS = "number_of_steps";
    private static final String INSTANCE_STATE_STEP_ADAPTER_POSITION = "step_adapter_position";
    private static final String INSTANCE_STATE_RECIPE_ID = "recipe_id";
    private static final String INSTANCE_STATE_FRAGMENT_TYPE = "fragment_type";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initializations();
        Intent intent = getIntent();
        getDataFromIntent(intent);
        if (savedInstanceState == null || !savedInstanceState.containsKey(INSTANCE_STATE_RECIPE_ID)) {
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

    private void initializations() {
        btnNextStep = (Button) findViewById(R.id.detailsActivity_btn_next_step);
        btnPreviousStep = (Button) findViewById(R.id.detailsActivity_btn_previous_step);
        stepDescriptionArrayList = new ArrayList<>();
        stepVideoUrlArrayList = new ArrayList<>();
        stepImageUrlArrayList = new ArrayList<>();
    }

    /**
     * get data from the intent
     *
     * @param intent
     */
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
            fragmentType = FRAGMENT_INGREDIENTS;
        }

    }

    /**
     * disable Ui Buttons
     */
    private void disableButtons() {
        btnNextStep.setEnabled(false);
        btnPreviousStep.setEnabled(false);
    }

    /**
     * starts a loader to retrieve recipe steps data from the Db
     */
    private void startStepsLoader() {
        getSupportLoaderManager().initLoader(LOADER_RECIPE_STEPS, null, this);
    }

    /**
     * set onClickListeners for the Ui Buttons
     */
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

    /**
     * make the Ui Buttons disappear.
     */
    private void makeButtonsDisappear() {
        LinearLayout btnsNextPreviousContainer = (LinearLayout) findViewById(R.id.detailsActivity_linearLayout_btns_container);
        btnsNextPreviousContainer.setVisibility(View.GONE);
    }

    //restore fragment's information from the previous state(needed after a device rotation).
    private void restoreFragmentInformation(Bundle savedInstanceState) {
        fragmentType = savedInstanceState.getInt(INSTANCE_STATE_FRAGMENT_TYPE);
        recipeId = savedInstanceState.getInt(INSTANCE_STATE_RECIPE_ID);
        numberOfSteps = savedInstanceState.getInt(INSTANCE_STATE_NUMBER_OF_STEPS);
        stepAdapterPosition = savedInstanceState.getInt(INSTANCE_STATE_STEP_ADAPTER_POSITION);
        stepDescriptionArrayList = savedInstanceState.getStringArrayList(INSTANCE_STATE_STEP_DESCRIPTION_ARRAYLIST);
        stepVideoUrlArrayList = savedInstanceState.getStringArrayList(INSTANCE_STATE_STEP_VIDEOURL_ARRAYLIST);
        stepImageUrlArrayList = savedInstanceState.getStringArrayList(INSTANCE_STATE_STEP_IMAGEURL_ARRAYLIST);
        setPreviousStepValues();

    }

    /**
     * Enable the Ui Buttons.
     */
    private void enableButtons() {
        btnNextStep.setEnabled(true);
        btnPreviousStep.setEnabled(true);
    }
    /**
     * Create the detail fragment depending on fragmentType
     * ,set the arguments and pass both to launchFragment().
     */
    private void openFragment() {
        Bundle arguments = new Bundle();
        if (fragmentType == FRAGMENT_STEP) {
            arguments.putString(StepDetailFragment.STEP_DESCRIPTION, stepDescription);
            arguments.putString(StepDetailFragment.STEP_VIDEO_URL, stepVideoURL);
            arguments.putString(StepDetailFragment.STEP_IMAGE_URL, stepImageURL);
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            Utils.openFragment(fragment, arguments,getSupportFragmentManager(),R.id.detailsActivity_nestedScrollView_fragment_container);
        } else {
            arguments.putInt(IngredientsFragment.INGREDIENT_RECIPE_ID, recipeId);
            IngredientsFragment fragment = new IngredientsFragment();
            Utils.openFragment(fragment, arguments,getSupportFragmentManager(),R.id.detailsActivity_nestedScrollView_fragment_container);
        }

    }

    /**
     * set the values of the previous step
     */
    private void setPreviousStepValues() {
        stepDescription = stepDescriptionArrayList.get(stepAdapterPosition);
        stepVideoURL = stepVideoUrlArrayList.get(stepAdapterPosition);
        stepImageURL = stepImageUrlArrayList.get(stepAdapterPosition);
    }

    /**
     * create the fragment and its arguments necessary to display the next step of the recipe
     * and pass both to launchFragment().
     */
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
        Utils.openFragment(fragment, arguments,getSupportFragmentManager(),R.id.detailsActivity_nestedScrollView_fragment_container);
    }

    /**
     * create the fragment and its arguments necessary to display the previous step of the recipe
     * and pass both to launchFragment().
     */
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
        Utils.openFragment(fragment, arguments,getSupportFragmentManager(),R.id.detailsActivity_nestedScrollView_fragment_container);
    }




    /**
     * Retrieve from the db all the steps for the recipe with an id=recipeId
     *
     * @param id   loader id
     * @param args
     * @return CursorLoader with the Steps
     */
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

    /**
     * Store all Steps data inside ArrayLists
     *
     * @param loader
     * @param data
     */
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


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Save the Current State of a StepFragment
     *
     * @param savedInstanceState
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (fragmentType == FRAGMENT_STEP) {
            savedInstanceState.putStringArrayList(INSTANCE_STATE_STEP_DESCRIPTION_ARRAYLIST, stepDescriptionArrayList);
            savedInstanceState.putStringArrayList(INSTANCE_STATE_STEP_VIDEOURL_ARRAYLIST, stepVideoUrlArrayList);
            savedInstanceState.putStringArrayList(INSTANCE_STATE_STEP_IMAGEURL_ARRAYLIST, stepImageUrlArrayList);
            savedInstanceState.putInt(INSTANCE_STATE_NUMBER_OF_STEPS, numberOfSteps);
            savedInstanceState.putInt(INSTANCE_STATE_STEP_ADAPTER_POSITION, stepAdapterPosition);
            savedInstanceState.putInt(INSTANCE_STATE_RECIPE_ID, recipeId);
            savedInstanceState.putInt(INSTANCE_STATE_FRAGMENT_TYPE, fragmentType);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_go_to_catalog:
                Intent intent=new Intent(this,SelectRecipeActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
