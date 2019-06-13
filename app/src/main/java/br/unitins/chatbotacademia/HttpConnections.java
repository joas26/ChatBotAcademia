package br.unitins.chatbotacademia;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import okhttp3.OkHttpClient;

public class HttpConnections extends AsyncTask<Void,Void,ItemTreino> {
    //método get


    protected ItemTreino doInBackground(Void... voids) {
        StringBuilder resposta = new StringBuilder();

        try {
            URL url  = new URL("Http://localhost:8080/WSRest/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept","application/json");
            connection.setConnectTimeout(5000);
            connection.connect();

          Scanner scanner  =  new Scanner(url.openStream());
        while(scanner.hasNext()){
            resposta.append(scanner.next());
        }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new Gson().fromJson(resposta.toString(),ItemTreino.class);
    }


}