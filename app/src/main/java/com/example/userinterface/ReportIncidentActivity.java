package com.example.userinterface;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportIncidentActivity extends AppCompatActivity {

    private TextView tvLocation, tvDateTime;
    private TextInputEditText etDescription;
    private AutoCompleteTextView spinnerType;
    private double currentLat = 0, currentLng = 0;
    private String recordedTime;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_incident);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://safe-campus-7377b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        etDescription = findViewById(R.id.etDescription);
        LinearLayout layoutLocation = findViewById(R.id.layoutLocation);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);
        spinnerType = findViewById(R.id.spinnerType);

        // Setup Dropdown
        String[] types = {"Accident", "Crime", "Damage", "Medical", "Fire", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types);
        spinnerType.setAdapter(adapter);

        // Record Current Date/Time
        recordedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        tvDateTime.setText("Date/Time: " + recordedTime);

        // Setup Real-time Location Callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        updateLocationUI(currentLat, currentLng);
                    }
                }
            }
        };

        // Click location to open Google Maps
        layoutLocation.setOnClickListener(v -> {
            if (currentLat != 0 && currentLng != 0) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(Incident+Location)", currentLat, currentLng, currentLat, currentLng);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Location not yet detected", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v -> submitIncident());
    }

    private void updateLocationUI(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                tvLocation.setText("Location: " + address);
            } else {
                tvLocation.setText(String.format(Locale.getDefault(), "Location: %.4f, %.4f", lat, lng));
            }
        } catch (IOException e) {
            tvLocation.setText(String.format(Locale.getDefault(), "Location: %.4f, %.4f", lat, lng));
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateIntervalMillis(500)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void submitIncident() {
        String type = spinnerType.getText().toString();
        String description = etDescription.getText().toString().trim();

        if (type.isEmpty()) {
            Toast.makeText(this, "Please select an incident type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please provide a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = "Anonymous";
                    if (snapshot.exists()) {
                        username = snapshot.child("username").getValue(String.class);
                    }
                    saveIncidentToFirebase(type, description, userId, username);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    saveIncidentToFirebase(type, description, userId, "Anonymous");
                }
            });
        } else {
            saveIncidentToFirebase(type, description, "Anonymous", "Anonymous");
        }
    }

    private void saveIncidentToFirebase(String type, String description, String userId, String username) {
        IncidentUI incident = new IncidentUI(type, description, recordedTime, currentLat, currentLng, userId, username);
        String incidentId = mDatabase.child("incidents").push().getKey();
        if (incidentId != null) {
            mDatabase.child("incidents").child(incidentId).setValue(incident)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ReportIncidentActivity.this, "Incident Reported Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ReportIncidentActivity.this, "Failed to report: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }
}
