package com.bignerdranch.android.recycleseattle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.recycleseattle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference mDB = FirebaseDatabase.getInstance().getReference("USERS");

            mDB.child(user.getUid()).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String score = dataSnapshot.getValue().toString();
                    int temp = Integer.valueOf(score);
                    temp++;
                    mDB.child(user.getUid()).child("score").setValue(temp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            prodFragment = new CreateProductEntryFragment();
        }
        fm.beginTransaction()
                .add(R.id.fragment_container, prodFragment)
                .commit();
    }
}
