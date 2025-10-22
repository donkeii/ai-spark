package com.example.secret;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_USER = 1;
    private static final int TYPE_BOT = 2;

    private final List<Message> messages = new ArrayList<>();

    public void add(Message m) {
        messages.add(m);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).fromUser ? TYPE_USER : TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            View v = inf.inflate(R.layout.item_message_user, parent, false);
            return new TextHolder(v);
        } else {
            View v = inf.inflate(R.layout.item_message_bot, parent, false);
            return new TextHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = messages.get(position);
        ((TextHolder) holder).txt.setText(m.text);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class TextHolder extends RecyclerView.ViewHolder {
        final TextView txt;
        TextHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txtMessage);
        }
    }
}


