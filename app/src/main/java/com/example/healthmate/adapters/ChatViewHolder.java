package com.example.healthmate.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    private TextView userMessageTextView;
    private TextView aiResponseTextView;

    public ChatViewHolder(@NonNull View itemView, boolean isUser) {
        super(itemView);

        if (isUser) {
            userMessageTextView = itemView.findViewById(R.id.user_message_textview);
        } else {
            aiResponseTextView = itemView.findViewById(R.id.ai_message_textview);
        }
    }

    public void bindUserMessage(String userMessage) {
        if (userMessageTextView != null) {
            userMessageTextView.setText(userMessage);
        }
    }

    public void bindAiMessage(String aiMessage) {
        if (aiResponseTextView != null) {
            aiResponseTextView.setText(aiMessage);
        }
    }
}
