package com.example.scratchappfeature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FontRVAdapter extends RecyclerView.Adapter<FontRVAdapter.ViewHolder>{

    private ArrayList<String> mFontList;
    private Context mContext;
    private OnItemClickListener mListener;

    public FontRVAdapter(Context context, ArrayList<String> fontList){
        this.mFontList = new ArrayList<> (fontList);
        this.mContext = context;

    }

    public interface OnItemClickListener {
        void onItemClick (int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.font_rv_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fontName = mFontList.get(position);
        holder.fontButton.setText(fontName);
    }

    @Override
    public int getItemCount() {
        return mFontList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button fontButton;

        public ViewHolder(View itemView, OnItemClickListener listener){
            super(itemView);
            fontButton = itemView.findViewById(R.id.fontRVButton);
            fontButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
    }

    }
}
