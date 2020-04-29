package com.parse.starter.registro_Usuario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.starter.Inicio.LogInActivity;
import com.parse.starter.R;
import com.parse.starter.VistaComercio.Administrador.AdministradorActivity;
import com.parse.starter.VistaComercio.Colaborador.ColaboradorActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class ContrasenaActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    String contrasena;
    String confirmacion;
    String correoUsuario;
    String nombreUsuario;
    String apellidoUsuario;
    String nombreComercio;
    String comercioId;
    String mensaje;

    Boolean isMan;
    Boolean esRegistroAdmin;
    Boolean usuarioToAdmin;
    Boolean esRegistroColaborador;

    java.util.Date fechaNacimiento = new java.util.Date();

    TextView siguienteTextView;
    TextView tituloTextView;

    EditText contrasena1EditText;
    EditText contrasena2EditText;

    java.util.Date fecha;

    ProgressDialog progressDialog;

    private void goToInfoBienvenida(){

        terminarSppiner();
        Intent intent = new Intent(getApplicationContext(), InfoInicialActivity.class);
        startActivity(intent);

    }

    private void guardarComercio(){

        ParseObject object = new ParseObject("Comercios");
        object.put("nombreComercio", nombreComercio);
        object.put("slogan", "");
        object.put("consumoPromedio", 0);
        object.put("visa", false);
        object.put("mastercard", false);
        object.put("american", false);
        object.put("numeroContacto", 0);
        object.put("horario", "");

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Comercios");
                    query.whereEqualTo("nombreComercio", nombreComercio);
                    query.setLimit(1);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                for (ParseObject object : objects){

                                    comercioId = object.getObjectId();

                                }

                                registrarAdminCol();

                            } else {

                                terminarSppiner();

                                Toast.makeText(ContrasenaActivity.this, "Parece que hubo un error - Intena de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    terminarSppiner();

                    Toast.makeText(ContrasenaActivity.this, "Parece que hubo un error - Intena de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void registrarAdminCol(){

        /*Log.i("Prueba", nombreComercio);
        Log.i("Prueba", comercioId);
        Log.i("Prueba", nombreUsuario + " " + apellidoUsuario);
        Log.i("Prueba", ParseUser.getCurrentUser().getObjectId());
        Log.i("Prueba", String.valueOf(fecha));
        Log.i("Prueba", correoUsuario);
        Log.i("Prueba", nombreUsuario);
        Log.i("Prueba", String.valueOf(esRegistroAdmin));;
        Log.i("Prueba", mensaje);*/

        ParseObject object = new ParseObject("EncuestasAplicadas");
        object.put("nombreComercio", nombreComercio);
        object.put("comercioId", comercioId);
        object.put("nombreColaborador", nombreUsuario + " " + apellidoUsuario);
        object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
        object.put("encuestasEnviadas", 0);
        object.put("encuestaEnDispositivo", 0);
        object.put("totalEncuestas", 0);
        object.put("fechaCreacion", fecha);
        object.put("fechaModificacion", fecha);
        object.put("correoColaborador", correoUsuario);
        object.put("esAdministrador", esRegistroAdmin);
        object.put("nombre", nombreUsuario);
        object.saveInBackground();
        terminarSppiner();

        if (esRegistroAdmin){

            Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
            startActivity(intent);

        } else {

            Intent intent = new Intent(getApplicationContext(), ColaboradorActivity.class);
            startActivity(intent);

        }
    }

    private void registrarCliente(){

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(correoUsuario);
        parseUser.setEmail(correoUsuario);
        parseUser.setPassword(contrasena);
        parseUser.put("nombre", nombreUsuario);
        parseUser.put("apellido", apellidoUsuario);
        parseUser.put("man", isMan);
        parseUser.put("woman", !isMan);
        parseUser.put("fechaDeNacimiento", this.fechaNacimiento);
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    if (esRegistroAdmin || esRegistroColaborador){

                        if (esRegistroAdmin){

                            mensaje = "Nuevo Admin, nuevo comercio";
                            guardarComercio();

                        } else {

                            mensaje = "Nuevo Colaborador, viejo comercio";
                            registrarAdminCol();

                        }

                    } else {

                        Log.i("Prueba", mensaje);
                        terminarSppiner();
                        goToInfoBienvenida();

                    }

                } else {

                    terminarSppiner();

                    Toast.makeText(ContrasenaActivity.this, "Parece que hubo un error - Intena de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }



    private void registroUsuario(){

        iniciarSppiner();

        Integer valueOf;
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("America/Mexico_City"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String valueOf2 = String.valueOf(calendar.get(Calendar.YEAR));
        String valueOf3 = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String valueOf4 = String.valueOf(calendar.get(Calendar.DATE));
        String valueOf5 = String.valueOf(calendar.get(Calendar.MINUTE));
        String valueOf6 = String.valueOf(calendar.get(Calendar.SECOND));
        Integer valueOf7 = Integer.valueOf(calendar.get(Calendar.AM_PM));
        Integer valueOf8 = Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + 6);

        if (valueOf7.intValue() == 0) {
            valueOf = Integer.valueOf(valueOf8.intValue() - 11);
        } else {
            valueOf = Integer.valueOf(calendar.get(Calendar.HOUR) + 7);
        }
        try {
            this.fecha = dateFormat.parse(valueOf4 + "/" + valueOf3 + "/" + valueOf2 + " " + valueOf + ":" + valueOf5 + ":" + valueOf6);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        /*Log.i("Prueba", nombreUsuario);
        Log.i("Prueba", correoUsuario);
        Log.i("Prueba", contrasena);*/

        if (esRegistroAdmin || esRegistroColaborador){

            if (usuarioToAdmin){

                ParseUser.logInInBackground(correoUsuario, contrasena, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (e == null){

                            if (esRegistroAdmin) {

                                mensaje = "Usuario a Admin, nuevo comercio";
                                guardarComercio();

                            } else {

                                mensaje = "Usuario a Colaborador, comercio existente";
                                registrarAdminCol();

                            }

                        } else {

                            terminarSppiner();

                            Toast.makeText(ContrasenaActivity.this, "Parece que hubo un error - Intena de nuevo", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            } else {

                /*Log.i("Prueba", String.valueOf(isMan));
                Log.i("Prueba", String.valueOf(fechaNacimiento));
                Log.i("Prueba", apellidoUsuario);*/

                registrarCliente();

            }

        } else {

            /*Log.i("Prueba", String.valueOf(isMan));
            Log.i("Prueba", String.valueOf(fechaNacimiento));
            Log.i("Prueba", apellidoUsuario);
            Log.i("Prueba", String.valueOf(esRegistroColaborador));*/

            mensaje = "Cliente normal";
            registrarCliente();

        }
    }

    public void siguiente(View view){

        if (usuarioToAdmin){

            registroUsuario();

        } else {

            if (contrasena.matches(confirmacion)){

                registroUsuario();

            } else {

                Toast.makeText(this, "Espera - la contraseña y la confirmación son diferentes", Toast.LENGTH_SHORT).show();

                return;

            }
        }
    }

    public void back(View view){

        finish();

    }

    public void terminarSppiner()
    {
        getWindow().clearFlags(16);
        this.progressDialog.dismiss();
    }

    public void iniciarSppiner()
    {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("Cargando...");
        this.progressDialog.show();
        getWindow().setFlags(16, 16);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasena);

        getSupportActionBar().hide();

        getWindow().setSoftInputMode(20);

        siguienteTextView = (TextView) findViewById(R.id.siguienteContraTextView);
        contrasena1EditText = (EditText) findViewById(R.id.contraRegEditText);
        contrasena2EditText = (EditText) findViewById(R.id.confirmContraEditText);
        tituloTextView = (TextView) findViewById(R.id.op1ContratextView);

        contrasena1EditText.addTextChangedListener(this);
        contrasena2EditText.addTextChangedListener(this);
        contrasena2EditText.setOnKeyListener(this);
        contrasena1EditText.setOnKeyListener(this);

        Intent intent = getIntent();
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        apellidoUsuario = intent.getStringExtra("apellidoUsuario");
        isMan = Boolean.valueOf(intent.getBooleanExtra("isMan", false));
        fechaNacimiento.setTime(intent.getLongExtra("fechaNacimiento", -1));
        correoUsuario = intent.getStringExtra("correoUsuario");
        esRegistroAdmin = intent.getBooleanExtra("esRegistroAdmin", false);
        esRegistroColaborador = intent.getBooleanExtra("esRegistroColaborador", false);
        usuarioToAdmin = intent.getBooleanExtra("usuarioToAdmin", false);

        if (esRegistroAdmin || esRegistroColaborador){

            nombreComercio = intent.getStringExtra("nombreComercio");
            comercioId = intent.getStringExtra("comercioId");

            if (usuarioToAdmin){

                tituloTextView.setText("Escribe la misma contraseña con la que te registraste en Pando");
                contrasena2EditText.setVisibility(View.INVISIBLE);

            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        contrasena = contrasena1EditText.getText().toString();
        confirmacion = contrasena2EditText.getText().toString();

        if (usuarioToAdmin) {

            if (contrasena.matches("")){

                siguienteTextView.setVisibility(View.INVISIBLE);
                return;

            } else {

                siguienteTextView.setVisibility(View.VISIBLE);
                return;

            }

        } else {

            if (contrasena.matches("") || confirmacion.matches("")){

                siguienteTextView.setVisibility(View.INVISIBLE);
                return;

            }

            if (contrasena.matches(confirmacion)){

                siguienteTextView.setVisibility(View.VISIBLE);
                return;

            }

            siguienteTextView.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == 66 && keyEvent.getAction() == 0){

            contrasena = contrasena1EditText.getText().toString();
            confirmacion = contrasena2EditText.getText().toString();

            if (usuarioToAdmin){

                if (contrasena.matches("")){

                    Toast.makeText(this, "Espera - escribe la contraseña", Toast.LENGTH_SHORT).show();

                } else {

                    registroUsuario();

                }

            } else {

                if (contrasena.matches("") || confirmacion.matches("")){

                    Toast.makeText(this, "Espera - se requiere llenar ambos campos", Toast.LENGTH_SHORT).show();

                } else {

                    if (contrasena.matches(confirmacion)){

                        registroUsuario();

                    } else {

                        Toast.makeText(this, "Espera - la contraseña y la confirmación son diferentes", Toast.LENGTH_SHORT).show();

                    }
                }
            }

        } else {

            return false;

        }
        return false;
    }
}
