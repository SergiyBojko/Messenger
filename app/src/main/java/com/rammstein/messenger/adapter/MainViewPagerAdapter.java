package com.rammstein.messenger.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.rammstein.messenger.fragment.ContactsFragment;
import com.rammstein.messenger.fragment.ProfileFragment;
import com.rammstein.messenger.fragment.RecentDialoguesFragment;

/**
 * Created by user on 12.05.2017.
 */

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            default:
            case 0:
                return new ProfileFragment();
            case 1 :
                return new ContactsFragment();
            case 2 :
                return new RecentDialoguesFragment();
        }
    }
}
