package com.example.scratchappfeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import classes.Profile;

public class IngredientsRVAdapter extends RecyclerView.Adapter<IngredientsRVAdapter.ViewHolder> {

    private ArrayList<String> mIngredientsList;
    private Context mContext;
    private String mProfileID;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnItemClickListener mListener;
    private CardView cardItem;

    public IngredientsRVAdapter(Context context, ArrayList<String> ingredientsList){
        this.mContext = context;
        this.mIngredientsList = new ArrayList<>(ingredientsList);
    }

    public void setProfileID(String profileID){
        this.mProfileID = profileID;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_rv_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String name = mIngredientsList.get(position);
        String[] tokens = name.split("/");
        holder.ingredientName.setText(tokens[0]);
        holder.categoryName.setText(tokens[1]);
        holder.cardItem.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View view){
                showMenu(view, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIngredientsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public View cardItem;
        TextView ingredientName;
        TextView categoryName;

        public ViewHolder (View itemView, OnItemClickListener listener){
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredientListName);
            categoryName = itemView.findViewById(R.id.ingredientsListCategory);
            cardItem = itemView.findViewById(R.id.ingredientRVItem);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });

        }
    }
    
    private void removeAt(int position){
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mIngredientsList.size());
        String ingredientToDelete = mIngredientsList.get(position);
        
        final Map<String, Object> savedIngredients = new HashMap<>();
        savedIngredients.put("savedIngredients", FieldValue.arrayRemove(ingredientToDelete));
        db.collection("profile").document(mProfileID)
                .update(savedIngredients);
        mIngredientsList.remove(position);
    }

    private void showMenu(View v, int position){
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_ingredients_tools, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.deletePopUpMenu) {
                    removeAt(position);
                }

                if (item.getItemId() == R.id.searchPopUpMenu){
                    //send to search with ingredient name
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
