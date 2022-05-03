package classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionsFAQ {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> recipe_create = new ArrayList<String>();
        recipe_create.add("1.) To create a recipe, click on the cooking icon on the toolbar.");
        recipe_create.add("2.) If you are unable to see the toolbar, head to the homescreen.");
        //----
        List<String> recipe_edit = new ArrayList<String>();
        recipe_edit.add("Click on your MyScratch icon to view your recipes.");
        //----
        List<String> profile_edit = new ArrayList<String>();
        profile_edit.add("Click on the three dots on the top right in the toolbar.");
        //----
        List<String> recipe_search = new ArrayList<String>();
        recipe_search.add("Click on the searchbar on the main screen..");
        //----
        List<String> follow = new ArrayList<String>();
        follow.add("Click on the searchbar on the main screen..");
        follow.add("Be mindful of exact spelling, type in names of users.");
        //----
        List<String> logout = new ArrayList<String>();
        logout.add("Click on the three dots, select settings.");
        logout.add("Select logout button and follow any directions if necessary.");
        //----
        expandableListDetail.put("How to Create Recipe", recipe_create);
        expandableListDetail.put("How to Edit Recipe", recipe_edit);
        expandableListDetail.put("How to Edit Profile", profile_edit);
        expandableListDetail.put("How to Search Recipes", recipe_search);
        expandableListDetail.put("How to Follow People", follow);
        expandableListDetail.put("How to Log Out", logout);
        return expandableListDetail;
    }
}
