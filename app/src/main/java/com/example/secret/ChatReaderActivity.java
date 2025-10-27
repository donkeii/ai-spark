package com.example.secret;

import android.os.Bundle;
import android.content.Intent;
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
    private String selectedDateRange = "21 tháng Năm - 20 tháng Sáu";
    private GeminiClient geminiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_reader);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.chat_tarot_reader));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        geminiClient = new GeminiClient(BuildConfig.GEMINI_API_KEY);

        // Bind new header views
        selectedIcon = findViewById(R.id.imgBadge);
        selectedName = findViewById(R.id.txtZodiac);
        selectedDate = findViewById(R.id.txtDateRange);

        // Wire 12 zodiac tiles to update the header
        wireZodiac(R.id.itemAries, "Bạch Dương", "21 tháng Ba - 19 tháng Tư");
        wireZodiac(R.id.itemTaurus, "Kim Ngưu", "20 tháng Tư - 20 tháng Năm");
        wireZodiac(R.id.itemGemini, "Song Tử", "21 tháng Năm - 20 tháng Sáu");
        wireZodiac(R.id.itemCancer, "Cự Giải", "21 tháng Sáu - 22 tháng Bảy");
        wireZodiac(R.id.itemLeo, "Sư Tử", "23 tháng Bảy - 22 tháng Tám");
        wireZodiac(R.id.itemVirgo, "Xử Nữ", "23 tháng Tám - 22 tháng Chín");
        wireZodiac(R.id.itemLibra, "Thiên Bình", "23 tháng Chín - 22 tháng Mười");
        wireZodiac(R.id.itemScorpio, "Bọ Cạp", "23 tháng Mười - 21 tháng Mười một");
        wireZodiac(R.id.itemSagittarius, "Nhân Mã", "22 tháng Mười một - 21 tháng Mười hai");
        wireZodiac(R.id.itemCapricorn, "Ma Kết", "22 tháng Mười hai - 19 tháng Một");
        wireZodiac(R.id.itemAquarius, "Bảo Bình", "20 tháng Một - 18 tháng Hai");
        wireZodiac(R.id.itemPisces, "Song Ngư", "19 tháng Hai - 20 tháng Ba");

        Button btnInterpret = findViewById(R.id.btnInterpret);
        if (btnInterpret != null) {
            btnInterpret.setOnClickListener(v -> generateAllAndOpen(btnInterpret));
        }
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

    private void selectZodiac(String zodiacName, String dateRange) {
        selectedName.setText(zodiacName);
        selectedDate.setText(dateRange);
        // Temporary emblem until per-zodiac icons are added
        selectedIcon.setImageResource(R.mipmap.ic_launcher_round);
        selectedZodiac = zodiacName;
        selectedDateRange = dateRange;
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

    private void generateAllAndOpen(Button trigger) {
        trigger.setEnabled(false);
        trigger.setText("Đang tạo...");

        final String[] daily = {""};
        final String[] weekly = {""};
        final String[] monthly = {""};
        final String[] yearly = {""};

        geminiClient.generateAsync(getSystemPrompt("daily"), getUserPrompt("daily"), new GeminiClient.GeminiCallback() {
            @Override
            public void onSuccess(String text) {
                daily[0] = text;
                geminiClient.generateAsync(getSystemPrompt("weekly"), getUserPrompt("weekly"), new GeminiClient.GeminiCallback() {
                    @Override
                    public void onSuccess(String text) {
                        weekly[0] = text;
                        geminiClient.generateAsync(getSystemPrompt("monthly"), getUserPrompt("monthly"), new GeminiClient.GeminiCallback() {
                            @Override
                            public void onSuccess(String text) {
                                monthly[0] = text;
                                geminiClient.generateAsync(getSystemPrompt("yearly"), getUserPrompt("yearly"), new GeminiClient.GeminiCallback() {
                                    @Override
                                    public void onSuccess(String text) {
                                        yearly[0] = text;
                                        runOnUiThread(() -> {
                                            trigger.setEnabled(true);
                                            trigger.setText("Interpret");
                                            Intent i = new Intent(ChatReaderActivity.this, ZodiacResultActivity.class);
                                            i.putExtra(ZodiacResultActivity.EXTRA_ZODIAC, selectedZodiac);
                                            i.putExtra(ZodiacResultActivity.EXTRA_DATE_RANGE, selectedDateRange);
                                            i.putExtra(ZodiacResultActivity.EXTRA_DAILY, daily[0]);
                                            i.putExtra(ZodiacResultActivity.EXTRA_WEEKLY, weekly[0]);
                                            i.putExtra(ZodiacResultActivity.EXTRA_MONTHLY, monthly[0]);
                                            i.putExtra(ZodiacResultActivity.EXTRA_YEARLY, yearly[0]);
                                            startActivity(i);
                                        });
                                    }

                                    @Override
                                    public void onError(String message) {
                                        runOnUiThread(() -> {
                                            trigger.setEnabled(true);
                                            trigger.setText("Interpret");
                                            Intent i = new Intent(ChatReaderActivity.this, ZodiacResultActivity.class);
                                            i.putExtra(ZodiacResultActivity.EXTRA_ZODIAC, selectedZodiac);
                                            i.putExtra(ZodiacResultActivity.EXTRA_DATE_RANGE, selectedDateRange);
                                            i.putExtra(ZodiacResultActivity.EXTRA_DAILY, daily[0]);
                                            i.putExtra(ZodiacResultActivity.EXTRA_WEEKLY, weekly[0]);
                                            i.putExtra(ZodiacResultActivity.EXTRA_MONTHLY, monthly[0]);
                                            i.putExtra(ZodiacResultActivity.EXTRA_YEARLY, "");
                                            startActivity(i);
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onError(String message) {
                                runOnUiThread(() -> {
                                    trigger.setEnabled(true);
                                    trigger.setText("Interpret");
                                    Intent i = new Intent(ChatReaderActivity.this, ZodiacResultActivity.class);
                                    i.putExtra(ZodiacResultActivity.EXTRA_ZODIAC, selectedZodiac);
                                    i.putExtra(ZodiacResultActivity.EXTRA_DATE_RANGE, selectedDateRange);
                                    i.putExtra(ZodiacResultActivity.EXTRA_DAILY, daily[0]);
                                    i.putExtra(ZodiacResultActivity.EXTRA_WEEKLY, weekly[0]);
                                    i.putExtra(ZodiacResultActivity.EXTRA_MONTHLY, "");
                                    i.putExtra(ZodiacResultActivity.EXTRA_YEARLY, "");
                                    startActivity(i);
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            trigger.setEnabled(true);
                            trigger.setText("Interpret");
                            Intent i = new Intent(ChatReaderActivity.this, ZodiacResultActivity.class);
                            i.putExtra(ZodiacResultActivity.EXTRA_ZODIAC, selectedZodiac);
                            i.putExtra(ZodiacResultActivity.EXTRA_DATE_RANGE, selectedDateRange);
                            i.putExtra(ZodiacResultActivity.EXTRA_DAILY, daily[0]);
                            i.putExtra(ZodiacResultActivity.EXTRA_WEEKLY, "");
                            i.putExtra(ZodiacResultActivity.EXTRA_MONTHLY, "");
                            i.putExtra(ZodiacResultActivity.EXTRA_YEARLY, "");
                            startActivity(i);
                        });
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    trigger.setEnabled(true);
                    trigger.setText("Interpret");
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


