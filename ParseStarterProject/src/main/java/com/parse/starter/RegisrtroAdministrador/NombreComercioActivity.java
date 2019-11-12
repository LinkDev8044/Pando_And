package com.parse.starter.RegisrtroAdministrador;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.Inicio.LogInActivity;
import com.parse.starter.R;
import com.parse.starter.registro_Usuario.ContrasenaActivity;

import java.util.List;

public class NombreComercioActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    String correoUsuario;
    String nombreUsuario;
    String apellidoUsuario;
    String nombreComercio;

    Boolean isMan;
    Boolean usuarioToAdmin;

    java.util.Date fechaNacimiento = new java.util.Date();

    TextView op1textView;
    TextView siguienteTextView;

    EditText nombreComEditText;

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

    private void goToContrasena(){

        iniciarSppiner();

        final String nomComNewSinEspacios = nombreComercio.replace(" ", "");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comercios");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        String nomComOld = object.getString("nombreComercio");
                        String nomComOldSinEspacios = nomComOld.replace(" ", "");

                        if (nomComNewSinEspacios.trim().equalsIgnoreCase(nomComOldSinEspacios.trim())){

                            terminarSppiner();

                            Toast.makeText(NombreComercioActivity.this, "Comercio ya registrado", Toast.LENGTH_SHORT).show();

                        } else {

                            Intent intent = new Intent(getApplicationContext(), ContrasenaActivity.class);
                            intent.putExtra("nombreUsuario", nombreUsuario);
                            intent.putExtra("apellidoUsuario", apellidoUsuario);
                            intent.putExtra("isMan", isMan);
                            intent.putExtra("fechaNacimiento", fechaNacimiento.getTime());
                            intent.putExtra("correoUsuario", correoUsuario);
                            intent.putExtra("esRegistroAdmin", true);
                            intent.putExtra("nombreComercio", nombreComercio);

                            if (usuarioToAdmin){

                                intent.putExtra("usuarioToAdmin", usuarioToAdmin);

                            }

                            terminarSppiner();

                            startActivity(intent);

                        }
                    }

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    public void siguiente(View view){

        goToContrasena();

    }

    public void back(View view){

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombre_comercio);

        getSupportActionBar().hide();

        getWindow().setSoftInputMode(20);

        op1textView = (TextView) findViewById(R.id.op1NomComTextView);
        siguienteTextView = (TextView) findViewById(R.id.sigNomComTextView);
        nombreComEditText = (EditText) findViewById(R.id.nombreComEditText);

        Intent intent = getIntent();

        nombreUsuario = intent.getStringExtra("nombreUsuario");
        apellidoUsuario = intent.getStringExtra("apellidoUsuario");
        isMan = intent.getBooleanExtra("isMan", false);
        fechaNacimiento.setTime(intent.getLongExtra("fechaNacimiento", -1));
        correoUsuario = intent.getStringExtra("correoUsuario");
        usuarioToAdmin = intent.getBooleanExtra("usuarioToAdmin", false);

        op1textView.setText("Hola " + nombreUsuario + ", ¿Cómo se llama tu comercio?");

        nombreComEditText.addTextChangedListener(this);
        nombreComEditText.setOnKeyListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        nombreComercio = nombreComEditText.getText().toString();

        if (nombreComercio.matches("")){

            siguienteTextView.setVisibility(View.INVISIBLE);

        } else {

            siguienteTextView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == 0 && keyCode == 66){

            nombreComercio = nombreComEditText.getText().toString();

            if (nombreComercio.matches("")){

                Toast.makeText(this, "Espera - se requiere ingresr el nombre del comercio para continuar", Toast.LENGTH_SHORT).show();


            } else {


                goToContrasena();

            }

        }
        return false;
    }
}
