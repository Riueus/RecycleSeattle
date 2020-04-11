package com.bignerdranch.android.recycolumbus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CreateProductEntryFragment extends Fragment {
    private static final String PRODUCT_NAME =
            "com.bignerdranch.android.recycolumbus.product_name";
    private static final String IS_RECYCLABLE =
            "com.bignerdranch.android.recycolumbus.is_recyclable";

    private TextView mProductNameField;

    public static boolean isRecyclable(Intent result) {
        return result.getBooleanExtra(IS_RECYCLABLE, false);
    }

    public static String getProductName(Intent result) {
        return result.getStringExtra(PRODUCT_NAME);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_product_entry, container, false);

        mProductNameField = v.findViewById(R.id.create_name_field);
        Button mYesButton = v.findViewById(R.id.create_yes_button);
        Button mNoButton = v.findViewById(R.id.create_no_button);

        mYesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setData(true);
            }
        });

        mNoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setData(false);
            }
        });

        return v;
    }

    private void setData(boolean isRecyclable){
        Intent data = new Intent();
        data.putExtra(PRODUCT_NAME, mProductNameField.getText().toString());
        data.putExtra(IS_RECYCLABLE, isRecyclable);
        if(getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        } else {
            Log.e("NULL","Null encountered when trying to setData");
        }
    }
}
