package dev.pmoschos.simplenotesapp2024.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * Helper class for interacting with Firebase services such as Realtime Database and Storage.
 * Provides static methods to obtain references to users, notes, and user images.
 * This class follows the Singleton pattern by preventing instantiation.
 */
public class DatabaseHelper {
    // Constants representing the database nodes for users and notes
    private static final String USERS = "users";
    private static final String NOTES = "notes";

    /**
     * Private constructor to prevent instantiation of this helper class.
     * All methods are static and can be accessed without creating an object of this class.
     */
    private DatabaseHelper() {}

    /**
     * Retrieves a DatabaseReference pointing to a specific user's data in the Realtime Database.
     *
     * @param userUID The unique identifier of the user.
     * @return DatabaseReference pointing to "/users/{userUID}".
     */
    public static DatabaseReference getUserReference(String userUID) {
        // Get the root reference to the "users" node and navigate to the specific user UID
        return FirebaseDatabase.getInstance().getReference(USERS).child(userUID);
    }

    /**
     * Retrieves a DatabaseReference pointing to a specific note of the current user in the Realtime Database.
     *
     * @param noteId The unique identifier of the note.
     * @return DatabaseReference pointing to "/users/{currentUserId}/notes/{noteId}".
     */
    public static DatabaseReference getNotesReference(String noteId) {
        // Ensure that the current user ID is obtained
        return FirebaseDatabase.getInstance().getReference(USERS).child(getUserId()).child(NOTES).child(noteId);
    }

    /**
     * Retrieves a DatabaseReference pointing to all notes of the current user in the Realtime Database.
     *
     * @return DatabaseReference pointing to "/users/{currentUserId}/notes".
     */
    public static DatabaseReference getAllNotesReference() {
        // Reference to the "notes" node under the current user's data
        return FirebaseDatabase.getInstance().getReference(USERS).child(getUserId()).child(NOTES);
    }

    /**
     * Retrieves the unique identifier (UID) of the currently authenticated user.
     *
     * @return The UID of the current user, or null if no user is authenticated.
     */
    public static String getUserId() {
        // Fetch the UID from Firebase Authentication
        return FirebaseAuth.getInstance().getUid();
    }

    /**
     * Retrieves the email address of the currently authenticated user.
     *
     * @return The email of the current user.
     * @throws NullPointerException if there is no authenticated user or email is null.
     */
    public static String getUserEmail() {
        // Get the current user and ensure it's not null, then retrieve the email
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
    }


    /**
     * Retrieves a StorageReference pointing to the location where a user's image is stored in Firebase Storage.
     *
     * @param userUID The unique identifier of the user.
     * @return StorageReference pointing to "/users/{userUID}" in Firebase Storage.
     */
    public static StorageReference getUserImageReference(String userUID) {
        // Get the root storage reference to the "users" node and navigate to the specific user UID
        return FirebaseStorage.getInstance().getReference(USERS).child(userUID);
    }
}
