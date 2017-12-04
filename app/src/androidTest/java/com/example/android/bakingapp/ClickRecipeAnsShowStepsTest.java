package com.example.android.bakingapp;


import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.activities.SelectRecipeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;



@RunWith(AndroidJUnit4.class)
public class ClickRecipeAnsShowStepsTest {
    @Rule
    public ActivityTestRule<SelectRecipeActivity> mActivityTestRule
            = new ActivityTestRule<>(SelectRecipeActivity.class);

    @Test
    public void click_recipe_to_open_SelectStepActivity() throws InterruptedException {

        onView(withId(R.id.selectRecipeActivity_recyclerView_recipes)).check(matches(isDisplayed()));
        onView(withId(R.id.selectRecipeActivity_recyclerView_recipes))
                .perform(
                      RecyclerViewActions.actionOnItemAtPosition(0,click())
                );
    }
}
