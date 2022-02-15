package com.example.scratchappfeature;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {

    private String recipeName;
    private String recipeTools;
    private String recipeIngredients;
    private String recipeInstructions;


    protected Recipe(Parcel in){
        this.recipeName = in.readString();
        this.recipeTools = in.readString();
        this.recipeIngredients = in.readString();
        this.recipeInstructions = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recipeName);
        dest.writeString(this.recipeTools);
        dest.writeString(this.recipeIngredients);
        dest.writeString(this.recipeInstructions);
    }


    public String getRecipeName() {return recipeName;}
    public String getRecipeTools() {return recipeTools;}
    public String getRecipeIngredients() {return recipeIngredients;}
    public String getRecipeInstructions() {return recipeInstructions;}

    
}
