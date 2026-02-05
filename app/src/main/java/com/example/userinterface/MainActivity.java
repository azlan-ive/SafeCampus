package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnReport = findViewById(R.id.btnReport);
        Button btnRecent = findViewById(R.id.btnRecent);
        Button btnAdminMarked = findViewById(R.id.btnAdminMarked);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnAbout  = findViewById(R.id.btnAbout);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnReport.setOnClickListener(v ->
                startActivity(new Intent(this, ReportIncidentActivity.class))
        );

        btnRecent.setOnClickListener(v ->
                startActivity(new Intent(this, RecentIncidentsActivity.class))
        );

        btnAdminMarked.setOnClickListener(v ->
                startActivity(new Intent(this, AdminMarkedLocationsActivity.class))
        );

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );

        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}