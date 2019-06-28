package br.unitins.chatbotacademia;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class FragmentoTelaTreino extends Fragment {

    View minhaView;
    ArrayList<ItemTreino> dataSource = null;
    private Context context;

    String[] exercicio = new String[15];
    String[] equipamento = new String[15];
    public  FragmentoTelaTreino(){

    }

    private RecyclerView recyclerView = null;
    private Adapter mAdapter;
    private Context mContext;
    FloatingActionButton botaoAtualiza;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view  = inflater.inflate(R.layout.layout_treino, container,false);
        //Botaoatualizar =(Button) view.findViewById(R.id.atualizar);
        botaoAtualiza =  (FloatingActionButton) view.findViewById(R.id.botaoAtualizar);
        dataSource = new ArrayList<ItemTreino>();

        //Runnable url;
        //new DownloadDados().execute();

            //Picasso.get().load(url).into(imagemAparelho);

        botaoAtualiza.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view){
                ArrayList<Treino> lista = null;


                try {
                    String buscarExercicios = new BuscarExercicio(6, 6).execute().get();
                    String buscarEquipamento = new BuscarEquipamento(6, 6).execute().get();
                    // Log.i("RETONO", ""+resposta);

                    JSONArray listaExercicios = new JSONArray(buscarExercicios);
                    JSONArray listaEquipamentos = new JSONArray(buscarEquipamento);

                    String percorrerExercicios;
                    String percorrerEquipamento;

                    // imprimir no LOG para controle
                    Log.i("EXERCICIOS", "" + listaExercicios);
                    Log.i("EQUIPAMENTOS", "" + listaEquipamentos);

                    int i = 0, y = 0;

                    // percorre o JSON RETORNADO E ATRIBUI AO ARRAY DE EXERCICIOS
                    for (i = 0; i < listaExercicios.length(); i++) {
                        percorrerExercicios = listaExercicios.getString(i);
                        exercicio[i] = percorrerExercicios;
                        Log.i("EXERCICIOsss", exercicio[i]);
                    }

                    // percorre o JSON RETORNADO E ATRIBUI AO ARRAY DE EQUIPAMENTO
                    for (i = 0; i < listaEquipamentos.length(); i++) {
                        percorrerEquipamento = listaEquipamentos.getString(i);
                        equipamento[i] = percorrerEquipamento;
                        Log.i("Equipamentos", exercicio[i]);
                    }


                }  catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            }
        });


        dataSource.add(new ItemTreino( "SEGUNDA","ROSCA DIRETA"  ,"PEK DEK", R.drawable.equipamento ));
        dataSource.add(new ItemTreino( "SEGUNDA","ROSCA ALTERNADA"  ,"PEK DEK", R.drawable.equipamento ));
        dataSource.add(new ItemTreino( "SEGUNDA","TRICEPS FRANCES"  ,"MAQUINA DE TRICEOS", R.drawable.triceps ));
        dataSource.add(new ItemTreino( "SEGUNDA","ABDUCAO"  ,"MAQUINA PERNA", R.drawable.aducao ));
        dataSource.add(new ItemTreino( "SEGUNDA","PERNA SUPERIOR"  ,"MAQUINA PERNA", R.drawable.aducao));
        recyclerView = view.findViewById(R.id.recycler_view1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        Adapter adpt = new Adapter(getActivity(),dataSource);
        recyclerView.setAdapter(adpt);



        return view;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }


}
