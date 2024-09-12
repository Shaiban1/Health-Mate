package com.example.healthmate.adapters;

public class ChatMessage {
    private String userMessage;
    private String aiResponse;

    public ChatMessage(String userMessage, String aiResponse) {
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }
}
