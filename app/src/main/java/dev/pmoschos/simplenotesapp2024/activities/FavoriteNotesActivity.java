package dev.pmoschos.simplenotesapp2024.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.pmoschos.simplenotesapp2024.R;
import dev.pmoschos.simplenotesapp2024.adapters.NoteAdapter;
import dev.pmoschos.simplenotesapp2024.helpers.DatabaseHelper;
import dev.pmoschos.simplenotesapp2024.models.NoteModel;

public class FavoriteNotesActivity extends AppCompatActivity {

    private RecyclerView notesRV;
    private NoteAdapter notesAdapter;
    private List<NoteModel> noteList;
    private TextView categoryTV;
    private Toolbar toolbar;
    private TextInputLayout searchTIL;
    private TextView searchTV;
    private TextInputEditText searchET;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize view components
        initView();
        // Fetch notes from Firebase
        fetchNotesFromFirebase();

        // Set category title to "Favorites"
        categoryTV.setText("Favorites");

        // Handle back button click on toolbar
        toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        // Set click listener for search icon to show search bar
        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTIL.setVisibility(View.VISIBLE);
                searchET.requestFocus();
            }
        });

        // Add text change listener to the search input
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (notesAdapter != null) {
                    // Filter notes based on user input
                    notesAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed after text is changed
            }
        });

    }

    // Initialize the view component
    private void initView() {
        notesRV = findViewById(R.id.notesRV);
        categoryTV = findViewById(R.id.categoryTV);
        toolbar = findViewById(R.id.toolbar);
        searchTIL = findViewById(R.id.searchTIL);
        searchTV = findViewById(R.id.searchTV);
        searchET = findViewById(R.id.searchET);

        // Set up RecyclerView with a staggered grid layout
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        notesRV.setLayoutManager(staggeredGridLayoutManager);
        noteList = new ArrayList<>();
        notesAdapter = new NoteAdapter(noteList);
        notesRV.setAdapter(notesAdapter);
    }

    // Fetch favorite notes from Firebase
    private void fetchNotesFromFirebase() {
        DatabaseHelper.getAllNotesReference().addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the previous list to avoid duplication
                noteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoteModel note = snapshot.getValue(NoteModel.class);

                    assert note != null;
                    // Check if the note is marked as favorite
                    if (note.isFavorite()) {
                        // Add favorite note to the list
                        noteList.add(note);
                    }
                }
                // Notify adapter about data changes
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Show error message if Firebase query fails
                Toast.makeText(FavoriteNotesActivity.this, "Failed to load notes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}