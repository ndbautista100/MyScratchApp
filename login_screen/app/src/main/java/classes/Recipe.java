package classes;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable { // Serializable allows classes to be transferred between Activities
    private String name;
    private String description;
    private String ingredients;
    private String tools;
    private String user_ID;

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

    public String getUser_ID(){ return user_ID;}

    public void setUser_ID(String user){this.user_ID = user;}

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", tools='" + tools + '\'' +
                ", user_ID='" + user_ID + '\'' +
                '}';
    }
}
