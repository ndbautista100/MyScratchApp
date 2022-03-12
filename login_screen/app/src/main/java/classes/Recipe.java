package classes;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class Recipe implements Serializable { // Serializable allows classes to be transferred between Activities
    private String name;
    private String description;
    private String ingredients;
    private String tools;
    private String user_ID;
    private String document_ID;

    private String imageName;
    private String image_URL;
    private int layoutChoice = 2131427391; //Layout One
    private int textBoxColor = 0xF2F2F2; //Gray
    private int backgroundColor = 0xFFFFFF; //White

    private String fontFamily = "sans-serif"; //default font

    public Recipe(String name, String description, String ingredients, String tools) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.tools = tools;
    }

    public Recipe(String name, String description, String ingredients, String tools, String userID) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.tools = tools;
        this.user_ID = userID;
        this.layoutChoice = 2131427384;
    }

    public Recipe() {

    }

    public Recipe(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user) {
        this.user_ID = user;
    }

    public String getDocument_ID()
    {
        return document_ID;
    }

    public void setDocument_ID(String id) {
        this.document_ID = id;
    }

    public String getImage_URL() {
        return image_URL;
    }

    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }

    public void setTextBoxColor (int color) {this.textBoxColor = color;}

    public int getTextBoxColor() {return this.textBoxColor;}

    public int getBackgroundColor() {return this.backgroundColor;}

    public void setBackgroundColor(int color) {this.backgroundColor = color;}

    public void setFontFamily(String font) {this.fontFamily = font;}

    public String getFontFamily() {return this.fontFamily;}


    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", image_URL='" + image_URL + '\'' +
                ", tools='" + tools + '\'' +
                ", user_ID='" + user_ID + '\'' +
                '}';
    }

    public int getLayoutChoice() {
        return layoutChoice;
    }

    public void setLayoutChoice(int layoutChoice) {
        this.layoutChoice = layoutChoice;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
