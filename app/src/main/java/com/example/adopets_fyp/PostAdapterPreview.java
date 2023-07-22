package com.example.adopets_fyp;

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


import java.util.List;

public class PostAdapterPreview extends RecyclerView.Adapter<PostAdapterPreview.ViewHolder> {

    private List<Post> postList;

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    private OnItemClickListener onItemClickListener;

    public PostAdapterPreview(List<Post> postList, OnItemClickListener onItemClickListener) {
        this.postList = postList;
        this.onItemClickListener = onItemClickListener;
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

                System.out.println("I'm here");
                Post clickedPost = postList.get(position);

                // Call the onItemClick method of the OnItemClickListener interface
                // Pass the clicked post to the method
                onItemClickListener.onItemClick(clickedPost);

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

