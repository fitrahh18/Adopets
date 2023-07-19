package com.example.adopets_fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
                        System.out.println("debug 15" + location);
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            userLocation = new LatLng(latitude, longitude);
                            System.out.println("debug 11" + userLocation);
                            Toast.makeText(
                                    MapsActivity2.this,
                                    "Latitude: " + latitude + ", Longitude: " + longitude,
                                    Toast.LENGTH_SHORT
                            ).show();
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
        }

        // Retrieve marked locations from Firebase Realtime Database
        DatabaseReference databaseReference = mDatabase.child("UsersNewsFeedApp").child("posts");
        DatabaseReference locationsRef = databaseReference.child(mAuth.getCurrentUser().getUid()).child("locations");
        locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    double latitude = Double.parseDouble(locationSnapshot.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(locationSnapshot.child("longitude").getValue().toString());
                    String placename = locationSnapshot.child("markerName").getValue(String.class);
                    LatLng latLng = new LatLng(latitude, longitude);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(placename));
                    marker.setTag(locationSnapshot.getKey());
                }

                // Move camera to the first marked location
                /*if (dataSnapshot.hasChildren()) {
                    double latitude = Double.parseDouble(dataSnapshot.child("0/latitude").getValue().toString());
                    double longitude = Double.parseDouble(dataSnapshot.child("0/longitude").getValue().toString());
                    LatLng firstLatLng = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 12));*/
                //}
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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();

                // Get the selected marker's position
                selectedMarker = marker;

                // Draw the polyline
                drawPolyline();

                return true;
            }
        });
    }

    private void drawPolyline() {
        if (userLocation != null && selectedMarker != null) {
            // Remove any existing polyline
            if (currentPolyline != null) {
                currentPolyline.remove();
            }

            LatLng destinationLatLng = selectedMarker.getPosition();

            // Create a GeoApiContext with your API key
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyDMk5qP6IKnAODrp8NHzt4iQbS4EHGdTVs")
                    .build();

            // Calculate directions using the Directions API
            DirectionsApiRequest directionsRequest = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(new com.google.maps.model.LatLng(userLocation.latitude, userLocation.longitude))
                    .destination(new com.google.maps.model.LatLng(destinationLatLng.latitude, destinationLatLng.longitude));

            directionsRequest.setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    // Get the first route from the result
                    if (result.routes != null && result.routes.length > 0) {
                        DirectionsRoute route = result.routes[0];

                        // Extract the polyline points from the route
                        List<LatLng> polylinePoints = new ArrayList<>();
                        for (com.google.maps.model.LatLng latLng : route.overviewPolyline.decodePath()) {
                            polylinePoints.add(new LatLng(latLng.lat, latLng.lng));
                        }

                        // Draw the polyline on the map
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .addAll(polylinePoints)
                                .width(8)
                                .color(Color.BLUE);
                        currentPolyline = mMap.addPolyline(polylineOptions);

                        // Adjust the map bounds to include the polyline and both markers
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder()
                                .include(userLocation)
                                .include(destinationLatLng);
                        for (LatLng point : polylinePoints) {
                            boundsBuilder.include(point);
                        }
                        LatLngBounds bounds = boundsBuilder.build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    Toast.makeText(MapsActivity2.this, "Failed to retrieve directions", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
