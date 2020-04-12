package com.bignerdranch.android.recycleseattle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private Button delete_button;
    private Button changePass_button;
    private Button logout_button;
    private ImageButton icon_button;
    private TextView emailText;
    private TextView nameText;
    private TextView scoreText;
    private TextView rankText;
    private Uri imageUri;
    private int REQUEST_CODE = 1;
    private DatabaseReference mDB;
    private int[] arr = {10, 3, 5, 1};

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_information:
                    startActivity(new Intent(SettingsActivity.this, InformationActivity.class));
                    return true;
                case R.id.navigation_location:
                    startActivity(new Intent(SettingsActivity.this, MapActivity.class));
                    return true;
                case R.id.navigation_search:
                    startActivity(new Intent(SettingsActivity.this, SearchActivity.class));
                    return true;
                case R.id.navigation_settings:
                    return true;
            }
            return false;
        }
    };

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //navigation bar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_settings).setChecked(true);

        delete_button = findViewById(R.id.deleteAcctButt);
        changePass_button = findViewById(R.id.changePassBut);
        logout_button = findViewById(R.id.logoutBut);
        icon_button = findViewById(R.id.changeIconBut);
        emailText = findViewById(R.id.emailText);
        emailText.setFocusable(false);
        nameText = findViewById(R.id.nameText);
        scoreText = findViewById(R.id.scoreText);
        rankText = findViewById(R.id.rankText);
        mDB = FirebaseDatabase.getInstance().getReference("USERS");

        //Get user's email from firebase
        final boolean isConnected = hasNetworkConnection();
        if (isConnected == true) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String email = user.getEmail();
            mDB.child(user.getUid()).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String score = dataSnapshot.getValue().toString();
                    int temp = Integer.valueOf(score);
                    int rank = 1;
                    scoreText.setText(score);
                    for (int i : arr) {
                        if (temp < i) {
                            rank++;
                        }
                    }
                    rankText.setText(Integer.toString(rank));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            String name = user.getDisplayName();
            emailText.setText(email);
            nameText.setText(name);

        }

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected == true) {
                    Intent intent = new Intent(SettingsActivity.this, DeleteAccountFragment.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                    alert.setTitle("Warning");
                    alert.setMessage("No Internet Connection");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog temp = alert.create();
                    temp.show();
                }
            }
        });

        changePass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected == true) {
                    Intent intent = new Intent(SettingsActivity.this, ChangePasswordFragment.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                    alert.setTitle("Warning");
                    alert.setMessage("No Internet Connection");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog temp = alert.create();
                    temp.show();
                }
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        icon_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        if (isConnected == false) {
            AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
            alert.setTitle("Warning");
            alert.setMessage("No Internet Connection");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog temp = alert.create();
            temp.show();
        }
    }

    private void openGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select Picture"), REQUEST_CODE);

    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected = true;
        boolean isWifiAvailable = networkInfo.isAvailable();
        boolean isWifiConnected = networkInfo.isConnected();
        networkInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileAvailable = networkInfo.isAvailable();
        boolean isMobileConnnected = networkInfo.isConnected();
        isConnected = (isMobileAvailable && isMobileConnnected) ||
                (isWifiAvailable && isWifiConnected);
        return (isConnected);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                icon_button.setImageBitmap(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
