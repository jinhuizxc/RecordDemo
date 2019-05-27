package com.example.recorddemo.record.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.recorddemo.record.fragment.FilePreViewFragment;
import com.example.recorddemo.record.fragment.RecordFragment;


public class RecordTabAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public RecordTabAdapter(FragmentManager fm) {
        super(fm);
    }

    public RecordTabAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return RecordFragment.newsInstance(position);
            }
            case 1: {
                return FilePreViewFragment.newsInstance(position);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
