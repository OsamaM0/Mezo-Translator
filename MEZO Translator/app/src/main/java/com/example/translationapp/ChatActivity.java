package com.example.translationapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageView sendBtn, micBtn, target_lang_flag, source_lang_flag, back_btn;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private String result;
    private TextView sourcelan,targetlan;
    private TranslationServiceManager serviceManager;
    private  Clipboard clipboard;
    private LanguageSelector languages;
    protected static final int RESULT_SPEECH = 1;
    // Declare the RelativeLayout

    private Model databaseCon;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //--------------------------------------------------------------------------------
        // ================================ ALL INTENTS =================================
        //--------------------------------------------------------------------------------
        Intent homeIntent = new Intent(ChatActivity.this, HomeActivity.class);
        Intent sourceLanguage = new Intent(ChatActivity.this,LanguagesPage.class);
        Intent targetLanguage = new Intent(ChatActivity.this,LanguagesPage.class);


        //--------------------------------------------------------------------------------
        // ============================= INITIALIZE VARIABLES ============================
        //--------------------------------------------------------------------------------

        messageList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        languages = new LanguageSelector();
        // Getting User Input from user chat box
        messageEditText = findViewById(R.id.message_etext);
        // Send text in chat to be translated
        sendBtn = findViewById(R.id.send_btn);
        // Mic button to convert speech to text
        micBtn = findViewById(R.id.mic_btn);
        //Getting source language from source language textView
        sourcelan = findViewById(R.id.sourcelanguage);
        source_lang_flag = findViewById(R.id.source_lang_flag);
        //Getting target language from target language textView
        targetlan = findViewById(R.id.targetlanguage);
        target_lang_flag = findViewById(R.id.target_lang_flag);
        // Return to home button
        back_btn = findViewById(R.id.back_btn);

        String languageIntent = getIntent().getStringExtra("Language");
        String required = getIntent().getStringExtra("Required");
        String previous = getIntent().getStringExtra("previous");
        String previousInput = getIntent().getStringExtra("messageEditText");

        //Getting capture text
        String text = getIntent().getStringExtra("Capture");
        if(text != " "){
            messageEditText.setText(text);
            text = "";
        }

        if(languageIntent != null){
            if (required.equals("source")){
                sourcelan.setText(languageIntent);
                targetlan.setText(previous);
                messageEditText.setText(previousInput); //Setting previous input text when selection languages

            }
            else{
                targetlan.setText(languageIntent);
                sourcelan.setText(previous);
                messageEditText.setText(previousInput); //Setting previous input text when selection languages
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


        // ---------------------------------------------------------------------------
        // ============================= ON CLICK LISTENERS ==========================
        // ---------------------------------------------------------------------------

        // ================================== SEND BUTTON ============================
        //setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);


        // ============================ TRANSLATE TEXT =======================================
        //Text translation logic placed here
        sendBtn.setOnClickListener((v)->{
                String userText = messageEditText.getText().toString().trim();
                addToChat(userText,Message.SENT_BY_ME);
                messageEditText.setText("");
                result = null;

                String source = sourcelan.getText().toString();
                String target = targetlan.getText().toString();

                String sourceLanCode = languages.getlanguageCode(source);
                String targetLanCode = languages.getlanguageCode(target);

                createThread(sourceLanCode,targetLanCode,userText);

            });

        // ============================ MIC BUTTON =======================================
        //Speech to text recognition
        micBtn.setOnClickListener((v)->{
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

        });


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
                String userText = messageEditText.getText().toString().trim();
                sourceLanguage.putExtra("Language","source");
                sourceLanguage.putExtra("Previous",targetlan.getText().toString());
                sourceLanguage.putExtra("userTxt",userText);
                sourceLanguage.putExtra("Intent","chat");
                startActivity(sourceLanguage);
            }
        });

        // ============================ TARGET LANGUAGE BUTTON =======================================
        //Getting target language from user selection
        targetlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userText = messageEditText.getText().toString().trim();
                targetLanguage.putExtra("Language","target");
                targetLanguage.putExtra("Previous",sourcelan.getText().toString());
                targetLanguage.putExtra("userTxt",userText);
                targetLanguage.putExtra("Intent","chat");
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

    }




    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

            }
        });


    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    public void createThread(String source,String target, String text){
        messageList.add(new Message("Typing...  ",Message.SENT_BY_BOT));

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

                    addResponse(result);



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
                    messageEditText.setText(concatenatedText.toString().trim());

                }
                break;
        }

    }


}








//    void callAPI(String question){
//        //okhttp
//        messageList.add(new Message("Typing...  ",Message.SENT_BY_BOT));
//
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("model", "gpt-3.5-turbo");
//
//            JSONArray messageArr = new JSONArray();
//            JSONObject obj = new JSONObject();
//            obj.put("role", "user");
//            obj.put("content", question);
//            messageArr.put(obj);
//
//            jsonBody.put("messages",messageArr);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
//        Request request = new Request.Builder()
//                .url("\n" +
//                        "https://api.openai.com/v1/chat/completions")
//                .header("Authorization", "Bearer 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")
//                .post(body)
//                .build();
//        System.out.println(request.toString());
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                addResponse("Failed to load response due to " +e.getMessage());
//
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if(response.isSuccessful()){
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(response.body().string());
//                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
//                        String result = jsonArray.getJSONObject(0)
//                                .getJSONObject("message")
//                                .getString("content");
//                        addResponse(result.trim());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }else{
//                    addResponse("Failed to load response due to " +response.body().toString());
//                }
//
//            }
//        });
//
//    }