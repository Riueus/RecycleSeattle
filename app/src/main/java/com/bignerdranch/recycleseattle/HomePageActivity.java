package com.bignerdranch.android.recycleseattle;

import android.support.v4.app.Fragment;

public class HomePageActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new HomePageFragment();
    }


}
