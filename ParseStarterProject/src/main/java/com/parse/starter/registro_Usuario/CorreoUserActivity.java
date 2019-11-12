package com.parse.starter.registro_Usuario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.RegisrtroAdministrador.NombreComercioActivity;

import java.sql.Date;
import java.util.List;

public class CorreoUserActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    String correoUsuario;
    String nombreUsuario;
    String apellidoUsuario;
    String comercioId;
    String nombreComercio;

    Boolean isMan;
    Boolean esRegistroAdmin;
    Boolean usuarioToAdmin;
    Boolean esRegistroColaborador;

    java.util.Date fechaNacimiento = new java.util.Date();

    EditText correoEditText;

    TextView siguienteTextView;

    ProgressDialog progressDialog;

    private void goToContrasena(){

        Intent intent = new Intent(getApplicationContext(), ContrasenaActivity.class);
        intent.putExtra("nombreUsuario", nombreUsuario);
        intent.putExtra("apellidoUsuario", apellidoUsuario);
        intent.putExtra("isMan", isMan);
        intent.putExtra("fechaNacimiento", fechaNacimiento.getTime());
        intent.putExtra("correoUsuario", correoUsuario);

        if (esRegistroAdmin){

            intent.putExtra("esRegistroAdmin", esRegistroAdmin);

        }

        terminarSppiner();

        startActivity(intent);

    }

    private void checkUser(){

        iniciarSppiner();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", correoUsuario);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        if (esRegistroAdmin || esRegistroColaborador){

                            correoUsuario = correoEditText.getText().toString();

                            for (ParseObject object : objects){

                                nombreUsuario = object.getString("nombre");
                                apellidoUsuario = object.getString("apellido");

                            }

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestasAplicadas");
                            query.whereEqualTo("correoColaborador", correoEditText.getText().toString());
                            query.whereEqualTo("esAdministrador", true);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {

                                    if (objects.size() > 0){

                                        terminarSppiner();

                                        Toast.makeText(CorreoUserActivity.this, "Parece que el correo ya esta registrado como administrador", Toast.LENGTH_SHORT).show();

                                    } else {

                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestasAplicadas");
                                        query.whereEqualTo("correoColaborador", correoEditText.getText().toString());
                                        query.whereEqualTo("esAdministrador", false);
                                        query.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, ParseException e) {

                                                if (objects.size() > 0){

                                                    terminarSppiner();

                                                    Toast.makeText(CorreoUserActivity.this, "Parece que el correo ya esta registrado como colaborador", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    usuarioToAdmin = true;

                                                    terminarSppiner();

                                                    if (esRegistroColaborador){

                                                        Intent intent = new Intent(getApplicationContext(), ContrasenaActivity.class);
                                                        intent.putExtra("correoUsuario", correoEditText.getText().toString());
                                                        intent.putExtra("usuarioToAdmin", usuarioToAdmin);
                                                        intent.putExtra("esRegistroColaborador", esRegistroColaborador);
                                                        intent.putExtra("nombreUsuario", nombreUsuario);
                                                        intent.putExtra("apellidoUsuario", apellidoUsuario);
                                                        intent.putExtra("nombreComercio", nombreComercio);
                                                        intent.putExtra("comercioId", comercioId);
                                                        startActivity(intent);

                                                    } else {

                                                        Intent intent = new Intent(getApplicationContext(), NombreComercioActivity.class);
                                                        intent.putExtra("correoUsuario", correoEditText.getText().toString());
                                                        intent.putExtra("usuarioToAdmin", usuarioToAdmin);
                                                        intent.putExtra("nombreUsuario", nombreUsuario);
                                                        intent.putExtra("apellidoUsuario", apellidoUsuario);
                                                        startActivity(intent);

                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        } else {

                            terminarSppiner();

                            Toast.makeText(CorreoUserActivity.this, "Parece que este correo ya esta registrado en nuestra base de datos", Toast.LENGTH_SHORT).show();

                        }

                    } else {

                        if (esRegistroAdmin || esRegistroColaborador){

                            Intent intent = new Intent(getApplicationContext(), NombreUserActivity.class);
                            intent.putExtra("esRegistroAdmin", esRegistroAdmin);
                            intent.putExtra("esRegistroColaborador", esRegistroColaborador);
                            intent.putExtra("correoUsuario", correoEditText.getText().toString());
                            intent.putExtra("comercioId", comercioId);
                            intent.putExtra("nombreComercio", nombreComercio);
                            terminarSppiner();
                            startActivity(intent);

                        } else {

                            goToContrasena();

                        }
                    }

                } else {

                    Log.i("Prueba", e.getMessage());

                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence paramCharSequence)
    {
        if (TextUtils.isEmpty(paramCharSequence)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(paramCharSequence).matches();
    }

    public void back (View view){

        finish();

    }

    public void siguiente(View view){

        correoUsuario = correoEditText.getText().toString();

        if (isValidEmail(correoUsuario)){

            checkUser();

            return;
        }

        Toast.makeText(this, "Espera - no parece un correo", Toast.LENGTH_SHORT).show();

    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correo_user);

        getSupportActionBar().hide();

        getWindow().setSoftInputMode(20);

        usuarioToAdmin = false;

        correoEditText = (EditText) findViewById(R.id.correoRegistroEditText);
        siguienteTextView = (TextView) findViewById(R.id.sigCorreoRegTextView);

        Intent intent = getIntent();
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        apellidoUsuario = intent.getStringExtra("apellidoUsuario");
        isMan = Boolean.valueOf(intent.getBooleanExtra("isMan", false));
        fechaNacimiento.setTime(intent.getLongExtra("fechaNacimiento", -1));
        esRegistroAdmin = intent.getBooleanExtra("esRegistroAdmin", false);
        esRegistroColaborador = intent.getBooleanExtra("esRegistroColaborador", false);
        nombreComercio = intent.getStringExtra("nombreComercio");
        comercioId = intent.getStringExtra("comercioId");

        correoEditText.addTextChangedListener(this);
        correoEditText.setOnKeyListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (correoEditText.getText().toString().matches("")){

            siguienteTextView.setVisibility(View.INVISIBLE);

        } else {

            siguienteTextView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == 66 && keyEvent.getAction() == 0) {

            if (correoEditText.getText().toString().matches("")){

                Toast.makeText(this, "Se requiere el correo electrónico para continuar", Toast.LENGTH_SHORT).show();

            } else {

                correoUsuario = correoEditText.getText().toString();

                if (isValidEmail(correoUsuario)){

                    checkUser();

                } else {

                    Toast.makeText(this, "Espera - no parece un correo", Toast.LENGTH_SHORT).show();

                }
            }

        }
        return false;
    }
}
