package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {
    private DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("messages");

        // Set up the ListView and Adapter for displaying messages
        ListView messageListView = findViewById(R.id.messageListView);
        List<Message> messages = new ArrayList<>();
        MessageAdapter messageAdapter = new MessageAdapter(this, messages);
        messageListView.setAdapter(messageAdapter);

        // Load messages from Firebase
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageAdapter.add(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // ... other overridden methods (onChildChanged, onChildRemoved, onChildMoved, onCancelled)
        });

        // Send button click listener
        Button sendButton = findViewById(R.id.sendButton);
        EditText messageEditText = findViewById(R.id.messageEditText);
        sendButton.setOnClickListener(view -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(messageText)) {
                sendMessage(messageText);
                messageEditText.setText("");
            }
        });
    }

    private void sendMessage(String messageText) {
        String messageId = messagesRef.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String username = "Fitrah"; // Replace with the username of the sender
        String imageUrl = "https://example.com/user1.png"; // Replace with the URL of the sender's image
        Message message = new Message(messageId, userId, username, imageUrl, messageText);
        messagesRef.child(userId).child("TGdKSiJyp6UD4jCM2EPKAXxW1Mg2").child(messageId).setValue(message);
    }

}
