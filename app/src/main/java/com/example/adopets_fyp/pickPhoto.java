package com.example.adopets_fyp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class pickPhoto extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private double selectedLatitude;
    private double selectedLongitude;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private StorageReference mStorage;

    private ImageView postImageView;
    private Button pinlocation;

    private EditText petname, petage;

    private Uri selectedImageUri;

    //new var

    private static final int PERMISSION_CODE = 2;
    String[] item3 = {"Available" , "Not available"}; //status
    String[] item = {"Male" , "Female"}; //gender

    private String[] speciesArray = {"Cat", "Dog", "Hamster", "Tortoise", "Rabbit", "Bird", "Guinea Pigs", "Hedgehog", "Lizard", "Ferret", "Fish", "Iguana", "Other"};
    private String[][] breedsArray = {
            {"Siamese", "British Short Hair", "Maine Coon", "Persian", "Ragdoll", "Sphynx", "Scottish Fold", "Munchkin", "American Shorthair", "American Curl", "British Longhair"}, // Cat breeds
            {"Bulldog", "German Shepherd", "Golden Retriever"}, // Dog breeds
            {"Mongolian", "Roborovski", "Mesocricetus"}, // Hamster breeds
            {"Tortoise Breed 1", "Tortoise Breed 2", "Tortoise Breed 3"}, // Tortoise breeds
            {"Rabbit Breed 1", "Rabbit Breed 2", "Rabbit Breed 3"}, // Rabbit breeds
            {"Bird Breed 1", "Bird Breed 2", "Bird Breed 3"}, // Bird breeds
            {"Guinea Pig Breed 1", "Guinea Pig Breed 2", "Guinea Pig Breed 3"}, // Guinea Pig breeds
            {"Hedgehog Breed 1", "Hedgehog Breed 2", "Hedgehog Breed 3"}, // Hedgehog breeds
            {"Lizard Breed 1", "Lizard Breed 2", "Lizard Breed 3"}, // Lizard breeds
            {"Ferret Breed 1", "Ferret Breed 2", "Ferret Breed 3"}, // Ferret breeds
            {"Fish Breed 1", "Fish Breed 2", "Fish Breed 3"}, // Fish breeds
            {"Iguana Breed 1", "Iguana Breed 2", "Iguana Breed 3"}, // Iguana breeds
            {"Other Breed 1", "Other Breed 2", "Other Breed 3"} // Other breeds
    };
    AutoCompleteTextView speciesAutoCompleteTextView;
    AutoCompleteTextView breedsAutoCompleteTextView;
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextView1;
    AutoCompleteTextView autoCompleteTextView2; //status
    AutoCompleteTextView autoCompleteTextView3;
    ArrayAdapter<String> adapterItems;
    //ArrayAdapter<String> adapterItems1;
    ArrayAdapter<String> adapterItems2; //status

    private ProgressBar progressBar;

    private static final int MAP_REQUEST_CODE = 1;

    double latitude,longitude;
    Button selectButton,submitButton;

    Spinner markerSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photo);

        postImageView = findViewById(R.id.pickimage);
        petname = findViewById(R.id.petname);
        progressBar = findViewById(R.id.progressBar);
        petage = findViewById(R.id.petage);
        postImageView = findViewById(R.id.pickimage);
        autoCompleteTextView = findViewById(R.id.gender);
        speciesAutoCompleteTextView = findViewById(R.id.speciess);
        breedsAutoCompleteTextView = findViewById(R.id.petbreeds);
        autoCompleteTextView2 = findViewById(R.id.status);
        submitButton = findViewById(R.id.submit);
        selectButton = findViewById(R.id.select);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        DatabaseReference databaseReference = mDatabase.child("User").child("posts");

        markerSpinner = findViewById(R.id.markerSpinner);

        // Retrieve saved locations from the Firebase Realtime Database
        DatabaseReference locationsRef = databaseReference.child(mAuth.getCurrentUser().getUid()).child("locations");
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> markerNames = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LocationData locationData = snapshot.getValue(LocationData.class);
                    if (locationData != null) {

                        markerNames.add(locationData.getMarkerName());
                        System.out.println("debug 1 : "+ markerNames);

                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(pickPhoto.this, android.R.layout.simple_spinner_item, markerNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                markerSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
            }
        });

        markerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection here
                String selectedMarker = (String) parent.getItemAtPosition(position);

                // Retrieve the location data for the selected marker
                DatabaseReference selectedMarkerRef = locationsRef.orderByChild("markerName").equalTo(selectedMarker).getRef();
                selectedMarkerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            LocationData locationData = snapshot.getValue(LocationData.class);
                            if (selectedMarker.equals(locationData.getMarkerName())) {
                                selectedLatitude = locationData.getLatitude();
                                selectedLongitude = locationData.getLongitude();

                                System.out.println("debug 2 : "+ selectedLatitude+selectedLongitude);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database read error
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
            }
        });


        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = petname.getText().toString().trim();
                String age = petage.getText().toString();
                String breed = breedsAutoCompleteTextView.getText().toString();
                String gender = autoCompleteTextView.getText().toString();
                String species = speciesAutoCompleteTextView.getText().toString();
                String status = autoCompleteTextView2.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || selectedImageUri == null) {
                    Toast.makeText(pickPhoto.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadImage(selectedImageUri, name, age,breed,gender,species,status);
                }
            }
        });



        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, item);
        adapterItems2 = new ArrayAdapter<>(this, R.layout.list_item, item3); //status
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView2.setAdapter(adapterItems2);

        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(this, R.layout.list_item, speciesArray);
        speciesAutoCompleteTextView.setAdapter(speciesAdapter);

        speciesAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateBreedsAdapter(i);
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(pickPhoto.this, "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item3 = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(pickPhoto.this, "Item: " + item3, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                postImageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri, final String postName, final String age, String breed, String gender, String species, String status) {
        StorageReference fileRef = mStorage.child("post_images").child(mAuth.getCurrentUser().getUid()).child(imageUri.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                // Get current date and time
                Date currentDate = new Date();

                // Define the desired date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                // Format the date
                String formattedDate = dateFormat.format(currentDate);

                createPost(imageUrl, postName, age,breed,gender,species,status,formattedDate,selectedLatitude,selectedLongitude);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(pickPhoto.this, "Image upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateBreedsAdapter(int speciesIndex) {
        ArrayAdapter<String> breedsAdapter = new ArrayAdapter<>(this, R.layout.list_item, breedsArray[speciesIndex]);
        breedsAutoCompleteTextView.setAdapter(breedsAdapter);
    }

    private void createPost(String imageUrl, String postName, String age, String breed, String gender, String species, String status, String date, double selectedLatitude, double selectedLongitude) {
        String userId = mAuth.getCurrentUser().getUid();

        DatabaseReference postRef = mDatabase.child("User").child("posts").child(userId).push();
        String postId = postRef.getKey();

        Post post = new Post(postId, imageUrl, postName, age, breed,userId,gender,species,status,date,selectedLatitude,selectedLongitude);
        postRef.setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(pickPhoto.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(pickPhoto.this,SecondMain.class);
                startActivity(intent);
            } else {
                Toast.makeText(pickPhoto.this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }



}