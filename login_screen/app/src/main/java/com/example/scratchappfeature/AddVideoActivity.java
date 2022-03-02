package com.example.scratchappfeature;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddVideoActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private Button uploadBtn;
    private EditText eT;
    private VideoView videoView;
    private static final int video_pick_gallery =100;
    private static final int video_pick_camera =101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private String [] cameraPermissions;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_videos);

        uploadBtn = findViewById(R.id.uploadVideo);
        eT =  findViewById(R.id.titleEt);
        videoView = findViewById(R.id.videoView);
        fab = findViewById(R.id.pickVideo);

        // camera permission
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPickDialog();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void videoPickDialog(){
        String [] options = { "Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick videos from").setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                if(i==0){

                }else if(i==1){

                }
            }
        })
        .show();
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void videoPickGallery(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivity(Intent.createChooser(intent, "Select Videos"), video_pick_gallery);
    }

    private booleancheckCamera
}
