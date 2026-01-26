package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputLayout tilUsername = findViewById(R.id.tilUsername);
        TextInputLayout tilPassword = findViewById(R.id.tilPassword);
        TextInputEditText etUsername = findViewById(R.id.etUsername);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> {

            String username = etUsername.getText() == null ? "" : etUsername.getText().toString().trim();
            String password = etPassword.getText() == null ? "" : etPassword.getText().toString().trim();

            // Clear previous errors
            tilUsername.setError(null);
            tilPassword.setError(null);

            // Internet check
            if (!NetworkUtil.isConnected(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
                return;
            }

            // Username required
            if (username.isEmpty()) {
                tilUsername.setError("Username is required");
                etUsername.requestFocus();
                return;
            }

            // Password required
            if (password.isEmpty()) {
                tilPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }

            // ✅ UI-only demo login success
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}
