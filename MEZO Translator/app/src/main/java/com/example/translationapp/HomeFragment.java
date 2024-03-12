package com.example.translationapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class HomeFragment extends Fragment {
    private LinearLayout translate_layout, camera_layout, history_layout;
    private LinearLayout translate_btn, camera_btn, history_btn, chat_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the button by its ID
        translate_btn = view.findViewById(R.id.translate_layout);
        camera_btn = view.findViewById(R.id.camera_layout);
        history_btn = view.findViewById(R.id.history_layout);
        chat_btn = view.findViewById(R.id.chat_layout);

        // Set an OnClickListener for the button
        translate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent object
                Intent intent = new Intent(getActivity(), Dashboard.class);

                // Start the activity
                startActivity(intent);
            }
        });


        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent object
                Intent intent = new Intent(getActivity(), Picture_Text_Activity.class);

                // Start the activity
                startActivity(intent);
            }
        });

        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent object
                Intent intent = new Intent(getActivity(), RecentHistory_Activity.class);

                // Start the activity
                startActivity(intent);
            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent object
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                // Start the activity
                startActivity(intent);
            }
        });

        // Return the inflated layout
        return view;
    }
}