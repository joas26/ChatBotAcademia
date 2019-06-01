package br.unitins.chatbotacademia;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatbotFragment extends Fragment {


    public ChatbotFragment() {
        // Required empty public constructor
    }

    TextView mensagensTextView;
    EditText inputEditText;
    ImageButton botaoEnviar;
    FloatingActionButton btnScan;

    TextView txtResultNomeAparelho;
    TextView txtResultInstrucoes;
    ImageView imagemAparelho;

    Context context;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        final Activity activity = getActivity();

      // textview que vai exibir na tela as mensagens do usuario e watson/webservice
        mensagensTextView =(TextView) view.findViewById(R.id.textViewMensagens);

        // editext para receber as mensagens do usuario
        inputEditText = (EditText) view.findViewById(R.id.inputEditText);

        // botao para enviar as mensagens do usuario
        botaoEnviar = (ImageButton) view.findViewById(R.id.botaoEnviar);

        // botao para ler o qrcode
        btnScan = (FloatingActionButton) view.findViewById(R.id.btnScan);

        txtResultNomeAparelho = (TextView)  view.findViewById(R.id.NomeAparelho);
        txtResultInstrucoes = (TextView) view.findViewById(R.id.instrucoes);
        imagemAparelho = (ImageView) view.findViewById(R.id.imagemAparelho);

        getResponse("oi");


        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                String input = inputEditText.getText().toString();
                mensagensTextView.append(Html.fromHtml("<p><b>Você:</b> " + input + "</p>"));
                inputEditText.setText("");
                getResponse(input);

            }
        });


        final IntentIntegrator integrator =  new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Camera Scan");
        integrator.setCameraId(0); // camera traseira

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //IntentIntegrator integrator =  new IntentIntegrator(this).forSupportFragment(this);
                //integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                //integrator.setPrompt("Camera Scan");
                //integrator.setCameraId(0); // camera traseira
                integrator.initiateScan();

            }
        });



       return view;

        }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private void getResponse(String input) {

        String workspaceId = "53b76527-ad9e-4fa6-90f5-6fb46baa3617";
        String urlAssistant = "https://gateway.watsonplatform.net/assistant/api/v1/workspaces/"+workspaceId+"/message?version=2019-02-28";

        String authentication = "YXBpa2V5OmE4TENFWmtRZjJ2ckxsc2Fza1hReG9zODdtQldVUVdVVl80bVVLTnlIUGY3";

        //Criar a estrutura json de entrada do usuario
        JSONObject inputJsonObject = new JSONObject();
        try {
            inputJsonObject.put("text",input);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("input",inputJsonObject);
            jsonBody.put("input",inputJsonObject);
            jsonBody.put("input",inputJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(urlAssistant)
                .addHeaders("Content-Type","application/json")
                .addHeaders("Authorization","Basic "+authentication)
                .addJSONObjectBody(jsonBody)
                .setPriority(Priority.HIGH)
                .setTag(getString(R.string.app_name))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String outputAssistant = "";

                        //passar a resposta em json
                        try {
                            String outputJsonObject = response.getJSONObject("output").getJSONArray("text").getString(0);
                            String outputJsonObject1 = response.getJSONObject("output").getJSONArray("text").getString(1);
                            String outputJsonObject2 = response.getJSONObject("output").getJSONArray("text").getString(2);
                            mensagensTextView.append(Html.fromHtml("<p><b>BotBuilder:</b> " + outputJsonObject + "</p>"));
                            mensagensTextView.append(Html.fromHtml("<p><b>BotBuilder:</b> " + outputJsonObject1 + "</p>"));
                            mensagensTextView.append(Html.fromHtml("<p><b>BotBuilder:</b> " + outputJsonObject2 + "</p>"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getActivity(), "ERRO DE CONEXÃO", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    // função que retorna os dados do qrcode
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result!= null){

            if(result.getContents() != null ) {

                final Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    public void run() {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(result.getContents());
                            String url = null;

                            txtResultNomeAparelho.setText(Html.fromHtml("<p><b>BotBuilder:</b> "+ obj.getString("nome")+"</p>"));
                            //txtResultNomeAparelho.append(Html.fromHtml("<p><b>BotBuilder:</b> " + txtResultNomeAparelho.getText() + "</p>"));
                            url = obj.getString("url");

                            //carregar imagem no image view
                            Picasso.get().load(url).into(imagemAparelho);

                            txtResultInstrucoes.setText(Html.fromHtml("<p><b>BotBuilder:</b> "+ obj.getString("instrucoes")+"</p>"));
                           // txtResultInstrucoes.append(Html.fromHtml("<p><b>BotBuilder:</b> " + txtResultInstrucoes.getText() + "</p>"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 2000);


            }else{
                Toast.makeText(getActivity(),"Result not found",Toast.LENGTH_SHORT).show();

            }

        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }


    }






    }




