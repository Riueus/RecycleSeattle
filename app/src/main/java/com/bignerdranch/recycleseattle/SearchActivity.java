package com.bignerdranch.android.recycleseattle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.android.recycleseattle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_information:
                    startActivity(new Intent(SearchActivity.this, InformationActivity.class));
                    return true;
                case R.id.navigation_location:
                    startActivity(new Intent(SearchActivity.this, MapActivity.class));
                    return true;
                case R.id.navigation_search:
                    return true;
                case R.id.navigation_settings:
                    startActivity(new Intent(SearchActivity.this, SettingsActivity.class));
                    return true;
            }
            return false;
        }
    };

    public static final String PRODUCT_EXISTS = "com.bignerdranch.android.recycolumbus.product_exists";
    public static final String BARCODE = "com.bignerdranch.android.recycolumbus.barcode";

    private static final int REQUEST_PHOTO = 0;
    private static final int CREATE_PRODUCT_ENTRY = 1;

    private Button mScannerButton;
    private TextView mSearchField;
    private ImageButton mSearchButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_search).setChecked(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("PRODUCTS");

        mSearchField = findViewById(R.id.search_search_field);

        mSearchButton = findViewById(R.id.search_search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String barcode = mSearchField.getText().toString();
                DatabaseReference productRef = mDatabase.child(barcode);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        startProductActivity(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                productRef.addListenerForSingleValueEvent(eventListener);
            }
        });

        mScannerButton = findViewById(R.id.search_scan_button);

        mScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanBarcodeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (scanBarcodeIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(scanBarcodeIntent, REQUEST_PHOTO);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_PHOTO) {
            Bundle extras = data.getExtras();
            Bitmap barcodeBitmap = (Bitmap) extras.get("data");
            FirebaseVisionImage barcode = FirebaseVisionImage.fromBitmap(barcodeBitmap);
            FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions
                    .Builder()
                    .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                    .build();

            FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                    .getVisionBarcodeDetector(options);
            Log.d("BARCODE", "I AM HERE");
            detector.detectInImage(barcode)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                            for (FirebaseVisionBarcode barcode: barcodes) {
                                Rect bounds = barcode.getBoundingBox();
                                Point[] corners = barcode.getCornerPoints();

                                String rawValue = barcode.getRawValue();
                                Log.d("BARCODE", "Hey " + rawValue);

                                int valueType = barcode.getValueType();
                                // See API reference for complete list of supported types
                                switch (valueType) {
                                    case FirebaseVisionBarcode.TYPE_WIFI:
                                        String ssid = barcode.getWifi().getSsid();
                                        String password = barcode.getWifi().getPassword();
                                        int type = barcode.getWifi().getEncryptionType();
                                        break;
                                    case FirebaseVisionBarcode.TYPE_URL:
                                        String title = barcode.getUrl().getTitle();
                                        String url = barcode.getUrl().getUrl();
                                        break;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("BARCODE", "DOOP");
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<FirebaseVisionBarcode>> task) {
                            Log.d("BARCODE", "DONE");
                        }
                    });
        } else if(requestCode == CREATE_PRODUCT_ENTRY){
            String barcode = mSearchField.getText().toString();
            String productName = CreateProductEntryFragment.getProductName(data);
            boolean isRecyclable = CreateProductEntryFragment.isRecyclable(data);
            createProduct(barcode, productName, isRecyclable);
        }

    }

    private void createProduct(String barcode, String productName, boolean isRecyclable) {
        FirebaseUser fbUser = mAuth.getCurrentUser();
        String userID = fbUser.getUid();
        Product prod = new Product(productName,isRecyclable, userID);
        mDatabase.child(barcode).setValue(prod);
    }

    private void startProductActivity(boolean productExists) {
        Intent prodIntent = ProductActivity.newIntent(SearchActivity.this);
        prodIntent.putExtra(PRODUCT_EXISTS, productExists);
        prodIntent.putExtra(BARCODE, mSearchField.getText().toString());
        startActivityForResult(prodIntent, CREATE_PRODUCT_ENTRY);
    }
}
