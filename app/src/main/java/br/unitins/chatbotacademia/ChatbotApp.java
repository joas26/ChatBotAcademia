package br.unitins.chatbotacademia;


import android.app.Application;

import com.androidnetworking.AndroidNetworking;


public class ChatbotApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
