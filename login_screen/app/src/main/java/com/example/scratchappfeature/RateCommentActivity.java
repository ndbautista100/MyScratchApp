package com.example.scratchappfeature;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import classes.RateComment;

public class RateCommentActivity extends AppCompatActivity {
    //TODO: connect to database, show recipe img + name
    //VARIABLES
    private RateComment rateComment;
    private static final String TAG = "RateCommentActivity";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference ratingsCollection = db.collection("ratingComment");
    private FirebaseAuth fauth;
    private FirebaseStorage storage;
    private EditText rateCommentText;
    private RatingBar ratingBarNum;
    private Button btn_SubmitRating;
    private StorageReference storageReference;
    private Uri imageLocationUri;
    private String userID;
    private String dbComment;
    private int stars;

    private Toolbar toolbarScratchNotes;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //MUST GET RECIPE ID
        Intent intent = getIntent();
        if (intent.hasExtra("open_recipe_from_id")) {
            String recipe_Id = intent.getStringExtra("open_recipe_from_id");
        }
        //MUST GET DOCUMENT ID FROM RECIPE ID
        //DocumentReference docRef = db.collection("ratingComment").document(recipe_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_comment);
        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();

        //Reference to Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/recipes");

        //TODO: fix the invoke virtual method on null object ref on 58, 60
        //Finding by ID, xml files
        ratingBarNum = (RatingBar)findViewById(R.id.ratingBarNum);
        //ratingBarNum.setRating(rateComment.getRatingNum());
        rateCommentText = (EditText)findViewById(R.id.commentEditText);
        //rateCommentText.setText(rateComment.getRatingTextComment());

        //Action on Button Click, just shows the rating number
        btn_SubmitRating = (Button) findViewById(R.id.btn_Submit);
        try {
            btn_SubmitRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //If rate comment is empty
                    if (TextUtils.isEmpty(rateCommentText.getText().toString())) {
                        rateCommentText.setError("Please enter a description for the rating.");
                        return;
                    }

                    String stringRating = String.valueOf(ratingBarNum.getRating());
                    Toast.makeText(getApplicationContext(), stringRating, Toast.LENGTH_LONG).show();
                    //Note here
                    stars = ratingBarNum.getNumStars();
                    saveRating(stars);
                    String dbComment = rateCommentText.getText().toString();
                    saveComment(dbComment);
                    openMainActivity();
                }
            });
        }catch (Exception e)
        {
            Log.e(TAG, "Exception caught");
                    e.printStackTrace();
        }
    }

    //TODO: Save rating num array perhaps?
    //Note here
    private void saveRating(int ratingNum){
        db.collection("ratingComments").document(rateComment.getDocument_ID())
                .update("rating", ratingNum);
    }
    private void saveComment(String ratingTextComment){
        db.collection("ratingComments").document(rateComment.getDocument_ID())
                .update("rating", rateComment);
    }
    public void openMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
