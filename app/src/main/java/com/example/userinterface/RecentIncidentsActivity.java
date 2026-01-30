package com.example.userinterface;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecentIncidentsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView rv;
    private IncidentAdapter adapter;
    private List<IncidentUI> incidentList;
    private List<CampusLocation> campusLocations;
    private DatabaseReference mDatabase, mLocationsRef;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private TextView tvHello, tvCurrentLocation;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker userMarker;
    private boolean isFirstLocation = true;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private final LatLng UITM_JASIN = new LatLng(2.2256, 102.4563);
    private static final int LOCATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_incidents);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvHello = findViewById(R.id.tvHello);
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        
        incidentList = new ArrayList<>();
        campusLocations = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupFirebase();
        setupRecyclerView();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        setupLocationTracking();
    }

    private void setupFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://safe-campus-7377b-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDatabase = db.getReference().child("incidents");
        mLocationsRef = db.getReference().child("locations");

        if (auth.getCurrentUser() != null) {
            db.getReference().child("users").child(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        if (s.exists()) tvHello.setText("Hello, " + s.child("username").getValue(String.class) + "!");
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
        }
    }

    private void setupRecyclerView() {
        rv = findViewById(R.id.rvIncidents);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IncidentAdapter(incidentList);
        rv.setAdapter(adapter);
    }

    private void setupLocationTracking() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) updateLocationUI(location);
                }
            }
        };
    }

    private void updateLocationUI(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
            if (userMarker != null) userMarker.remove();
            userMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng).title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            
            if (isFirstLocation) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                isFirstLocation = false;
            }
        }

        // Run address lookup in background to prevent SafeCampus isn't responding (ANR)
        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String finalAddress = addresses.get(0).getAddressLine(0);
                    runOnUiThread(() -> tvCurrentLocation.setText(finalAddress));
                }
            } catch (IOException e) {
                runOnUiThread(() -> tvCurrentLocation.setText(location.getLatitude() + ", " + location.getLongitude()));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UITM_JASIN, 16f));
        startLocationUpdates();
        fetchData();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build();
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        mLocationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                campusLocations.clear();
                for (DataSnapshot ds : s.getChildren()) {
                    CampusLocation loc = ds.getValue(CampusLocation.class);
                    if (loc != null) campusLocations.add(loc);
                }
                updateMapMarkers();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                incidentList.clear();
                for (DataSnapshot ds : s.getChildren()) {
                    IncidentUI in = ds.getValue(IncidentUI.class);
                    if (in != null) incidentList.add(in);
                }
                Collections.sort(incidentList, (o1, o2) -> (o2.time != null && o1.time != null) ? o2.time.compareTo(o1.time) : 0);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                layoutEmpty.setVisibility(incidentList.isEmpty() ? View.VISIBLE : View.GONE);
                updateMapMarkers();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { progressBar.setVisibility(View.GONE); }
        });
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        mMap.clear();
        userMarker = null;

        for (CampusLocation loc : campusLocations) {
            float hue = BitmapDescriptorFactory.HUE_VIOLET;
            if (loc.type != null && loc.type.equalsIgnoreCase("clinic")) hue = BitmapDescriptorFactory.HUE_GREEN;
            mMap.addMarker(new MarkerOptions().position(new LatLng(loc.lat, loc.lng)).title(loc.name).snippet(loc.type).icon(BitmapDescriptorFactory.defaultMarker(hue)));
        }

        for (IncidentUI in : incidentList) {
            if (in.latitude != 0) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(in.latitude, in.longitude)).title(in.type).snippet(in.description).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }
        }
    }

    @Override protected void onResume() { super.onResume(); startLocationUpdates(); }
    @Override protected void onPause() { super.onPause(); fusedLocationClient.removeLocationUpdates(locationCallback); }
}
