package com.example.translationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    LinearLayout translate_layout, camera_layout, history_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_nav, R.string.close_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_translate);
        }




//        // ____________  OPEN TRANSLATOR ACTIVITY ____________
//        translate_layout = findViewById(R.id.translate_layout);
//        Intent translatorIntent = new Intent(HomeActivity.this, Dashboard.class);
//
//        translate_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(translatorIntent);
//            }
//        });
//
//        // ____________  OPEN CAMERA ACTIVITY ____________
//        camera_layout = findViewById(R.id.camera_layout);
//        Intent cameraIntent = new Intent(HomeActivity.this , Picture_Text_Activity.class);
//
//        camera_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(cameraIntent);
//            }
//        });
//
//        // ____________  OPEN HISTORY ACTIVITY ____________
//        history_layout =   findViewById(R.id.history_layout);
//        Intent historyIntent = new Intent(HomeActivity.this, RecentHistory_Activity.class);
//
//        history_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(historyIntent);
//            }
//        });



    }


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {



            switch (item.getItemId()) {
                case R.id.nav_translate:
                    startActivity(new Intent(HomeActivity.this , Dashboard.class));
                    break;
                case R.id.nav_camera:
                    startActivity(new Intent(HomeActivity.this , Picture_Text_Activity.class));
                    break;
                case R.id.nav_history:
                    startActivity(new Intent(HomeActivity.this , RecentHistory_Activity.class));
                    break;
                case R.id.nav_settings:
//                    startActivity(new Intent(HomeActivity.this , RecentHistory_Activity.class));
                    break;
                case R.id.nav_share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareBody = "Your body here";
                    String shareSub = "Your subject here";
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                    break;

            }
            return true;
        }



        @Override
        public void onBackPressed() {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

}