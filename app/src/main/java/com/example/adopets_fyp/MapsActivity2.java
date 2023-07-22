package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.adopets_fyp.R;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity2 extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private LatLng userLocation;
    private Marker selectedMarker;
    private Polyline currentPolyline;
    private LatLng destinationLatLng;
    Button btnCancelNavigation;
    double latitudePost, longitudePost;

    private boolean navigationMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MapsActivity2.this,
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
                        MapsActivity2.this,
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
                    Toast.makeText(MapsActivity2.this, "User location not available.", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(MapsActivity2.this,SecondMain.class);
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
                Toast.makeText(MapsActivity2.this, "Failed to get directions.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
