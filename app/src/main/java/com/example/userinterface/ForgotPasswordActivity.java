package com.example.userinterface;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        MaterialButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnReset = findViewById(R.id.btnReset);
        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        TextInputEditText etEmail = findViewById(R.id.etEmail);

        btnBack.setOnClickListener(v -> finish());

        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                tilEmail.setError("Email is required");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Enter a valid email address");
                return;
            }

            tilEmail.setError(null);
            
            // Simulating sending reset link
            Toast.makeText(this, "Reset link sent to " + email, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
