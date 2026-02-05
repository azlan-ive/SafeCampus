package com.example.userinterface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private static final String APP_URL = "https://github.com/azlan-ive/SafeCampus.git";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvUrl = findViewById(R.id.tvUrl);

        // Set URL
        tvUrl.setText(APP_URL);

        // Open URL
        tvUrl.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Cannot open URL", Toast.LENGTH_LONG).show();
            }
        });

    }
}
