package com.amrizal.example.tabtester;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by amrizal.zainuddin on 30/3/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        if(position >= this.mNumOfTabs)
            return null;

        return StockFragment.newInstance(String.valueOf(position + 1), null);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
