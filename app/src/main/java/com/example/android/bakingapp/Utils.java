package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


public class Utils {
    public static void openFragment(Fragment fragment, Bundle arguments, FragmentManager fragmentManager,int layoutFragmentContainer) {
        fragment.setArguments(arguments);
        fragmentManager.beginTransaction()
                .replace(layoutFragmentContainer, fragment)
                .commit();
    }

    public static void openActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }

}
