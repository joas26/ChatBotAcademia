package br.unitins.chatbotacademia;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Timer;

public class ExemploZxingIntegration extends AppCompatActivity {

    Button btnScan;
    TextView txtResultName;
    TextView txtResultIdade;
    ImageView imagemAparelho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exemplo_zxing_integration);

        btnScan = (Button) findViewById(R.id.btnScan);

        txtResultName = (TextView) findViewById(R.id.txtResultNome);
        txtResultIdade =  (TextView) findViewById(R.id.txtResultIdade);
        imagemAparelho = (ImageView) findViewById(R.id.imagemAparelho);


        final Activity activity = this;

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Camera Scan");
                integrator.setCameraId(0); // camera traseira
                integrator.initiateScan();

            }
        });

    }




        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                                txtResultName.setText(obj.getString("nome"));
                                url = obj.getString("url");
                                //carregar imagem no image view
                                Picasso.get().load(url).into(imagemAparelho);
                                txtResultIdade.setText(obj.getString("idade"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 2000);


                }else{
                    Toast.makeText(this,"Result not found",Toast.LENGTH_SHORT).show();

                }

            }else{
                super.onActivityResult(requestCode, resultCode, data);
            }


    }



}
