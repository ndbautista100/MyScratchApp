package com.example.scratchappfeature;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExploreActivity extends AppCompatActivity {
    FloatingActionButton addVideoBtn;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);


        setTitle("Videos");
        // instantiate ui view
        addVideoBtn = findViewById(R.id.addVideoBtn);

        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(ExploreActivity.this, AddVideoActivity.class)));
            }
        });
    }
}
