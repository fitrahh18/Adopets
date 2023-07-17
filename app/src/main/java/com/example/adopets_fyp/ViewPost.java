package com.example.adopets_fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ViewPost extends AppCompatActivity {

    TextView status;

    private boolean isInitialText = true;
    private final String initialText = "Adopt";
    private final String newText = "Booked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        Button floatButton = findViewById(R.id.floatingButton);
        status = findViewById(R.id.ivstatus);

        Intent intent = getIntent();
        String petName = intent.getStringExtra("petName");
        String petAge = intent.getStringExtra("petAge");
        String petBreed = intent.getStringExtra("petBreed");
        String petGender = intent.getStringExtra("petGender");
        String petSpecies = intent.getStringExtra("petSpecies");
        String petDate = intent.getStringExtra("postDate");
        String petStatus = intent.getStringExtra("petStatus");
        String imageUrl = intent.getStringExtra("imageUrl");


        ImageView profilepic = findViewById(R.id.ivProfile);
        TextView username = findViewById(R.id.title);
        ImageView petimage = findViewById(R.id.ivPost);
        TextView ptname = findViewById(R.id.ivname);
        TextView ptage = findViewById(R.id.ivage);
        TextView ptgender = findViewById(R.id.ivgender);
        TextView ptbreed = findViewById(R.id.ivBreed);
        TextView ptspecies = findViewById(R.id.ivspecies);
        TextView ptstatus = findViewById(R.id.ivstatus);

        ptname.setText(petName);
        ptage.setText(petAge);
        ptgender.setText(petGender);
        ptbreed.setText(petBreed);
        ptspecies.setText(petSpecies);
        ptstatus.setText(petStatus);


        Glide.with(this)
                .load(imageUrl).into(petimage);

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInitialText) {
                    showConfirmationDialog();
                } else {
                    floatButton.setText(initialText);
                    status.setText("Available");
                    isInitialText = true;
                }
            }
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reminder");
        builder.setMessage("Ensure to contact the pet's owner to confirm adoption and set the date for the adoption.");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Change the text and set the flag when the user confirms
                Button floatButton = findViewById(R.id.floatingButton);
                floatButton.setText(newText);
                status.setText("Booked");
                isInitialText = false;
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
