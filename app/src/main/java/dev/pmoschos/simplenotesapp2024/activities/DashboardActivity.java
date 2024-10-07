package dev.pmoschos.simplenotesapp2024.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// attention !
import android.Manifest;
import android.widget.Toast;
// to this import
import dev.pmoschos.simplenotesapp2024.R;
import dev.pmoschos.simplenotesapp2024.adapters.DashCardAdapter;
import dev.pmoschos.simplenotesapp2024.helpers.DatabaseHelper;
import dev.pmoschos.simplenotesapp2024.helpers.RequestCode;
import dev.pmoschos.simplenotesapp2024.models.DashCardModel;
import dev.pmoschos.simplenotesapp2024.models.UserModel;
import dev.pmoschos.simplenotesapp2024.helpers.ImageHandler;
import dev.pmoschos.simplenotesapp2024.helpers.PermissionHelper;


public class DashboardActivity extends AppCompatActivity {

    private GridView gridView;
    private DashCardAdapter dashCardAdapter;
    private List<DashCardModel> dashCardModelList;
    private TextView usernameTV;
    private FloatingActionButton createNoteButton;
    private ImageView favoriteIV;
    private ImageView settingsIV;
    private TextView numberOfNotesTV;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ShapeableImageView profileSIV;

    // Added these for images etc
    private PermissionHelper permissionHelper;
    private ImageHandler imageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize view components
        initView();
        // Retrieve user data from Firebase
        getUserData();

        // Set the adapter for the grid view
        dashCardAdapter = new DashCardAdapter(this, dashCardModelList);
        gridView.setAdapter(dashCardAdapter);

        // Set click listener for the create note button
        createNoteButton.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, CreateNoteActivity.class);
            startActivity(intent);
        });

        // Set click listener for the favorite button
        favoriteIV.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, FavoriteNotesActivity.class);
            startActivity(intent);
        });

        // Set click listener for the settings button
        settingsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the PermissionHelper
        permissionHelper = new PermissionHelper(this);

        // Initialize the launchers for camera and gallery
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        boolean uploadOnFirebase = true;
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
                            boolean uploadOnFirebase = true;
                            imageHandler.handleGalleryResult(data, uploadOnFirebase);
                        }
                    }
                }
        );

        // Initialize the ImageHandler for managing profile images
        imageHandler = new ImageHandler(this, profileSIV, cameraLauncher, galleryLauncher);

        // Set click listener for the profile image to show image picker dialog
        profileSIV.setOnClickListener(view -> showImagePickerDialog());
    }

    // Retrieve user data from Firebase and update the UI accordingly
    private void getUserData() {
        DatabaseHelper.getUserReference(DatabaseHelper.getUserId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);

                    if (userModel != null) {
                        // Set welcome message with user's name
                        usernameTV.setText("Welcome " + userModel.getUserName() + "!");

                        if (!TextUtils.isEmpty(userModel.getProfileImage())) {
                            Picasso.get()
                                    .load(userModel.getProfileImage())
                                    .fit()
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_person) // Placeholder image while loading
                                    .error(R.drawable.ic_error) // Error image if loading fails
                                    .into(profileSIV);
                        } else {
                            profileSIV.setImageResource(R.drawable.ic_person); // Default profile image if no image is set
                        }

                    }

                    // Get the number of notes and update the TextView
                    DataSnapshot dataSnapshot = snapshot.child("notes");
                    numberOfNotesTV.setText(String.format("%d", dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if the Firebase query fails
            }
        });

    }

    // Initialize the view components
    private void initView() {
        usernameTV = findViewById(R.id.usernameTV);
        gridView = findViewById(R.id.gridView);
        createNoteButton = findViewById(R.id.createNoteButton);
        favoriteIV = findViewById(R.id.favoriteIV);
        numberOfNotesTV = findViewById(R.id.numberOfNotesTV);
        profileSIV = findViewById(R.id.profileSIV);
        settingsIV = findViewById(R.id.settingsIV);

        // Initialize the data for the dashboard cards
        dashCardModelList = new ArrayList<>();
        dashCardModelList.add(new DashCardModel(R.drawable.ic_personal, "Personal", ""));
        dashCardModelList.add(new DashCardModel(R.drawable.ic_academic, "Academic", ""));
        dashCardModelList.add(new DashCardModel(R.drawable.ic_work, "Work", ""));
        dashCardModelList.add(new DashCardModel(R.drawable.ic_others, "Others", ""));
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
                        // Request camera permission if not granted
                        permissionHelper.requestCameraPermission(DashboardActivity.this);
                    }
                } else {
                    // Open Gallery
                    if (permissionHelper.checkStoragePermission()) {
                        imageHandler.openGallery();
                    } else {
                        // Request storage permission if not granted
                        permissionHelper.requestStoragePermission(DashboardActivity.this);
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