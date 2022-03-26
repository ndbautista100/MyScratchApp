package com.example.scratchappfeature;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class RateRecipeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String ratingComment;
    private float ratingNum;
    // Below was copied over
    // *------------------------------------*
    private ImageView recipeImagesImageView;
    private Button showImagesButton;
    private TextView toolsTextView;
    private TextView ingredientsTextView;
    private int SELECT_PICTURE = 200;
    FirebaseAuth fAuth;
    // *------------------------------------*
    public String getRatingComment() {
        return ratingComment;
    }
    public void setRatingComment() {
        this.ratingComment = ratingComment;
    }
    public float getRatingNum(){
        return ratingNum;
    }
    public void setRatingNum(){
        this.ratingNum = ratingNum;
    }
}
    /*
    Try to put a user check in here. If the user_ID in ratingComments collection matches the
    same user_ID in the recipes collection, then they are NOT allowed to make a comment on
    their own recipe. Maybe do this in the beginning before continuing.
    */

    //collection('scratchUsers').get();

    /*********/
    /*
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_recipe);
        Button getRating = findViewById(R.id.btn_getRating);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        getRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = "Rating is :" + ratingBar.getRating();
                Toast.makeText(RateRecipeActivity.this, rating, Toast.LENGTH_LONG).show();
            }
        }

        );

        public void openRateRecipeActivity(Ratingcomments ratingcomments) {
            // parse Recipe object to Map
            ObjectMapper oMapper = new ObjectMapper();
            //Map<String, Object> recipeMap = oMapper.convertValue(ratingcomments, Map.class);
        }
    }
     */
