package com.example.userinterface;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentIncidentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_incidents);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // RecyclerView
        RecyclerView rv = findViewById(R.id.rvIncidents);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<IncidentUI> data = new ArrayList<>();
        data.add(new IncidentUI("Accident", "Bike accident near library", "2026-01-24 20:10"));
        data.add(new IncidentUI("Crime", "Suspicious activity at parking lot", "2026-01-24 19:40"));
        data.add(new IncidentUI("Damage", "Broken streetlight near hostel", "2026-01-24 18:20"));

        // Sort by latest time
        Collections.sort(data, (o1, o2) -> o2.time.compareTo(o1.time));

        IncidentAdapter adapter = new IncidentAdapter(data);
        rv.setAdapter(adapter);
    }
}
