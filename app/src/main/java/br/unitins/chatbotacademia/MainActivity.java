package br.unitins.chatbotacademia;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        private Integer idaluno;
        private String nomeAluno;
        private String emailAluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Intent it  = getIntent();
        idaluno= it.getIntExtra("idPessoa",0);
        nomeAluno  = it.getStringExtra("nome");
        emailAluno = it.getStringExtra("email");

        Log.i("nomeAluno ",nomeAluno);
        Log.i("EmailAluno ",emailAluno);





        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //FloatingActionButton fab = findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View hView =  navigationView.getHeaderView(0);
        TextView nomeUsuario = (TextView) hView.findViewById(R.id.textNomeUsuario);
        TextView EmailUsuario = (TextView) hView.findViewById(R.id.textEmailUsuario);
        nomeUsuario.setText(nomeAluno);
        EmailUsuario.setText(emailAluno);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       // FragmentManager fragmentManager = getFragmentManager();
        if (id == R.id.nav_tela_inicio) {

           FragmentoTelaInicio fragment = new FragmentoTelaInicio();
            FragmentTransaction fragmentTrasaction =
                    getSupportFragmentManager().beginTransaction();


            // Handle the camera action
        }else if (id == R.id.nav_tela_treino) {

            FragmentoTelaTreino fragment = new FragmentoTelaTreino();
            FragmentTransaction fragmentTrasaction =
                    getSupportFragmentManager().beginTransaction();

            fragmentTrasaction.replace(R.id.fragment_container,fragment);
            fragmentTrasaction.commit();

        }
        else if (id == R.id.nav_tela_chatbotWatson) {




            ChatWatson fragment = new ChatWatson();
            Bundle data = new Bundle();
            data.putInt("id",idaluno);
            fragment.setArguments(data);
            FragmentTransaction fragmentTrasaction =
                    getSupportFragmentManager().beginTransaction();

            fragmentTrasaction.replace(R.id.fragment_container,fragment);
            fragmentTrasaction.commit();

        }

         else if (id == R.id.nav_tela_compartilhar) {




        }
         else if (id == R.id.nav_tela_sobre) {

            FragmentoTelaSobre fragment = new FragmentoTelaSobre();
            FragmentTransaction fragmentTrasaction =
                    getSupportFragmentManager().beginTransaction();

            fragmentTrasaction.replace(R.id.fragment_container,fragment);
            fragmentTrasaction.commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
