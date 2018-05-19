package com.example.dell.afinal.bean;

public class MessageEvent {
    private final String message;

    public MessageEvent(String msg) {
        message = msg;
    }

    public String getMessage() {
        return message;
    }
}