package com.example.translationapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class LanguageSelector {

    String[] languages = {
            "Afrikaans", "Albanian", "Amharic", "Arabic", "Armenian", "Azerbaijani",
            "Basque", "Belarusian", "Bengali", "Bosnian", "Bulgarian", "Catalan",
            "Cebuano", "Chichewa", "Chinese (Simplified)", "Chinese (Traditional)",
            "Corsican", "Croatian", "Czech", "Danish", "Dutch",
            "English", "Esperanto", "Estonian", "Filipino", "Finnish", "French",
            "Frisian", "Galician", "Georgian", "German", "Greek", "Gujarati",
            "Haitian Creole", "Hausa", "Hawaiian", "Hebrew", "Hindi", "Hmong",
            "Hungarian", "Icelandic", "Igbo", "Indonesian", "Irish", "Italian",
            "Japanese", "Javanese", "Kannada", "Kazakh", "Khmer", "Kinyarwanda",
            "Korean", "Kurdish (Kurmanji)", "Kyrgyz", "Lao", "Latin", "Latvian",
            "Lithuanian", "Luxembourgish", "Macedonian", "Malagasy", "Malay",
            "Malayalam", "Maltese", "Maori", "Marathi", "Mongolian", "Myanmar (Burmese)",
            "Nepali", "Norwegian", "Odia (Oriya)", "Pashto", "Persian", "Polish",
            "Portuguese", "Punjabi", "Romanian", "Russian", "Samoan", "Scots Gaelic",
            "Serbian", "Sesotho", "Shona", "Sindhi", "Sinhala", "Slovak", "Slovenian",
            "Somali", "Spanish", "Sundanese", "Swahili", "Swedish", "Tajik", "Tamil",
            "Tatar", "Telugu", "Thai", "Turkish", "Turkmen", "Ukrainian", "Urdu",
            "Uyghur", "Uzbek", "Vietnamese", "Welsh", "Xhosa", "Yiddish", "Yoruba",
            "Zulu", "Hebrew", "Chinese (Simplified)"};

    private String [] langCode  =  {"af", "sq", "am", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca",
            "ceb", "ny", "zh-CN", "zh-TW", "co", "hr", "cs", "da", "nl",
            "en", "eo", "et", "tl", "fi", "fr",
            "fy", "gl", "ka", "de", "el", "gu",
            "ht", "ha", "haw", "iw", "hi", "hmn",
            "hu", "is", "ig", "id", "ga", "it",
            "ja", "jw", "kn", "kk", "km", "rw",
            "ko", "ku", "ky", "lo", "la", "lv",
            "lt", "lb", "mk", "mg", "ms", "ml",
            "mt", "mi", "mr", "mn", "my", "ne",
            "no", "or", "ps", "fa", "pl", "pt",
            "pa", "ro", "ru", "sm", "gd", "sr",
            "st", "sn", "sd", "si", "sk", "sl",
            "so", "es", "su", "sw", "sv", "tg",
            "ta", "tt", "te", "th", "tr", "tk",
            "uk", "ur", "ug", "uz", "vi", "cy",
            "xh", "yi", "yo", "zu", "he", "zh"};

    private  String  sourcelan;
    private  String  targetlan;

    private HashMap<String,String> languageCodeMap;

    LanguageSelector(){
        sourcelan = "English";
        targetlan = "Korean";
        languageCodeMap = new HashMap<>();
        AddLanguages();
    }

    public  String getSourcelan() {
        return sourcelan;
    }

    public  String getTargetlan() {
        return targetlan;
    }

    public void setLanguage(String language, String code){
        languageCodeMap.put(language,code);
    }


    public String getlanguageCode(String language){
        String languagecode = " ";

        for (String lang :languageCodeMap.keySet()
             ) {
                    if(lang.equals(language)){
                        languagecode = languageCodeMap.get(lang);
                    }
        }

        return  languagecode;
    }

    public ArrayList<String> listAllLanguages() {
        ArrayList<String> languages = new ArrayList<>();
        for (String language : languageCodeMap.keySet()) {
            languages.add(language);
        }
        Collections.sort(languages);
        return languages;
    }


    private void AddLanguages(){
       // Arrays.sort(languages);
       // Arrays.sort(langCode);

        for (int i = 0; i < languages.length; i++) {
            setLanguage(languages[i],langCode[i]);
        }
    }
}
