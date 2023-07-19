package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class SecondMain extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_IMAGE_PICK = 1;
    //homePagePostView
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    TextView uploadphoto;
    Button buttons3;
    private Uri selectedImageUri;
    //others
    Button signOutBtn, mylocate;
    TextView lrusername, lruseremail;
    GoogleSignInClient gsc;
    private MeowBottomNavigation bottomNavigation;
    RelativeLayout nearby, home, profile;

    ImageView userpphoto;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        uploadphoto = findViewById(R.id.puser);
        signOutBtn = findViewById(R.id.signout);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        nearby = findViewById(R.id.nearbyy);
        home = findViewById(R.id.homee);
        profile = findViewById(R.id.profile);
        mylocate = findViewById(R.id.mylocation);
        userpphoto = findViewById(R.id.userphoto);
        lrusername = findViewById(R.id.usernameprofile);
        lruseremail = findViewById(R.id.useremailprofile);
        buttons3 = findViewById(R.id.button3);

        buttons3.setOnClickListener(view -> {
            startActivity(new Intent(SecondMain.this, MapsActivity2.class));
        });

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

// Specify the path to the image URL
        String path = "UsersNewsFeedApp/posts/" + mAuth.getCurrentUser().getUid() + "/profile";

        databaseRef.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = null;

                    imageUrl = dataSnapshot.child("userimageUrl").getValue(String.class);

                    String rusername =dataSnapshot.child("username").getValue(String.class);
                    String ruseremail =dataSnapshot.child("useremail").getValue(String.class);
                    lruseremail.setText(ruseremail);
                    lrusername.setText(rusername);

                    Picasso.get().load(imageUrl).into(userpphoto);

                    Toast.makeText(SecondMain.this, "Image retrieve success", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SecondMain.this, "Image retrieve failed", Toast.LENGTH_SHORT).show();
                    // Handle the case when the image URL is not found in the database
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if the data retrieval is cancelled or fails
            }
        });
        uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        signOutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(SecondMain.this, SignIn.class));
        });

        mylocate.setOnClickListener(view -> {
            startActivity(new Intent(SecondMain.this, MapsActivity.class));
        });


        bottomNavigation.show(2, true);

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_near_me_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.home));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_person_24));

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()) {

                    case 1:
                        nearby.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        break;

                    case 2:
                        nearby.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        profile.setVisibility(View.GONE);
                        break;

                    case 3:
                        nearby.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        profile.setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        });
        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES
                switch (model.getId()) {

                    case 1:
                        nearby.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        break;
                    case 2:
                        nearby.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        profile.setVisibility(View.GONE);
                        break;
                    case 3:
                        nearby.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        profile.setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateMap(location.getLatitude(), location.getLongitude());

                }
            }
        };

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        fetchPosts();
    }

    private void fetchPosts() {
        DatabaseReference postsRef = mDatabase.child("UsersNewsFeedApp").child("posts").child(mAuth.getCurrentUser().getUid());
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Post post = postSnapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getImageUrl() != null) {
                        postList.add(post);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:

                startActivity(new Intent(SecondMain.this, pickPhoto.class));
                break;
            case R.id.search:

                showDialog();
                break;

            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
      /*final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomlayout);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);*/

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Post");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.bottomlayout, null);
        builder.setView(dialogView);

        //Spinner speacies
        AutoCompleteTextView spinner1 = dialogView.findViewById(R.id.speciesa);

        String[] items1 = getResources().getStringArray(R.array.spinner1_items);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items1);
        spinner1.setAdapter(adapter1);

        //Spinner gender
        AutoCompleteTextView spinner2 = dialogView.findViewById(R.id.gendera);

        String[] items2 = getResources().getStringArray(R.array.spinner2_items);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items2);
        spinner2.setAdapter(adapter2);

        //Spinner age
        AutoCompleteTextView spinner3 = dialogView.findViewById(R.id.agea);

        String[] items3 = getResources().getStringArray(R.array.spinner3_items);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items3);
        spinner3.setAdapter(adapter3);

        //Spinner location
        AutoCompleteTextView spinner4 = dialogView.findViewById(R.id.locationa);

        String[] items4 = getResources().getStringArray(R.array.spinner4_items);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items4);
        spinner4.setAdapter(adapter4);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String selectedValueSpeacies = spinner1.getText().toString();
                String selectedValueGender = spinner2.getText().toString();
                String selectedValueAge = spinner3.getText().toString();
                String selectedValueLocation = spinner4.getText().toString();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.TOP;
            window.setAttributes(layoutParams);
        }

        dialog.show();
    }

    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                startActivity(new Intent(SecondMain.this, SignIn.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void updateMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        // Clear existing markers
        googleMap.clear();

        // Create a marker for the current location
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Current Location");
        googleMap.addMarker(markerOptions);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, enable the location layer
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                // Exception thrown if permission is denied
                // Handle accordingly (e.g., show a message, disable location features)
            }
        } else {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String usernames = "fitrah";
        String emails = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                System.out.println("debug 6 "+selectedImageUri);
                userpphoto.setImageURI(selectedImageUri);
                uploadImage(selectedImageUri, usernames, emails);
            }
        }
    }

    private void uploadImage(Uri imageUri, final String username, final String email) {
        System.out.println("debug 5 "+imageUri);
        StorageReference fileRef = mStorage.child("post_images").child(mAuth.getCurrentUser().getUid()).child(imageUri.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                createPost(imageUrl, username, email);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(SecondMain.this, "Image upload failed", Toast.LENGTH_SHORT).show();
        });

    }

    private void createPost(String imageUrl, String username, String email) {
        String userId = mAuth.getCurrentUser().getUid();

        DatabaseReference postRef = mDatabase.child("UsersNewsFeedApp").child("posts").child(userId).child("profile");
        String postId = postRef.getKey();

        User user = new User(imageUrl, username, email);
        postRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SecondMain.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SecondMain.this, SecondMain.class);
                startActivity(intent);
            } else {
                Toast.makeText(SecondMain.this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }
}