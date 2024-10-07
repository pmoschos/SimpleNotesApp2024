package dev.pmoschos.simplenotesapp2024.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import dev.pmoschos.simplenotesapp2024.R;
import dev.pmoschos.simplenotesapp2024.helpers.DatabaseHelper;
import dev.pmoschos.simplenotesapp2024.helpers.ImageHandler;
import dev.pmoschos.simplenotesapp2024.helpers.PermissionHelper;
import dev.pmoschos.simplenotesapp2024.helpers.RequestCode;
import dev.pmoschos.simplenotesapp2024.models.UserModel;

public class SettingsActivity extends AppCompatActivity {

    private ImageView profileIV;
    private TextInputEditText usernameInput;
    private MaterialButton updateBtn;
    private MaterialButton signOutBtn;
    private Uri newImageUri;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    // Added these for images etc
    private PermissionHelper permissionHelper;
    private ImageHandler imageHandler;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize view components
        initView();
        // Fetch user data from Firebase
        getUserData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Set the toolbar as the app bar

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);    // Enable back button in the toolbar
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

        // Initialize the PermissionHelper
        permissionHelper = new PermissionHelper(this);

        // Initialize the launchers
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //. .. . . . .
                        selectedImageUri = imageHandler.getCacheImagePath();
                        boolean uploadOnFirebase = false;
                        imageHandler.handleCameraResult(uploadOnFirebase);
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUri = data.getData();
                            boolean uploadOnFirebase = false;
                            imageHandler.handleGalleryResult(data, uploadOnFirebase);
                        }
                    }
                }
        );

        // Initialize the ImageHandler
        imageHandler = new ImageHandler(this, profileIV, cameraLauncher, galleryLauncher);

        profileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerDialog();
            }
        });

        // Set click listener for update button to update user profile
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameInput.getText().toString().trim();

                // Validate username input
                if (username.isEmpty()) {
                    usernameInput.setError("Please enter your username");
                    return;
                }

                // Upload selected image to Firebase if available
                if (selectedImageUri != null) {
                    imageHandler.uploadImageToFirebase(selectedImageUri);
                }

                // Update user data in Firebase Realtime Database
                HashMap<String, Object> map = new HashMap<>();
                map.put("userName", username);

                DatabaseHelper.getUserReference(DatabaseHelper.getUserId()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Show success message
                        Toast.makeText(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show error message if update fails
                        Toast.makeText(SettingsActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // Set click listener for sign out button
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out the user and navigate to LoginActivity
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                // Finish all activities below this one
                finishAffinity();
            }
        });

    }

    // Initialize the view components
    private void initView() {
        profileIV = findViewById(R.id.profileIV);
        usernameInput = findViewById(R.id.usernameInput);
        updateBtn = findViewById(R.id.updateBtn);
        signOutBtn = findViewById(R.id.signOutBtn);
    }

    // Fetch user data from Firebase and update the UI
    private void getUserData() {
        DatabaseHelper.getUserReference(DatabaseHelper.getUserId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        if (!TextUtils.isEmpty(userModel.getProfileImage())) {
                            Picasso.get()
                                    .load(userModel.getProfileImage())
                                    .fit()
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_person) // Placeholder image while loading
                                    .error(R.drawable.ic_error)        // Error image if loading fails
                                    .into(profileIV);
                        } else {
                            profileIV.setImageResource(R.drawable.ic_person); // Default profile image if no image is set
                        }
                        // Set the username input field with the current username
                        usernameInput.setText(userModel.getUserName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if the Firebase query fails
            }
        });
    }

    // Show a dialog to choose between Camera and Gallery for selecting an image
    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Open Camera
                    if (permissionHelper.checkCameraPermission()) {
                        imageHandler.openCamera();
                    } else {
                        permissionHelper.requestCameraPermission(SettingsActivity.this);
                    }
                } else {
                    // Open Gallery
                    if (permissionHelper.checkStoragePermission()) {
                        imageHandler.openGallery();
                    } else {
                        permissionHelper.requestStoragePermission(SettingsActivity.this);
                    }
                }
            }
        }).show();
    }

    // Handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestCode rc = RequestCode.fromInt(requestCode);
        if (rc != null) {
            switch (rc) {
                case CAMERA:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Open camera if permission is granted
                        imageHandler.openCamera();
                    } else {
                        Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GALLERY:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Open gallery if permission is granted
                        imageHandler.openGallery();
                    } else {
                        Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    // Handle other cases if any
                    break;
            }
        } else {
            // Handle unknown request code
            Toast.makeText(this, "Unknown Permission Request", Toast.LENGTH_SHORT).show();
        }
    }
}