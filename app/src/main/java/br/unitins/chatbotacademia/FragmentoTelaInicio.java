package br.unitins.chatbotacademia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentoTelaInicio  extends Fragment {

    View minhaView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        minhaView = inflater.inflate(R.layout.layout_inicio, container,false);
        return minhaView;
    }

}
