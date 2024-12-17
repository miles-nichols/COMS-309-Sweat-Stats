package com.example.fitnesstracker;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for managing and displaying fragments in a ViewPager.
 * This adapter is used to create and manage fragments dynamically in a ViewPager,
 * allowing fragments to be swiped between with corresponding titles.
 *
 * @author Miles Nichols
 */
public class AdapterViewPager extends FragmentPagerAdapter {

    /**
     * List of fragments to be displayed in the ViewPager.
     */
    private final List<Fragment> fragmentList = new ArrayList<>();

    /**
     * List of titles for the fragments.
     */
    private final List<String> fragmentTitleList = new ArrayList<>();

    /**
     * Constructor for AdapterViewPager.
     *
     * @param manager The FragmentManager that manages the fragments.
     */
    public AdapterViewPager(FragmentManager manager) {
        super(manager);
    }

    /**
     * Returns the fragment at the specified position in the fragment list.
     *
     * @param position The position of the fragment in the list.
     * @return The Fragment at the specified position.
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /**
     * Returns the total number of fragments in the list.
     *
     * @return The size of the fragment list.
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * Adds a fragment and its title to the adapter.
     *
     * @param fragment The fragment to be added.
     * @param title The title corresponding to the fragment.
     */
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    /**
     * Returns the title for the fragment at the specified position.
     *
     * @param position The position of the fragment in the list.
     * @return The title of the fragment at the specified position.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
