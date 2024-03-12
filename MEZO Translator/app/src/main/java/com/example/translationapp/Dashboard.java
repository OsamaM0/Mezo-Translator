package com.example.translationapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {
    private EditText userText, translatedText;
    private View translateBtn;
    private String result;
    private TextView sourcelan,targetlan;
    private TranslationServiceManager serviceManager;
    private  Clipboard clipboard;
    private LanguageSelector languages;
    protected static final int RESULT_SPEECH = 1;
    private ImageView imageView7,history,goback,showCamera, target_lang_flag, source_lang_flag;
    // Declare the RelativeLayout

    private Model databaseCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_dashboard);

        //SQLite Database connectivity #########

         databaseCon = new Model(this);

        //##############

        //All Intents
        Intent shutdownIntent = new Intent(Dashboard.this,MainActivity.class);
        Intent homeIntent = new Intent(Dashboard.this, HomeActivity.class);
        Intent sourceLanguage = new Intent(Dashboard.this,LanguagesPage.class);
        Intent targetLanguage = new Intent(Dashboard.this,LanguagesPage.class);
        Intent historyIntent = new Intent(Dashboard.this,RecentHistory_Activity.class);


        //Initialize variables
        languages = new LanguageSelector();

        // Getting User Input from user text box
        userText = findViewById(R.id.translatedtextbox);

        //Getting Translate Button through which
        //user can translate text
        translateBtn = findViewById(R.id.translatetextbtn);

        //Text View where translated text display
        translatedText = findViewById(R.id.translatedTextView);

        //Getting source language from source language textView
        sourcelan = findViewById(R.id.sourcelanguage);
        source_lang_flag = findViewById(R.id.source_lang_flag);


        //Getting target language from target language textView
        targetlan = findViewById(R.id.targetlanguage);
        target_lang_flag = findViewById(R.id.target_lang_flag);



        String languageIntent = getIntent().getStringExtra("Language");
        String required = getIntent().getStringExtra("Required");
        String previous = getIntent().getStringExtra("previous");
        String previousInput = getIntent().getStringExtra("userText");

        //Getting capture text
        String text = getIntent().getStringExtra("Capture");
        if(text != " "){
            userText.setText(text);
            text = "";
        }

        if(languageIntent != null){
            if (required.equals("source")){
                sourcelan.setText(languageIntent);
                targetlan.setText(previous);
                userText.setText(previousInput); //Setting previous input text when selection languages

            }
            else{
                targetlan.setText(languageIntent);
                sourcelan.setText(previous);
                userText.setText(previousInput); //Setting previous input text when selection languages
            }
        }
        else{
            targetlan.setText(languages.getTargetlan());
            sourcelan.setText(languages.getSourcelan());
        }

        int resID = getResources().getIdentifier("flag_"+getCountryCode(sourcelan.getText().toString()), "drawable", getPackageName());
        source_lang_flag.setImageResource(resID);

        resID = getResources().getIdentifier("flag_"+getCountryCode(targetlan.getText().toString()), "drawable", getPackageName());
        target_lang_flag.setImageResource(resID);




        //Implement swapping the source and target language
        ImageView swaplan = findViewById(R.id.changelanPos);

        swaplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String swap = sourcelan.getText().toString();
                sourcelan.setText(targetlan.getText().toString());
                targetlan.setText(swap);

                int resID = getResources().getIdentifier("flag_"+getCountryCode(sourcelan.getText().toString()), "drawable", getPackageName());
                source_lang_flag.setImageResource(resID);

                resID = getResources().getIdentifier("flag_"+getCountryCode(targetlan.getText().toString()), "drawable", getPackageName());
                target_lang_flag.setImageResource(resID);
            }
        });

        //Text translation logic placed here
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String source = sourcelan.getText().toString();
                String target = targetlan.getText().toString();

                String sourceLanCode = languages.getlanguageCode(source);
                String targetLanCode = languages.getlanguageCode(target);

                createThread(sourceLanCode,targetLanCode,userText.getText().toString());
                translatedText.setText(result);
                //Translated text save into database
                //for later use

                if(result != null){
                    databaseCon.Insert(userText.getText().toString(), translatedText.getText().toString());
                }
            }
        });

        //Getting source language from user selection
        sourcelan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceLanguage.putExtra("Language","source");
                sourceLanguage.putExtra("Previous",targetlan.getText().toString());
                sourceLanguage.putExtra("userTxt",userText.getText().toString());
                startActivity(sourceLanguage);
            }
        });

        //Getting target language from user selection
        targetlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetLanguage.putExtra("Language","target");
                targetLanguage.putExtra("Previous",sourcelan.getText().toString());
                targetLanguage.putExtra("userTxt",userText.getText().toString());
                startActivity(targetLanguage);
            }
        });

        clipboard = new Clipboard(Dashboard.this);

        //Implement CopyText Logic for userText
        ImageView inputtxtCopybtn = findViewById(R.id.usertxtCopy);
        inputtxtCopybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = userText.getText().toString(); // Move this line inside onClick

                if (!inputText.trim().isEmpty()) {
                    clipboard.SaveToClipboard(inputText);
                } else {
                    Toast.makeText(Dashboard.this, "Text is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Implement CopyText Logic for Translated Text
        ImageView translatedtxtCopybtn = findViewById(R.id.translatedtxtCopy);

        translatedtxtCopybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String translatetxt = translatedText.getText().toString();
                if (!translatetxt.trim().isEmpty()) {
                    clipboard.SaveToClipboard(translatetxt);
                } else {
                    Toast.makeText(Dashboard.this, "Text is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Implement Listen text logic for user text
        ImageView listenUserText = findViewById(R.id.usertextListen);
        TextListner listner = new TextListner(this);

        listenUserText.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String text = userText.getText().toString();
                    listner.speak(text);
             }
         });

        //Implement Listen text logic for translated text
        ImageView listenTransText = findViewById(R.id.translatedttxtListen);

        listenTransText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = translatedText.getText().toString();
                listner.speak(text);
            }
        });

        //Shut Down feature
        //User navigate to startup screen
        goback = findViewById(R.id.back_btn);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(homeIntent);
            }
        });

        //Recent History feature
        //User navigate to history screen
        history = findViewById(R.id.history_btn);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(historyIntent);
            }
        });


        showCamera=findViewById(R.id.showCamera);
        showCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera=new Intent(Dashboard.this,Picture_Text_Activity.class);
                startActivity(camera);

            }
        });



        //Speech to text recognition
        imageView7 = findViewById(R.id.imageView7);
        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sourceLanCode = languages.getlanguageCode(sourcelan.getText().toString());
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, sourceLanCode );

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }
            }
        });


        // Set an OnGlobalLayoutListener to detect changes in the layout
    }

   public void createThread(String source,String target, String text){

       serviceManager = new TranslationServiceManager();

        new Thread(() -> {
            try {
                result = serviceManager.translateText(source, target, text);

                //Convert String to Json Object
                try {
                    String jsonString = result; // Your JSON string

                    JSONObject jsonObject = new JSONObject(jsonString);

                    //Get Nested Objects
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    result = data.getString("translatedText");


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Handle any exceptions
            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    StringBuilder concatenatedText = new StringBuilder();
                    for (String s : text) {
                        concatenatedText.append(s).append(" ");
                    }

                    // Set the concatenated text to userText
                    userText.setText(concatenatedText.toString().trim());

                }
                break;
        }

    }


    public String getCountryCode(String languageName) {

        String langauge_country_code []= {"af-ZA",
                "am-ET",
                "ar-EG",
                "ar-AE",
                "ar-BH",
                "ar-DZ",
                "ar-IQ",
                "ar-JO",
                "ar-KW",
                "ar-LB",
                "ar-LY",
                "ar-MA",
                "arn-CL",
                "ar-OM",
                "ar-QA",
                "ar-SA",
                "ar-SY",
                "ar-TN",
                "ar-YE",
                "as-IN",
                "az-AZ",
                "az-Cyrl-AZ",
                "az-Latn-AZ",
                "ba-RU",
                "be-BY",
                "bg-BG",
                "bn-BD",
                "bn-IN",
                "bo-CN",
                "br-FR",
                "bs-BA",
                "bs-Cyrl-BA",
                "bs-Latn-BA",
                "ca-ES",
                "co-FR",
                "cs-CZ",
                "cy-GB",
                "de-DE",
                "da-DK",
                "de-AT",
                "de-CH",
                "de-LI",
                "de-LU",
                "dsb-DE",
                "dv-MV",
                "el-GR",
                "en-UK",
                "en-US",
                "en-AU",
                "en-BZ",
                "en-CA",
                "en-GB",
                "en-IE",
                "en-IN",
                "en-JM",
                "en-MY",
                "en-NZ",
                "en-PH",
                "en-SG",
                "en-TT",
                "en-ZA",
                "en-ZW",
                "es-ES",
                "es-AR",
                "es-BO",
                "es-CL",
                "es-CO",
                "es-CR",
                "es-DO",
                "es-EC",
                "es-GT",
                "es-HN",
                "es-MX",
                "es-NI",
                "es-PA",
                "es-PE",
                "es-PR",
                "es-PY",
                "es-SV",
                "es-US",
                "es-UY",
                "es-VE",
                "et-EE",
                "eu-ES",
                "fa-IR",
                "fi-FI",
                "fil-PH",
                "fo-FO",
                "fr-FR",
                "fr-BE",
                "fr-CA",
                "fr-CH",
                "fr-LU",
                "fr-MC",
                "fy-NL",
                "ga-IE",
                "gd-GB",
                "gl-ES",
                "gsw-FR",
                "gu-IN",
                "ha-NG",
                "he-IL",
                "hi-IN",
                "hr-HR",
                "hr-BA",
                "hsb-DE",
                "hu-HU",
                "hy-AM",
                "id-ID",
                "ig-NG",
                "ii-CN",
                "is-IS",
                "it-CH",
                "it-IT",
                "iu-Cans-CA",
                "iu-Latn-CA",
                "ja-JP",
                "ka-GE",
                "kk-KZ",
                "kl-GL",
                "km-KH",
                "kn-IN",
                "kok-IN",
                "ko-KR",
                "ky-KG",
                "lb-LU",
                "lo-LA",
                "lt-LT",
                "lv-LV",
                "mi-NZ",
                "mk-MK",
                "ml-IN",
                "mn-MN",
                "mn-CN",
                "moh-CA",
                "mr-IN",
                "ms-BN",
                "ms-MY",
                "mt-MT",
                "nb-NO",
                "ne-NP",
                "nl-BE",
                "nl-NL",
                "nn-NO",
                "nso-ZA",
                "oc-FR",
                "or-IN",
                "pa-IN",
                "pl-PL",
                "prs-AF",
                "ps-AF",
                "pt-BR",
                "pt-PT",
                "qut-GT",
                "quz-BO",
                "quz-EC",
                "quz-PE",
                "rm-CH",
                "ro-RO",
                "ru-RU",
                "rw-RW",
                "sah-RU",
                "sa-IN",
                "se-FI",
                "se-NO",
                "se-SE",
                "si-LK",
                "sk-SK",
                "sl-SI",
                "sma-NO",
                "sma-SE",
                "smj-NO",
                "smj-SE",
                "smn-FI",
                "sms-FI",
                "sq-AL",
                "sr-Cyrl-BA",
                "sr-Cyrl-CS",
                "sr-Cyrl-ME",
                "sr-Cyrl-RS",
                "sr-Latn-BA",
                "sr-Latn-CS",
                "sr-Latn-ME",
                "sr-Latn-RS",
                "sv-FI",
                "sv-SE",
                "sw-KE",
                "syr-SY",
                "ta-IN",
                "te-IN",
                "tg-Cyrl-TJ",
                "th-TH",
                "tk-TM",
                "tn-ZA",
                "tr-TR",
                "tt-RU",
                "tzm-Latn-DZ",
                "ug-CN",
                "uk-UA",
                "ur-PK",
                "uz-UZ",
                "uz-Cyrl-UZ",
                "uz-Latn-UZ",
                "vi-VN",
                "wo-SN",
                "xh-ZA",
                "yo-NG",
                "zh-CN",
                "zh-HK",
                "zh-MO",
                "zh-SG",
                "zh-TW",
                "zu-ZA"};
        for (int i = 0; i < langauge_country_code.length; i++) {
            if (getLanguageCode(languageName.toLowerCase()).equals(langauge_country_code[i].split("-")[0])) {
                return langauge_country_code[i].split("-")[1].toLowerCase();
            }
        }
        return languageName;
    }
    // Method to get the ISO 639-1 language code from the language name using built-in functionality
    public static String getLanguageCode(String languageName) {
        Locale[] availableLocales = Locale.getAvailableLocales();
        for (Locale locale : availableLocales) {
            if (locale.getDisplayLanguage().equalsIgnoreCase(languageName)) {
                System.out.println(locale.getLanguage());
                return locale.getLanguage();
            }
        }
        return "Unknown";
    }
}




