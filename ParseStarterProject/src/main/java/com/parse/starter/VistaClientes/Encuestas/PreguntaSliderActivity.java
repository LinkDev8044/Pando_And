package com.parse.starter.VistaClientes.Encuestas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.ChatEditText.ChatEditText;
import com.parse.starter.R;
import com.parse.starter.registro_Usuario.InfoInicialActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class PreguntaSliderActivity extends AppCompatActivity {

    String comercioId;
    String nombreCliente;
    String encuestaActivaId;
    String nombreComercio;
    String apellidoCliente;

    Boolean escribiendoMensaje;

    TextView calificacionTextView;
    TextView pregunta1TextView;
    TextView pregunta2TextView;
    TextView siguienteTextView;
    TextView pocoProbableTextView;
    TextView muyProbableTextView;

    ChatEditText mensajeEditText;

    SeekBar sliderSeekBar;

    java.util.Date fecha;

    ProgressDialog progressDialog;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet1 = new ConstraintSet();
    ConstraintSet constraintSet2 = new ConstraintSet();

    public void siguiente(View view){

        if (escribiendoMensaje){

            regresar();

            return;

        }

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

        ParseObject object = new ParseObject("CalificacionNPS");
        object.put("comercioId", comercioId);
        object.put("nombreComercio", nombreComercio);
        object.put("nombreUsuario", nombreCliente + " " + apellidoCliente);
        object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
        object.put("fechaCreacion", fecha);
        object.put("visto", false);
        object.put("comentario", mensajeEditText.getText().toString());
        object.put("calificacion", Integer.valueOf(calificacionTextView.getText().toString()));
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    terminarSppiner();

                    Intent intent = new Intent(getApplicationContext(), InfoInicialActivity.class);
                    intent.putExtra("esAgradecimiento", true);
                    intent.putExtra("comercioId", comercioId);
                    intent.putExtra("nombreCliente", nombreCliente);
                    intent.putExtra("encuestaActivaId", encuestaActivaId);
                    startActivity(intent);

                    return;

                } else {

                    terminarSppiner();

                    Toast.makeText(PreguntaSliderActivity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
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

        pregunta1TextView.setVisibility(View.VISIBLE);
        calificacionTextView.setVisibility(View.VISIBLE);
        sliderSeekBar.setVisibility(View.VISIBLE);
        pocoProbableTextView.setVisibility(View.VISIBLE);
        muyProbableTextView.setVisibility(View.VISIBLE);

    }

    private void changeView(){

        escribiendoMensaje = true;

        siguienteTextView.setText("Ok");

        pregunta1TextView.setVisibility(View.INVISIBLE);
        calificacionTextView.setVisibility(View.INVISIBLE);
        sliderSeekBar.setVisibility(View.INVISIBLE);
        pocoProbableTextView.setVisibility(View.INVISIBLE);
        muyProbableTextView.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                android.support.transition.TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet2.applyTo(constraintLayout);

            }
        }, 150);

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
        setContentView(R.layout.activity_pregunta_slider);

        getSupportActionBar().hide();

        getWindow().setSoftInputMode(18);

        escribiendoMensaje = false;

        Intent intent = getIntent();

        comercioId = intent.getStringExtra("comercioId");
        nombreCliente = intent.getStringExtra("nombreCliente");
        encuestaActivaId = intent.getStringExtra("encuestaActivaId");
        nombreComercio = intent.getStringExtra("nombreComercio");
        apellidoCliente = intent.getStringExtra("apellidoCliente");

        pregunta1TextView = (TextView) findViewById(R.id.op1SliderTextView);
        pregunta2TextView = (TextView) findViewById(R.id.op3SliderTextView);
        calificacionTextView = (TextView) findViewById(R.id.op2SliderTextView);
        mensajeEditText = (ChatEditText) findViewById(R.id.op1SliderEditText);
        siguienteTextView = (TextView) findViewById(R.id.op4SliderTextView);
        sliderSeekBar = (SeekBar) findViewById(R.id.sliderSeekBar);
        pocoProbableTextView = (TextView) findViewById(R.id.op5SliderTextView);
        muyProbableTextView = (TextView) findViewById(R.id.op6SliderTextView);

        constraintLayout = (ConstraintLayout) findViewById(R.id.sliderConstraintLayout);
        constraintSet1.clone(constraintLayout);
        constraintSet2.clone(this, R.layout.detalle_pregunta_slider);

        pregunta2TextView.setVisibility(View.INVISIBLE);
        mensajeEditText.setVisibility(View.INVISIBLE);
        siguienteTextView.setVisibility(View.INVISIBLE);

        calificacionTextView.setText(String.valueOf(5));

        sliderSeekBar.setMax(10);
        sliderSeekBar.incrementProgressBy(1);
        sliderSeekBar.setProgress(5);
        sliderSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                calificacionTextView.setText(String.valueOf(i));

                pregunta2TextView.setVisibility(View.VISIBLE);
                mensajeEditText.setVisibility(View.VISIBLE);
                siguienteTextView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            regresar();

                        }
                    }, 300);
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
