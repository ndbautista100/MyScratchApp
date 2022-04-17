package classes;

import androidx.annotation.NonNull;
import com.example.scratchappfeature.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class RateComment implements Serializable {
    //VARIABLES
    private int ratingNum;
    private String ratingTextComment;
    private String userID;
    private String document_ID;
    private String imageName;
    String image_URL;
    private String name;

    //CONSTRUCTORS
    public RateComment() { }
    public RateComment(int ratingNum, String ratingTextComment, String document_ID){
        this.ratingNum = ratingNum;
        this.ratingTextComment = ratingTextComment;
        this.document_ID = document_ID;
    }
    public RateComment(int ratingNum, String ratingTextComment, String document_ID,
                       String imageName, String image_URL, String name){
        this.ratingNum = ratingNum;
        this.ratingTextComment = ratingTextComment;
        this.document_ID = document_ID;
        this.imageName = imageName;
        this.image_URL = image_URL;
        this.name = name;
    }

    //GETTERS & SETTERS
    public int getRatingNum(){
        return ratingNum;
    }
    public void setRatingNum(int ratingNum){
        this.ratingNum = ratingNum;
    }
    public String getRatingTextComment(){
        return ratingTextComment;
    }
    public void setRatingTextComment(String ratingTextComment){
        this.ratingTextComment = ratingTextComment;
    }
    public String getUserID(){
        return userID;
    }
    public void setUserID(String userID){
        this.userID = userID;
    }
    public String getDocument_ID(){
        return document_ID;
    }
    public void setDocument_ID(String document_ID){
        this.document_ID = document_ID;
    }
    public String getImageName(){
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public String getImage_URL() {
        return image_URL;
    }
    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //TO STRING
    @NonNull
    @Override
    public String toString() {
        return "RateComment{" +
                "ratingNum='" + ratingNum + '\'' +
                ", ratingTextComment='" + ratingTextComment + '\'' +
                ", document_ID='" + document_ID + '\'' +
                '}';
    }
}