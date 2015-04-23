package com.mineee.adapter;

/**
 * Created by keerthick on 4/13/2015.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mineee.fragment.NewsFeedListFragment;
import com.mineee.fragment.ProfileFragment;
import com.mineee.fragment.SettingsFragment;
import com.mineee.main.R;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private String loggedUserId = null;

    public SectionsPagerAdapter(FragmentManager fm,String loggedUserId) {
        super(fm);
        this.loggedUserId = loggedUserId;

    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0)
        return NewsFeedListFragment.newInstance(position + 1);
        else if(position == 1) {
                if (loggedUserId != null)
                    return ProfileFragment.newInstance(position + 1,loggedUserId);
                else
                   return null;
        }
        else
            return SettingsFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    //@Override
    public CharSequence getPageTitle(int position,Context context) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return context.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return context.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return context.getString(R.string.title_section3).toUpperCase(l);
        }
        return null;
    }
}