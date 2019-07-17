package com.parse.starter.Inicio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.AyudaYSugerencias.AyudaSugerenciaActivity;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.ClienteActivity;
import com.parse.starter.registro_Usuario.NombreUserActivity;

public class inicio_Pando_Activity extends AppCompatActivity {

    ProgressDialog progressDialog;

    public void goToAyuda(View view){

        Intent intent = new Intent(getApplicationContext(), AyudaSugerenciaActivity.class);
        intent.putExtra("esAnonimo", true);
        startActivity(intent);

    }

    public void goToLogIn(View view){

        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(intent);

    }

    public void goToNombreUsuario(View view){

        Intent intent = new Intent(getApplicationContext(), NombreUserActivity.class);
        startActivity(intent);


    }

    public void terminarSppiner(){

        getWindow().clearFlags(16);
        this.progressDialog.dismiss();

    }

    public void iniciarSppiner(){

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("Cargando...");
        this.progressDialog.show();
        getWindow().setFlags(16, 16);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio__pando_);

        iniciarSppiner();

        getSupportActionBar().hide();

        if (ParseUser.getCurrentUser() != null){

            terminarSppiner();

            startActivity(new Intent(getApplicationContext(), ClienteActivity.class));

        } else {

            terminarSppiner();

        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
