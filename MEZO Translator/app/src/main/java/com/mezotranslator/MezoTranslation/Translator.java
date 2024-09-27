package com.mezotranslator.MezoTranslation;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class TranslationServiceManager {

    private static final String API_URL = "https://text-translator2.p.rapidapi.com/translate";

    private static final String API_KEY = "7177bd9bb5msh6b4e4a59265c338p1330d0jsn96dae73c9d76";

    public String translateText(String sourceLanguage, String targetLanguage, String text) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(
                    mediaType,
                    "source_language=" + sourceLanguage + "&target_language=" + targetLanguage + "&text=" + text
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("x-rapidapi-key", API_KEY)
                    .addHeader("x-rapidapi-host", "text-translator2.p.rapidapi.com")
                    .build();

                    Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                // Handle the error
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


