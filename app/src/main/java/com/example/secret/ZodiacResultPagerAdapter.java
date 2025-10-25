package com.example.secret;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ZodiacResultPagerAdapter extends FragmentStateAdapter {
    private final String daily;
    private final String weekly;
    private final String monthly;
    private final String yearly;

    public ZodiacResultPagerAdapter(@NonNull FragmentActivity fa,
                                    String daily,
                                    String weekly,
                                    String monthly,
                                    String yearly) {
        super(fa);
        this.daily = daily;
        this.weekly = weekly;
        this.monthly = monthly;
        this.yearly = yearly;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return ZodiacSummaryFragment.newInstance("Today", daily);
            case 1: return ZodiacSummaryFragment.newInstance("Weekly", weekly);
            case 2: return ZodiacSummaryFragment.newInstance("Monthly", monthly);
            default: return ZodiacSummaryFragment.newInstance("Yearly", yearly);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}


