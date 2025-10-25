package com.example.secret;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Simple shuffle screen that mirrors the provided mock.
 * - Shows instructional texts
 * - Deck image reacts to taps
 * - Removed bottom buttons per latest UI
 */
public class TarotShuffleActivity extends AppCompatActivity {
    private boolean userShuffledOnce = false;
    private String[] assetFilenames;
    private boolean cardRevealed = false;
    private String revealedFilename = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarot_shuffle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.get_tarot_reading));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView hintTap = findViewById(R.id.hintTapDeck);
        TextView hintAuto = findViewById(R.id.hintAuto);
        ImageView deck = findViewById(R.id.deckImage);
        Button btnInterpret = findViewById(R.id.btnInterpret);

        // Read available filenames from assets/tarot (PNG files only)
        try {
            String[] all = getAssets().list("tarot");
            if (all != null && all.length > 0) {
                List<String> filtered = new ArrayList<>();
                for (String name : all) {
                    if (name == null) continue;
                    String lower = name.toLowerCase(Locale.US);
                    if (lower.endsWith(".png") || lower.endsWith(".webp") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                        filtered.add(name);
                    }
                }
                assetFilenames = filtered.toArray(new String[0]);
            } else {
                assetFilenames = null;
            }
        } catch (Exception ignored) {
            assetFilenames = null;
        }

        // Tap deck to simulate a shuffle
        deck.setOnClickListener(v -> {
            if (cardRevealed) return; // reveal only once
            userShuffledOnce = true;
            // simple visual feedback: alpha pulse
            deck.animate().alpha(0.6f).setDuration(120).withEndAction(() -> deck.animate().alpha(1f).setDuration(120)).start();

            // If we have assets, show a random card back from assets/tarot
            if (assetFilenames != null && assetFilenames.length > 0) {
                int idx = (int) (Math.random() * assetFilenames.length);
                String name = assetFilenames[idx];
                AssetImageLoader.loadInto(deck, this, "tarot/" + name);
                cardRevealed = true;
                revealedFilename = name;
                if (btnInterpret != null) btnInterpret.setVisibility(android.view.View.VISIBLE);
            } else {
                Toast.makeText(this, "Chưa tìm thấy ảnh trong assets/tarot", Toast.LENGTH_SHORT).show();
            }
        });

        if (btnInterpret != null) {
            btnInterpret.setOnClickListener(v -> {
                if (!cardRevealed || revealedFilename == null) return;
                android.content.Intent i = new android.content.Intent(this, TarotInterpretActivity.class);
                i.putExtra(TarotInterpretActivity.EXTRA_FILENAME, revealedFilename);
                startActivity(i);
            });
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


