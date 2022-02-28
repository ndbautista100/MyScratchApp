package com.example.scratchappfeature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class LayoutRVAdapter extends RecyclerView.Adapter<LayoutRVAdapter.ViewHolder> {


    private String mImageUrls;
    private Context mContext;

    public LayoutRVAdapter(Context context, String imageUrl) {
        this.mImageUrls = imageUrl;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mImageUrls != null){
            Picasso.with(mContext.getApplicationContext())
                    .load(mImageUrls)
                    .into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked Picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1; //mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder (View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}