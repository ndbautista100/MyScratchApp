package com.example.scratchappfeature;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import classes.RateComment;

public class RateCommentActivity extends AppCompatActivity {
    //TODO: connect to database, show recipe img + name
    //VARIABLES
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private RateComment rateComment;
    private RatingBar ratingBarNum;
    private Button btn_Submit;
    private StorageReference storageReference;
    private Uri imageLocationUri;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_comment);
        userID = auth.getCurrentUser().getUid();

        //Reference to Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/recipes");

        //Finding by ID, xml files
        ratingBarNum = (RatingBar) findViewById(R.id.ratingBarNum);
        btn_Submit = (Button) findViewById(R.id.btn_Submit);

        //Action on Button Click
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringRating = String.valueOf(ratingBarNum.getRating());
                Toast.makeText(getApplicationContext(), stringRating, Toast.LENGTH_LONG).show();
            }
        });
    }
    //TODO: Save rating num to database, array perhaps?
    private void saveRating(int ratingNum){
        db.collection("ratingComments").document(rateComment.getDocument_ID())
                .update("rating", ratingNum);
    }
}
