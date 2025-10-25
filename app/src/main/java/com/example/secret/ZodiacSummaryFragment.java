package com.example.secret;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ZodiacSummaryFragment extends Fragment {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_TEXT = "arg_text";

    public static ZodiacSummaryFragment newInstance(String title, String text) {
        ZodiacSummaryFragment f = new ZodiacSummaryFragment();
        Bundle b = new Bundle();
        b.putString(ARG_TITLE, title);
        b.putString(ARG_TEXT, text);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_zodiac_summary, container, false);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvSummary = v.findViewById(R.id.tvSummary);
        Button btnDetail = v.findViewById(R.id.btnDetail);

        Bundle args = getArguments();
        final String title = args != null ? args.getString(ARG_TITLE, "") : "";
        final String text = args != null ? args.getString(ARG_TEXT, "") : "";

        tvTitle.setText(title);
        tvSummary.setText(text);

        btnDetail.setOnClickListener(v1 -> {
            Intent i = new Intent(requireContext(), ZodiacDetailActivity.class);
            i.putExtra(ZodiacDetailActivity.EXTRA_TITLE, title);
            i.putExtra(ZodiacDetailActivity.EXTRA_TEXT, text);
            startActivity(i);
        });

        return v;
    }
}


