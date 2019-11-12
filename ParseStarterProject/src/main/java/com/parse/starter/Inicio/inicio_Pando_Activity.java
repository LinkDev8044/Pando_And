package com.parse.starter.Inicio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.AyudaYSugerencias.AyudaSugerenciaActivity;
import com.parse.starter.R;
import com.parse.starter.RegisrtroAdministrador.InicioPandoNegociosActivity;
import com.parse.starter.VistaClientes.ClienteActivity;
import com.parse.starter.registro_Usuario.NombreUserActivity;

import java.util.List;

public class inicio_Pando_Activity extends AppCompatActivity {

    TextView goToNegociosTextView;

    ProgressDialog progressDialog;

    public void goToNegocios(View view){

        Intent intent = new Intent(getApplicationContext(), InicioPandoNegociosActivity.class);
        startActivity(intent);

    }

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

        goToNegociosTextView = (TextView) findViewById(R.id.goToNegociosTextView);

        getSupportActionBar().hide();

    }

    @Override
    protected void onStart() {
        super.onStart();

        iniciarSppiner();

        if (ParseUser.getCurrentUser() != null){

            ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestasAplicadas");
            query.whereEqualTo("colaboradorId", ParseUser.getCurrentUser().getObjectId());
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        if (objects.size() > 0){

                            startActivity(new Intent(getApplicationContext(), SeleccionPerfilActivity.class));

                        } else {

                            terminarSppiner();

                            startActivity(new Intent(getApplicationContext(), ClienteActivity.class));

                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(inicio_Pando_Activity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("AdmitirNuevoComercio");
            query.whereEqualTo("puertaAbierta", true);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (objects.size() > 0){

                        goToNegociosTextView.setVisibility(View.VISIBLE);

                    } else {

                        goToNegociosTextView.setVisibility(View.INVISIBLE);

                    }

                    terminarSppiner();

                }
            });
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
