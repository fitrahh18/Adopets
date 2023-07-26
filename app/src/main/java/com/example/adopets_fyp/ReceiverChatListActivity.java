package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiverChatListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ListView lvChats;
    private List<String> senderUidList;

    private Map<String, User> usersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_chat_list);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        lvChats = findViewById(R.id.lvChats);
        senderUidList = new ArrayList<>();
        usersMap = new HashMap<>();

        String receiverUid = mAuth.getCurrentUser().getUid();
        DatabaseReference postsRef = mDatabase.child("User").child("posts");

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String senderUid = userSnapshot.getKey();
                    System.out.println("senderUid : "+senderUid);

                    assert senderUid != null;
                    if (!senderUid.equals(receiverUid)) {
                            senderUidList.add(senderUid);
                        }

                }
                System.out.println("senderlist : "+senderUidList);
                // Fetch user information for each unique sender UID
                for (String senderUid : senderUidList) {
                    DatabaseReference userRef = mDatabase.child("User").child("posts").child(senderUid).child("profile");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                usersMap.put(senderUid, user);
                            }
                            // Once all user information is fetched, update the chat list
                            if (usersMap.size() == senderUidList.size()) {
                                populateChatList();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error case if the data retrieval is cancelled or fails
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if the data retrieval is cancelled or fails
            }
        });


    }

        private void populateChatList() {

            // Create a custom adapter using the list of senderUidList and usersMap
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_user_chat, R.id.usernameTextView, senderUidList) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.list_item_user_chat, parent, false);
                    }

                    String senderUid = senderUidList.get(position);
                    System.out.println("selected sender uid here : "+senderUid);
                    User users = usersMap.get(senderUid);
                    System.out.println("user selected sender uid here : ");
                    String imgUrl = users.getUserimageUrl();
                    System.out.println("imgUrl here : "+imgUrl);

                    if (users != null) {
                        TextView tvUsername = convertView.findViewById(R.id.usernameTextView);
                        ImageView imgUser = convertView.findViewById(R.id.profileImageView);

                        tvUsername.setText(users.getUsername());
                        // You may need to load the user's image from the URL using an image loading library like Picasso or Glide
                        Picasso.get().load(imgUrl).into(imgUser);
                    }

                    return convertView;
                }
            };

            lvChats.setAdapter(adapter);
        lvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSenderUid = senderUidList.get(position);
                String receiverUid = mAuth.getCurrentUser().getUid();

                // Fetch the user information for the selected sender UID
                User selectedUser = usersMap.get(selectedSenderUid);
                System.out.println("user information 1 : "+selectedUser.getUsername());
                System.out.println("user information 2 : "+selectedUser.getUserimageUrl());
                // Check if the user information is available
                if (selectedUser != null) {
                    // Create the intent and pass user information as extras
                    Intent intent = new Intent(ReceiverChatListActivity.this, ChatActivity.class);
                    intent.putExtra("receiverUid", selectedSenderUid);
                    intent.putExtra("senderUid", receiverUid);
                    intent.putExtra("selectedUserPhone", selectedUser.getPhone());
                    intent.putExtra("selectedUserEmail", selectedUser.getUseremail());
                    intent.putExtra("selectedUserImageUrl", selectedUser.getUserimageUrl());
                    intent.putExtra("selectedUsername", selectedUser.getUsername());
                    startActivity(intent);
                }
            }
        });
    }

}

