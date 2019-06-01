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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ChatWatson extends Fragment {

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


  private void createServices() {

    // Autenticação com o watson usando as credencias do serviço do chatbot
    watsonAssistant = new Assistant("2018-11-08", new IamOptions.Builder()
            .apiKey(mContext.getString(R.string.assistant_apikey))
            .build());
    watsonAssistant.setEndPoint(mContext.getString(R.string.assistant_url));

    // Autenticação para usar os servicos de texto para fala
    textToSpeech = new TextToSpeech();
    textToSpeech.setIamCredentials(new IamOptions.Builder()
            .apiKey(mContext.getString(R.string.TTS_apikey))
            .build());
    textToSpeech.setEndPoint(mContext.getString(R.string.TTS_url));

    // Autenticação para usar os servicos de texto fala para texto
    speechService = new SpeechToText();
    speechService.setIamCredentials(new IamOptions.Builder()
            .apiKey(mContext.getString(R.string.STT_apikey))
            .build());
    speechService.setEndPoint(mContext.getString(R.string.STT_url));
  }

  @Override
  public View  onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.chatwatson, container, false);

    mContext = getActivity();

    inputMessage = view.findViewById(R.id.message);
    ImagemEquipamento = view.findViewById(R.id.ImagemEquipamento);
    btnSend = view.findViewById(R.id.btn_send);
    btnRecord = view.findViewById(R.id.btn_record);

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


    int permission = ContextCompat.checkSelfPermission(getActivity(),
      Manifest.permission.RECORD_AUDIO);

    if (permission != PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission to record denied");
      makeRequest();
    } else {
      Log.i(TAG, "Permission to record was already granted");
    }


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
        recordMessage();

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

    btnRecord.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        recordMessage();
      }
    });

    createServices();
    sendMessage();

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

  // Speech-to-Text Record Audio permission
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case REQUEST_RECORD_AUDIO_PERMISSION:
        permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        break;
      case RECORD_REQUEST_CODE: {

        if (grantResults.length == 0
          || grantResults[0] !=
          PackageManager.PERMISSION_GRANTED) {

          Log.i(TAG, "Permission has been denied by user");
        } else {
          Log.i(TAG, "Permission has been granted by user");
        }
        return;
      }

      case MicrophoneHelper.REQUEST_PERMISSION: {
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(getActivity(), "Permission to record audio denied", Toast.LENGTH_SHORT).show();
        }
      }
    }
    // if (!permissionToRecordAccepted ) finish();

  }

  protected void makeRequest() {
    ActivityCompat.requestPermissions(getActivity(),
      new String[]{Manifest.permission.RECORD_AUDIO},
      MicrophoneHelper.REQUEST_PERMISSION);
  }

  // Sending a message to Watson Assistant Service
  private void sendMessage() {

    final String inputmessage = this.inputMessage.getText().toString().trim();
    if (!this.initialRequest) {
      Message inputMessage = new Message();
      inputMessage.setMessage(inputmessage);
      inputMessage.setId("1");
      messageArrayList.add(inputMessage);
    } else if(this.initialRequest) {
      Message inputMessage = new Message();
      inputMessage.setMessage(inputmessage);
      inputMessage.setId("100");
      this.initialRequest = false;
      Toast.makeText(getActivity(), "Tap on the message for Voice", Toast.LENGTH_LONG).show();

    }else{
      Message inputMessage = new Message();
      inputMessage.setMessage(inputmessage);
      inputMessage.setId("50");
      this.initialRequest = false;
      Toast.makeText(getActivity(), "é a mensagem do qrcode", Toast.LENGTH_LONG).show();
    }

    this.inputMessage.setText("");
    mAdapter.notifyDataSetChanged();

    Thread thread = new Thread(new Runnable() {
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

          //int qtdMensagem = 0;
            String intencao = "";
          if (response != null &&
                  response.getOutput() != null &&
                  !response.getOutput().getGeneric().isEmpty()) {

            if ("text".equals(response.getOutput().getGeneric().get(0).getResponseType())) {
              outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
             intencao =  response.getOutput().getIntents().toString().concat("asd");
              System.out.println("INTENÇÃO:"+intencao);
              messageArrayList.add(outMessage);
              //outMessage.setId("" + qtdMensagem);
              //System.out.println("QTDMENSAGENS1:" + qtdMensagem);
              //qtdMensagem++;


              // não ta funcionando ainda tem que
              // verificar se a intenção de saudação foi chamada
              if(response.getOutput().getIntents().equals("saudação")){
                    outMessage.setMessage(response.getOutput().getGeneric().get(0).getText());
                    messageArrayList.add(outMessage);
                    //saudacao=1;


                }

            }

          }



          int tamanho = response.getOutput().getGeneric().size();
          System.out.println("QTDMENSAGENS:"+tamanho);




            // speak the message
           // new SayTask().execute(outMessage.getMessage());

            getActivity().runOnUiThread(new Runnable() {
              public void run() {
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1) {
                  recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                }

              }
            });
          }
         catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    thread.start();

  }


  // função que retorna os dados do qrcode
  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
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


      }else{
        Toast.makeText(getActivity(),"Result not found",Toast.LENGTH_SHORT).show();

      }

    }else{
      super.onActivityResult(requestCode, resultCode, data);
    }


  }





  /*private class SayTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
      streamPlayer.playStream(textToSpeech.synthesize(new SynthesizeOptions.Builder()
        .text(params[0])
        .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
        .accept(SynthesizeOptions.Accept.AUDIO_WAV)
        .build()).execute().getResult());
      return "Did synthesize";
    }
  }

  */

  //Record a message via Watson Speech to Text
  private void recordMessage() {
    if (listening != true) {
      capture = microphoneHelper.getInputStream(true);
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            speechService.recognizeUsingWebSocket(getRecognizeOptions(capture), new MicrophoneRecognizeDelegate());
          } catch (Exception e) {
            showError(e);
          }
        }
      }).start();
      listening = true;
      Toast.makeText(getActivity(), "Listening....Click to Stop", Toast.LENGTH_LONG).show();

    } else {
      try {
        microphoneHelper.closeInputStream();
        listening = false;
        Toast.makeText(getActivity(), "Stopped Listening....Click to Start", Toast.LENGTH_LONG).show();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * Check Internet Connection
   *
   * @return
   */
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

  //Private Methods - Speech to Text
  private RecognizeOptions getRecognizeOptions(InputStream audio) {
    return new RecognizeOptions.Builder()
      .audio(audio)
      .contentType(ContentType.OPUS.toString())
      .model("en-US_BroadbandModel")
      .interimResults(true)
      .inactivityTimeout(2000)
      .build();
  }

  //Watson Speech to Text Methods.
  private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {
    @Override
    public void onTranscription(SpeechRecognitionResults speechResults) {
      if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
        String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
        showMicText(text);
      }
    }

    @Override
    public void onError(Exception e) {
      showError(e);
      enableMicButton();
    }

    @Override
    public void onDisconnected() {
      enableMicButton();
    }

  }

  private void showMicText(final String text) {
      getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        inputMessage.setText(text);
      }
    });
  }

  private void enableMicButton() {
      getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        btnRecord.setEnabled(true);
      }
    });
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


}



