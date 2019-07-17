package com.parse.starter.AyudaYSugerencias;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.ChatEditText.ChatEditText;
import com.parse.starter.R;
import com.parse.starter.registro_Usuario.InfoInicialActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AyudaSugerenciaActivity extends AppCompatActivity {

    String correoCliente;
    String nombreCliente;
    String apellidoCliente;
    String usuarioId;

    Boolean escribiendoMensaje;
    Boolean esAnonimo;

    CharSequence[] colors = {"Ayuda", "Sugerencia"};

    java.util.Date fecha;

    TextView opcionTextView;
    TextView siguienteTextView;

    EditText correoEditText;
    ChatEditText mensajeEditText;

    ImageView backImageView;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet1 = new ConstraintSet();
    ConstraintSet constraintSet2 = new ConstraintSet();

    ProgressDialog progressDialog;

    public static boolean isValidEmail(CharSequence paramCharSequence)
    {
        if (TextUtils.isEmpty(paramCharSequence)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(paramCharSequence).matches();
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

    public void siguiente( View view){

        if (escribiendoMensaje){

            regresar();

            return;

        }

        if (isValidEmail(correoEditText.getText().toString())){

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

            if (esAnonimo){

                usuarioId = "Anónimo";

            } else {

                usuarioId = ParseUser.getCurrentUser().getObjectId();
            }

            ParseObject object = new ParseObject("Ayuda");
            object.put("usuarioId", usuarioId);
            object.put("nombreUsuario", nombreCliente + " " + apellidoCliente);
            object.put("correoUsuario", correoEditText.getText().toString());
            object.put("tipo", opcionTextView.getText().toString());
            object.put("mensaje", mensajeEditText.getText().toString());
            object.put("fechaCreacion", fecha);
            object.put("fechaModificacion", fecha);
            object.put("visto", false);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null){

                        terminarSppiner();

                        Log.i("Prueba", "Yei");
                        Intent intent = new Intent(getApplicationContext(), InfoInicialActivity.class);
                        intent.putExtra("mensajeAyuda", true);
                        intent.putExtra("esAnonimo", esAnonimo);
                        startActivity(intent);
                        return;

                    }

                    terminarSppiner();

                    Toast.makeText(AyudaSugerenciaActivity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                }
            });

        } else {

            Toast.makeText(this, "Espera - No parece un correo", Toast.LENGTH_SHORT).show();

        }
    }

    public void regresar (){

        escribiendoMensaje = false;

        siguienteTextView.setText("Siguiente");

        if (getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

        TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet1.applyTo(constraintLayout);

        opcionTextView.setVisibility(View.VISIBLE);
        correoEditText.setVisibility(View.VISIBLE);
        backImageView.setVisibility(View.VISIBLE);
        mensajeEditText.setVisibility(View.VISIBLE);

        if (mensajeEditText.getText().toString().matches("")){

            siguienteTextView.setVisibility(View.INVISIBLE);

            return;

        }

        siguienteTextView.setVisibility(View.VISIBLE);

    }

    public void changeView(){

        escribiendoMensaje = true;

        siguienteTextView.setText("Ok");
        siguienteTextView.setVisibility(View.VISIBLE);
        backImageView.setVisibility(View.INVISIBLE);
        opcionTextView.setVisibility(View.INVISIBLE);
        correoEditText.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet2.applyTo(constraintLayout);

            }
        }, 150);

    }

    public void opciones(View view){

        AlertDialog.Builder menu = new AlertDialog.Builder(this);
        menu.setTitle("Selecciona una opción:");
        menu.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //opcionTextView.setTextSize(24);
                correoEditText.setVisibility(View.VISIBLE);
                mensajeEditText.setVisibility(View.VISIBLE);

                if (i == 0){

                    opcionTextView.setText("Ayuda");

                    return;
                }

                opcionTextView.setText("Sugerencia");

            }
        });

        menu.show();

    }

    public void back(View view){

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda_sugerencia);

        getWindow().setSoftInputMode(18);

        escribiendoMensaje = false;

        constraintLayout = (ConstraintLayout) findViewById(R.id.ayudaConstraintLayout);
        constraintSet1.clone(constraintLayout);
        constraintSet2.clone(this, R.layout.detalle_ayuda_sugerencia);

        correoEditText = (EditText) findViewById(R.id.correoAyudaTextEdit);
        mensajeEditText = (ChatEditText) findViewById(R.id.mensajeEditText);
        opcionTextView = (TextView) findViewById(R.id.opcionAyudaTextView);
        siguienteTextView = (TextView) findViewById(R.id.siguienteAyudaTextView);
        backImageView = (ImageView) findViewById(R.id.backAyudaImageView);

        Intent intent = getIntent();
        esAnonimo = intent.getBooleanExtra("esAnonimo", false);

        siguienteTextView.setVisibility(View.INVISIBLE);

        if (esAnonimo){

            correoEditText.setEnabled(true);

            nombreCliente = "Anónimo";
            apellidoCliente = "";

        } else {

            correoCliente = intent.getStringExtra("correoCliente");
            nombreCliente = intent.getStringExtra("nombreCliente");
            apellidoCliente = intent.getStringExtra("apellidoCliente");
            correoEditText.setEnabled(false);
            correoEditText.setInputType(InputType.TYPE_NULL);

        }

        correoEditText.setText(correoCliente);

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
}
