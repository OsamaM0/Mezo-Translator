package com.mezotranslator.MezoTranslation;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] images = {R.drawable.bc_camera, R.drawable.bc_voice, R.drawable.bc_chat};
    private ImageSliderAdapter adapter;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);

        adapter = new ImageSliderAdapter(this, images);
        viewPager.setAdapter(adapter);

        // Add dots dynamically
        addDots();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                updateDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Button to switch to MainActivity
        Button switchButton = findViewById(R.id.continueBtn);
        switchButton.setOnClickListener(view -> {
            if (currentPosition == images.length - 1) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            } else {
                viewPager.setCurrentItem(currentPosition + 1);
            }
        });
    }

    private void addDots() {
        for (int i = 0; i < images.length; i++) {
            ImageView dot = new ImageView(this);
            int dotSize = 40;//getResources().getDimensionPixelSize(R.dimen.dot_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);

            // Create circular shape for the dot
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setSize(dotSize, dotSize);
            drawable.setColor(getResources().getColor(R.color.light_gray)); // Set inactive color
            dot.setBackground(drawable);

            dotsLayout.addView(dot);
        }

        // Highlight the first dot
        if (dotsLayout.getChildCount() > 0) {
            updateDots(0); // Highlight the first dot initially
        }
    }

    private void updateDots(int position) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            View dotView = dotsLayout.getChildAt(i);
            GradientDrawable drawable = (GradientDrawable) dotView.getBackground();

            if (i == position) {
                drawable.setColor(getResources().getColor(R.color.light_blue_purple)); // Set active color
            } else {
                drawable.setColor(getResources().getColor(R.color.light_gray)); // Set inactive color
            }
        }
    }
}
