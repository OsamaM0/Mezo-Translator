package com.example.translationapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
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
    private ImageView imageView7,history,shutdown,showCamera;
    // Declare the RelativeLayout

    private Model databaseCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //This line hide default header

        setContentView(R.layout.activity_dashboard);

        //SQLite Database connectivity #########

         databaseCon = new Model(this);

        //##############

        //All Intents
        Intent shutdownIntent = new Intent(Dashboard.this,MainActivity.class);
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

        //Getting target language from target language textView
        targetlan = findViewById(R.id.targetlanguage);



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


        //Implement swapping the source and target language
        ImageView swaplan = findViewById(R.id.changelanPos);

        swaplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String swap = sourcelan.getText().toString();
                sourcelan.setText(targetlan.getText().toString());
                targetlan.setText(swap);
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
        shutdown = findViewById(R.id.shutdownImg);
        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(shutdownIntent);
            }
        });

        //Recent History feature
        //User navigate to history screen
        history = findViewById(R.id.imageView8);
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

}

