package com.example.scratchappfeature;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddVideoActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private Button uploadBtn;
    private EditText eT;
    private VideoView videoView;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_videos);

        uploadBtn = findViewById(R.id.uploadVideo);
        eT =  findViewById(R.id.titleEt);
        videoView = findViewById(R.id.videoView);
        fab = findViewById(R.id.pickVideo);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
