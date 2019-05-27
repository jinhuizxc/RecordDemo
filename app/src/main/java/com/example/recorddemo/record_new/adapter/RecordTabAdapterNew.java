package com.example.recorddemo.record_new.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.recorddemo.record.fragment.FilePreViewFragment;
import com.example.recorddemo.record.fragment.RecordFragment;
import com.example.recorddemo.record_new.fragment.FilePreViewFragmentNew;
import com.example.recorddemo.record_new.fragment.RecordFragmentNew;


public class RecordTabAdapterNew extends FragmentPagerAdapter {

    private String[] titles;

    public RecordTabAdapterNew(FragmentManager fm) {
        super(fm);
    }

    public RecordTabAdapterNew(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return RecordFragmentNew.newsInstance(position);
            }
            case 1: {
                return FilePreViewFragmentNew.newsInstance(position);
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
