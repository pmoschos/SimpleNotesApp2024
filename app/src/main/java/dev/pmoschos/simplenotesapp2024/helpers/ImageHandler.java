package dev.pmoschos.simplenotesapp2024.helpers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Helper class to handle image operations such as capturing photos,
 * selecting images from the gallery, and uploading them to Firebase Storage.
 */
public class ImageHandler {

    private Activity activity;
    private ImageView profileSIV;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String filename;

    /**
     * Constructor to initialize the ImageHandler with required components.
     *
     * @param activity The current activity that uses the ImageHandler.
     * @param profileSIV The ImageView where the image will be displayed.
     * @param cameraLauncher Launcher for handling the camera result.
     * @param galleryLauncher Launcher for handling the gallery result.
     */
    public ImageHandler(Activity activity, ImageView profileSIV,
                        ActivityResultLauncher<Intent> cameraLauncher,
                        ActivityResultLauncher<Intent> galleryLauncher) {
        this.activity = activity;
        this.profileSIV = profileSIV;
        this.cameraLauncher = cameraLauncher;
        this.galleryLauncher = galleryLauncher;
    }

    /**
     * Opens the camera to capture an image and saves the captured image to cache.
     */
    public void openCamera() {
        // Create a unique filename for the image.
        filename = System.currentTimeMillis() + ".jpg";
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Save the image to cache.
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath());
        // Launch the camera.
        cameraLauncher.launch(cameraIntent);
    }

    /**
     * Generates a URI to the cached image path where the captured image will be stored.
     *
     * @return The URI of the cached image file.
     */
    public Uri getCacheImagePath() {
        File path = new File(activity.getExternalCacheDir(), "camera");
        // Create the directory if it doesn't exist.
        if (!path.exists()) path.mkdirs();
        // Create the image file in the directory.
        File image = new File(path, filename);
        return FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName() + ".provider", image);
    }

    /**
     * Opens the gallery to select an image. Uses different URIs based on Android version.
     */
    public void openGallery() {
        Intent galleryIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android TIRAMISU (API level 33) and above, use VOLUME_EXTERNAL URI.
            galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL));
        } else {
            // For older versions, use EXTERNAL_CONTENT_URI.
            galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        // Launch the gallery picker.
        galleryLauncher.launch(galleryIntent);
    }

    /**
     * Handles the result of the camera activity, sets the captured image in the ImageView,
     * and optionally uploads the image to Firebase.
     *
     * @param uploadOnFirebase Whether to upload the image to Firebase after capture.
     */
    public void handleCameraResult(boolean uploadOnFirebase) {
        // Get the cached image URI.
        Uri selectedImageUri = getCacheImagePath();
        // Display the image in the ImageView.
        profileSIV.setImageURI(selectedImageUri);
        if (uploadOnFirebase) {
            // Upload the image to Firebase.
            uploadImageToFirebase(selectedImageUri);
        }
    }

    /**
     * Handles the result of the gallery activity, sets the selected image in the ImageView,
     * and optionally uploads the image to Firebase.
     *
     * @param data The Intent containing the selected image data.
     * @param uploadOnFirebase Whether to upload the selected image to Firebase.
     */
    public void handleGalleryResult(Intent data, boolean uploadOnFirebase) {
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            try {
                // Get the Bitmap from the selected image URI.
                Bitmap galleryPhoto = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);
                // Display the image in the ImageView.
                profileSIV.setImageBitmap(galleryPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (uploadOnFirebase) {
                // Upload the image to Firebase.
                uploadImageToFirebase(selectedImageUri);
            }
        }
    }

    /**
     * Uploads the selected image to Firebase Storage and updates the user's profile image in Firebase Database.
     *
     * @param selectedImageUri The URI of the image to upload.
     */
    public void uploadImageToFirebase(Uri selectedImageUri) {
        // Upload the image file to Firebase Storage and get the download URL.
        DatabaseHelper.getUserImageReference(DatabaseHelper.getUserId())
                .putFile(selectedImageUri)
                .continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> DatabaseHelper.getUserImageReference(DatabaseHelper.getUserId()).getDownloadUrl())
                .addOnSuccessListener(uri -> {
                    // Get the download URL as a string.
                    String profileImageUri = uri.toString();
                    HashMap<String, Object> map = new HashMap<>();
                    // Create a map with the image URL.
                    map.put("profileImage", profileImageUri);

                    // Update the user's profile image URL in Firebase Database.
                    DatabaseHelper.getUserReference(DatabaseHelper.getUserId()).updateChildren(map)
                            .addOnSuccessListener(unused -> Toast.makeText(activity, "Image uploaded successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(activity, "Image upload failed", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(activity, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show());
    }
}