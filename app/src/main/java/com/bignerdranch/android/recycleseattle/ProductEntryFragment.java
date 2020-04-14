package com.bignerdranch.android.recycolumbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductEntryFragment extends Fragment {
    private TextView mBarcodeField;
    private TextView mNameField;
    private TextView mRecyclabilityField;

    private DatabaseReference mDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = FirebaseDatabase.getInstance().getReference("PRODUCTS");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_entry, container,false);

        mBarcodeField = v.findViewById(R.id.product_barcode);
        mNameField = v.findViewById(R.id.product_name);
        mRecyclabilityField = v.findViewById(R.id.product_recyclability);

        Intent productIntent = getActivity().getIntent();

        final String barcode = productIntent.getStringExtra(SearchFragment.BARCODE);

        DatabaseReference productRef = mDB.child(barcode);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product prod = dataSnapshot.getValue(Product.class);
                mBarcodeField.setText(barcode);
                mNameField.setText(prod.name);
                mRecyclabilityField.setText(prod.isRecyclable ? "Yes" : "No");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        productRef.addValueEventListener(eventListener);

        return v;
    }
}
