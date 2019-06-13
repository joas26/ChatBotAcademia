package br.unitins.chatbotacademia;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by labmacmini01 on 04/04/18.
 */
class ItemHolder extends RecyclerView.ViewHolder{

    ImageView imagem = null;
    TextView exercicio = null;
    TextView equipamento = null;
    TextView diaSemana = null;


    ItemHolder(View view){
        super(view);
        imagem = (ImageView)view.findViewById(R.id.imageView);
        exercicio = (TextView)view.findViewById(R.id.exercicio);
        equipamento = (TextView) view.findViewById(R.id.equipamento);
        diaSemana = (TextView) view.findViewById(R.id.diasemana);
    }
}

public class ItemTreino {
    String diaSemana;
    String exercicio;
    String equipamento;
    int urlImagem;

    ItemTreino( String diaSemana,String exercicio, String equipamento,
                int urlImagem){

        diaSemana = diaSemana;
        exercicio = exercicio;
        urlImagem = urlImagem;
        equipamento = equipamento;

    }
}
