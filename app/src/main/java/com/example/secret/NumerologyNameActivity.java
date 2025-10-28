package com.example.secret;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NumerologyNameActivity extends AppCompatActivity {

    public static final String EXTRA_MONTH = "month";
    public static final String EXTRA_DAY = "day";
    public static final String EXTRA_YEAR = "year";
    public static final String EXTRA_GENDER = "gender";

    private EditText etFullName;
    private int month, day, year;
    private String gender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numerology_name);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Enter Your Name");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get data from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            month = extras.getInt(EXTRA_MONTH, 2);
            day = extras.getInt(EXTRA_DAY, 12);
            year = extras.getInt(EXTRA_YEAR, 1981);
            gender = extras.getString(EXTRA_GENDER, "Male");
        }

        setupViews();
    }

    private void setupViews() {
        etFullName = findViewById(R.id.etFullName);
        Button btnContinue = findViewById(R.id.btnContinue);

        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                if (validateInput()) {
                    proceedToResult();
                }
            });
        }
    }

    private boolean validateInput() {
        String fullName = etFullName.getText().toString().trim();
        
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fullName.length() < 2) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void proceedToResult() {
        String fullName = etFullName.getText().toString().trim();
        
        // Navigate to numerology result screen
        android.content.Intent intent = new android.content.Intent(this, NumerologyResultActivity.class);
        intent.putExtra(NumerologyResultActivity.EXTRA_MONTH, month);
        intent.putExtra(NumerologyResultActivity.EXTRA_DAY, day);
        intent.putExtra(NumerologyResultActivity.EXTRA_YEAR, year);
        intent.putExtra(NumerologyResultActivity.EXTRA_GENDER, gender);
        intent.putExtra(NumerologyResultActivity.EXTRA_FULL_NAME, fullName);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
