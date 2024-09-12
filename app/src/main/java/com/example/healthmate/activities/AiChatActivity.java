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


public class AiChatActivity extends AppCompatActivity {

    private EditText userInputEditText;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private ChatFutures chatModel;
    private LottieAnimationView loadingAnimationView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        chatRecyclerView = findViewById(R.id.chat_recyclerview);
        userInputEditText = findViewById(R.id.user_input_edittext);
        sendButton = findViewById(R.id.send_button);
        loadingAnimationView = findViewById(R.id.loading_animation);
        loadingAnimationView.setAnimation(R.raw.chat_loading);
        loadingAnimationView.playAnimation();

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        // Assuming GeminiResp is handling chat model
        chatModel = GeminiResp.getChatModel().startChat();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingAnimationView.setVisibility(View.VISIBLE);
                String userInput = userInputEditText.getText().toString();

                // Add User's message to the chat
                addUserMessage(userInput);

                // Clear input field
                userInputEditText.setText("");

                // Request AI's response with a callback
                GeminiResp.getResponse(chatModel, userInput, new ResponseCallback() {
                    @Override
                    public void onResponse(final String response) {
                        loadingAnimationView.setVisibility(View.GONE);

                        // Delay before showing the AI's response
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addAiMessage(response);  // Add AI response as a new message
                            }
                        }, 1000);  // 1-second delay before showing AI response
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadingAnimationView.setVisibility(View.GONE);
                        throwable.printStackTrace();
                    }
                });
            }
        });
    }

    // Method to add the user's message to the chat list
    private void addUserMessage(String userInput) {
        ChatMessage userMessage = new ChatMessage(userInput, null);  // User message, AI response is null
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);  // Scroll to the bottom
    }

    // Method to add the AI's message to the chat list
    private void addAiMessage(String aiResponse) {
        ChatMessage aiMessage = new ChatMessage(null, aiResponse);  // AI response, User message is null
        chatMessages.add(aiMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);  // Scroll to the bottom
    }
}

