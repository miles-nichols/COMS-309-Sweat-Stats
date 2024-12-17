// LiftPagerAdapter.java

package com.example.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Adapter for managing LiftFragments in a ViewPager2.
 */
public class LiftPagerAdapter extends FragmentStateAdapter {

    private List<ObjectLift> lifts;
    private ObjectActiveWorkout activeWorkout;
    private String completedWorkoutName;

    public LiftPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<ObjectLift> lifts, ObjectActiveWorkout activeWorkout, String completedWorkoutName) {
        super(fragmentActivity);
        this.lifts = lifts;
        this.activeWorkout = activeWorkout;
        this.completedWorkoutName = completedWorkoutName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ObjectLift lift = lifts.get(position);
        return LiftFragment.newInstance(lift.getTitle(), completedWorkoutName, activeWorkout);
    }

    @Override
    public int getItemCount() {
        return lifts.size();
    }
}
