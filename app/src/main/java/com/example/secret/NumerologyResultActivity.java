package com.example.secret;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NumerologyResultActivity extends AppCompatActivity {

    public static final String EXTRA_MONTH = "month";
    public static final String EXTRA_DAY = "day";
    public static final String EXTRA_YEAR = "year";
    public static final String EXTRA_GENDER = "gender";
    public static final String EXTRA_FULL_NAME = "fullName";

    private static final String SYSTEM_PROMPT =
            "I want you to act as an experienced numerologist. Your task is to decode and interpret the meaning of each numerology number, including:\n\n" +
            "Life Path Number: The core blueprint of one’s life purpose, challenges, and natural direction.\n\n" +
            "Destiny (Expression) Number: The talents, abilities, and potential one is meant to express in this lifetime.\n\n" +
            "Soul Urge (Heart's Desire) Number: The inner motivations, emotional needs, and what truly fulfills the soul.\n\n" +
            "Personality Number: The outer impression, how others perceive the individual, and their social expression.\n\n" +
            "Maturity Number: The culmination of one’s life lessons and personal evolution, showing the traits that emerge with age and experience.\n\n" +
            "Birthday Number: The specific gifts, talents, and personality traits revealed by the day of birth.\n\n" +
            "Challenge Number: The recurring obstacles or life tests that shape growth and character development.\n\n" +
            "You will interpret each number with spiritual depth, symbolic wisdom, and practical insight, explaining both the light and shadow aspects. Write in an intuitive, mystical, and empathetic tone — as if giving a personal numerology reading. Answer it in Vietnamese";

    private GeminiClient geminiClient;

    private String fullName;
    private String gender;
    private int day;
    private int month;
    private int year;

    private int lifePathNumber;
    private int destinyNumber;
    private int soulUrgeNumber;
    private int personalityNumber;
    private int maturityNumber;
    private int birthdayNumber;
    private int challengeNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numerology_result);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Numerology Analysis");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        geminiClient = new GeminiClient(BuildConfig.GEMINI_API_KEY);

        displayResults();
    }

    private void displayResults() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        month = extras.getInt(EXTRA_MONTH, 2);
        day = extras.getInt(EXTRA_DAY, 12);
        year = extras.getInt(EXTRA_YEAR, 1981);
        gender = extras.getString(EXTRA_GENDER, "Male");
        fullName = extras.getString(EXTRA_FULL_NAME, "");

        // Calculate numerology numbers
        lifePathNumber = calculateLifePathNumber(day, month, year);
        destinyNumber = calculateDestinyNumber(fullName);
        soulUrgeNumber = calculateSoulUrgeNumber(fullName);
        personalityNumber = calculatePersonalityNumber(fullName);
        maturityNumber = calculateMaturityNumber(day, month, year);
        birthdayNumber = calculateBirthdayNumber(day);
        challengeNumber = calculateChallengeNumber(day, month, year);

        // Display the results
        setText(R.id.tvLifePathNumber, String.valueOf(lifePathNumber));
        setText(R.id.tvDestinyNumber, String.valueOf(destinyNumber));
        setText(R.id.tvSoulUrgeNumber, String.valueOf(soulUrgeNumber));
        setText(R.id.tvPersonalityNumber, String.valueOf(personalityNumber));
        setText(R.id.tvMaturityNumber, String.valueOf(maturityNumber));
        setText(R.id.tvBirthdayNumber, String.valueOf(birthdayNumber));
        setText(R.id.tvChallengeNumber, String.valueOf(challengeNumber));

        // Set descriptions
        setText(R.id.tvLifePathDesc, getLifePathDescription(lifePathNumber));
        setText(R.id.tvDestinyDesc, getDestinyDescription(destinyNumber));
        setText(R.id.tvSoulUrgeDesc, getSoulUrgeDescription(soulUrgeNumber));
        setText(R.id.tvPersonalityDesc, getPersonalityDescription(personalityNumber));
        setText(R.id.tvMaturityDesc, getMaturityDescription(maturityNumber));
        setText(R.id.tvBirthdayDesc, getBirthdayDescription(birthdayNumber));
        setText(R.id.tvChallengeDesc, getChallengeDescription(challengeNumber));
        // Pinnacle number removed from UI

        setupClickHandlers();
    }

    private void setupClickHandlers() {
        View lifePathView = findViewById(R.id.tvLifePathNumber);
        if (lifePathView != null) {
            lifePathView.setOnClickListener(v -> showNumerologyDetail("Life Path Number", lifePathNumber));
        }
        View destinyView = findViewById(R.id.tvDestinyNumber);
        if (destinyView != null) {
            destinyView.setOnClickListener(v -> showNumerologyDetail("Destiny (Expression) Number", destinyNumber));
        }
        View soulUrgeView = findViewById(R.id.tvSoulUrgeNumber);
        if (soulUrgeView != null) {
            soulUrgeView.setOnClickListener(v -> showNumerologyDetail("Soul Urge (Heart's Desire) Number", soulUrgeNumber));
        }
        View personalityView = findViewById(R.id.tvPersonalityNumber);
        if (personalityView != null) {
            personalityView.setOnClickListener(v -> showNumerologyDetail("Personality Number", personalityNumber));
        }
        View maturityView = findViewById(R.id.tvMaturityNumber);
        if (maturityView != null) {
            maturityView.setOnClickListener(v -> showNumerologyDetail("Maturity Number", maturityNumber));
        }
        View birthdayView = findViewById(R.id.tvBirthdayNumber);
        if (birthdayView != null) {
            birthdayView.setOnClickListener(v -> showNumerologyDetail("Birthday Number", birthdayNumber));
        }
        View challengeView = findViewById(R.id.tvChallengeNumber);
        if (challengeView != null) {
            challengeView.setOnClickListener(v -> showNumerologyDetail("Challenge Number", challengeNumber));
        }
    }

    private void showNumerologyDetail(String sectionTitle, int number) {
        TextView contentView = new TextView(this);
        contentView.setText("Đang giải nghĩa...\n\nVui lòng chờ.");
        contentView.setTextColor(getResources().getColor(android.R.color.white));
        contentView.setTextSize(14f);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        contentView.setPadding(pad, pad, pad, pad);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(contentView);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(sectionTitle + " - " + number)
                .setView(scrollView)
                .setNegativeButton("Đóng", (d, which) -> d.dismiss())
                .create();
        dialog.show();

        String userMessage =
                "Hãy giải nghĩa chi tiết cho mục: " + sectionTitle + " với con số: " + number + "\n" +
                "Thông tin người dùng:\n" +
                "- Họ tên: " + (fullName == null ? "" : fullName) + "\n" +
                "- Giới tính: " + (gender == null ? "" : gender) + "\n" +
                String.format("- Ngày sinh: %02d/%02d/%04d\n\n", day, month, year) +
                "Viết bằng tiếng Việt, rõ ràng, có chiều sâu tâm linh, gồm ưu/nhược (light/shadow).";

        geminiClient.generateAsync(SYSTEM_PROMPT, userMessage, new GeminiClient.GeminiCallback() {
            @Override
            public void onSuccess(String text) {
                runOnUiThread(() -> contentView.setText(text == null || text.trim().isEmpty() ? "(Không có nội dung trả về)" : text));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> contentView.setText("Lỗi: " + (message == null ? "Không xác định" : message)));
            }
        });
    }

    private void setText(int id, String text) {
        TextView tv = findViewById(id);
        if (tv != null) tv.setText(text);
    }

    private int calculateLifePathNumber(int day, int month, int year) {
        int sum = day + month + year;
        return reduceToSingleDigitExceptMaster(sum);
    }

    private int calculateDestinyNumber(String fullName) {
        return calculateNameNumber(fullName);
    }

    private int calculateSoulUrgeNumber(String fullName) {
        return calculateVowelNumber(fullName);
    }

    private int calculatePersonalityNumber(String fullName) {
        return calculateConsonantNumber(fullName);
    }

    private int calculateMaturityNumber(int day, int month, int year) {
        return reduceToSingleDigit(day + month + year);
    }

    private int calculateBirthdayNumber(int day) {
        return reduceToSingleDigit(day);
    }

    private int calculateChallengeNumber(int day, int month, int year) {
        return reduceToSingleDigit(day + month + year);
    }

    // Pinnacle number calculation removed

    private int reduceToSingleDigit(int number) {
        while (number > 9) {
            int sum = 0;
            while (number > 0) {
                sum += number % 10;
                number /= 10;
            }
            number = sum;
        }
        return number;
    }

    private int reduceToSingleDigitExceptMaster(int number) {
        while (number > 9) {
            // Check for Master Numbers (11, 22, 33)
            if (number == 11 || number == 22 || number == 33) {
                return number; // Keep Master Numbers as is
            }
            
            int sum = 0;
            while (number > 0) {
                sum += number % 10;
                number /= 10;
            }
            number = sum;
        }
        return number;
    }

    private int calculateNameNumber(String name) {
        if (name == null || name.trim().isEmpty()) return 0;
        
        int sum = 0;
        String cleanName = name.toUpperCase().replaceAll("[^A-Z]", "");
        
        for (char c : cleanName.toCharArray()) {
            sum += getLetterValue(c);
        }
        
        return reduceToSingleDigit(sum);
    }

    private int calculateVowelNumber(String name) {
        if (name == null || name.trim().isEmpty()) return 0;
        
        int sum = 0;
        String cleanName = name.toUpperCase().replaceAll("[^A-Z]", "");
        
        for (char c : cleanName.toCharArray()) {
            if (isVowel(c)) {
                sum += getLetterValue(c);
            }
        }
        
        return reduceToSingleDigit(sum);
    }

    private int calculateConsonantNumber(String name) {
        if (name == null || name.trim().isEmpty()) return 0;
        
        int sum = 0;
        String cleanName = name.toUpperCase().replaceAll("[^A-Z]", "");
        
        for (char c : cleanName.toCharArray()) {
            if (!isVowel(c)) {
                sum += getLetterValue(c);
            }
        }
        
        return reduceToSingleDigit(sum);
    }

    private int getLetterValue(char letter) {
        // A=1, B=2, C=3, ..., I=9, J=1, K=2, ..., R=9, S=1, T=2, ..., Z=8
        int value = (letter - 'A' + 1) % 9;
        return value == 0 ? 9 : value;
    }

    private boolean isVowel(char letter) {
        return letter == 'A' || letter == 'E' || letter == 'I' || letter == 'O' || letter == 'U';
    }

    // Description methods
    private String getLifePathDescription(int number) {
        // Handle Master Numbers first
        if (number == 11) {
            return "You are a Master Teacher with intuitive abilities and spiritual insight. You inspire others through your wisdom and understanding.";
        }
        if (number == 22) {
            return "You are a Master Builder with the ability to manifest dreams into reality. You have great potential for material and spiritual success.";
        }
        if (number == 33) {
            return "You are a Master Healer with exceptional compassion and healing abilities. You serve humanity through love and spiritual guidance.";
        }
        
        // Handle regular numbers 1-9
        String[] descriptions = {
            "Your life path is about spiritual enlightenment and humanitarian service.",
            "You are a natural leader with strong intuition and diplomatic skills.",
            "You excel in creative expression and communication.",
            "You are practical, organized, and focused on building solid foundations.",
            "You seek freedom, adventure, and new experiences.",
            "You are nurturing, responsible, and family-oriented.",
            "You are analytical, spiritual, and seek deeper understanding.",
            "You are ambitious, materialistic, and focused on success.",
            "You are compassionate, wise, and focused on universal love."
        };
        
        if (number >= 1 && number <= 9) {
            return descriptions[number - 1];
        }
        
        return "Your life path number reveals your spiritual journey and purpose in this lifetime.";
    }

    private String getDestinyDescription(int number) {
        return "Your destiny number " + number + " indicates your life's purpose and the path you're meant to follow.";
    }

    private String getSoulUrgeDescription(int number) {
        return "Your soul urge number " + number + " reveals your innermost desires and motivations.";
    }

    private String getPersonalityDescription(int number) {
        return "Your personality number " + number + " shows how others perceive you.";
    }

    private String getMaturityDescription(int number) {
        return "Your maturity number " + number + " indicates your potential for growth and development.";
    }

    private String getBirthdayDescription(int number) {
        return "Your birthday number " + number + " reveals your natural talents and abilities.";
    }

    private String getChallengeDescription(int number) {
        return "Your challenge number " + number + " indicates the obstacles you must overcome.";
    }

    // Pinnacle description removed

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
