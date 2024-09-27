package com.mezotranslator.MezoTranslation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class PhrasesActivity extends AppCompatActivity {

    private LinearLayout phrasesLayout;
    private TextView sourcelan,targetlan;
    private ImageView target_lang_flag, source_lang_flag;
    private LanguageSelector languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrases);


        //--------------------------------------------------------------------------------
        // ================================ ALL INTENTS =================================
        //--------------------------------------------------------------------------------
        Intent homeIntent = new Intent(PhrasesActivity.this, HomeActivity.class);
        Intent sourceLanguage = new Intent(PhrasesActivity.this,LanguagesPage.class);
        Intent targetLanguage = new Intent(PhrasesActivity.this,LanguagesPage.class);


        //--------------------------------------------------------------------------------
        // ============================= INITIALIZE VARIABLES ============================
        //--------------------------------------------------------------------------------

        phrasesLayout = findViewById(R.id.phrases_items_lst);
        //Getting source language from source language textView
        sourcelan = findViewById(R.id.sourcelanguage);
        source_lang_flag = findViewById(R.id.source_lang_flag);
        //Getting target language from target language textView
        targetlan = findViewById(R.id.targetlanguage);
        target_lang_flag = findViewById(R.id.target_lang_flag);
        //
        languages = new LanguageSelector();
        // Return to home button
        ImageView back_btn = findViewById(R.id.back_btn);

        String languageIntent = getIntent().getStringExtra("Language");
        String required = getIntent().getStringExtra("Required");
        String previous = getIntent().getStringExtra("previous");


        if(languageIntent != null){
            if (required.equals("source")){
                sourcelan.setText(languageIntent);
                targetlan.setText(previous);
            }
            else{
                targetlan.setText(languageIntent);
                sourcelan.setText(previous);
            }
        }
        else{
            targetlan.setText(languages.getTargetlan());
            sourcelan.setText(languages.getSourcelan());
        }

        int resID = getResources().getIdentifier("flag_"+new Dashboard().getCountryCode(sourcelan.getText().toString()), "drawable", getPackageName());
        source_lang_flag.setImageResource(resID);

        resID = getResources().getIdentifier("flag_"+new Dashboard().getCountryCode(targetlan.getText().toString()), "drawable", getPackageName());
        target_lang_flag.setImageResource(resID);

        String source = sourcelan.getText().toString();
        String target = targetlan.getText().toString();

        String sourceLanCode = languages.getlanguageCode(source);
        String targetLanCode = languages.getlanguageCode(target);

        // ---------------------------------------------------------------------------
        // ============================= ON CLICK LISTENERS ==========================
        // ---------------------------------------------------------------------------

        // ============================ SWAP LANGUAGE BUTTON =======================================
        //Implement swapping the source and target language
        ImageView swaplan = findViewById(R.id.changelanPos);
        swaplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String swap = sourcelan.getText().toString();
                sourcelan.setText(targetlan.getText().toString());
                targetlan.setText(swap);

                int resID = getResources().getIdentifier("flag_"+new Dashboard().getCountryCode(sourcelan.getText().toString()), "drawable", getPackageName());
                source_lang_flag.setImageResource(resID);

                resID = getResources().getIdentifier("flag_"+new Dashboard().getCountryCode(targetlan.getText().toString()), "drawable", getPackageName());
                target_lang_flag.setImageResource(resID);
            }
        });



        // ============================ SOURCE LANGUAGE BUTTON =======================================
        //Getting source language from user selection
        sourcelan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sourceLanguage.putExtra("Language","source");
                sourceLanguage.putExtra("Previous",targetlan.getText().toString());
                sourceLanguage.putExtra("userTxt","Hi");
                sourceLanguage.putExtra("Intent","phrases");
                startActivity(sourceLanguage);
            }
        });

        // ============================ TARGET LANGUAGE BUTTON =======================================
        //Getting target language from user selection
        targetlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetLanguage.putExtra("Language","target");
                targetLanguage.putExtra("Previous",sourcelan.getText().toString());
                targetLanguage.putExtra("userTxt","Hi");
                targetLanguage.putExtra("Intent","phrases");
                startActivity(targetLanguage);
            }
        });

        // ============================ HOME BUTTON =======================================
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
            }
        });

        // ---------------------------------------------------------------------------
        // ============================= SHOW JSON FILE =============================
        // ---------------------------------------------------------------------------

        try {
            // Load JSON data from assets
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            Iterator<String> keys = jsonObject.keys();

            LinearLayout rowLayout = null; // Initialize row layout
            int items = 0;

            // Itrat through Cards
            while (keys.hasNext()) {
                String key = keys.next();
                JSONArray data  = jsonObject.getJSONArray(key);
                JSONArray src_data = null;
                JSONArray trgt_data = null;
                int end = 0;
                // Choose data based on language
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    if (obj.getString("langCode").equals(sourceLanCode)) {
                        src_data = obj.getJSONArray("data");
                        end += 1;
                    }
                    else if (obj.getString("langCode").equals(targetLanCode)) {
                        trgt_data = obj.getJSONArray("data");
                        end += 1;
                    }
                    if (end == 2)
                        break;
                }
                // There is no Translation for this specific language
                if (end != 2) {
                    Toast.makeText(getApplicationContext(), R.string.no_translatoin_for_this_language, Toast.LENGTH_SHORT).show();
                    break;
                }
                // Create a card for each key
                LinearLayout cardView  = createCard(key, src_data, trgt_data);
                if ( items % 2 == 0) { // Check if even number of children
                    // Create a new row layout every two cards
                    rowLayout = new LinearLayout(this);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    phrasesLayout.addView(rowLayout);
                    rowLayout.addView(cardView);
                } else {
                    // Add the card to the current row layout
                    rowLayout.addView(cardView);
                }
                items++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    // Method to load JSON from assets folder
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("data-base-phrasebook.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // Method to create card for each key

    private LinearLayout createCard(String key, JSONArray source_data, JSONArray target_data) {
        // Get the screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // Calculate half of the screen width
        int desiredWidth = (screenWidth / 2) - 75;

        // Set the layout parameters for your view
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(desiredWidth,  ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 16, 16, 16);

        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setLayoutParams(params);
        cardLayout.setOrientation(LinearLayout.VERTICAL); // Changed orientation to vertical
        cardLayout.setClickable(true);
        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show list of words when card is clicked
                showWordsList(key, source_data, target_data);
            }
        });

        TextView textView = new TextView(this);
        ImageView imgView = new ImageView(this);
        String key_word = key.substring(7);
        int resID = getResources().getIdentifier("ic_"+key_word.toLowerCase(), "drawable", getPackageName());
        imgView.setImageResource(resID);
        textView.setText(key_word);
        imgView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        imgView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                400));

        cardLayout.setBackgroundResource(R.drawable.textbox_border);
        System.out.println(cardLayout.getTextAlignment());
        cardLayout.addView(imgView);
        cardLayout.addView(textView);
        cardLayout.setPadding(10,70,10,10);

        return cardLayout;
    }


    // Method to show list of words
    private void showWordsList(String key, JSONArray source_data, JSONArray target_data) {
        ArrayList<String> srcWordsList = new ArrayList<>();
        ArrayList<String> trgtWordsList = new ArrayList<>();
        for (int i = 0; i < source_data.length(); i++) {
            try {
                srcWordsList.add(source_data.getString(i));
                trgtWordsList.add(target_data.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Create a custom adapter to display words with appropriate functionality for source and target
        CustomAdapter adapter = new CustomAdapter(this, R.layout.phrases_item_list, srcWordsList, trgtWordsList);

        // Create a ListView to display words
        ListView listView = new ListView(this);
        listView.setAdapter(adapter);

        // Display the ListView in a dialog
        new androidx.appcompat.app.AlertDialog.Builder(PhrasesActivity.this)
                .setView(listView)
                .setCancelable(true)
                .show();
    }

    // Custom adapter to handle source and target words separately
    class CustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> srcWordsList;
        private ArrayList<String> trgtWordsList;
        private LayoutInflater inflater; // Added for layout inflation

        public CustomAdapter(Context context, int resource, ArrayList<String> srcWordsList, ArrayList<String> trgtWordsList) {
            super(context, resource);
            this.srcWordsList = srcWordsList;
            this.trgtWordsList = trgtWordsList;
            inflater = LayoutInflater.from(context); // Initialize inflater
        }

        @Override
        public int getCount() {
            return srcWordsList.size(); // Assuming both lists have the same size
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView; // Removed super.getView()

            // Inflate the layout
            itemView = inflater.inflate(R.layout.phrases_item_list, parent, false);

            // Get the source and target words at this position
            String srcWord = srcWordsList.get(position);
            String trgtWord = trgtWordsList.get(position);

            // Update source word text view
            TextView srcTextView = itemView.findViewById(R.id.source_txt); // Assuming this ID is for source text
            srcTextView.setText(srcWord);

            // Update target word text view and enable copy/voice for target
            TextView trgtTextView = itemView.findViewById(R.id.target_txt); // Assuming this ID is for target text
            trgtTextView.setText(trgtWord);

            // ... (Copy and TTS logic using trgtWord)
            // Get the copy icon ImageView
            ImageView copyIcon = itemView.findViewById(R.id.copy_btn);

            // Set click listener for the copy icon
            copyIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Copy the word to clipboard
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Word", trgtWord);
                    clipboard.setPrimaryClip(clip);

                    // Inform the user that the word has been copied
                    Toast.makeText(getApplicationContext(), R.string.clipboard_copy, Toast.LENGTH_SHORT).show();
                }
            });

            // Get the text-to-speech button
            ImageView ttsButton = itemView.findViewById(R.id.tts_btn);
            TextListner listner = new TextListner(PhrasesActivity.this);
            // Initialize TextToSpeech
            ttsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.speak(trgtWord);
                }
            });

            return itemView;
        }
    }



}
