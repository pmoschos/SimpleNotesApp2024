package dev.pmoschos.simplenotesapp2024.models;

import java.util.Date;

public class NoteModel {
    private String id;
    private String category;
    private String title;
    private String description;
    private Date date;
    private String image;
    private boolean favorite;

    public NoteModel() {}

    public NoteModel(String id, String category, String title, String description, Date date, String image, boolean favorite) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.date = date;
        this.image = image;
        this.favorite = favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
