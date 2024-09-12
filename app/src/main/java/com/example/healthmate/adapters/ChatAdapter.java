package com.example.healthmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.getUserMessage() != null) {
            return VIEW_TYPE_USER;  // User message
        } else {
            return VIEW_TYPE_AI;  // AI message
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_message, parent, false);
            return new AiMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            ((UserMessageViewHolder) holder).bind(chatMessage.getUserMessage());
        } else {
            ((AiMessageViewHolder) holder).bind(chatMessage.getAiResponse());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // ViewHolder for user messages
    public class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView userMessageTextView;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageTextView = itemView.findViewById(R.id.user_message_textview);
        }

        public void bind(String userMessage) {
            userMessageTextView.setText(userMessage);
        }
    }

    // ViewHolder for AI messages
    public class AiMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView aiMessageTextView;

        public AiMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            aiMessageTextView = itemView.findViewById(R.id.ai_message_textview);
        }

        public void bind(String aiMessage) {
            aiMessageTextView.setText(aiMessage);
        }
    }
}
