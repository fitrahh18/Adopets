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
import android.util.Log;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class SecondMain extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_IMAGE_PICK = 1;
    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private boolean navigationMode = false;
    //homePagePostView
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private LatLng userLocation;
    private Marker selectedMarker;
    private Polyline currentPolyline;
    private LatLng destinationLatLng;
    Button btnCancelNavigation;
    double latitudePost, longitudePost;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    TextView uploadphoto;
    private Uri selectedImageUri;
    //others
    Button signOutBtn, mylocate;
    TextView lrusername, lruseremail;
    GoogleSignInClient gsc;
    private MeowBottomNavigation bottomNavigation;
    RelativeLayout nearby, home, profile;
    TextView phonenumber;

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
        phonenumber = findViewById(R.id.userphoneprofile);


        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

// Specify the path to the image URL
        String path = "User/posts/" + mAuth.getCurrentUser().getUid() + "/profile";

        databaseRef.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = null;

                    imageUrl = dataSnapshot.child("userimageUrl").getValue(String.class);
                    String rusername =dataSnapshot.child("username").getValue(String.class);
                    String ruseremail =dataSnapshot.child("useremail").getValue(String.class);
                    String ruserphone=dataSnapshot.child("phone").getValue(String.class);
                    lruseremail.setText(ruseremail);
                    lrusername.setText(rusername);
                    phonenumber.setText(ruserphone);

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



        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        fetchPosts();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SecondMain.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            getCurrentLocation();
        }

        btnCancelNavigation = findViewById(R.id.btn_cancel_navigation);

        if (navigationMode) {
            btnCancelNavigation.setVisibility(View.VISIBLE);
        } else if (!navigationMode) {
            btnCancelNavigation.setVisibility(View.GONE);
        }

    }

    private void fetchPosts(String selectedValueSpecies, String selectedValueGender, String selectedValueAge) {
        DatabaseReference postsRef = mDatabase.child("User").child("posts");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postsSnapshot : postSnapshot.getChildren()) {
                        Post post = postsSnapshot.getValue(Post.class);
                        if (post != null && post.getImageUrl() != null) {
                            // Check if the post matches the selected search criteria
                            if (selectedValueSpecies.isEmpty() || post.getPetSpecies().equals(selectedValueSpecies)) {
                                if (selectedValueGender.isEmpty() || post.getPetGender().equals(selectedValueGender)) {
                                    if (selectedValueAge.isEmpty() || post.getPetAge().equals(selectedValueAge)) {

                                        postList.add(post);

                                    }
                                }
                            }
                        }
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

    private void fetchPosts() {
        DatabaseReference postsRef = mDatabase.child("User").child("posts");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postsSnapshot : postSnapshot.getChildren()) {

                        Post post = postsSnapshot.getValue(Post.class);
                        assert post != null;
                        if (post.getImageUrl() != null) {
                            postList.add(post);
                        }

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



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String selectedValueSpeacies = spinner1.getText().toString();
                String selectedValueGender = spinner2.getText().toString();
                String selectedValueAge = spinner3.getText().toString();

                fetchPosts(selectedValueSpeacies, selectedValueGender, selectedValueAge);
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

        DatabaseReference postRef = mDatabase.child("User").child("posts").child(userId).child("profile");

        // Create a map to hold the updated values
        Map<String, Object> updateValues = new HashMap<>();
        updateValues.put("userimageUrl", imageUrl);

        postRef.updateChildren(updateValues).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SecondMain.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SecondMain.this, SecondMain.class);
                startActivity(intent);
            } else {
                Toast.makeText(SecondMain.this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //map activity
    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Location location = locationResult.getLastLocation();
                        if (location != null) {

                            if (navigationMode) {
                                btnCancelNavigation.setVisibility(View.VISIBLE);
                            } else if (!navigationMode) {
                                btnCancelNavigation.setVisibility(View.GONE);
                            }

                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            userLocation = new LatLng(latitude, longitude);
                            focusCameraOnLocation(userLocation);
                            Intent intent = getIntent();
                            latitudePost= intent.getDoubleExtra("latitudes",0);
                            longitudePost= intent.getDoubleExtra("longitudes",0);
                            System.out.println("debug 19 "+userLocation);
                            if (latitudePost!=0 && longitudePost!=0&&userLocation!=null){
                                LatLng postdestinationLatLng=new LatLng(latitudePost, longitudePost);
                                navigationMode=true;
                                drawPolyline(userLocation,postdestinationLatLng);
                                focusCameraOnLocation(userLocation);
                            }else if (destinationLatLng != null) {
                                drawPolyline(userLocation, destinationLatLng);
                            }
                        }
                    }
                }, getMainLooper()
        );

        return;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(
                        this,
                        "Permission denied!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }

        // Retrieve marked locations from Firebase Realtime Database
        DatabaseReference databaseReference = mDatabase.child("User").child("posts");
        DatabaseReference locationsRef = databaseReference;
        locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot locationSnapshot = userSnapshot.child("locations");
                    for (DataSnapshot locationsSnapshot : locationSnapshot.getChildren()) {
                        double latitude = Double.parseDouble(locationsSnapshot.child("latitude").getValue().toString());
                        double longitude = Double.parseDouble(locationsSnapshot.child("longitude").getValue().toString());
                        String placename = locationsSnapshot.child("markerName").getValue(String.class);
                        LatLng latLng = new LatLng(latitude, longitude);
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(placename));
                        marker.setTag(locationSnapshot.getKey());

                        // Set up marker click listener
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                selectedMarker = marker;
                                showNavigationDialog();
                                return true;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(
                        SecondMain.this,
                        "Failed to retrieve marked locations from Firebase.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Call the method to add the "Cancel Navigation" button click listener
        setupCancelNavigationButton();
    }
    private void showNavigationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start Navigation");
        builder.setMessage("Do you want to navigate to " + selectedMarker.getTitle() + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                navigationMode = true;
                // Start navigation
                if (userLocation != null) {
                    destinationLatLng = selectedMarker.getPosition();
                    drawPolyline(userLocation, destinationLatLng);
                    focusCameraOnLocation(userLocation); // Focus camera on user's current location
                } else {
                    Toast.makeText(SecondMain.this, "User location not available.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void drawPolyline(LatLng origin, LatLng destination) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCuBp-Fnefr1Xe5RxLgxMh3D2OzOQzxyaE")
                .build();

        DirectionsApiRequest req = DirectionsApi.getDirections(context, origin.latitude + "," + origin.longitude,
                        destination.latitude + "," + destination.longitude)
                .mode(TravelMode.DRIVING)
                .optimizeWaypoints(true);

        req.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPolyline == null) {
                            // Create a new polyline if it doesn't exist
                            List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
                            currentPolyline = mMap.addPolyline(new PolylineOptions()
                                    .addAll(decodedPath)
                                    .width(12)
                                    .color(Color.BLUE));
                        } else {
                            // Update the existing polyline's points
                            currentPolyline.setPoints(PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath()));
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                showDirectionErrorToast();
                Log.e("DirectionsAPI", "Error: " + e.getMessage());
            }
        });
    }

    // Method to focus the camera on a given location
    private void focusCameraOnLocation(LatLng location) {
        if (mMap != null&&navigationMode) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18f));
        } else if(mMap != null && !navigationMode){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        }
    }
    // Method to cancel navigation and remove the polyline
    private void cancelNavigation() {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = null;

        }
        if (latitudePost!=0 && longitudePost!=0){
            Intent intent = new Intent(SecondMain.this,SecondMain.class);
            startActivity(intent);
        }

        destinationLatLng = null; // Reset the destination

        navigationMode = false;

        latitudePost=0;
        longitudePost=0;
    }

    // Method to add the "Cancel Navigation" button click listener
    private void setupCancelNavigationButton() {

        btnCancelNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNavigation();
                // Optionally, reset the camera focus to the user's current location after canceling
                if (userLocation != null) {
                    focusCameraOnLocation(userLocation);
                }
            }
        });
    }


    private void showDirectionErrorToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SecondMain.this, "Failed to get directions.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
