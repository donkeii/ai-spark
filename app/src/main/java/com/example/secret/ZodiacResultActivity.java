package com.example.secret;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ZodiacResultActivity extends AppCompatActivity {
    public static final String EXTRA_ZODIAC = "extra_zodiac";
    public static final String EXTRA_DAILY = "extra_daily";
    public static final String EXTRA_WEEKLY = "extra_weekly";
    public static final String EXTRA_MONTHLY = "extra_monthly";
    public static final String EXTRA_YEARLY = "extra_yearly";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zodiac_result);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_ZODIAC));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String daily = getIntent().getStringExtra(EXTRA_DAILY);
        String weekly = getIntent().getStringExtra(EXTRA_WEEKLY);
        String monthly = getIntent().getStringExtra(EXTRA_MONTHLY);
        String yearly = getIntent().getStringExtra(EXTRA_YEARLY);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ZodiacResultPagerAdapter adapter = new ZodiacResultPagerAdapter(this,
                daily, weekly, monthly, yearly);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Today"); break;
                case 1: tab.setText("Weekly"); break;
                case 2: tab.setText("Monthly"); break;
                case 3: tab.setText("Yearly"); break;
            }
        }).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


