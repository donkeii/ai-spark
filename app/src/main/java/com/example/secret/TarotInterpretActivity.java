package com.example.secret;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Shows the selected tarot image and fetches an AI interpretation using GeminiClient.
 */
public class TarotInterpretActivity extends AppCompatActivity {
    public static final String EXTRA_FILENAME = "extra_filename";

    private GeminiClient geminiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarot_interpret);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tarot Reading");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String filename = getIntent().getStringExtra(EXTRA_FILENAME);

        ImageView image = findViewById(R.id.imgTarot);
        TextView title = findViewById(R.id.tvCardName);
        TextView content = findViewById(R.id.tvInterpretation);
        ProgressBar progress = findViewById(R.id.progress);
        ScrollView scroll = findViewById(R.id.scroll);

        // Show image from assets and the filename as title
        if (filename != null) {
            AssetImageLoader.loadInto(image, this, "tarot/" + filename);
            title.setText(filename);
        } else {
            title.setText("Unknown card");
        }

        geminiClient = new GeminiClient(BuildConfig.GEMINI_API_KEY);
        progress.setVisibility(View.VISIBLE);
        content.setText("");

        String systemPrompt = getSystemPrompt();
        String userPrompt = getUserPrompt(filename);
        geminiClient.generateAsync(systemPrompt, userPrompt, new GeminiClient.GeminiCallback() {
            @Override
            public void onSuccess(String text) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    content.setText(text);
                    scroll.fullScroll(View.FOCUS_UP);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    content.setText("Lỗi: " + message);
                });
            }
        });
    }

    private String getSystemPrompt() {
        return "You are a skilled tarot reader. Provide empathetic, mystical, and insightful interpretations. Reply in Vietnamese.";
    }

    private String getUserPrompt(String filename) {
        String name = filename != null ? filename : "Unknown";
        return "I want you to act as a tarot reader. I will provide you with picture name of a tarot card, such as \"00-TheFool.png\" or \"00-TheFool-Reverse.png.\" You will interpret the card comprehensively according to the following categories:\n\n" +
                "Overview: A general interpretation of the card’s energy and symbolism.\n" +
                "Work: How this card influences career, ambitions, and professional growth.\n" +
                "Love: Its implications for relationships, emotions, and personal connections.\n" +
                "Finance: Guidance on money, material stability, and financial decisions.\n" +
                "Health: Insights into physical and emotional well-being.\n" +
                "Spirit: The spiritual message, inner wisdom, and life lessons the card conveys.\n\n" +
                "Use an intuitive, mystical, and empathetic tone, as if performing a real tarot reading. Answer is in Vietnamese.\n\n" +
                "Card filename: " + name;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


