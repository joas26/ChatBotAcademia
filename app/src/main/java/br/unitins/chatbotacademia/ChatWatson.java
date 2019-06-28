package br.unitins.chatbotacademia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class ChatWatson extends Fragment {

    private Context context;

    public ChatWatson() {
        // Required empty public constructor
    }

    FloatingActionButton btnScan;
    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageView ImagemEquipamento;
    private ImageButton btnSend;
    private ImageButton btnRecord;
    StreamPlayer streamPlayer = new StreamPlayer();
    private boolean initialRequest;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String TAG = "MainActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private boolean listening = false;
    private MicrophoneInputStream capture;
    private Context mContext;
    private MicrophoneHelper microphoneHelper;

    private Assistant watsonAssistant;
    private SessionResponse watsonAssistantSession;
    private SpeechToText speechService;
    private TextToSpeech textToSpeech;
    private AppCompatImageView loading;


    private void createServices() {

        // Autenticação com o watson usando as credencias do serviço do chatbot
        watsonAssistant = new Assistant("2018-11-08", new IamOptions.Builder()
                .apiKey(mContext.getString(R.string.assistant_apikey))
                .build());
        watsonAssistant.setEndPoint(mContext.getString(R.string.assistant_url));
    }


    private Integer idAluno;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // receber os parametros vindo da activity
        Bundle data = getArguments();
        idAluno = data.getInt("id");
        Log.i("IDALUNO", "" + idAluno);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chatwatson, container, false);

        mContext = getActivity();

        inputMessage = view.findViewById(R.id.message);
        ImagemEquipamento = view.findViewById(R.id.ImagemEquipamento);
        btnSend = view.findViewById(R.id.btn_send);
        btnRecord = view.findViewById(R.id.btn_record);
        loading = view.findViewById(R.id.loading);

        // botao para ler o qrcode
        btnScan = (FloatingActionButton) view.findViewById(R.id.btnScan);

        String customFont = "Montserrat-Regular.ttf";
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), customFont);
        //inputMessage.setTypeface(typeface);

        recyclerView = view.findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);
        microphoneHelper = new MicrophoneHelper(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Message audioMessage = (Message) messageArrayList.get(position);
                if (audioMessage != null && !audioMessage.getMessage().isEmpty()) {
                    //  new SayTask().execute(audioMessage.getMessage());
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    sendMessage();
                }
            }
        });

        createServices();
        sendMessage();

        final IntentIntegrator integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
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

    // Sending a message to Watson Assistant Service
    private void sendMessage() {

        final String inputmessage = this.inputMessage.getText().toString().trim();
        if (!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
        } else if (this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
            Toast.makeText(getActivity(), "Tap on the message for Voice", Toast.LENGTH_LONG).show();

        } else {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("50");
            this.initialRequest = false;
            Toast.makeText(getActivity(), "é a mensagem do qrcode", Toast.LENGTH_LONG).show();
        }

        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    if (watsonAssistantSession == null) {
                        ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(mContext.getString(R.string.assistant_id)).build());
                        watsonAssistantSession = call.execute().getResult();
                    }

                    MessageInput input = new MessageInput.Builder()
                            .text(inputmessage)
                            .build();
                    MessageOptions options = new MessageOptions.Builder()
                            .assistantId(mContext.getString(R.string.assistant_id))
                            .input(input)
                            .sessionId(watsonAssistantSession.getSessionId())
                            .build();
                    MessageResponse response = watsonAssistant.message(options).execute().getResult();
                    Log.i(TAG, "run: " + response);
                    final Message outMessage = new Message();


                    int intencaoSaudacao = 1;
                    int intencaoTreino = 1;
                    int intencaoFalaComInstrutor = 1;
                    int quemSouEu = 1;

                    if (response != null &&
                            response.getOutput() != null &&
                            !response.getOutput().getGeneric().isEmpty()) {

                        if ("text".equals(response.getOutput().getGeneric().get(0).getResponseType())) {
                            String intencao = response.getOutput().getIntents().toString();

                            String mensagem = response.getOutput().getGeneric().get(0).getText();
                            System.out.println("MENSAGEM " + mensagem);

                            if (mensagem.contains("Eu sou o BotBuilder") && (intencaoSaudacao == 1)) {
                                final Message mensagem1 = new Message();
                                final Message mensagem2 = new Message();
                                final Message mensagem3 = new Message();

                                mensagem1.setMessage(response.getOutput().getGeneric().get(0).getText());
                                mensagem2.setMessage(response.getOutput().getGeneric().get(1).getText());
                                messageArrayList.add(mensagem1);
                                messageArrayList.add(mensagem2);

                                intencaoSaudacao++;

                                Integer hoje = DiadaSemana();
                                Log.i("INTENÇÃO:", "MSG INICIAL DO CHATBOT");
                                // Treino("Segunda");


                            } else if (intencao.contains("saudação") && (intencaoSaudacao >1 )) {
                                String mensagem2 = "Olá novamente ;) o que vc deseja fazer?";
                                outMessage.setMessage(mensagem2);
                                messageArrayList.add(outMessage);
                                intencaoSaudacao++;
                                Log.i("INTENÇÃO:", "entrou na intenção SAUDAÇÃO " + intencaoSaudacao + " vezes ");
                            }

                            if (intencao.contains("treino") && (intencaoTreino == 1)) {

                                if(intencao.contains("fimdetreino")){
                                    outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                                    messageArrayList.add(outMessage);
                                    Log.i("INTENÇÃO:", "ENTROU NO FIMDETREINO");
                                }else{

                                    outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                                    messageArrayList.add(outMessage);
                                    Log.i("INTENÇÃO:", "entrou na intenção TREINO");
                                    Integer hoje = DiadaSemana();
                                    Treino(hoje);
                                }

                            } else if (intencao.contains("treino") && (intencaoTreino > 1)) {
                                outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                                messageArrayList.add(outMessage);
                                Log.i("INTENÇÃO:", "entrou na intenção TREINO" + intencaoTreino + " vezes");
                            }
                            if (intencao.contains("falarComInstrutor")) {
                                outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                                messageArrayList.add(outMessage);
                                Log.i("INTENÇÃO:", "entrou na intenção FALAR COM INSTRUTOR");
                            }

                            if (intencao.contains("General_Human_or_Bot")) {
                                outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                                messageArrayList.add(outMessage);
                                Log.i("INTENÇÃO:", "entrou na intenção quem é vc ");
                            }

                            if (intencao.contains("General_Negative_Feedback")) {
                                outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                                messageArrayList.add(outMessage);
                                Log.i("INTENÇÃO:", "entrou na intenção palavrao " + intencaoSaudacao + " vezes ");
                                String intencao1 = response.getOutput().getIntents().get(0).getIntent().toString();
                                System.out.println("INTENCAO1 " + intencao1);
                            }

                            if (mensagem.contains("não entendi")) {
                                outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                                messageArrayList.add(outMessage);

                                Log.i("INTENÇÃO:", "entrou na intenção NAO ENTENDI");
                            }

                            if (intencao.contains("General_Ending")) {
                                outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                                messageArrayList.add(outMessage);

                            }

                        }


                    }

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > 1) {
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    // metodo que retorna o dia da semana
    public Integer DiadaSemana() {

        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);

        Integer dia2;
        int dia = c.get(c.DAY_OF_WEEK);
        switch (dia) {
            case Calendar.SUNDAY:
                dia2 = 1;
                break;
            case Calendar.MONDAY:
                dia2 = 2;
                //   nome = "Segunda";
                break;
            case Calendar.TUESDAY:
                dia2 = 3;
                // nome = "Terça";
                break;
            case Calendar.WEDNESDAY:
                dia2 = 4;
                // nome = "Quarta";
                break;
            case Calendar.THURSDAY:
                dia2 = 5;
                // nome = "Quinta";
                break;
            case Calendar.FRIDAY:
                dia2 = 6;
                // nome = "Sexta";
                break;
            case Calendar.SATURDAY:
                dia2 = 7;
                //nome = "Sábado";
                break;
        }
        return dia;
    }


    // função que retorna os dados do qrcode
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            if (result.getContents() != null) {

                final Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    public void run() {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(result.getContents());
                            String url = null;
                            final Message outMessage1 = new Message();
                            outMessage1.setMessage(obj.getString("nome"));
                            messageArrayList.add(outMessage1);

                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    if (mAdapter.getItemCount() > 1) {
                                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                                    }

                                }
                            });

                            final Message outMessage2 = new Message();
                            outMessage2.setMessage(obj.getString("instrucoes"));
                            messageArrayList.add(outMessage2);

                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    if (mAdapter.getItemCount() > 1) {
                                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                                    }

                                }
                            });

                            final Message outMessage3 = new Message();
                            url = obj.getString("url");
                            //Picasso.get().load(url).into(ImagemEquipamento);
                            //outMessage2.setImagemEquipamento(ImagemEquipamento);
                            outMessage3.setMessage(obj.getString("url"));
                            messageArrayList.add(outMessage3);

                            //carregar imagem no image view


                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    if (mAdapter.getItemCount() > 1) {
                                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                                    }

                                }
                            });

                            //carregar imagem no image view
                            //  Picasso.get().load(url).into(imagemAparelho);

                            // txtResultInstrucoes.setText(Html.fromHtml("<p><b>BotBuilder:</b> "+ obj.getString("instrucoes")+"</p>"));
                            // txtResultInstrucoes.append(Html.fromHtml("<p><b>BotBuilder:</b> " + txtResultInstrucoes.getText() + "</p>"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 2000);


            } else {
                Toast.makeText(getActivity(), "Result not found", Toast.LENGTH_SHORT).show();

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

     // Check Internet Connection
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(getActivity(), " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void showError(final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void Treino(Integer dia) {
        try {

            String buscarExercicios = new BuscarExercicio(idAluno, 6).execute().get();
            String buscarEquipamento = new BuscarEquipamento(idAluno, 6).execute().get();
            // Log.i("RETONO", ""+resposta);

            JSONArray listaExercicios = new JSONArray(buscarExercicios);
            JSONArray listaEquipamentos = new JSONArray(buscarEquipamento);

            String percorrerExercicios;
            String percorrerEquipamento;

            // imprimir no LOG para controle
            Log.i("EXERCICIOS", "" + listaExercicios);
            Log.i("EQUIPAMENTOS", "" + listaEquipamentos);

            int i = 0, y = 0;

            String[] exercicio = new String[15];
            String[] equipamento = new String[15];

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

            // CASO NÃO TENHA NADA CADASTRADO NO DIA ATUAL
            if (buscarExercicios.equals("[]")) {

                final Message message = new Message();
                String orientacao = "Não foi cadastrado treino para este dia";
                message.setMessage(orientacao);
                messageArrayList.add(message);

                //SE TIVER TREINO CADASTRADO PRO DIA ATUAL
            } else {

                String orientacao = "";
                int cont = 0;
                while (cont < exercicio.length) {

                    if (cont == 0) {

                        if (exercicio[cont] == null || equipamento[cont] == null) {

                        } else {
                            orientacao = "Dirija-se ao equipamento " + equipamento[cont] + " e leia o qrcode do exercicio "
                                    + exercicio[cont] + " para receber as instruções do treino";
                            final Message message = new Message();
                            message.setMessage(orientacao);
                            messageArrayList.add(message);

                        }
                    }

                    if (cont >= 1) {

                        if (exercicio[cont] == null || equipamento[cont] == null) {

                        } else {

                            orientacao = "Depois dirija-se ao equipamento " + equipamento[cont] + " e leia o qrcode do exercicio "
                                    + exercicio[cont] + " para receber as instruções do treino";
                            final Message message = new Message();
                            message.setMessage(orientacao);
                            messageArrayList.add(message);

                        }

                    }
                    cont++;
                }


            }

        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

}




