package dev.pmoschos.simplenotesapp2024.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.pmoschos.simplenotesapp2024.R;
import dev.pmoschos.simplenotesapp2024.helpers.DatabaseHelper;
import dev.pmoschos.simplenotesapp2024.models.NoteModel;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NotesViewHolder> {
    private List<NoteModel> notesList;
    private List<NoteModel> filteredNotesList;

    public NoteAdapter(List<NoteModel> notesList) {
        this.notesList = notesList;
        this.filteredNotesList = notesList;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        NoteModel note = notesList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.descriptionTextView.setText(note.getDescription());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // Get the date from firebase
        String currentDate = sdf.format(note.getDate());
        holder.note_date.setText(currentDate);

        if (note.isFavorite()) {
            holder.filled_star_iv.setVisibility(View.VISIBLE);
            holder.empty_star_iv.setVisibility(View.GONE);
        } else {
            holder.filled_star_iv.setVisibility(View.GONE);
            holder.empty_star_iv.setVisibility(View.VISIBLE);
        }

        holder.filled_star_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("favorite", false);
                DatabaseHelper.getNotesReference(note.getId()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        holder.empty_star_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("favorite", true);

                DatabaseHelper.getNotesReference(note.getId()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchQuery = charSequence.toString().trim().toLowerCase();

                if (searchQuery.isEmpty()) {
                    notesList = new ArrayList<>(filteredNotesList); // Reset to original list
                } else {
                    notesList = new ArrayList<>();
                    for (NoteModel note : filteredNotesList) {
                        if (note.getTitle().toLowerCase().contains(searchQuery)) {
                            notesList.add(note);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = notesList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                // Notify that the data set has changed
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView note_date;
        private ImageView filled_star_iv;
        private ImageView empty_star_iv;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title);
            descriptionTextView = itemView.findViewById(R.id.note_description);
            note_date = itemView.findViewById(R.id.note_date);
            filled_star_iv = itemView.findViewById(R.id.filled_star_iv);
            empty_star_iv = itemView.findViewById(R.id.empty_star_iv);
        }
    }
}

