package com.example.adopets_fyp;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewPost extends AppCompatActivity {

    TextView ptstatus;
    Button getlocation;
    Button floatButton, getcontact;

    private boolean isInitialText = true;
    private final String initialText = "Adopt";
    private final String newText = "Booked";
    private FirebaseAuth mAuth;
    TextView lrusername;
    ImageView userpphoto;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getcontact = findViewById(R.id.ivContact);
        getlocation = findViewById(R.id.ivgetlocation);
        floatButton = findViewById(R.id.floatingButton);
        ptstatus = findViewById(R.id.ivstatus);

        Intent intent = getIntent();
        String petName = intent.getStringExtra("petName");
        String petAge = intent.getStringExtra("petAge");
        String petBreed = intent.getStringExtra("petBreed");
        String petGender = intent.getStringExtra("petGender");
        String petSpecies = intent.getStringExtra("petSpecies");
        String petDate = intent.getStringExtra("postDate");
        String petStatus = intent.getStringExtra("petStatus");
        String imageUrl = intent.getStringExtra("imageUrl");
        double latitude = intent.getDoubleExtra("latitude",0);
        double longitude = intent.getDoubleExtra("longitude",0);
        System.out.println("debug 17"+latitude);

        ImageView userpphoto = findViewById(R.id.ivProfile);
        TextView lrusername = findViewById(R.id.title);
        ImageView petimage = findViewById(R.id.ivPost);
        TextView ptname = findViewById(R.id.ivname);
        TextView ptage = findViewById(R.id.ivage);
        TextView ptgender = findViewById(R.id.ivgender);
        TextView ptbreed = findViewById(R.id.ivBreed);
        TextView ptspecies = findViewById(R.id.ivspecies);

        ptname.setText(petName);
        ptage.setText(petAge);
        ptgender.setText(petGender);
        ptbreed.setText(petBreed);
        ptspecies.setText(petSpecies);
        ptstatus.setText(petStatus);

        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPost.this, MapsActivity2.class);
                intent.putExtra("latitudes", latitude);
                intent.putExtra("longitudes", longitude);
                startActivity(intent);
            }
        });
        getcontact.setOnClickListener(view -> {
            startActivity(new Intent(ViewPost.this, Chat.class));
        });


        Glide.with(this)
                .load(imageUrl).into(petimage);

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInitialText) {
                    showConfirmationDialog();
                } else {
                    floatButton.setText(initialText);
                    ptstatus.setText("Available");
                    isInitialText = true;
                }
            }
        });
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

// Specify the path to the image URL
        String path = "User/posts/" + mAuth.getCurrentUser().getUid() + "/profile";

        databaseRef.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = null;

                    imageUrl = dataSnapshot.child("userimageUrl").getValue(String.class);
                    String rusername = dataSnapshot.child("username").getValue(String.class);
                    lrusername.setText(rusername);

                    Picasso.get().load(imageUrl).into(userpphoto);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if the data retrieval is cancelled or fails
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
                ptstatus.setText("Booked");
                isInitialText = false;
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
