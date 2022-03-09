package classes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Profile {
    private String pname, pbio, pfavoritefood;
    String pimageURL;
    private String userID;
    private ArrayList<String> followers;

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
        followers =  new ArrayList<String>();
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

    public String getImageURL(){return pimageURL;}

    public void setImageURL(String imageURL){this.pimageURL = imageURL;}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFriends(String userID) {
        if(!followers.contains(userID)) {
            followers.add(userID);
        }
    }
}