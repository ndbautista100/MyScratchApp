package classes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile {
    private String pname, pbio, pfavoritefood;
    private String profileImageURL;
    private String profileImageName;
    private String bannerImageURL;
    private String bannerImageName;
    private String userID;
    //private ArrayList<String> followers;
    private HashMap<String, Integer> followers;
    private ArrayList<String> savedRecipes;
    private ArrayList<String> savedIngredients;

    public Profile() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public Profile(String name, String bio, String favoritefood, String userid) {
        pname = name;
        pbio = bio;
        pfavoritefood = favoritefood;
        userID = userid;
        //followers =  new ArrayList<>();
        followers = new HashMap<>();
        savedRecipes = new ArrayList<>();
        savedIngredients = new ArrayList<>();
    }

    // getter methods for all variables.
    public String getpname() {
        return pname;
    }

    public void setname(String name) {
        pname = name;
    }

    public String getbio() {
        return pbio;
    }

    // setter method for all variables.
    public void setbio(String bio) {
        pbio = bio;
    }

    public String getfavoritefood() {
        return pfavoritefood;
    }

    public void setfavoritefood(String favoritefood) {
        pfavoritefood = favoritefood;
    }

    public String getProfileImageURL(){return profileImageURL;}

    public void setProfileImageURL(String profileImageURL){this.profileImageURL = profileImageURL;}

    public String getProfileImageName() {
        return profileImageName;
    }

    public void setProfileImageName(String profileImageName) {
        this.profileImageName = profileImageName;
    }

    public String getBannerImageURL(){return bannerImageURL;}

    public void setBannerImageURL(String bannerImageURL){this.bannerImageURL = bannerImageURL;}

    public String getBannerImageName() {
        return bannerImageName;
    }

    public void setBannerImageName(String bannerImageName) {
        this.bannerImageName = bannerImageName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    /*
    public ArrayList<String> getFollowers() {
        return followers;
    }*/

    public ArrayList<String> getSavedRecipes() {return this.savedRecipes;}

    /*
    public void setFriends(String userID) {
        if(!followers.contains(userID)) {
            followers.add(userID);
        }
    }*/

    public ArrayList<String> getSavedIngredients() {
        return this.savedIngredients;
    }
}