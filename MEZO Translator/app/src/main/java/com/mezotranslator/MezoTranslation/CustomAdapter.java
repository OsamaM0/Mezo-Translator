package com.mezotranslator.MezoTranslation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class CustomAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList<String> userTextList;
    private final ArrayList<String> translatedTextList;

    public CustomAdapter(Context context, ArrayList<String> userTextList, ArrayList<String> translatedTextList) {
        super(context, R.layout.list_item_layout, userTextList);
        this.context = context;
        this.userTextList = userTextList;
        this.translatedTextList = translatedTextList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_layout, parent, false);

        // Assuming you have two TextViews in your list item layout
        TextView userTextView = rowView.findViewById(R.id.userTextView);
        TextView translatedTextView = rowView.findViewById(R.id.translatedTextView);

        userTextView.setText(userTextList.get(position));
        translatedTextView.setText(translatedTextList.get(position));

        return rowView;
    }
}
