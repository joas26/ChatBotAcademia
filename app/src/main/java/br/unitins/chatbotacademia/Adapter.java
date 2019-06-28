package br.unitins.chatbotacademia;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter  extends RecyclerView.Adapter<ItemHolder>{
    View itemView;


    Context contexto = null;
    ArrayList<ItemTreino> lista = null;

    Adapter(Context contexto, ArrayList<ItemTreino> lista ){
        this.contexto =  contexto;
        this.lista = lista;
    }

    // each data item is just a string in this case

    @NonNull

    //METODO CHAMADO N VEZES PARA INFLAR O XML DA CELULA E RETORNAR UM OBJETO DE LAYOUT
    /* Método que deverá retornar layout criado pelo ViewHolder já inflado em uma view. */
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View celula = LayoutInflater.from(contexto).inflate(R.layout.item_recycler, parent, false);
        ItemHolder item = new ItemHolder(celula);
        return item;
    }

    /*
     * Método que recebe o ViewHolder e a posição da lista.
     * Aqui é recuperado o objeto da lista de Objetos pela posição e associado à ViewHolder.
     * É onde a mágica acontece!
     * */
    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        ItemTreino item = lista.get(position);

        holder.exercicio.setText(item.exercicio);
        holder.equipamento.setText(item.equipamento);
        holder.diaSemana.setText(item.diaSemana);
        holder.imagem.setImageBitmap(BitmapFactory.decodeResource(contexto.getResources(), item.urlImagem));

    }

    @Override
    public int getItemCount() {

        return (lista !=null) ?  lista.size() : 0;

    }
}
