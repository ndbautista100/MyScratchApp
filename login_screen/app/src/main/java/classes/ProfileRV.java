package classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scratchappfeature.R;

import java.util.ArrayList;

public class ProfileRV extends RecyclerView.Adapter<ProfileRV.ViewHolder> {
    // creating variables for our ArrayList and context
    private ArrayList<Profile> profileArray;
    private Context context;

    // creating constructor for our adapter class
    public ProfileRV(ArrayList<Profile> profileArray, Context context) {
        this.profileArray = profileArray;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileRV.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_profile_pagae, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileRV.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Profile profile = profileArray.get(position);
        holder.nameTV.setText(profile.getpname());
        holder.bioTV.setText(profile.getbio());
        holder.favoritefoodTV.setText(profile.getfavoritefood());
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return coursesArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView nameTV;
        private final TextView bioTV;
        private final TextView favoritefoodTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            courseNameTV = itemView.findViewById(R.id.idTVCourseName);
            courseDurationTV = itemView.findViewById(R.id.idTVCourseDuration);
            courseDescTV = itemView.findViewById(R.id.idTVCourseDescription);
        }
    }
}
