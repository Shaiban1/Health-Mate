package com.example.healthmate.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.healthmate.R;
import com.example.healthmate.adapters.ChatAdapter;
import com.example.healthmate.adapters.ChatMessage;
import com.example.healthmate.interfaces.ResponseCallback;
import com.google.ai.client.generativeai.java.ChatFutures;
import java.util.ArrayList;
import java.util.List;
import android.os.Looper;
import android.text.TextUtils;


public class AiChatActivity extends AppCompatActivity {

    private EditText userInputEditText;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private ChatFutures chatModel;
    private LottieAnimationView loadingAnimationView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        initViews();  // Initialize UI components
        setupChat();  // Setup chat components

        sendButton.setOnClickListener(v -> handleUserInput());
    }

    // Method to initialize UI components
    private void initViews() {
        userInputEditText = findViewById(R.id.user_input_edittext);
        sendButton = findViewById(R.id.send_button);
        chatRecyclerView = findViewById(R.id.chat_recyclerview);
        loadingAnimationView = findViewById(R.id.loading_animation);

        loadingAnimationView.setAnimation(R.raw.chat_loading);
        loadingAnimationView.playAnimation();

        handler = new Handler(Looper.getMainLooper());  // Ensure using the main thread
    }

    // Method to set up the chat components
    private void setupChat() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Initialize the chat model (assuming GeminiResp is a helper class)
        chatModel = GeminiResp.getChatModel().startChat();
    }

    // Handle user input when the send button is clicked
    private void handleUserInput() {
        String userInput = userInputEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userInput)) {
            return;  // Avoid sending empty messages
        }

        addUserMessage(userInput);  // Add user's message to the chat
        userInputEditText.setText("");  // Clear the input field

        loadingAnimationView.setVisibility(View.VISIBLE);  // Show loading animation

        // Request AI response asynchronously
        GeminiResp.getResponse(chatModel, userInput, new ResponseCallback() {
            @Override
            public void onResponse(final String response) {
                handler.postDelayed(() -> {
                    loadingAnimationView.setVisibility(View.GONE);
                    addAiMessage(response);  // Add AI response
                }, 1000);  // 1-second delay
            }

            @Override
            public void onError(Throwable throwable) {
                loadingAnimationView.setVisibility(View.GONE);
                throwable.printStackTrace();
            }
        });
    }

    // Method to add the user's message to the chat list
    private void addUserMessage(String userInput) {
        ChatMessage userMessage = new ChatMessage(userInput, null);
        chatMessages.add(userMessage);
        updateChat();
    }

    // Method to add the AI's message to the chat list
    private void addAiMessage(String aiResponse) {
        ChatMessage aiMessage = new ChatMessage(null, aiResponse);
        chatMessages.add(aiMessage);
        updateChat();
    }

    // Common method to update the chat and scroll to the bottom
    private void updateChat() {
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up the handler to prevent memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
