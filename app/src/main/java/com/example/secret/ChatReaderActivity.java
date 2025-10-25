package com.example.secret;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
// import removed: Intent not used after removing Save flow
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

        Button btnDaily = findViewById(R.id.btnDaily);
        Button btnWeekly = findViewById(R.id.btnWeekly);
        Button btnMonthly = findViewById(R.id.btnMonthly);
        Button btnYearly = findViewById(R.id.btnYearly);

        btnDaily.setOnClickListener(v -> runAstroPrompt(btnDaily, "daily"));
        btnWeekly.setOnClickListener(v -> runAstroPrompt(btnWeekly, "weekly"));
        btnMonthly.setOnClickListener(v -> runAstroPrompt(btnMonthly, "monthly"));
        btnYearly.setOnClickListener(v -> runAstroPrompt(btnYearly, "yearly"));
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

    private void runAstroPrompt(Button button, String predictionType) {
        button.setEnabled(false);
        button.setText("Đang tạo...");

        String systemPrompt = getSystemPrompt(predictionType);
        String userPrompt = getUserPrompt(predictionType);

        geminiClient.generateAsync(systemPrompt, userPrompt, new GeminiClient.GeminiCallback() {
            @Override
            public void onSuccess(String text) {
                runOnUiThread(() -> {
                    button.setEnabled(true);
                    button.setText(getButtonText(predictionType));
                    new AlertDialog.Builder(ChatReaderActivity.this)
                            .setTitle(selectedZodiac + " - " + getTitleText(predictionType))
                            .setMessage(text)
                            .setPositiveButton("OK", null)
                            .show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    button.setEnabled(true);
                    button.setText(getButtonText(predictionType));
                    new AlertDialog.Builder(ChatReaderActivity.this)
                            .setTitle("Lỗi")
                            .setMessage(message)
                            .setPositiveButton("OK", null)
                            .show();
                });
            }
        });
    }

    private String getSystemPrompt(String predictionType) {
        String basePrompt = "As an experienced astrologer, your task is to write compelling and insightful horoscopes tailored for a [zodiac sign]. This will require deep knowledge of astrological charts, planetary movements, and their potential impacts on various signs. Your horoscopes should offer guidance, inspiration, and warnings where necessary, catering specifically to the needs, challenges, and opportunities of your target audience. Each prediction should be personalized and detailed, providing valuable and actionable insights. Ensure your horoscopes are accessible, engaging, and foster a sense of connection and trust with your readers. Answer in Vietnamese.";
        
        switch (predictionType) {
            case "daily":
                return basePrompt + " Focus specifically on today's energy, opportunities, and challenges.";
            case "weekly":
                return basePrompt + " Focus specifically on this week's trends, major themes, and weekly guidance.";
            case "monthly":
                return basePrompt + " Focus specifically on this month's overall energy, major themes, and monthly guidance.";
            case "yearly":
                return basePrompt + " Focus specifically on this year's overall energy, major themes, and yearly guidance.";
            default:
                return basePrompt;
        }
    }

    private String getUserPrompt(String predictionType) {
        switch (predictionType) {
            case "daily":
                return "Hãy viết dự đoán cho cung " + selectedZodiac + " cho hôm nay. Trả lời bằng tiếng Việt, 3-6 câu, có lời khuyên thực tế, tránh nói chung chung. Tập trung vào năng lượng, cơ hội và thách thức của ngày hôm nay.";
            case "weekly":
                return "Hãy viết dự đoán cho cung " + selectedZodiac + " cho tuần này. Trả lời bằng tiếng Việt, 3-6 câu, có lời khuyên thực tế, tránh nói chung chung. Tập trung vào xu hướng, chủ đề chính và hướng dẫn cho tuần này.";
            case "monthly":
                return "Hãy viết dự đoán cho cung " + selectedZodiac + " cho tháng này. Trả lời bằng tiếng Việt, 3-6 câu, có lời khuyên thực tế, tránh nói chung chung. Tập trung vào năng lượng tổng thể, chủ đề chính và hướng dẫn cho tháng này.";
            case "yearly":
                return "Hãy viết dự đoán cho cung " + selectedZodiac + " cho năm nay. Trả lời bằng tiếng Việt, 3-6 câu, có lời khuyên thực tế, tránh nói chung chung. Tập trung vào năng lượng tổng thể, chủ đề chính và hướng dẫn cho năm này.";
            default:
                return "Hãy viết dự đoán cho cung " + selectedZodiac + ".";
        }
    }

    private String getButtonText(String predictionType) {
        switch (predictionType) {
            case "daily":
                return "Hôm nay";
            case "weekly":
                return "Tuần này";
            case "monthly":
                return "Tháng này";
            case "yearly":
                return "Năm nay";
            default:
                return "Dự đoán";
        }
    }

    private String getTitleText(String predictionType) {
        switch (predictionType) {
            case "daily":
                return "Dự đoán hôm nay";
            case "weekly":
                return "Dự đoán tuần này";
            case "monthly":
                return "Dự đoán tháng này";
            case "yearly":
                return "Dự đoán năm nay";
            default:
                return "Dự đoán";
        }
    }

    // Save flow removed; no generate-all logic needed here
}


