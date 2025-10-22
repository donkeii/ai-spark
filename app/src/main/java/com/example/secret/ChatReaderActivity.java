package com.example.secret;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChatReaderActivity extends AppCompatActivity {
    private ImageView selectedIcon;
    private TextView selectedName;
    private TextView selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_reader);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.chat_tarot_reader));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectedIcon = findViewById(R.id.selectedIcon);
        selectedName = findViewById(R.id.selectedName);
        selectedDate = findViewById(R.id.selectedDate);

        wireZodiac(R.id.itemAquarius, "Aquarius", "Jan 20 - Feb 18");
        wireZodiac(R.id.itemPisces, "Pisces", "Feb 19 - Mar 20");
        wireZodiac(R.id.itemAries, "Aries", "Mar 21 - Apr 19");
        wireZodiac(R.id.itemTaurus, "Taurus", "Apr 20 - May 20");
        wireZodiac(R.id.itemGemini, "Gemini", "May 21 - Jun 20");
        wireZodiac(R.id.itemCancer, "Cancer", "Jun 21 - Jul 22");
        wireZodiac(R.id.itemLeo, "Leo", "Jul 23 - Aug 22");
        wireZodiac(R.id.itemVirgo, "Virgo", "Aug 23 - Sep 22");
        wireZodiac(R.id.itemLibra, "Libra", "Sep 23 - Oct 22");
        wireZodiac(R.id.itemScorpio, "Scorpio", "Oct 23 - Nov 21");
        wireZodiac(R.id.itemSagittarius, "Sagittarius", "Nov 22 - Dec 21");
        wireZodiac(R.id.itemCapricorn, "Capricorn", "Dec 22 - Jan 19");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void wireZodiac(int viewId, String name, String date) {
        View item = findViewById(viewId);
        if (item == null) return;
        item.setOnClickListener(v -> selectZodiac(name, date));
    }

    private void selectZodiac(String name, String date) {
        selectedName.setText(name);
        selectedDate.setText(date);
        // Placeholder icon until individual zodiac icons are added
        selectedIcon.setImageResource(R.mipmap.ic_launcher_round);
    }
}


