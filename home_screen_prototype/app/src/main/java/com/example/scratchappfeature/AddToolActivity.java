package com.example.scratchappfeature;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class AddToolActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tool);

        editText = (EditText) findViewById(R.id.add_tool_name);
        // createRecipeActivity = findViewById(R.layout.activity_create_recipe);

    }
}