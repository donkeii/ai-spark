package com.example.secret;

public class Message {
    public final boolean fromUser;
    public final String text;

    public Message(boolean fromUser, String text) {
        this.fromUser = fromUser;
        this.text = text;
    }
}


