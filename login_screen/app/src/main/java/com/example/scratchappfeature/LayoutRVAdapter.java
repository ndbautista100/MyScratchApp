package com.example.scratchappfeature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LayoutRVAdapter extends RecyclerView.Adapter<LayoutRVAdapter.ViewHolder> {


    private ArrayList<String> mImagesUrls = new ArrayList<>();
    private Context mContext;
    private int layoutId;

    public LayoutRVAdapter(Context context, ArrayList<String> imageUrls, int layoutId) {
        this.mImagesUrls = imageUrls;
        this.mContext = context;
        this.layoutId = layoutId;
    }

    public LayoutRVAdapter(View inflate) {
    }

    @NonNull
    @Override
    public LayoutRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LayoutRVAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mImagesUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView recipeImage;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}