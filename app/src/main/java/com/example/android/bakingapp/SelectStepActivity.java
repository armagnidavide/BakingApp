package com.example.android.bakingapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.databinding.ActivitySelectRecipeStepBinding;
import com.example.android.bakingapp.sql.Contracts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ,....} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class SelectStepActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String RECIPE_ID = "recipe_id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_SERVING = "recipe_serving";
    private static final int LOADER_STEP = 1;
    ActivitySelectRecipeStepBinding binding;
    /**
     * Whether or not the activity is in two-pane mode
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private TextView txtVwRecipeName;
    private TextView txtVwRecipeServing;
    private Button btnShowIngredients;
    private int recipeId;
    private String recipeName;
    private int recipeServing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_recipe_step);
        getDataFromIntent();
        initializations();
        displayIntentData();
        startLoader();
        checkDetailContainerViewInsideLayout();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        recipeId = intent.getIntExtra(RECIPE_ID, 0);
        recipeName = intent.getStringExtra(RECIPE_NAME);
        recipeServing = intent.getIntExtra(RECIPE_SERVING, 0);
    }

    private void checkDetailContainerViewInsideLayout() {
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void startLoader() {
        getSupportLoaderManager().initLoader(LOADER_STEP, null, this);
    }

    private void initializations() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_steps);
        txtVwRecipeName = (TextView) findViewById(R.id.txtVw_recipe_name);
        txtVwRecipeServing = (TextView) findViewById(R.id.txtVw_recipe_serving);
        btnShowIngredients = (Button) findViewById(R.id.btn_show_ingredients);
    }

    private void displayIntentData() {
        txtVwRecipeName.setText(recipeName);
        txtVwRecipeServing.setText(String.valueOf(recipeServing));
    }

    public void displayIngredients(View v) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(IngredientsFragment.INGREDIENT_RECIPE_ID,recipeId);
            IngredientsFragment fragment = new IngredientsFragment();
            openFragment(fragment, arguments);
        } else {
            Context context = v.getContext();
            Intent intent = new Intent(context, StepDetailActivity.class);
            intent.putExtra(IngredientsFragment.INGREDIENT_RECIPE_ID,recipeId);
            openActivity(intent);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_STEP) {
            return new CursorLoader(this, ContentUris.withAppendedId(Contracts.StepsEntry.CONTENT_URI, recipeId),
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Step> stepArrayList = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            for (int i = 0; i < data.getCount(); i++) {
                Step step = new Step();
                String stepDescription = data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_DESCRIPTION));
                String stepImageUrl = data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_THUMBNAIL_URL));
                String stepShortDescription = data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_SHORT_DESCRIPTION));
                String stepVideoURL=data.getString(data.getColumnIndex(Contracts.StepsEntry.COLUMN_STEP_VIDEO_URL));

                step.setDescription(stepDescription);
                step.setThumbnailURL(stepImageUrl);
                step.setShortDescription(stepShortDescription);
                step.setVideoURL(stepVideoURL);
                stepArrayList.add(step);
                data.moveToNext();
            }
            setupRecyclerView(stepArrayList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void setupRecyclerView(ArrayList<Step> steps) {

        recyclerView.setAdapter(new StepRecyclerViewAdapter(this, steps));
    }

    public void openFragment(Fragment fragment, Bundle arguments) {
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
    }

    public void openActivity(Intent intent) {
        startActivity(intent);
    }

    public class StepRecyclerViewAdapter extends RecyclerView.Adapter<StepRecyclerViewAdapter.CustomViewHolder> {
        private List<Step> stepList;
        private Context mContext;


        public StepRecyclerViewAdapter(Context context, List<Step> stepList) {
            this.stepList = stepList;
            this.mContext = context;
        }

        @Override
        public StepRecyclerViewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.step_item, null);
            return new StepRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final StepRecyclerViewAdapter.CustomViewHolder customViewHolder, int position) {
            customViewHolder.step = stepList.get(position);
            //Render image using Picasso library
            if (!TextUtils.isEmpty(customViewHolder.step.getThumbnailURL())) {
                Picasso.with(mContext).load(customViewHolder.step.getThumbnailURL())
                        .error(R.drawable.recipe_placeholder)
                        .placeholder(R.drawable.recipe_placeholder)
                        .into(customViewHolder.imgVwStepImage);
            }else{
                customViewHolder.imgVwStepImage.setImageDrawable(getResources().getDrawable(R.drawable.recipe_placeholder));
            }
            //Setting step's short description
            customViewHolder.txtVwStepShortDescription.setText(customViewHolder.step.getShortDescription());
            customViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(StepDetailFragment.STEP_DESCRIPTION, customViewHolder.step.getDescription());
                        arguments.putString(StepDetailFragment.STEP_VIDEO_URL, customViewHolder.step.getVideoURL());
                        arguments.putString(StepDetailFragment.STEP_IMAGE_URL, customViewHolder.step.getThumbnailURL());
                        StepDetailFragment fragment = new StepDetailFragment();
                        openFragment(fragment, arguments);

                    } else {
                        Intent intent = new Intent(getApplicationContext(), StepDetailActivity.class);
                        intent.putExtra(StepDetailFragment.STEP_DESCRIPTION, customViewHolder.step.getDescription());
                        intent.putExtra(StepDetailFragment.STEP_VIDEO_URL, customViewHolder.step.getVideoURL());
                        intent.putExtra(StepDetailFragment.STEP_IMAGE_URL, customViewHolder.step.getThumbnailURL());
                        intent.putExtra(StepDetailActivity.FRAGMENT_TYPE,StepDetailActivity.FRAGMENT_STEP);
                        openActivity(intent);


                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != stepList ? stepList.size() : 0);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            View view;
            ImageView imgVwStepImage;
            TextView txtVwStepShortDescription;
            Step step;

            CustomViewHolder(View view) {
                super(view);
                this.view = view;
                this.imgVwStepImage = (ImageView) view.findViewById(R.id.imgVw_recipe_step_image);
                this.txtVwStepShortDescription = (TextView) view.findViewById(R.id.txtVw_recipe_step_short_description);

            }


        }
    }

}
