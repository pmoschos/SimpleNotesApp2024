package dev.pmoschos.simplenotesapp2024.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Helper class to manage camera and storage permissions.
 * This class provides methods to check and request these permissions
 * based on the Android version and the app's context.
 */
public class PermissionHelper {

    // The context of the current state of the application/object, needed for checking permissions.
    private Context context;

    /**
     * Constructor that initializes the PermissionHelper with the given context.
     * @param context The context in which permissions will be checked and requested.
     */
    public PermissionHelper(Context context) {
        this.context = context;
    }

    /**
     * Checks if the CAMERA permission has been granted.
     *
     * @return true if the CAMERA permission is granted, false otherwise.
     */
    public boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks if the appropriate storage permission is granted.
     * For Android versions >= TIRAMISU (API level 33), it checks for
     * {@code READ_MEDIA_IMAGES}, otherwise it checks for {@code READ_EXTERNAL_STORAGE}.
     *
     * @return true if the required storage permission is granted, false otherwise.
     */
    public void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, RequestCode.CAMERA.getCode());
    }

    /**
     * Checks if the appropriate storage permission is granted.
     * For Android versions >= TIRAMISU (API level 33), it checks for
     * {@code READ_MEDIA_IMAGES}, otherwise it checks for {@code READ_EXTERNAL_STORAGE}.
     *
     * @return true if the required storage permission is granted, false otherwise.
     */
    public boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Requests the appropriate storage permission based on the Android version.
     * For Android versions >= TIRAMISU, it requests {@code READ_MEDIA_IMAGES},
     * otherwise it requests {@code READ_EXTERNAL_STORAGE}.
     *
     * @param activity The activity that initiates the permission request.
     */
    public void requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, RequestCode.GALLERY.getCode());
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RequestCode.GALLERY.getCode());
        }
    }
}
