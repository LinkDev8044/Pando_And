package com.parse.starter.VistaClientes;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.parse.starter.Inicio.inicio_Pando_Activity;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.ListaComercios.ListaComerciosFragment;
import com.parse.starter.VistaClientes.PerfilCliente.PerfilClienteFragment;

public class ClienteActivity extends AppCompatActivity {

    int recuento;

    BottomNavigationView bottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            recuento = getSupportFragmentManager().getBackStackEntryCount();

            if (item.getItemId() == R.id.nav_lista_comercios){

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListaComerciosFragment()).commit();

            } else {

                if (item.getItemId() == R.id.nav_perfil){

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PerfilClienteFragment()).commit();

                }
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        getSupportActionBar().setTitle("Pando");

        if (ParseUser.getCurrentUser() == null){

            startActivity(new Intent(getApplicationContext(), inicio_Pando_Activity.class));

            return;

        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListaComerciosFragment()).commit();

    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();

        Log.i("Prueba", String.valueOf(fm.getBackStackEntryCount()));
        Log.i("Prueba", String.valueOf(recuento));

        if (recuento == (fm).getBackStackEntryCount()) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return;

        }

        if ((fm).getBackStackEntryCount() > 0){

            (fm).popBackStack();

            return;
        }

        super.onBackPressed();

    }
}
