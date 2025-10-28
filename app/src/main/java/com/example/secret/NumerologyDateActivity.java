package com.example.secret;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.NumberPicker;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NumerologyDateActivity extends AppCompatActivity {

    private NumberPicker monthPicker, dayPicker, yearPicker;
    private RadioGroup genderGroup;
    private String selectedGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numerology_date);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.daily_card));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupDatePickers();
        setupGenderSelection();
        setupContinueButton();
    }

    private void setupDatePickers() {
        monthPicker = findViewById(R.id.monthPicker);
        dayPicker = findViewById(R.id.dayPicker);
        yearPicker = findViewById(R.id.yearPicker);

        // Setup month picker
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                                     "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});

        // Setup day picker
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);

        // Setup year picker
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(1990);

        // Set default values
        monthPicker.setValue(2); // February
        dayPicker.setValue(12);
        yearPicker.setValue(1981);

        // Style the NumberPickers
        styleNumberPicker(monthPicker);
        styleNumberPicker(dayPicker);
        styleNumberPicker(yearPicker);
    }

    private void styleNumberPicker(NumberPicker picker) {
        // Make the picker font white and larger
        android.widget.EditText text = null;
        int count = picker.getChildCount();
        for (int i = 0; i < count; i++) {
            android.view.View child = picker.getChildAt(i);
            if (child instanceof android.widget.EditText) {
                text = (android.widget.EditText) child;
                break;
            }
        }
        if (text != null) {
            text.setTextSize(18);
            text.setTextColor(android.graphics.Color.WHITE);
        }
    }

    private void setupGenderSelection() {
        genderGroup = findViewById(R.id.genderGroup);
        
        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadio = findViewById(checkedId);
            if (selectedRadio != null) {
                selectedGender = selectedRadio.getText().toString();
            }
        });

        // Set default to Male
        RadioButton maleRadio = findViewById(R.id.radioMale);
        if (maleRadio != null) {
            maleRadio.setChecked(true);
            selectedGender = "Male";
        }
    }

    private void setupContinueButton() {
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
        // Basic validation
        if (selectedGender == null || selectedGender.isEmpty()) {
            android.widget.Toast.makeText(this, "Please select your gender", android.widget.Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void proceedToResult() {
        int month = monthPicker.getValue();
        int day = dayPicker.getValue();
        int year = yearPicker.getValue();
        
        // Navigate to name input screen
        android.content.Intent intent = new android.content.Intent(this, NumerologyNameActivity.class);
        intent.putExtra(NumerologyNameActivity.EXTRA_MONTH, month);
        intent.putExtra(NumerologyNameActivity.EXTRA_DAY, day);
        intent.putExtra(NumerologyNameActivity.EXTRA_YEAR, year);
        intent.putExtra(NumerologyNameActivity.EXTRA_GENDER, selectedGender);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
