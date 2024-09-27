package com.mezotranslator.MezoTranslation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class Clipboard {
    private ClipboardManager clipboardManager;
    private Context context;
    Clipboard(Context context){
        this.context = context;
    }

    public void SaveToClipboard(String text){
        try {
            clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("User Text", text);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            //hande exception
            Toast.makeText(context, "Text not copied", Toast.LENGTH_SHORT).show();
        }
    }

}
