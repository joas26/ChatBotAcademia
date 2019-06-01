package br.unitins.chatbotacademia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentoTelaInicio  extends Fragment {

    View minhaView;

    Button botao;

    public FragmentoTelaInicio(){


    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.layout_inicio, container,false);


        //implementar o fragment da tela inicio aqui


        return view;


    }

}
