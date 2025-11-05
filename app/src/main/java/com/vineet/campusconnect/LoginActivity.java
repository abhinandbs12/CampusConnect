package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// FIX 1: Add explicit import for RegisterActivity to resolve "cannot find symbol" error
import com.vineet.campusconnect.RegisterActivity;
// FIX 2: If MainActivity also gives a "cannot find symbol" error, uncomment the line below.
// import com.vineet.campusconnect.MainActivity;

public class LoginActivity extends AppCompatActivity {

    // 1. Declare our UI elements
    TextInputEditText etEmail, etPassword;
    MaterialButton loginButton, goToRegisterButton;

    // 2. Declare Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 3. Find all the UI elements by their ID
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        loginButton = findViewById(R.id.btn_login);

        // FIX 3: Corrected ID for the "Go to Register" button from tv_go_to_register
        // to tv_go_to_register_bottom, matching the improved UI layout.
        goToRegisterButton = findViewById(R.id.tv_go_to_register_bottom);

        // 4. Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 5. Set click listener for the LOGIN button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the fields
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 1. Basic Validation
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Show a progress message
                Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();

                // 3. Sign in the user with Firebase Auth
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success!
                                    Log.d("Auth", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // 4. Send user to the MainActivity
                                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    // Clear all previous activities
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish(); // Close the LoginActivity

                                } else {
                                    // If sign in fails, display a message
                                    Log.w("Auth", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // 6. Set click listener for the "Go to Register" button
        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will open the RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}