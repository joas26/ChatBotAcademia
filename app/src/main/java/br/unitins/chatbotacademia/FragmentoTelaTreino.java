package br.unitins.chatbotacademia;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class FragmentoTelaTreino extends Fragment {

    View minhaView;
    ArrayList<ItemTreino> dataSource = null;
    Button Botaoatualizar;

    public  FragmentoTelaTreino(){

    }

    private RecyclerView recyclerView;
    private Adapter mAdapter;
    private Context mContext;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view  = inflater.inflate(R.layout.layout_treino, container,false);
        Botaoatualizar =(Button) view.findViewById(R.id.atualizar);
        dataSource = new ArrayList<>();

        //Runnable url;
        //new DownloadDados().execute();

            // Picasso.get().load(url).into(imagemAparelho);

        Botaoatualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                HttpConnections service  = new HttpConnections();
                try {
                    ItemTreino retorno =  service.execute().get();
                    //String dos = retorno.diaSemana;
                    String retorno1 = retorno.toString();
                    Log.i("", retorno1);
                    // dataSource.add(new ItemTreino( retorno.diaSemana,  , Equipamento,1));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

       LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);

        recyclerView = view.findViewById(R.id.recycler_view1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        Adapter adpt = new Adapter(getActivity(),dataSource);
        recyclerView.setAdapter(adpt);

        return view;
    }


}
