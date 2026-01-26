package com.example.userinterface;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportIncidentActivity extends AppCompatActivity {

    private TextView tvLocation, tvDateTime;
    private double currentLat = 0, currentLng = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_incident);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        LinearLayout layoutLocation = findViewById(R.id.layoutLocation);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);
        AutoCompleteTextView spinnerType = findViewById(R.id.spinnerType);

        // Setup Dropdown
        String[] types = {"Accident", "Crime", "Damage", "Medical", "Fire", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types);
        spinnerType.setAdapter(adapter);

        // Record Current Date/Time (Static)
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        tvDateTime.setText("Date/Time: " + currentTime);

        // Check Location Permissions
        requestLocation();

        // Click location to open Google Maps
        layoutLocation.setOnClickListener(v -> {
            if (currentLat != 0 && currentLng != 0) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(Incident+Location)", currentLat, currentLng, currentLat, currentLng);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Location not yet detected", Toast.LENGTH_SHORT).show();
                requestLocation();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(this, "Incident Reported Successfully", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    tvLocation.setText(String.format(Locale.getDefault(), "Location: %.4f, %.4f (Detected)", currentLat, currentLng));
                } else {
                    tvLocation.setText("Location: Detection failed (Check GPS)");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }
}
