package com.parse.starter.RegisrtroAdministrador;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.starter.Inicio.LogInActivity;
import com.parse.starter.R;
import com.parse.starter.registro_Usuario.CorreoUserActivity;

public class InicioPandoNegociosActivity extends AppCompatActivity {

    public void goToIniSesionTextView(View view){

        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(intent);

    }

    public void goToRegistroAdmin(View view){

        Intent intent = new Intent(getApplicationContext(), CorreoUserActivity.class);
        intent.putExtra("esRegistroAdmin", true);
        startActivity(intent);

    }

    public void goToRegistroColaborador(View view){

        Intent intent = new Intent(getApplicationContext(), CodigoComercioActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_pando_negocios);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
