package com.example.secret;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FeatureActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "title";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title != null && title.equals(getString(R.string.chat_tarot_reader))) {
            setContentView(R.layout.activity_chat_reader);
        } else {
            setContentView(R.layout.activity_feature);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title != null ? title : getString(R.string.app_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}