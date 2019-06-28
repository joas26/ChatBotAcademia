package br.unitins.chatbotacademia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class TelaLogin extends AppCompatActivity {


   private Integer idAluno = 0;
    private Integer idPessoa = 0;
   private String nome="";
   private String email="";
   private  String login = "";
   private  String senha = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);


        Button botaoEntrar = (Button) findViewById(R.id.btnEntrar);
        final EditText edLogin = (EditText)  findViewById(R.id.edtUsuario);
        final EditText edSenha = (EditText) findViewById(R.id.edtSenha);


        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String autenticacao= new Autenticacao(edLogin.getText().toString(), edSenha.getText().toString()).execute().get();
                    // Log.i("RETONO", ""+resposta);

                    JSONArray resposta = new JSONArray(autenticacao);
                    JSONArray percorrer = new JSONArray();


                    Log.i("Reposta",""+resposta.toString());


                    int i = 0, y = 0;
                   // String percorrer = "";
                    for (i = 0; i < resposta.length(); i++) {
                        percorrer =resposta.getJSONArray(i);
                        for(y=0; y < percorrer.length();y++) {
                            idAluno = percorrer.getInt(0);
                            login = percorrer.getString(1);
                            senha = percorrer.getString(2);
                            nome = percorrer.getString(3);
                            email = percorrer.getString(4);
                            idPessoa = percorrer.getInt(5);
                            Log.i("ID", ""+idAluno);
                            Log.i("LOGIN", ""+login);
                            Log.i("SENHA", ""+senha);
                            Log.i("NOME", ""+nome);
                            Log.i("EMAIL", ""+email);

                        }

                    }


                    if (resposta.toString().equals("[]")){
                        Toast toast =  Toast.makeText(getApplicationContext(),"Verifique os dados digitados",Toast.LENGTH_LONG);
                        toast.show();
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putInt("idAluno",idAluno);
                        bundle.putString("login",login);
                        bundle.putString("senha",senha);
                        bundle.putString("nome",nome);
                        bundle.putString("email",email);
                        bundle.putInt("id",idPessoa);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                        } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
     }
    });

}
}

