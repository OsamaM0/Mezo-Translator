package com.example.translationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class PolicyPage extends AppCompatActivity {

    private Button openSplashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_policy_page);

        //Get SplashScreen navigation button
         openSplashScreen = findViewById(R.id.continueBtn);

        openSplashScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //These piece of code for navigate to SplashScreen
                    Intent intent = new Intent(PolicyPage.this, MainActivity.class);
                    startActivity(intent);

                }catch (Exception e){

                    //Handle Error
                    Toast.makeText(PolicyPage.this, "Unable to redirect Splash Screen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}