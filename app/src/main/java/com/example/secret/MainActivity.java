package com.example.secret;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Apply saved theme before rendering UI
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int savedMode = prefs.getInt("nightMode", AppCompatDelegate.MODE_NIGHT_YES);
        AppCompatDelegate.setDefaultNightMode(savedMode);
        setContentView(R.layout.activity_main);

        // Tarot Reading Cards - sử dụng đúng ID từ layout
        findViewById(R.id.cardTarotReading).setOnClickListener(v -> openFeature("Get Tarot Reading"));
        findViewById(R.id.cardChatReader).setOnClickListener(v -> {
            Intent i = new Intent(this, ChatReaderActivity.class);
            startActivity(i);
        });
        findViewById(R.id.cardDailyCard).setOnClickListener(v -> openFeature("Daily Card"));
        findViewById(R.id.cardYesOrNo).setOnClickListener(v -> openFeature("Yes or No"));

        // Spin wheel button
        findViewById(R.id.btnSpinWheel).setOnClickListener(v -> openFeature("Spin the Wheel"));

        // Options button popup
        Button optionsBtn = findViewById(R.id.btnOptions);
        if (optionsBtn != null) {
            optionsBtn.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(this, v);
                popup.inflate(R.menu.menu_options);
                popup.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
                popup.show();
            });
        }

        populateGreeting();
    }

    private boolean onBottomItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_tarot) {
            return true; // already here
        }
        if (item.getItemId() == R.id.nav_horoscope) {
            startActivity(new Intent(this, HoroscopeActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        if (item.getItemId() == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        return false;
    }

    private void openFeature(String title) {
        Intent i = new Intent(this, FeatureActivity.class);
        i.putExtra(FeatureActivity.EXTRA_TITLE, title);
        startActivity(i);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_theme_dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit().putInt("nightMode", AppCompatDelegate.MODE_NIGHT_YES).apply();
            return true;
        }
        if (item.getItemId() == R.id.menu_theme_light) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit().putInt("nightMode", AppCompatDelegate.MODE_NIGHT_NO).apply();
            return true;
        }
        if (item.getItemId() == R.id.menu_privacy) {
            openFeature("Privacy Policy");
            return true;
        }
        if (item.getItemId() == R.id.menu_version) {
            Toast.makeText(this, "Version: " + getAppVersionName(), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private String getAppVersionName() {
        try {
            PackageInfo p = getPackageManager().getPackageInfo(getPackageName(), 0);
            return p.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private void populateGreeting() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", "Bạn");

        String greeting = getGreetingPrefix() + ", " + fullName;
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) tvGreeting.setText(greeting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateGreeting();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate parseDob(String dob) {
        try {
            DateTimeFormatter fmt = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return LocalDate.parse(dob, fmt);
            }
        } catch (DateTimeParseException e) {
            return null;
        }
        return null;
    }

    private String getGreetingPrefix() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Chào buổi sáng";
        if (hour < 18) return "Chào buổi chiều";
        return "Chào buổi tối";
    }
}