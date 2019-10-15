package com.parse.starter.VistaClientes.Encuestas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.parse.starter.AyudaYSugerencias.AyudaSugerenciaActivity;
import com.parse.starter.ChatEditText.ChatEditText;
import com.parse.starter.R;
import com.parse.starter.registro_Usuario.InfoInicialActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class PreguntaCaritasActivity extends AppCompatActivity {

    String comercioId;
    String encuestaActivaId;
    String nombreCliente;
    String nombreComercio;
    String encuestaActiva;
    String apellidoCliente;

    ArrayList<String> PREGUNTA = new ArrayList();
    ArrayList<String> PREGUNTAID = new ArrayList();

    Boolean escribiendoMensaje;
    Boolean sadComercio;
    Boolean mehComercio;
    Boolean happyComercio;
    Boolean nps;

    ArrayList<Boolean> PREGUNTAOBLIGATORIA = new ArrayList();

    int numeroDePregunta;
    int numeroDePreguntas;

    TextView siguienteTextView;
    TextView preguntaTextView;

    ImageView sadImageView;
    ImageView mehImageView;
    ImageView happyImageView;

    java.util.Date fecha;

    ChatEditText mensajeEditText;

    ProgressDialog progressDialog;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet1 = new ConstraintSet();
    ConstraintSet constraintSet2 = new ConstraintSet();

    private void cargarPregunta(){

        if (numeroDePregunta <= (numeroDePreguntas - 1)){

            iniciarSppiner();

            preguntaTextView.setText(PREGUNTA.get(numeroDePregunta));

            caritasInicial();

            mensajeEditText.setText("");
            
            if (PREGUNTAOBLIGATORIA.get(numeroDePregunta)){
                
                changeView();


            } else {

                regresar();

            }

            terminarSppiner();

        } else {

            Intent intent;

            if (nps){

                intent = new Intent(getApplicationContext(), PreguntaSliderActivity.class);
                intent.putExtra("comercioId", comercioId);
                intent.putExtra("nombreCliente", nombreCliente);
                intent.putExtra("encuestaActivaId", encuestaActivaId);
                intent.putExtra("nombreComercio", nombreComercio);
                intent.putExtra("apellidoCliente", apellidoCliente);
                startActivity(intent);

                return;

            }

            intent = new Intent(getApplicationContext(), InfoInicialActivity.class);
            intent.putExtra("esAgradecimiento", true);
            intent.putExtra("comercioId", comercioId);
            intent.putExtra("nombreCliente", nombreCliente);
            intent.putExtra("encuestaActivaId", encuestaActivaId);
            startActivity(intent);

        }
    }

    public void sad(View view){

        siguienteTextView.setVisibility(View.VISIBLE);

        sadImageView.setImageResource(R.drawable.sad_clicked);
        mehImageView.setImageResource(R.drawable.meh);
        happyImageView.setImageResource(R.drawable.happy);

        sadComercio = true;
        mehComercio = false;
        happyComercio = false;

    }

    public void meh(View view){

        siguienteTextView.setVisibility(View.VISIBLE);

        sadImageView.setImageResource(R.drawable.sad);
        mehImageView.setImageResource(R.drawable.meh_clicked);
        happyImageView.setImageResource(R.drawable.happy);

        sadComercio = false;
        mehComercio = true;
        happyComercio = false;
    }

    public void happy(View view){

        siguienteTextView.setVisibility(View.VISIBLE);

        sadImageView.setImageResource(R.drawable.sad);
        mehImageView.setImageResource(R.drawable.meh);
        happyImageView.setImageResource(R.drawable.happy_clicked);

        sadComercio = false;
        mehComercio = false;
        happyComercio = true;

    }

    private void guardarRespuesta(){

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

        ParseObject object = new ParseObject("Retroalimentacion");
        object.put("comercioId", comercioId);
        object.put("nombreComercio", nombreComercio);
        object.put("pregunta", PREGUNTA.get(numeroDePregunta));
        object.put("preguntaId", PREGUNTAID.get(numeroDePregunta));
        object.put("sad", sadComercio);
        object.put("meh", mehComercio);
        object.put("happy", happyComercio);
        object.put("Comentario", mensajeEditText.getText().toString());
        object.put("fechaCreacion", fecha);
        object.put("nombreEncuesta", encuestaActiva);
        object.put("encuestaId", encuestaActivaId);
        object.put("esObligatorio", PREGUNTAOBLIGATORIA.get(numeroDePregunta));
        object.put("usuario", nombreCliente + " " + apellidoCliente);
        object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Encuestas");
                    query.whereEqualTo("comercioId", comercioId);
                    query.whereEqualTo("objectId", encuestaActivaId);
                    query.setLimit(1);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                for (ParseObject object : objects){

                                    int cantidadRespuesta = object.getInt("numeroDeRespuestas") + 1;
                                    object.put("numeroDeRespuestas", cantidadRespuesta);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            if (e == null){

                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("PreguntasEncuesta");
                                                query.whereEqualTo("comercioId", comercioId);
                                                query.whereEqualTo("objectId", PREGUNTAID.get(numeroDePregunta));
                                                query.setLimit(1);
                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> objects, ParseException e) {

                                                        for (ParseObject object : objects){

                                                            if (mensajeEditText.getText().toString().isEmpty() == false){

                                                                int nuevoValor = object.getInt("comentarios") + 1;
                                                                object.put("comentarios", nuevoValor);

                                                            }

                                                            if (sadComercio){

                                                                int nuevoValor = object.getInt("sad") + 1;
                                                                object.put("sad", nuevoValor);

                                                            } else if (mehComercio){

                                                                int nuevoValor = object.getInt("meh") + 1;
                                                                object.put("meh", nuevoValor);

                                                            } else if (happyComercio){

                                                                int nuevoValor = object.getInt("happy") + 1;
                                                                object.put("happy", nuevoValor);

                                                            }

                                                            object.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {

                                                                    if (e == null){

                                                                        terminarSppiner();

                                                                        numeroDePregunta += 1;

                                                                        cargarPregunta();

                                                                    } else {

                                                                        terminarSppiner();

                                                                        Toast.makeText(PreguntaCaritasActivity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                            } else {

                                                terminarSppiner();

                                                Toast.makeText(PreguntaCaritasActivity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });
                                }

                            } else {

                                terminarSppiner();

                                Toast.makeText(PreguntaCaritasActivity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } else {

                    terminarSppiner();

                    Toast.makeText(PreguntaCaritasActivity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void siguiente(View view){

        
        if (PREGUNTAOBLIGATORIA.get(numeroDePregunta)){

            if (mensajeEditText.getText().toString().matches("")) {

                Toast.makeText(this, "En esta pregunta nos gustaría mucho saber tu opinión", Toast.LENGTH_SHORT).show();

            } else {

                guardarRespuesta();

            }

        } else {

            if (escribiendoMensaje){

                regresar();

                return;
            }

            guardarRespuesta();

        }
    }

    private void regresar(){

        escribiendoMensaje = false;

        siguienteTextView.setText("Siguiente");

        if (getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

        android.support.transition.TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet1.applyTo(constraintLayout);

        sadImageView.setVisibility(View.VISIBLE);
        mehImageView.setVisibility(View.VISIBLE);
        happyImageView.setVisibility(View.VISIBLE);

        if (!sadComercio && !mehComercio && !happyComercio){

            siguienteTextView.setVisibility(View.INVISIBLE);

        }

    }

    private void changeView(){

        escribiendoMensaje = true;

        siguienteTextView.setVisibility(View.VISIBLE);
        
        if (PREGUNTAOBLIGATORIA.get(numeroDePregunta)){

            siguienteTextView.setText("Siguiente");
            
        } else {

            siguienteTextView.setText("Ok");
        }
        

        sadImageView.setVisibility(View.INVISIBLE);
        mehImageView.setVisibility(View.INVISIBLE);
        happyImageView.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                android.support.transition.TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet2.applyTo(constraintLayout);

                if (PREGUNTAOBLIGATORIA.get(numeroDePregunta)){

                    mensajeEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

            }
        }, 150);
    }

    private void caritasInicial(){

        siguienteTextView.setVisibility(View.INVISIBLE);

        sadImageView.setImageResource(R.drawable.sad);
        mehImageView.setImageResource(R.drawable.meh);
        happyImageView.setImageResource(R.drawable.happy);

        sadComercio = false;
        mehComercio = false;
        happyComercio = false;
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
        setContentView(R.layout.activity_pregunta_caritas);

        getSupportActionBar().hide();

        getWindow().setSoftInputMode(18);

        iniciarSppiner();

        numeroDePregunta = 0;

        escribiendoMensaje = false;

        constraintLayout = (ConstraintLayout) findViewById(R.id.caritasConstraintLayout);
        constraintSet1.clone(constraintLayout);
        constraintSet2.clone(this, R.layout.detalle_pregunta_caritas);

        mensajeEditText = (ChatEditText) findViewById(R.id.op1CaritasEditText);
        sadImageView = (ImageView) findViewById(R.id.op1CaritasImageView);
        mehImageView = (ImageView) findViewById(R.id.op2CaritasImageView);
        happyImageView = (ImageView) findViewById(R.id.op3CaritasImageView);
        siguienteTextView = (TextView) findViewById(R.id.op3CaritasTextView);
        preguntaTextView = (TextView) findViewById(R.id.op1CaritasTextView);

        caritasInicial();

        Intent intent = getIntent();

        comercioId = intent.getStringExtra("comercioId");
        encuestaActivaId = intent.getStringExtra("encuestaActivaId");
        numeroDePreguntas = intent.getIntExtra("numeroDePreguntas", 0);
        nombreCliente = intent.getStringExtra("nombreCliente");
        nombreComercio = intent.getStringExtra("nombreComercio");
        encuestaActiva = intent.getStringExtra("encuestaActiva");
        apellidoCliente = intent.getStringExtra("apellidoCliente");

        PREGUNTA.clear();
        PREGUNTAID.clear();
        PREGUNTAOBLIGATORIA.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestaActiva");
        query.whereEqualTo("comercioId", comercioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                for (ParseObject object : objects){

                    nps = object.getBoolean("nps");

                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery("PreguntasEncuesta");
                query.whereEqualTo("comercioId", comercioId);
                query.whereEqualTo("encuestaId", encuestaActivaId);
                query.whereEqualTo("preguntaEliminada", false);
                query.orderByAscending("numeroPregunta");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        for (ParseObject object : objects){

                            PREGUNTA.add(object.getString("pregunta"));
                            PREGUNTAID.add(object.getObjectId());
                            PREGUNTAOBLIGATORIA.add(object.getBoolean("esObligatorio"));

                        }

                        Log.i("Prueba", PREGUNTA.get(numeroDePregunta));

                        terminarSppiner();

                        cargarPregunta();

                    }
                });
            }
        });

        mensajeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                changeView();

                return false;
            }
        });

        mensajeEditText.setKeyImeChangeListener(new ChatEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {

                if (4 == event.getKeyCode() && event.getAction() == 0){

                    if (!PREGUNTAOBLIGATORIA.get(numeroDePregunta)) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                regresar();

                            }
                        }, 300);

                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
