package dev.pmoschos.simplenotesapp2024.models;

public class DashCardModel {
    private int imageResId;
    private String title;
    private String fileCount;

    // Constructor
    public DashCardModel(int imageResId, String title, String fileCount) {
        this.imageResId = imageResId;
        this.title = title;
        this.fileCount = fileCount;
    }

    // Getters
    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getFileCount() {
        return fileCount;
    }
}
