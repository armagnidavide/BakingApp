package com.example.android.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.Step;
import com.example.android.bakingapp.activities.DetailsActivity;
import com.example.android.bakingapp.fragments.StepDetailFragment;
import com.example.android.bakingapp.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The adapter used for the RecyclerView in SelectStepActivity
 */
public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.CustomViewHolder> {
    private List<Step> stepList;
    private Context mContext;
    private boolean mTwoPane;
    private int recipeId;
    private FragmentManager fragmentManager;

    public StepsAdapter(Context context, List<Step> stepList, boolean mTwoPane, int recipeId, FragmentManager fragmentManager) {
        this.stepList = stepList;
        this.mContext = context;
        this.mTwoPane = mTwoPane;
        this.recipeId = recipeId;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public StepsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.step_item, viewGroup, false);
        return new StepsAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StepsAdapter.CustomViewHolder customViewHolder, final int position) {
        customViewHolder.step = stepList.get(position);
        if (!TextUtils.isEmpty(customViewHolder.step.getThumbnailURL())) {
            Picasso.with(mContext).load(customViewHolder.step.getThumbnailURL())
                    .error(R.drawable.recipe_placeholder)
                    .placeholder(R.drawable.recipe_placeholder)
                    .into(customViewHolder.imgVwStepImage);
        } else {
            customViewHolder.imgVwStepImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.recipe_placeholder));
        }
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
                    Utils.openFragment(fragment, arguments, fragmentManager,R.id.stepList_frameLayout_fragment_container);
                } else {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra(StepDetailFragment.STEP_DESCRIPTION, customViewHolder.step.getDescription());
                    intent.putExtra(StepDetailFragment.STEP_VIDEO_URL, customViewHolder.step.getVideoURL());
                    intent.putExtra(StepDetailFragment.STEP_IMAGE_URL, customViewHolder.step.getThumbnailURL());
                    intent.putExtra(DetailsActivity.FRAGMENT_TYPE, DetailsActivity.FRAGMENT_STEP);
                    intent.putExtra(DetailsActivity.STEP_ADAPTER_POSITION, customViewHolder.getAdapterPosition());
                    intent.putExtra(DetailsActivity.RECIPE_ID, recipeId);
                    Utils.openActivity(mContext, intent);
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
            this.imgVwStepImage = (ImageView) view.findViewById(R.id.stepDetailFragment_imgVw_step_image);
            this.txtVwStepShortDescription = (TextView) view.findViewById(R.id.txtVw_recipe_step_short_description);

        }


    }
}
