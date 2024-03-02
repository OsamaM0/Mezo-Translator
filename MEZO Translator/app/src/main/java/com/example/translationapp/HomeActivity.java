package com.example.translationapp;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //This line hide default header

        setContentView(R.layout.activity_home);

        // ____________  OPEN TRANSLATOR ACTIVITY ____________
        linearLayout = findViewById(R.id.translate_layout);
        Intent translatorIntent = new Intent(HomeActivity.this,Dashboard.class);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(translatorIntent);
            }
        });

        // ____________  OPEN CAMERA ACTIVITY ____________
        linearLayout = findViewById(R.id.camera_layout);
        Intent cameraIntent = new Intent(HomeActivity.this,Picture_Text_Activity.class);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(cameraIntent);
            }
        });

        // ____________  OPEN HISTORY ACTIVITY ____________
        linearLayout = findViewById(R.id.history_layout);
        Intent historyIntent = new Intent(HomeActivity.this,RecentHistory_Activity.class);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(historyIntent);
            }
        });




    }
}