package com.example.scratchappfeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToolsRVAdapter extends RecyclerView.Adapter<ToolsRVAdapter.ViewHolder>{

    private Context mContext;
    private OnItemClickListener mListener;
    private ArrayList<String> mToolsList;
    private String mProfileID;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CardView cardItem;

    public void setProfileID(String profileID){
        this.mProfileID = profileID;
    }

    public ToolsRVAdapter(Context context, ArrayList<String> toolsList){
        this.mContext = context;
        this.mToolsList = new ArrayList<>(toolsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tool_rv_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String name = mToolsList.get(position);
        holder.toolName.setText(name);
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "asdf;asdf", Toast.LENGTH_SHORT).show();
            }
        });

        holder.cardItem.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View view){
                showMenu(view, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mToolsList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public View cardItem;
        TextView toolName;

        public ViewHolder (View itemView, OnItemClickListener listener){
            super(itemView);
            toolName = itemView.findViewById(R.id.toolListName);
            cardItem = itemView.findViewById(R.id.toolRVItem);
            itemView.setOnClickListener(view -> {
                if (listener != null){
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    private void removeAt(int position){
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mToolsList.size());
        String toolToDelete = mToolsList.get(position);

        final Map<String, Object> savedTools = new HashMap<>();
        savedTools.put("savedTools", FieldValue.arrayRemove(toolToDelete));
        db.collection("profile").document(mProfileID)
                .update(savedTools);
        mToolsList.remove(position);
    }

    private void showMenu(View v, int position){
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_ingredients_tools, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item){
                if (item.getItemId() == R.id.deletePopUpMenu){
                    removeAt(position);
                }
                if (item.getItemId() == R.id.searchPopUpMenu){
                    String name = mToolsList.get(position);
                    Intent intent = new Intent(mContext, ExploreActivity_Revamp.class);
                    intent.putExtra("open_explore_from_tools", name);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
