package com.example.scratchappfeature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import classes.Recipe;

public class RecipeRVAdapter extends RecyclerView.Adapter<RecipeRVAdapter.ViewHolder> {
    private ArrayList<Recipe> dataModelArrayList;
    private Context context;

    // constructor class for our Adapter
    public RecipeRVAdapter(ArrayList<Recipe> dataModelArrayList, Context context) {
        this.dataModelArrayList = dataModelArrayList;
        this.context = context;
    }

    public RecipeRVAdapter(View inflate) {

    }

    @NonNull
    @Override
    public RecipeRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.image_lv_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRVAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        Recipe model = dataModelArrayList.get(position);
        holder.courseNameTV.setText(model.getName());

        // we are using Picasso to load images
        // from URL inside our image view.
        Picasso.with(context.getApplicationContext()).load(model.getImage_URL()).into(holder.courseIV);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting on click listener
                // for our items of recycler items.
                Toast.makeText(context, "Clicked item is " + model.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView courseNameTV;
        private ImageView courseIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            courseNameTV = itemView.findViewById(R.id.idTVtext);
            courseIV = itemView.findViewById(R.id.idIVimage);
        }
    }
}
