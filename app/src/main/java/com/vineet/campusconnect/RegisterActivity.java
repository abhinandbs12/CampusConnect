package com.vineet.campusconnect; // <-- Make sure this line matches!

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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // 1. Declare our UI elements
    TextInputEditText etName, etBranch, etEmail, etPassword;
    MaterialButton registerButton, goToLoginButton;

    // 2. Declare Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 3. Find all the UI elements by their ID
        etName = findViewById(R.id.et_register_name);
        etBranch = findViewById(R.id.et_register_branch);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        registerButton = findViewById(R.id.btn_register);
        goToLoginButton = findViewById(R.id.tv_go_to_login);

        // 4. Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 5. Set click listener for the REGISTER button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get all the text from the fields
                String name = etName.getText().toString().trim();
                String branch = etBranch.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 1. Basic Validation
                if (name.isEmpty() || branch.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Show a progress message
                Toast.makeText(RegisterActivity.this.peekAvailableContext(), "Creating account...", Toast.LENGTH_SHORT).show();

                // 3. Create the user in Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Authentication was successful!
                                    Log.d("Auth", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // 4. Now, save the extra user data to Firestore
                                    saveUserDataToFirestore(user, name, branch);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Auth", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // 6. Set click listener for the "Go to Login" button
        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This just closes the current (Register) screen
                finish();
            }
        });
    }

    // 7. This is our new helper method
    private void saveUserDataToFirestore(FirebaseUser firebaseUser, String name, String branch) {
        String uid = firebaseUser.getUid();

        // Create a new user map
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("branch", branch);
        userData.put("email", firebaseUser.getEmail());

        // Get the current timestamp
        userData.put("joinedTimestamp", FieldValue.serverTimestamp());

        // Save to Firestore in the "users" collection
        db.collection("users").document(uid)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Data saved! Now we can send the user to login
                            Toast.makeText(RegisterActivity.this, "Account created! Please login.", Toast.LENGTH_LONG).show();

                            // Send user to LoginActivity
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            // Clear all previous activities from the stack
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish(); // Close the RegisterActivity

                        } else {
                            // If saving data fails, show an error
                            Toast.makeText(RegisterActivity.this, "Error saving user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}