package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText etMessage;
    private ImageButton btnSend;
    private ListView lvMessages;

    private String receiverUid;
    private String senderUid;
    private String senderUsername;
    private String chatId;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    private String selectedUserPhone,selectedUserEmail,selectedUserImageUrl,selectedUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        lvMessages = findViewById(R.id.lvMessages);

        receiverUid = getIntent().getStringExtra("receiverUid");
        senderUid = getIntent().getStringExtra("senderUid");


        selectedUserPhone = getIntent().getStringExtra("selectedUserPhone");
        selectedUserEmail = getIntent().getStringExtra("selectedUserEmail");
        selectedUserImageUrl = getIntent().getStringExtra("selectedUserImageUrl");
        selectedUsername = getIntent().getStringExtra("selectedUsername");

        TextView tvSenderUsername = findViewById(R.id.tvSenderUsername);
        tvSenderUsername.setText(selectedUsername);

        if (senderUid == null){
            senderUid = mAuth.getCurrentUser().getUid();
        }
        senderUsername = mAuth.getCurrentUser().getDisplayName();

        // Create a unique chatId based on the sender and receiver UIDs
        chatId = getChatId(senderUid, receiverUid);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList,senderUid);
        lvMessages.setAdapter(messageAdapter);

        loadMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void loadMessages() {
        DatabaseReference messagesRef = mDatabase.child("messages").child(chatId);

        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
                scrollToBottom();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Not needed for this implementation
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Not needed for this implementation
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Not needed for this implementation
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Not needed for this implementation
            }
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText)) {
            DatabaseReference messagesRef = mDatabase.child("messages").child(chatId).push();

            String messageId = messagesRef.getKey();
            long timestamp = System.currentTimeMillis();

            Message message = new Message(messageId, senderUid, receiverUid, senderUsername, messageText, timestamp);

            messagesRef.setValue(message)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            etMessage.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void scrollToBottom() {
        lvMessages.post(new Runnable() {
            @Override
            public void run() {
                lvMessages.setSelection(messageAdapter.getCount() - 1);
            }
        });
    }

    private String getChatId(String uid1, String uid2) {
        if (uid1 == null || uid2 == null) {
            return ""; // Or any default value that makes sense in your case
        }

        List<String> uids = new ArrayList<>();
        uids.add(uid1);
        uids.add(uid2);
        uids.sort(String::compareTo);
        return TextUtils.join("_", uids);
    }

}
