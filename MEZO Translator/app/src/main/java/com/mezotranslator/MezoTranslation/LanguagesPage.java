package com.mezotranslator.MezoTranslation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanguagesPage extends AppCompatActivity {
    private LanguageSelector lanlistview;
    private SearchView searchText;
    private  ArrayAdapter<String> adapter;
    private  ArrayList<String> languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Removing default top header of application
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_languages_page);

        Intent dashboardIntent = new Intent(LanguagesPage.this,Dashboard.class);
        Intent chatIntent = new Intent(LanguagesPage.this,ChatActivity.class);
        Intent phrasesIntent = new Intent(LanguagesPage.this,PhrasesActivity.class);


        ListView langList = findViewById(R.id.list1);

        searchText = findViewById(R.id.search_bar);

        String required = getIntent().getStringExtra("Language");
        String previously = getIntent().getStringExtra("Previous");
        String usertxt = getIntent().getStringExtra("userTxt");
        String intent = getIntent().getStringExtra("Intent");

        try {
            lanlistview = new LanguageSelector();
            if (lanlistview != null) {
                // Assuming lanlistview is an instance of some class that has a method listAllLanguages()
                languages = lanlistview.listAllLanguages();

                // Assuming languages is a List<String>
                Collections.sort(languages);

                // Copy the original list to avoid modifying it directly
                List<String> list = new ArrayList<>(languages);

                // Assuming previously is a String representing the language to be removed
                list.remove(previously);

                // Now, list contains the modified list with 'previously' removed
                adapter = new ArrayAdapter<>(LanguagesPage.this, android.R.layout.simple_dropdown_item_1line, list);
                langList.setAdapter(adapter);
            }

            else{
                Log.e("ListView Null Error", "onCreate: List view is null");
            }

        }catch(Exception e){
            Log.e("ListView Error", "onCreate: List view items exception");
        }



        langList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (intent.equals("chat")) {
                    if(required.equals("source")){
                        chatIntent.putExtra("Required","source");
                        chatIntent.putExtra("Language",parent.getItemAtPosition(position).toString());
                        chatIntent.putExtra("previous",previously);
                    }
                    else{
                        chatIntent.putExtra("Language",parent.getItemAtPosition(position).toString());
                        chatIntent.putExtra("Required","target");
                        chatIntent.putExtra("previous",previously);
                    }
                    chatIntent.putExtra("userText",usertxt);
                    startActivity(chatIntent);
                }
                else if (intent.equals("phrases")) {
                    if(required.equals("source")){
                        phrasesIntent.putExtra("Required","source");
                        phrasesIntent.putExtra("Language",parent.getItemAtPosition(position).toString());
                        phrasesIntent.putExtra("previous",previously);
                    }
                    else{
                        phrasesIntent.putExtra("Language",parent.getItemAtPosition(position).toString());
                        phrasesIntent.putExtra("Required","target");
                        phrasesIntent.putExtra("previous",previously);
                    }
                    phrasesIntent.putExtra("userText",usertxt);
                    startActivity(phrasesIntent);
                }
                else if (intent.equals("dashboard")){
                    if (required.equals("source")) {
                        dashboardIntent.putExtra("Required", "source");
                        dashboardIntent.putExtra("Language", parent.getItemAtPosition(position).toString());
                        dashboardIntent.putExtra("previous", previously);
                    } else {
                        dashboardIntent.putExtra("Language", parent.getItemAtPosition(position).toString());
                        dashboardIntent.putExtra("Required", "target");
                        dashboardIntent.putExtra("previous", previously);
                    }
                    dashboardIntent.putExtra("userText",usertxt);
                    startActivity(dashboardIntent);
                }
            }
        });


        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


    }
}