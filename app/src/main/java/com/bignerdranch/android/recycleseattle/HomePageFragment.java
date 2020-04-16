package com.bignerdranch.android.recycleseattle;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePageFragment extends Fragment {

    public static final String BUTTON_PRESSED = "Was it sign in or was it log in?";

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("BLAH", "HELLO " + currentUser.getDisplayName());
            Intent infoIntent = InformationActivity.newIntent(getActivity());
            startActivity(infoIntent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);

        Button mLogInButton = v.findViewById(R.id.home_login_button);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAuthenticationActivity(view);
            }
        });

        Button mSignUpButton = v.findViewById(R.id.home_signup_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAuthenticationActivity(view);
            }
        });

        return v;
    }

    private void startAuthenticationActivity(View buttonPressed) {
        Intent authIntent = AuthenticationActivity.newIntent(getActivity());
        authIntent.putExtra(BUTTON_PRESSED, buttonPressed.getId());
        startActivity(authIntent);
    }
}
