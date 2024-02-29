package com.example.translationapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

 public class TextListner {

    private Context context;
    private TextToSpeech textToSpeech;

     public TextListner(Context context) {
        this.context = context;
        initializeTextToSpeech();
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("Language Issue", "onInit: Language not found");
                    }
                } else {
                    Log.e("Initializing", "onInit: Initializing failed" );

                }
            }
        });
    }

    public void speak(String listenText) {
        textToSpeech.speak(listenText, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
