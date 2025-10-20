package com.example.secret;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EditText etFullName = findViewById(R.id.etFullName);
        TextView tvDob = findViewById(R.id.tvDob);
        TextView tvZodiacValue = findViewById(R.id.tvZodiacValue);
        Spinner spGender = findViewById(R.id.spGender);
        Spinner spRelationship = findViewById(R.id.spRelationship);
        Button btnPickDob = findViewById(R.id.btnPickDob);
        Button btnSave = findViewById(R.id.btnSave);

        // Adapters
        spGender.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Nam", "Nữ", "Khác"}));
        spRelationship.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Độc thân", "Đang crush", "Trong mối quan hệ", "Mới chia tay", "Đính hôn", "Đã kết hôn", "Phức tạp", "Ly hôn", "Góa"}));

        // Load saved values
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        etFullName.setText(prefs.getString("fullName", ""));
        tvDob.setText(prefs.getString("dobDisplay", "--/--/----"));
        tvZodiacValue.setText(prefs.getString("zodiac", "-"));
        setSpinnerToValue(spGender, prefs.getString("gender", "Nam"));
        setSpinnerToValue(spRelationship, prefs.getString("relationship", "Độc thân"));

        btnPickDob.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dlg = new DatePickerDialog(this, (DatePicker view, int year, int month, int dayOfMonth) -> {
                String display = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                String iso = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvDob.setText(display);
                String zodiac = ZodiacUtils.getZodiac(month + 1, dayOfMonth);
                tvZodiacValue.setText(zodiac);
                // Save DOB fields immediately
                prefs.edit().putString("dob", iso).putString("dobDisplay", display).putString("zodiac", zodiac).apply();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dlg.show();
        });

        btnSave.setOnClickListener(v -> {
            prefs.edit()
                    .putString("fullName", etFullName.getText().toString())
                    .putString("gender", spGender.getSelectedItem().toString())
                    .putString("relationship", spRelationship.getSelectedItem().toString())
                    .apply();
            finish();
        });
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}


