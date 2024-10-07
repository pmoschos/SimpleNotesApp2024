package dev.pmoschos.simplenotesapp2024.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import dev.pmoschos.simplenotesapp2024.R;
import dev.pmoschos.simplenotesapp2024.helpers.DatabaseHelper;
import dev.pmoschos.simplenotesapp2024.models.UserModel;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton registerButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize view components
        initView();

        // Get FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Set the toolbar as the app bar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);  // Disable default title
        }

        // Handle back button click on toolbar
        toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        // Handle physical back button press with OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back press here (e.g., finish the activity)
                finish(); // Finish the activity and go back
            }
        });

        // Set click listener for register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = Objects.requireNonNull(usernameInput.getText()).toString().trim();
                String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
                String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();
                String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString().trim();

                // Validate user input
                if (username.isEmpty()) {
                    usernameInput.setError("Please enter your username");
                    return;
                }
                if (email.isEmpty()) {
                    emailInput.setError("Please enter your email");
                    return;
                }
                if (password.isEmpty()) {
                    passwordInput.setError("Please enter your password");
                    return;
                }
                if (confirmPassword.isEmpty()) {
                    confirmPasswordInput.setError("Please confirm your password");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    confirmPasswordInput.setError("Passwords do not match");
                    return;
                }

                // Create user with email and password using FirebaseAuth
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        // Create UserModel instance and save user data to Firebase Realtime Database
                        UserModel userModel = new UserModel(username, email, DatabaseHelper.getUserId(), "");

                        DatabaseHelper.getUserReference(DatabaseHelper.getUserId()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Sign out the user after successful registration
                                auth.signOut();
                                Toast.makeText(RegisterActivity.this, "Registration completed", Toast.LENGTH_SHORT).show();

                                // Navigate to LoginActivity
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                // Finish current activity
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Show error message if saving user data fails
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show error message if registration fails
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        // Show message if registration is canceled
                        Toast.makeText(RegisterActivity.this, "Registration canceled", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    // Method to initialize the input fields and register button
    private void initView() {
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
    }
}