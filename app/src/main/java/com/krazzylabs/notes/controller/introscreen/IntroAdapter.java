package com.krazzylabs.notes.controller.introscreen;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Kiran Shinde on 7/17/2017.
 */

public class IntroAdapter extends FragmentPagerAdapter {

    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IntroFragment.newInstance(Color.parseColor("#ffcc00"), position);
            case 1:
                return IntroFragment.newInstance(Color.parseColor("#ffa000"), position);
            default:
                return IntroFragment.newInstance(Color.parseColor("#e67e22"), position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


}