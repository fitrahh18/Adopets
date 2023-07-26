package com.example.adopets_fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Boolean act;
    private List<Post> postList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public PostAdapter(List<Post> postList,Boolean act) {

        this.postList = postList;
        this.act=act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.petName.setText(post.getPetName());
        holder.petGender.setText(post.getPetGender());
        holder.date.setText(post.getFormattedDatePost());

        // Load the post image using Glide library
        Glide.with(holder.itemView)
                .load(post.getImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.default_profile_image))
                .into(holder.petImageView);

        // Set click listener for the post item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // Get the clicked post
                    Post clickedPost = postList.get(holder.getAdapterPosition());
                    User user = new User();

                    // Start the ViewPostActivity and pass the post details
                    Intent intent = new Intent(v.getContext(), ViewPost.class);
                    intent.putExtra("postId", clickedPost.getPostId());
                    intent.putExtra("imageUrl", clickedPost.getImageUrl());
                    intent.putExtra("petName", clickedPost.getPetName());
                    intent.putExtra("petAge", clickedPost.getPetAge());
                    intent.putExtra("petGender", clickedPost.getPetGender());
                    intent.putExtra("petBreed", clickedPost.getPetBreed());
                    intent.putExtra("petSpecies", clickedPost.getPetSpecies());
                    intent.putExtra("postDate", clickedPost.getFormattedDatePost());
                    intent.putExtra("petStatus", clickedPost.getPetStatus());
                    intent.putExtra("userId", clickedPost.getUserId());
                    intent.putExtra("latitude", clickedPost.getLatitude());
                    intent.putExtra("longitude", clickedPost.getLongitude());
                    intent.putExtra("receiverUid", clickedPost.getUserId());
                    intent.putExtra("senderUsername", user.getUsername());
                    System.out.println("receiver uid : "+clickedPost.getUserId());
                    v.getContext().startActivity(intent);
                }
        });

        if (act) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Delete Post");
                    builder.setMessage("Are you sure you want to delete this post?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Post clickedPost = postList.get(holder.getAdapterPosition());
                            removePostFromFirebase(clickedPost.getPostId());
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User clicked "No," so do nothing and dismiss the dialog
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return false;
                }
            });
        }
    }

    private void removePostFromFirebase(String postId) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView petImageView;
        TextView petName;
        TextView petGender,petBreed,petSpecies,petStatus,date;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            petImageView = itemView.findViewById(R.id.feral);
            petName = itemView.findViewById(R.id.name);
            petGender = itemView.findViewById(R.id.gender);
            date = itemView.findViewById(R.id.date);
        }
    }
}

