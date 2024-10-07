package dev.pmoschos.simplenotesapp2024.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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

public class NoteViewActivity extends AppCompatActivity {

    private RecyclerView notesRV;
    private NoteAdapter notesAdapter;
    private List<NoteModel> noteList;
    private TextView categoryTV;
    private Toolbar toolbar;
    private TextInputLayout searchTIL;
    private TextView searchTV;
    private TextInputEditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize view components
        initView();

        // Set up RecyclerView with a staggered grid layout
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        notesRV.setLayoutManager(staggeredGridLayoutManager);
        noteList = new ArrayList<>();
        notesAdapter = new NoteAdapter(noteList);
        notesRV.setAdapter(notesAdapter);

        // Fetch notes from Firebase
        fetchNotesFromFirebase();

        // Set category title from intent extra
        categoryTV.setText(getIntent().getStringExtra("category"));

        // Handle back button click on toolbar
        toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        // Set click listener for search icon to make search input visible
        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make the search input visible
                searchTIL.setVisibility(View.VISIBLE);
                // Focus on the search input
                searchET.requestFocus();
            }
        });

        // Add text change listener for search input
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

        // Handle back button press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to DashboardActivity when the back button is pressed
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
                // Finish all activities below this one
                finishAffinity();
            }
        });

    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        // Return the default OnBackInvokedDispatcher
        return super.getOnBackInvokedDispatcher();
    }

    // Initialize the view components
    private void initView() {
        notesRV = findViewById(R.id.notesRV);
        categoryTV = findViewById(R.id.categoryTV);
        toolbar = findViewById(R.id.toolbar);
        searchTIL = findViewById(R.id.searchTIL);
        searchTV = findViewById(R.id.searchTV);
        searchET = findViewById(R.id.searchET);
    }

    // Fetch notes from Firebase and update the UI
    private void fetchNotesFromFirebase() {
        DatabaseHelper.getAllNotesReference().addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the previous list to avoid duplicates
                noteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoteModel note = snapshot.getValue(NoteModel.class);

                    // Add note to the list if it matches the selected category
                    if (Objects.equals(getIntent().getStringExtra("category"), note.getCategory())) {
                        noteList.add(note);
                    }
                }
                // Notify adapter about data changes
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Show error message if Firebase query fails
                Toast.makeText(NoteViewActivity.this, "Failed to load notes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}