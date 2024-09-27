package com.mezotranslator.MezoTranslation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class RecentHistory_Activity extends AppCompatActivity {

    private ImageView goback, textImg;
    private Model database;
    private ConstraintLayout showEmpty,showData;
    FloatingActionButton clearbtn;
    ListView datalistView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_recent_history);

        //All Intents
        Intent homeIntent = new Intent(RecentHistory_Activity.this, HomeActivity.class);


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
        goback = findViewById(R.id.back_btn);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
            }
        });
    }
}