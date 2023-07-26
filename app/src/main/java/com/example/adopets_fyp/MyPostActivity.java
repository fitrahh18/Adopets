// MyPostActivity.java

package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> myPostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myPostList = new ArrayList<>();
        postAdapter = new PostAdapter(myPostList,true);
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        recyclerView.setAdapter(postAdapter);

        fetchMyPosts();
    }
    private void removePostFromFirebase(String postId) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference postRef = mDatabase.child("User").child("posts").child(currentUserId).child(postId);
        postRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Post removal from Firebase is successful.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error that occurred during post removal.
                    }
                });
    }

    private void fetchMyPosts() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference postsRef = mDatabase.child("User").child("posts").child(currentUserId);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPostList.clear();

                for (DataSnapshot postsSnapshot : dataSnapshot.getChildren()) {
                    Post post = postsSnapshot.getValue(Post.class);
                    if (post != null && post.getImageUrl() != null) {
                        myPostList.add(post);
                    }
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
