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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import classes.RateComment;
import classes.Recipe;

public class RateCommentActivity extends AppCompatActivity {
    //VARIABLES
    private RateComment rateComment;
    private Recipe recipe;
    private static final String TAG = "RateCommentActivity";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference ratingsCollection = db.collection("ratingComments");
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_comment);
        ratingBarNum = (RatingBar)findViewById(R.id.ratingBarNum);
        //ratingBarNum.setRating(rateComment.getRatingNum());
        rateCommentText = (EditText)findViewById(R.id.commentEditText);
        //rateCommentText.setText(rateComment.getRatingTextComment());
        //Action on Button Click, just shows the rating number
        btn_SubmitRating = (Button) findViewById(R.id.btn_Submit);

        fauth = FirebaseAuth.getInstance();
        userID = fauth.getCurrentUser().getUid();

        //MUST GET RECIPE ID
        Intent intent = getIntent();
        if (intent.hasExtra("open_recipe_from_id")) {
            String document_ID = intent.getStringExtra("open_recipe_from_id");
            //DocumentReference docRef = db.collection("recipes").document(recipe_ID);
            /*
            DocumentReference docRef = db.collection("ratingComments").document(document_ID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "Found Document");
                        recipe = document.toObject(Recipe.class);
                    } else {
                        Log.e(TAG, "No such Document");
                    }
                } else {
                    Log.e(TAG, "get failed with" + task.getException());
                }
            });
            */
        }

        //Reference to Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/recipes");

        try {
            btn_SubmitRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //If rate comment is empty
                    if (TextUtils.isEmpty(rateCommentText.getText().toString())) {
                        rateCommentText.setError("Please enter a description for the rating.");
                        return;
                    }
                    float check = ratingBarNum.getRating();
                    if (check == 0)
                    {
                        Toast.makeText(RateCommentActivity.this, "Please select a rating between 1 thru 5.", Toast.LENGTH_SHORT).show();
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
