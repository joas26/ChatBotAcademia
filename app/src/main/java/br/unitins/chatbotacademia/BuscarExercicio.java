package br.unitins.chatbotacademia;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class BuscarExercicio extends AsyncTask<Void, Void, String> {

        private final Integer id;
        private  Integer dia;

        public BuscarExercicio(Integer id, Integer dia) {
            this.id = id;
            this.dia = dia;
        }
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder resposta = new StringBuilder();
            //String resposta = null;
            try {
                URL url  = new URL("http://192.168.0.110:8080/WSRest/api/treino/listarExercicio/6/6");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.connect();

                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    resposta.append(scanner.next());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resposta.toString();
        }
}
