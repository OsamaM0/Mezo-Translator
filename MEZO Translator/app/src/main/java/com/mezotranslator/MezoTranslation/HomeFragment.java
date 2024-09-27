package com.mezotranslator.MezoTranslation;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class HomeFragment extends Fragment {
    private LinearLayout translate_layout, camera_layout, history_layout;
    private LinearLayout translate_btn, camera_btn, history_btn, chat_btn, phrases_btn;
    private NativeAd nativeAd;

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
        phrases_btn = view.findViewById(R.id.phrases_layout);


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


        phrases_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent object
                Intent intent = new Intent(getActivity(), PhrasesActivity.class);

                // Start the activity
                startActivity(intent);
            }
        });


        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        // Load the Native Ad
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(getActivity(), initializationStatus -> {
        });

        // Load the native ad
        AdLoader adLoader = new AdLoader.Builder(getActivity(), getString(R.string.native_add_unit_id))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // If already an ad is loaded, replace it
                        if (HomeFragment.this.nativeAd != null) {
                            HomeFragment.this.nativeAd.destroy();
                        }

                        // Assign the new ad
                        HomeFragment.this.nativeAd = nativeAd;

                        // Inflate the NativeAdView and populate it
                        NativeAdView adView = (NativeAdView) view.findViewById(R.id.native_ad_container);
                        populateNativeAdView(nativeAd, adView);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the error
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());

        // Return the inflated layout
        return view;
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets
        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);

        // Populate other components if available
        adView.setNativeAd(nativeAd);
    }

    @Override
    public void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}
