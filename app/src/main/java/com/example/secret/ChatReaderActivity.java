package com.example.secret;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChatReaderActivity extends AppCompatActivity {
    private ImageView selectedIcon;
    private TextView selectedName;
    private TextView selectedDate;
    private String selectedZodiac = "Aries";
    private GeminiClient geminiClient;

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
        geminiClient = new GeminiClient(BuildConfig.GEMINI_API_KEY);

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

        Button save = findViewById(R.id.btnSaveZodiac);
        save.setOnClickListener(v -> runAstroPrompt(save));
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
        selectedZodiac = name;
    }

    private void runAstroPrompt(Button saveButton) {
        saveButton.setEnabled(false);
        saveButton.setText("Đang tạo...");

        String systemPrompt = "As an experienced astrologer, your task is to write compelling and insightful daily, weekly, monthly and yearly horoscopes tailored for a [zodiac sign]. This will require deep knowledge of astrological charts, planetary movements, and their potential impacts on various signs. Your horoscopes should offer guidance, inspiration, and warnings where necessary, catering specifically to the needs, challenges, and opportunities of your target audience. Each prediction should be personalized and detailed, providing valuable and actionable insights. Ensure your horoscopes are accessible, engaging, and foster a sense of connection and trust with your readers. Answer in Vietnamese.";
        String userPrompt = "Hãy viết dự đoán cho cung: " + selectedZodiac + ". Trả lời bằng tiếng Việt và trình bày rõ 4 phần: 1) Hôm nay, 2) Tuần này, 3) Tháng này, 4) Năm nay. Mỗi phần 3-6 câu, có lời khuyên thực tế, tránh nói chung chung.";

        geminiClient.generateAsync(systemPrompt, userPrompt, new GeminiClient.GeminiCallback() {
            @Override
            public void onSuccess(String text) {
                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    saveButton.setText("Save");
                    new AlertDialog.Builder(ChatReaderActivity.this)
                            .setTitle(selectedZodiac)
                            .setMessage(text)
                            .setPositiveButton("OK", null)
                            .show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    saveButton.setText("Save");
                    new AlertDialog.Builder(ChatReaderActivity.this)
                            .setTitle("Lỗi")
                            .setMessage(message)
                            .setPositiveButton("OK", null)
                            .show();
                });
            }
        });
    }
}


