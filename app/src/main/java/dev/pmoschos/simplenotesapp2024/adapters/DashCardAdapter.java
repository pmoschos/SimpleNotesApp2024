package dev.pmoschos.simplenotesapp2024.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import dev.pmoschos.simplenotesapp2024.R;
import dev.pmoschos.simplenotesapp2024.activities.NoteViewActivity;
import dev.pmoschos.simplenotesapp2024.helpers.DatabaseHelper;
import dev.pmoschos.simplenotesapp2024.models.DashCardModel;
import dev.pmoschos.simplenotesapp2024.models.NoteModel;
import dev.pmoschos.simplenotesapp2024.models.UserModel;

public class DashCardAdapter extends BaseAdapter {

    private Context context;
    private List<DashCardModel> dashCardList;

    // Constructor
    public DashCardAdapter(Context context, List<DashCardModel> dashCardList) {
        this.context = context;
        this.dashCardList = dashCardList;
    }

    @Override
    public int getCount() {
        return dashCardList.size();
    }

    @Override
    public Object getItem(int position) {
        return dashCardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate the layout for a dash card item if it is not being reused
            convertView = LayoutInflater.from(context).inflate(R.layout.dash_card, parent, false);
        }

        // Find views for the current item
        ConstraintLayout cardNoteView = convertView.findViewById(R.id.cardNoteView);
        ImageView circularImageView = convertView.findViewById(R.id.circularImageView);
        TextView titleTV = convertView.findViewById(R.id.titleTV);
        TextView filesTV = convertView.findViewById(R.id.filesTV);

        // Get current DashCardModel
        DashCardModel card = dashCardList.get(position);

        // Bind data to views
        circularImageView.setImageResource(card.getImageResId());
        titleTV.setText(card.getTitle());

        // Retrieve the category title from the DashCardModel
        String category = card.getTitle();

        // Query for the specific category in Firebase
        DatabaseHelper.getAllNotesReference().orderByChild("category").equalTo(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int counterForCategory = (int) snapshot.getChildrenCount();
                    filesTV.setText(counterForCategory + " Files");
                } else {
                    filesTV.setText("0 Files");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if the Firebase query fails
            }
        });

        // Set an OnClickListener to navigate to NoteViewActivity when the card is clicked
        cardNoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NoteViewActivity.class);
                // Pass the category as an extra
                intent.putExtra("category", card.getTitle());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}