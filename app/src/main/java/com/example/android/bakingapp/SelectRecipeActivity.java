package com.example.android.bakingapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.databinding.ActivitySelectRecipeBinding;

public class SelectRecipeActivity extends AppCompatActivity {


ActivitySelectRecipeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recipe);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_recipe);
        binding.txtVwHello.setText("Hello from Data Binding");
    }
}
