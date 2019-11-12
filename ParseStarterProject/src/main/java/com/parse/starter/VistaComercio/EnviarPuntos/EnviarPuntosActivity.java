package com.parse.starter.VistaComercio.EnviarPuntos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.ChatEditText.ChatEditText;
import com.parse.starter.R;
import com.parse.starter.VistaComercio.EnviarEncuesta.EnviarEncuestaActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class EnviarPuntosActivity extends AppCompatActivity implements View.OnKeyListener, TextWatcher {

    String comercioId;
    String nombreComercio;
    String encuestaActiva;
    String encuestaActivaId;
    String nombreCompletoAdmin;
    String usuario;
    String usuarioId;
    String correoCliente;
    String recompensaActiva;

    Double porcentaje;
    Double puntos;

    int contador;
    int numeroDePreguntas;

    Boolean encuestaEnviada;

    Date fecha;

    TextView op1TextView;
    TextView op2TextView;
    TextView op3TextView;
    TextView op4TextView;
    TextView op5TextView;
    TextView op6TextView;
    TextView op7TextView;
    TextView op8TextView;
    TextView op9TextView;
    TextView op10TextView;
    TextView op11TextView;

    ImageView op1ImageView;

    ChatEditText enviarPtsEditText;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet1 = new ConstraintSet();
    ConstraintSet constraintSet2 = new ConstraintSet();

    ProgressDialog progressDialog;

    private void guardadoExitoso(){

        contador = contador + 1;

        enviarPtsEditText.setEnabled(false);

        op4TextView.setText("Puntos enviados al cliente");
        op4TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
        op6TextView.setText("LISTO");
        op1ImageView.setImageResource(R.drawable.success);
        Toast.makeText(this, "ENVIADO con ÉXITO", Toast.LENGTH_SHORT).show();
        terminarSppiner();

    }

    public void enviarPuntos(View view){

        if (contador == 0){

            contador = contador + 1;

            Toast.makeText(this, "Vuelve a presionar el botón para confirmar", Toast.LENGTH_SHORT).show();

            op6TextView.setText("CONFIRMAR");

        } else if (contador == 1){

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

            regresar();

            iniciarSppiner();

            ParseObject object = new ParseObject("PuntosEnviados");
            object.put("nombreComercio", nombreComercio);
            object.put("comercioId", comercioId);
            object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
            object.put("nombreColaborador", nombreCompletoAdmin);
            object.put("usuarioId", usuarioId);
            object.put("nombreUsuario", usuario);
            object.put("fechaCreacion", fecha);
            object.put("fechaModificacion", fecha);
            object.put("consumo", enviarPtsEditText.getText().toString());
            object.put("puntosEnviados", Double.valueOf(op5TextView.getText().toString()));
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null){

                        final ParseObject object = new ParseObject("HistorialPuntos");
                        object.put("nombreComercio", nombreComercio);
                        object.put("usuarioId", usuarioId);
                        object.put("fechaModificacion", fecha);
                        object.put("fechaCreacion", fecha);
                        object.put("puntos", Double.valueOf(op5TextView.getText().toString()));
                        object.put("tipo", "esEnviar");
                        object.put("comercioId", comercioId);
                        object.put("correoUsuario", correoCliente);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null){

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("VisitasCliente");
                                    query.whereEqualTo("comercioId", comercioId);
                                    query.whereEqualTo("usuarioId", usuarioId);
                                    query.setLimit(1);
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {

                                            if (e == null){

                                                if (objects.size() > 0){

                                                    for (ParseObject object : objects){

                                                        long diff = fecha.getTime() - object.getDate("fechaModificacion").getTime();
                                                        long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);

                                                        if (diffInHours >= 8){

                                                            int nuevoValor = object.getInt("numeroDeVisitas") + 1;
                                                            object.put("numeroDeVisitas", nuevoValor);
                                                            object.put("fechaModificacion", fecha);
                                                            object.saveInBackground();

                                                        } else {

                                                            Log.i("Prueba","Nelson Mandela");

                                                        }
                                                    }

                                                } else {

                                                    ParseObject object = new ParseObject("VisitasCliente");
                                                    object.put("nombreComercio", nombreComercio);
                                                    object.put("comercioId", comercioId);
                                                    object.put("nombreUsuario", usuario);
                                                    object.put("usuarioId", usuarioId);
                                                    object.put("fechacreacion", fecha);
                                                    object.put("fechaModificacion", fecha);
                                                    object.put("numeroDeVisitas", 1);
                                                    object.put("nombreCompleto", nombreCompletoAdmin);
                                                    object.saveInBackground();

                                                }

                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
                                                query.whereEqualTo("comercioId", comercioId);
                                                query.whereEqualTo("usuarioId", usuarioId);
                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> objects, ParseException e) {

                                                        if (e == null){

                                                            if (objects.size() > 0){

                                                                for (ParseObject object : objects){

                                                                    object.put("fechaModificacion", fecha);

                                                                    Double puntosNuevos = Double.valueOf(op5TextView.getText().toString());
                                                                    Double puntosViejos = Double.valueOf(object.getDouble("puntos"));
                                                                    Double puntosForSave = Double.valueOf(String.format("%.2f", puntosViejos + puntosNuevos));
                                                                    object.put("puntos", puntosForSave);

                                                                    Double consumoNuevo = Double.valueOf(enviarPtsEditText.getText().toString());
                                                                    Double consumoViejo = Double.valueOf(object.getDouble("consumo"));
                                                                    Double consumoForSave = Double.valueOf(String.format("%.2f", consumoViejo + consumoNuevo));
                                                                    object.put("consumo", consumoForSave);

                                                                    object.saveInBackground();

                                                                }

                                                            } else {

                                                                ParseObject object = new ParseObject("PuntosCliente");
                                                                object.put("usuarioId", usuarioId);
                                                                object.put("nombreUsuario", usuario);
                                                                object.put("fechaCreacion", fecha);
                                                                object.put("fechaModificacion", fecha);
                                                                object.put("consumo", Double.valueOf(enviarPtsEditText.getText().toString()));
                                                                object.put("puntos", Double.valueOf(op5TextView.getText().toString()));
                                                                object.put("nombreComercio", nombreComercio);
                                                                object.put("comercioId", comercioId);
                                                                object.saveInBackground();

                                                            }

                                                            if (encuestaEnviada){

                                                                guardadoExitoso();

                                                            } else {

                                                                ParseObject object = new ParseObject("EncuestaPendiente");
                                                                object.put("nombreComercio", nombreComercio);
                                                                object.put("comercioId", comercioId);
                                                                object.put("nombreEncuesta", encuestaActiva);
                                                                object.put("encuestaId", encuestaActivaId);
                                                                object.put("fechaCreacion", fecha);
                                                                object.put("fechaModificacion", fecha);
                                                                object.put("numeroDePreguntas", numeroDePreguntas);
                                                                object.put("recompensaActiva", recompensaActiva);
                                                                object.put("usuario", usuario);
                                                                object.put("usuarioId", usuarioId);
                                                                object.put("activo", true);
                                                                object.put("encuestaAplicada", false);
                                                                object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
                                                                object.put("nombreColaborador", nombreCompletoAdmin);
                                                                object.put("correoColaborador", ParseUser.getCurrentUser().getEmail());
                                                                object.saveInBackground();

                                                                guardadoExitoso();

                                                            }

                                                        } else {

                                                            terminarSppiner();

                                                            Toast.makeText(EnviarPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });

                                            } else {

                                                terminarSppiner();

                                                Toast.makeText(EnviarPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                } else {

                                    terminarSppiner();

                                    Toast.makeText(EnviarPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    } else {

                        terminarSppiner();

                        Toast.makeText(EnviarPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else if (contador == 2){

            finish();

        }
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

    private void buttonDisabled(){

        verificarEncuesta();
        op6TextView.setBackgroundColor(Color.GRAY);
        op6TextView.setEnabled(false);

    }

    private void buttonEnabled(){

        verificarEncuesta();
        op6TextView.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        op6TextView.setEnabled(true);

    }

    private void verificarEncuesta(){

        if (encuestaEnviada){

            op6TextView.setText("Enviar puntos");

        } else {

            op6TextView.setText("Enviar puntos y encuesta");

        }

    }

    private void regresar (){

        //escribiendoMensaje = false;

        if (getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

        TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet1.applyTo(constraintLayout);

        op1ImageView.setVisibility(View.VISIBLE);
        op7TextView.setVisibility(View.VISIBLE);
        op8TextView.setVisibility(View.VISIBLE);
        op9TextView.setVisibility(View.VISIBLE);
        op10TextView.setVisibility(View.VISIBLE);
        op11TextView.setVisibility(View.VISIBLE);

    }

    private void changeView(){

        op1ImageView.setVisibility(View.INVISIBLE);
        op7TextView.setVisibility(View.INVISIBLE);
        op8TextView.setVisibility(View.INVISIBLE);
        op9TextView.setVisibility(View.INVISIBLE);
        op10TextView.setVisibility(View.INVISIBLE);
        op11TextView.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet2.applyTo(constraintLayout);

            }
        }, 150);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_puntos);

        getWindow().setSoftInputMode(18);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Enviar puntos");

        constraintLayout = (ConstraintLayout) findViewById(R.id.enviarPtsConstraintLayout);
        constraintSet1.clone(constraintLayout);
        constraintSet2.clone(this, R.layout.detalle_enviar_puntos);

        enviarPtsEditText = (ChatEditText) findViewById(R.id.op1EnviarPtsEditText);
        op1ImageView = (ImageView) findViewById(R.id.op1EnviarPtsImageView);
        op1TextView = (TextView) findViewById(R.id.op1EnviarPtsTexttView);
        op2TextView = (TextView) findViewById(R.id.op2EnviarPtsTexttView);
        op3TextView = (TextView) findViewById(R.id.op3EnviarPtsTexttView);
        op4TextView = (TextView) findViewById(R.id.op4EnviarPtsTexttView);
        op5TextView = (TextView) findViewById(R.id.op5EnviarPtsTexttView);
        op6TextView = (TextView) findViewById(R.id.op6EnviarPtsTexttView);
        op7TextView = (TextView) findViewById(R.id.op7EnviarPtsTexttView);
        op8TextView = (TextView) findViewById(R.id.op8EnviarPtsTexttView);
        op9TextView = (TextView) findViewById(R.id.op9EnviarPtsTexttView);
        op10TextView = (TextView) findViewById(R.id.op10EnviarPtsTexttView);
        op11TextView = (TextView) findViewById(R.id.op11EnviarPtsTexttView);

        Intent intent = getIntent();

        encuestaEnviada = intent.getBooleanExtra("encuestaEnviada", false);
        comercioId = intent.getStringExtra("comercioId");
        nombreComercio = intent.getStringExtra("nombreComercio");
        encuestaActiva = intent.getStringExtra("encuestaActiva");
        encuestaActivaId = intent.getStringExtra("encuestaActivaId");
        numeroDePreguntas = intent.getIntExtra("numeroDePreguntas", 0);
        nombreCompletoAdmin = intent.getStringExtra("nombreCompletoAdmin");
        usuario = intent.getStringExtra("usuario");
        usuarioId = intent.getStringExtra("usuarioId");
        correoCliente = intent.getStringExtra("correoCliente");
        recompensaActiva = intent.getStringExtra("recompensaActiva");

        verificarEncuesta();
        buttonDisabled();

        enviarPtsEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                changeView();

                return false;

            }
        });

        enviarPtsEditText.setKeyImeChangeListener(new ChatEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {

                if (4 == event.getKeyCode() && event.getAction() == 0){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            regresar();

                        }
                    }, 300);
                }
            }
        });

        enviarPtsEditText.setOnKeyListener(this);
        enviarPtsEditText.addTextChangedListener(this);

        int maxLength = 8;
        enviarPtsEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

        iniciarSppiner();

        porcentaje = 0.0;
        contador = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosActivos");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("eliminado", false);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        porcentaje = object.getDouble("porcentaje");

                        op9TextView.setText(String.valueOf(porcentaje.intValue()) + "%");

                    }

                    Double puntosEjemplo = 100 * (porcentaje / 100);

                    if ((puntosEjemplo % 1) == 0){

                        op11TextView.setText(String.valueOf(puntosEjemplo.intValue()) + " pts");

                    } else {

                        op11TextView.setText(String.format("%.2f", puntosEjemplo) + " pts");

                    }

                    terminarSppiner();

                } else {

                    terminarSppiner();

                    Toast.makeText(EnviarPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == 0 && keyCode == 66){

            regresar();

        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        contador = 0;

        buttonDisabled();

        String referencia = enviarPtsEditText.getText().toString();

        if ( referencia.matches("") || referencia.isEmpty() || referencia.equals(".")) {

            op5TextView.setText("0");

            buttonDisabled();

        } else if (Double.valueOf(referencia) <= 0.0){

            op5TextView.setText("0");

            buttonDisabled();

        } else {

            buttonEnabled();

            Double valorConversion = porcentaje / 100;
            puntos = Double.valueOf(enviarPtsEditText.getText().toString()) * valorConversion;

            if ((puntos % 1) == 0){

                op5TextView.setText(String.valueOf(puntos.intValue()));

            } else {

                op5TextView.setText(String.format("%.2f", puntos));

            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
