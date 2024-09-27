package com.mezotranslator.MezoTranslation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity1 extends AppCompatActivity {

    private TextView viewPolicyPage;
    private View proceedbtn;
    private CheckBox IsAgree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide(); //This line hide default header


        setContentView(R.layout.activity_main);




        Intent dashboardIntent = new Intent(MainActivity1.this, HomeActivity.class);
        Intent policyIntent = new Intent(MainActivity1.this, PolicyPage.class);

        //Get policyPage navigation link
        viewPolicyPage = findViewById(R.id.readPolicyLink);

        viewPolicyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //These piece of code for navigate to Policy Page
                     startActivity(policyIntent);

                }catch (Exception e){

                    //Handle Error
                    Toast.makeText(MainActivity1.this, "Unable to redirect Policy Page", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Get button through which user
        // proceed_to_dashboard page
        proceedbtn = findViewById(R.id.continueBtn);

        //Get agree box through user agree our policy
        IsAgree = findViewById(R.id.policyCheckBox);

        proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(IsAgree.isChecked()){
                        //These piece of code for navigate to Dashboard
                        startActivity(dashboardIntent);
                    }
                    else{
                        Toast.makeText(MainActivity1.this, "Agree policy agreement", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){

                    //Handle Error
                    Toast.makeText(MainActivity1.this, "Unable to redirect Dashboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}