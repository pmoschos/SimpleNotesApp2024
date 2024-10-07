package dev.pmoschos.simplenotesapp2024.helpers;

/**
 * Enum representing different request codes for handling permissions and activity results.
 * Each request code has a unique integer value associated with it.
 */
public enum RequestCode {
    CAMERA(100), // Request code for accessing the camera.
    GALLERY(101); // Request code for accessing the gallery.

    private final int code;

    /**
     * Constructor to initialize the RequestCode with a specific integer value.
     *
     * @param code The integer value representing the request code.
     */
    RequestCode(int code) {
        this.code = code;
    }

    /**
     * Gets the integer value of the request code.
     *
     * @return The integer value representing the request code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Converts an integer to the corresponding RequestCode.
     * Iterates through the enum values to find a match.
     *
     * @param code The integer value to convert.
     * @return The corresponding RequestCode if found, or null if no match exists.
     */
    public static RequestCode fromInt(int code) {
        for (RequestCode requestCode : RequestCode.values()) {
            if (requestCode.getCode() == code) {
                return requestCode;
            }
        }
        // Alternatively, you can throw an exception if no match is found.
        return null;
    }
}
