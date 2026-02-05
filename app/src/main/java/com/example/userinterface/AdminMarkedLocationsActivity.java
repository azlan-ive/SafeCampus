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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminMarkedLocationsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView rv;
    private LocationAdapter adapter;
    private List<CampusLocation> locationList;
    private DatabaseReference mDatabase, mUsersRef;
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
        setContentView(R.layout.activity_admin_marked_locations);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvHello = findViewById(R.id.tvHello);
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        
        locationList = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupFirebase();
        setupRecyclerView();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupLocationTracking();
    }

    private void setupFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://safe-campus-7377b-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDatabase = db.getReference().child("locations");

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
        rv = findViewById(R.id.rvLocations);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationAdapter(locationList);
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
        fetchLocations();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build();
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    private void fetchLocations() {
        progressBar.setVisibility(View.VISIBLE);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        CampusLocation loc = postSnapshot.getValue(CampusLocation.class);
                        if (loc != null) locationList.add(loc);
                    }
                } else {
                    // Fallback mock data
                    locationList.add(new CampusLocation("Hospital Jasin", "CLINIC", 1.647309, 103.409963));
                    locationList.add(new CampusLocation("PPP", "SECURITY", 1.598709, 103.368226));
                    locationList.add(new CampusLocation("Bilik Azlan", "CLINIC", 2.224379, 102.456476));
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                layoutEmpty.setVisibility(locationList.isEmpty() ? View.VISIBLE : View.GONE);
                updateMapMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        mMap.clear();
        userMarker = null;

        for (CampusLocation loc : locationList) {
            float hue = BitmapDescriptorFactory.HUE_VIOLET;
            if (loc.type != null) {
                if (loc.type.equalsIgnoreCase("CLINIC")) hue = BitmapDescriptorFactory.HUE_GREEN;
                else if (loc.type.equalsIgnoreCase("SECURITY")) hue = BitmapDescriptorFactory.HUE_AZURE;
                else if (loc.type.equalsIgnoreCase("EMERGENCY")) hue = BitmapDescriptorFactory.HUE_RED;
            }
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(loc.lat, loc.lng))
                    .title(loc.name)
                    .snippet(loc.type)
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
        }
    }

    @Override protected void onResume() { super.onResume(); startLocationUpdates(); }
    @Override protected void onPause() { super.onPause(); fusedLocationClient.removeLocationUpdates(locationCallback); }

    private class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
        private List<CampusLocation> locations;
        LocationAdapter(List<CampusLocation> locations) { this.locations = locations; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.incidentcard, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CampusLocation item = locations.get(position);
            holder.tvType.setText(item.type);
            holder.tvTitle.setText(item.name);
            holder.tvTime.setVisibility(View.GONE);
            holder.tvBy.setVisibility(View.GONE);
            holder.tvUserId.setVisibility(View.GONE);
            holder.tvDesc.setText("Campus safety point: " + item.type);
            holder.tvLocation.setText(String.format(Locale.getDefault(), "%.4f, %.4f", item.lat, item.lng));

            int color = 0xFF6750A4;
            if (item.type != null) {
                if (item.type.equalsIgnoreCase("CLINIC")) color = 0xFF2E7D32;
                else if (item.type.equalsIgnoreCase("SECURITY")) color = 0xFF1565C0;
                else if (item.type.equalsIgnoreCase("EMERGENCY")) color = 0xFFC62828;
            }
            holder.tvType.setTextColor(color);

            holder.itemView.setOnClickListener(v -> {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", item.lat, item.lng, item.lat, item.lng, item.name);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            });
        }

        @Override public int getItemCount() { return locations.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvType, tvTitle, tvTime, tvLocation, tvDesc, tvBy, tvUserId;
            ViewHolder(View itemView) {
                super(itemView);
                tvType = itemView.findViewById(R.id.tvType);
                tvTitle = itemView.findViewById(R.id.tvType);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                tvBy = itemView.findViewById(R.id.tvBy);
                tvUserId = itemView.findViewById(R.id.tvUserId);
            }
        }
    }
}
