package classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    private String name;
    private String description;
    private ArrayList<String> ingredients;
    private ArrayList<String> tools;

    public Recipe(String name, String description, ArrayList<String> ingredients, ArrayList<String> tools) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.tools = tools;
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

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getTools() {
        return tools;
    }

    public void setTools(ArrayList<String> tools) {
        this.tools = tools;
    }
}
