package dev.pmoschos.simplenotesapp2024.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.Objects;

import dev.pmoschos.simplenotesapp2024.R;
import dev.pmoschos.simplenotesapp2024.helpers.DatabaseHelper;
import dev.pmoschos.simplenotesapp2024.models.NoteModel;

public class CreateNoteActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private Toolbar toolbar;
    private TextView titleTV;
    private TextView saveTV;
    private TextInputEditText noteTitle;
    private TextInputEditText noteDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        // Set up the toolbar (optional)
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize the data for the spinner
        String[] categories = {"Personal", "Academic", "Work", "Others"};

        // Handle back button click on toolbar
        toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        // Setting up the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                Toast.makeText(CreateNoteActivity.this, "Selected: " + selectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });

        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = categorySpinner.getSelectedItem().toString();
                String title = Objects.requireNonNull(noteTitle.getText()).toString().trim();
                String description = Objects.requireNonNull(noteDescription.getText()).toString().trim();

                if (title.isEmpty()) {
                    noteTitle.setError("Please enter the title");
                    return;
                }
                if (description.isEmpty()) {
                    noteDescription.setError("Please enter the description");
                    return;
                }

                // We create unique key for our new note using Firebase API
                String noteId = DatabaseHelper.getAllNotesReference().push().getKey();

                NoteModel noteModel = new NoteModel(noteId, category, title, description, new Date(), "", false);

                DatabaseHelper.getNotesReference(noteId).setValue(noteModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateNoteActivity.this, "Note created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), NoteViewActivity.class);
                        intent.putExtra("category", noteModel.getCategory());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateNoteActivity.this, "Note creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void initView() {
        saveTV = findViewById(R.id.saveTV);
        categorySpinner = findViewById(R.id.category_spinner);
        toolbar = findViewById(R.id.toolbar);
        noteTitle = findViewById(R.id.title_input);
        noteDescription = findViewById(R.id.description_input);
    }
}