package com.vineet.campusconnect.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vineet.campusconnect.fragments.LostFoundListFragment;

public class LostFoundPagerAdapter extends FragmentStateAdapter {

    public LostFoundPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Position 0 = "LOST" tab
        // Position 1 = "FOUND" tab
        if (position == 1) {
            return LostFoundListFragment.newInstance("FOUND");
        }
        return LostFoundListFragment.newInstance("LOST");
    }

    @Override
    public int getItemCount() {
        return 2; // We have 2 tabs
    }
}