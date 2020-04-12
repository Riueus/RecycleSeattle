package com.bignerdranch.android.recycleseattle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AuthenticationActivity extends AppCompatActivity {

    private Button mLogInToggle;
    private Button mSignUpToggle;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, AuthenticationActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_authentication);

        mLogInToggle = findViewById(R.id.log_in_toggle);
        mSignUpToggle = findViewById(R.id.sign_up_toggle);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(R.id.authentication_fragment_container) == null) {
            Intent intent = getIntent();
            int buttonPressedToInitiateActivity = intent.getIntExtra(HomePageFragment.BUTTON_PRESSED, 0);

            attachAuthenticationFragment(fm, buttonPressedToInitiateActivity);
        }

        mLogInToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.authentication_fragment_container, new LogInFragment())
                        .commit();
                mLogInToggle.setEnabled(false);
                mSignUpToggle.setEnabled(true);
            }
        });

        mSignUpToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.authentication_fragment_container, new SignUpFragment())
                        .commit();
                mSignUpToggle.setEnabled(false);
                mLogInToggle.setEnabled(true);
            }
        });

    }

    private void attachAuthenticationFragment(FragmentManager fm, int buttonPressed) {
        Fragment authFragment;
        switch (buttonPressed) {
            case R.id.home_login_button:
                authFragment = new LogInFragment();
                mLogInToggle.setEnabled(false);
                break;
            case R.id.home_signup_button:
                authFragment = new SignUpFragment();
                mSignUpToggle.setEnabled(false);
                break;
            default:
                authFragment = null;
                break;
        }

        fm.beginTransaction()
                .add(R.id.authentication_fragment_container, authFragment)
                .commit();
    }
}
