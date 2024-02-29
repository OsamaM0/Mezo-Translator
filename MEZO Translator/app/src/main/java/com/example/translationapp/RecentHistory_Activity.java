package com.example.translationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecentHistory_Activity extends AppCompatActivity {

    private ImageView shutdown, textImg;
    private Model database;
    private ConstraintLayout showEmpty,showData;
    Button clearbtn;
    ListView datalistView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //This line hide default header

        setContentView(R.layout.activity_recent_history);

        //All Intents
        Intent shutdownIntent = new Intent(RecentHistory_Activity.this,MainActivity.class);
        Intent dashboardIntent = new Intent(RecentHistory_Activity.this,Dashboard.class);

        shutdown = findViewById(R.id.shutdownImg);
        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(shutdownIntent);
            }
        });

        textImg = findViewById(R.id.imageView5);
        textImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(dashboardIntent);
            }
        });

        //Database related implementation
         database  = new Model(this);

        showEmpty = findViewById(R.id.emtyHistory);
        showData = findViewById(R.id.historyLayout);

        //If there is no recent translation data the empty list show
        if(!database.checkRecent()){
            showEmpty.setVisibility(View.VISIBLE);
            showData.setVisibility(View.GONE);
        }

        //If there is recent any translation data, it will show in list view
        else {
            showEmpty.setVisibility(View.GONE);

            datalistView = findViewById(R.id.translationlist);

            ArrayList<String> userTextList = database.getUserTextList();
            ArrayList<String> translatedTextList = database.getTranslatedTextList();

            CustomAdapter adapter = new CustomAdapter(RecentHistory_Activity.this, userTextList, translatedTextList);
            datalistView.setAdapter(adapter);

            showData.setVisibility(View.VISIBLE);
        }


        clearbtn = findViewById(R.id.clearbtn);
        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.clearAll();
                showData.setVisibility(View.GONE);
                showEmpty.setVisibility(View.VISIBLE);

            }
        });

    }
}