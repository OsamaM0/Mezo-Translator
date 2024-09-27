package com.mezotranslator.MezoTranslation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean isNightModeChanged = false;
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
        }

        // Create a new ad view.
        AdView adView = (AdView)findViewById(R.id.adView);
//        adView.setAdUnitId(getAdmobKey());
//        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);




    }


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_premium:
                    startActivity(new Intent(HomeActivity.this , PaymentActivity.class));
                    break;
                case R.id.nav_translate:
                    startActivity(new Intent(HomeActivity.this , Dashboard.class));
                    break;
                case R.id.nav_camera:
                    startActivity(new Intent(HomeActivity.this , Picture_Text_Activity.class));
                    break;
                case R.id.nav_history:
                    startActivity(new Intent(HomeActivity.this , RecentHistory_Activity.class));
                    break;

                case R.id.nav_mode:
                    NavigationMenuItemView navigationView = findViewById(R.id.nav_mode);
                    navigationView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isNightModeChanged) return; // Prevent multiple executions before restart

                            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                            switch (currentNightMode) {
                                case Configuration.UI_MODE_NIGHT_NO:
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                    break;
                                case Configuration.UI_MODE_NIGHT_YES:
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    break;
                            }
                            boolean isNightModeChanged = true; // Set flag to true to block further clicks
                            recreate(); // Restart the activity to apply the change
                        }
                    });
                    break;

                case R.id.nav_lang:
                    NavigationMenuItemView nav_lang = findViewById(R.id.nav_lang);
                    nav_lang.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String currentLang = getResources().getConfiguration().locale.getLanguage();
                            Locale newLocale;

                            if (currentLang.equals("en")) {
                                newLocale = new Locale("ar");
                            } else {
                                newLocale = new Locale("en");
                            }

                            // Save the selected language to SharedPreferences or similar for persistence
//                            saveLanguagePreference(newLocale.getLanguage());

                            // Update the configuration with the new locale
                            updateLocale(newLocale);

                            // Restart the activity to apply the language changes
                            restartActivity();
                        }
                    });
                    break;

                case R.id.nav_share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareBody = "MEZO APP";
                    String shareSub = "Share MEZO App with your friends";
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                    break;

                case R.id.nav_about:
                    startActivity(new Intent(HomeActivity.this , PolicyPage.class));
                    break;
                case R.id.nav_logout:
                    onStop();
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

    private void updateLocale(Locale newLocale) {
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(newLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

//    private void saveLanguagePreference(String language) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        preferences.edit().putString("language", language).apply();
//    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }



}