package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextInputLayout tilUsername = findViewById(R.id.tilUsername);
        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        TextInputLayout tilPassword = findViewById(R.id.tilPassword);
        TextInputEditText etUsername = findViewById(R.id.etUsername);
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty()) {
                tilUsername.setError("Username is required");
                return;
            }
            if (email.isEmpty()) {
                tilEmail.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                tilPassword.setError("Password is required");
                return;
            }

            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
            finish(); // Go back to login
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}
