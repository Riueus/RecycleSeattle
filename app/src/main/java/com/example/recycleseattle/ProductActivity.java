package com.bignerdranch.android.recycolumbus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ProductActivity extends AppCompatActivity {

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, ProductActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();

        if(fm.findFragmentById(R.id.fragment_container) == null) {
            Intent intent = getIntent();
            boolean productExists = intent.getBooleanExtra(SearchFragment.PRODUCT_EXISTS, false);
            attachProductFragment(fm, productExists);
        }

    }

    private void attachProductFragment(FragmentManager fm, boolean productExists) {
        Fragment prodFragment;
        if(productExists){
            prodFragment = new ProductEntryFragment();
        } else {
            prodFragment = new CreateProductEntryFragment();
        }
        fm.beginTransaction()
                .add(R.id.fragment_container, prodFragment)
                .commit();
    }
}
