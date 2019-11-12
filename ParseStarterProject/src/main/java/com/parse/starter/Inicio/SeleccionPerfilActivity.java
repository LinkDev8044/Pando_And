package com.parse.starter.Inicio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.ClienteActivity;
import com.parse.starter.VistaComercio.Administrador.AdministradorActivity;
import com.parse.starter.VistaComercio.Colaborador.ColaboradorActivity;

import java.util.List;

public class SeleccionPerfilActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    public void iniciarSppiner() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("Cargando...");
        this.progressDialog.show();
        getWindow().setFlags(16, 16);
    }

    public void terminarSppiner() {
        getWindow().clearFlags(16);
        this.progressDialog.dismiss();
    }

    public void goToPandoNormal(View view){

        Intent intent = new Intent(getApplicationContext(), ClienteActivity.class);
        startActivity(intent);

    }

    public void goToPandoEmpresas(View view){

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestasAplicadas");
        query.whereEqualTo("colaboradorId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("esAdministrador", true);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    terminarSppiner();

                    if (objects.size() > 0){

                        Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
                        startActivity(intent);

                    } else {

                        Intent intent = new Intent(getApplicationContext(), ColaboradorActivity.class);
                        startActivity(intent);

                    }

                } else {

                    terminarSppiner();

                    Toast.makeText(SeleccionPerfilActivity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_perfil);

        getSupportActionBar().hide();
        
    }
}
