package com.parse.starter.VistaClientes.ValidarPuntos;

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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class ValidarPuntosActivity extends AppCompatActivity implements View.OnKeyListener, TextWatcher {

    String usuario;
    String usuarioId;
    String comercioId;
    String nombreComercio;
    String nombreCompletoAdmin;

    int contador;

    Double puntosCliente;
    Double puntos;

    Date fecha;

    TextView op1TextView;
    TextView op3TextView;
    TextView op5TextView;
    TextView op6TextView;
    TextView op7TextView;
    TextView op8TextView;
    TextView op9TextView;
    TextView op10TextView;

    ImageView op1ImageView;

    ChatEditText validarEditText;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet1 = new ConstraintSet();
    ConstraintSet constraintSet2 = new ConstraintSet();

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

    private void guardadoExitoso(){

        contador = contador + 1;

        validarEditText.setEnabled(false);
        op6TextView.setText("Puntos canjeados:");
        op10TextView.setText("LISTO");
        op1ImageView.setImageResource(R.drawable.success);
        Toast.makeText(this, "ENVIADO con ÉXITO", Toast.LENGTH_SHORT).show();
        terminarSppiner();

    }

    public void validarPuntos (View view){

        if (contador == 0){

            contador = contador + 1;

            Toast.makeText(this, "Vuelve a presionar el botón para confirmar", Toast.LENGTH_SHORT).show();

            op10TextView.setText("CONFIRMAR");

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

            iniciarSppiner();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
            query.whereEqualTo("usuarioId", usuarioId);
            query.whereEqualTo("comercioId", comercioId);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            object.put("puntos", Double.valueOf(op5TextView.getText().toString()));

                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null) {

                                        ParseObject object = new ParseObject("HistorialPuntos");
                                        object.put("nombreComercio", nombreComercio);
                                        object.put("usuarioId", usuarioId);
                                        object.put("fechaModificacion", fecha);
                                        object.put("fechaCreacion", fecha);
                                        object.put("puntos", Double.valueOf(validarEditText.getText().toString()));
                                        object.put("tipo", "esCanjear");
                                        object.put("comercioId", comercioId);
                                        object.put("correoUsuario", ParseUser.getCurrentUser().getEmail());
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {

                                                if (e ==  null){

                                                    ParseObject object = new ParseObject("PuntosCanjeados");
                                                    object.put("nombreComercio", nombreComercio);
                                                    object.put("comercioId", comercioId);
                                                    object.put("nombreColaborador", nombreCompletoAdmin);
                                                    object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
                                                    object.put("usuarioId", usuarioId);
                                                    object.put("nombreUsuario", usuario);
                                                    object.put("fechaCreacion", fecha);
                                                    object.put("fechaModificacion", fecha);
                                                    object.put("puntosDisponibles", puntosCliente);
                                                    object.put("puntosRestantes", Double.valueOf(op5TextView.getText().toString()));
                                                    object.put("puntosCanjeados", Double.valueOf(validarEditText.getText().toString()));
                                                    object.put("equivalencia", Double.valueOf(validarEditText.getText().toString()));
                                                    object.saveInBackground();

                                                    guardadoExitoso();

                                                } else {

                                                    terminarSppiner();

                                                    Toast.makeText(ValidarPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                                    } else {

                                        terminarSppiner();

                                        Toast.makeText(ValidarPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(ValidarPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else if (contador == 2){

            finish();

        }
    }

    private void buttonDisabled(){

        op10TextView.setText("Canjear puntos");
        op10TextView.setTextColor(Color.WHITE);

        op10TextView.setBackgroundColor(getResources().getColor(R.color.gris_oscuro_pando));
        op10TextView.setEnabled(false);

    }

    private void buttonEnabled(){


        op10TextView.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        op10TextView.setEnabled(true);

    }

    private void changeView(){

        op1ImageView.setVisibility(View.INVISIBLE);
        op1TextView.setVisibility(View.INVISIBLE);
        op9TextView.setVisibility(View.INVISIBLE);
        op10TextView.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet2.applyTo(constraintLayout);

            }
        }, 150);

    }

    private void regresar (){

        if (getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

        TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet1.applyTo(constraintLayout);

        op1ImageView.setVisibility(View.VISIBLE);
        op1TextView.setVisibility(View.VISIBLE);
        op9TextView.setVisibility(View.VISIBLE);
        op10TextView.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_puntos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Canjear puntos");
        contador = 0;
        puntos = 0.0;

        constraintLayout = (ConstraintLayout) findViewById(R.id.validarConstraintLayout);
        constraintSet1.clone(constraintLayout);
        constraintSet2.clone(this, R.layout.detalle_canjear_puntos);

        validarEditText = (ChatEditText) findViewById(R.id.op1ValidarEditText);
        op1ImageView = (ImageView) findViewById(R.id.op1ValidarPImageView);
        op1TextView = (TextView) findViewById(R.id.op1ValidarPTextView);
        op3TextView = (TextView) findViewById(R.id.op3ValidarPTextView);
        op5TextView = (TextView) findViewById(R.id.op5ValidarPTextView);
        op6TextView = (TextView) findViewById(R.id.op6ValidarPTextView);
        op7TextView = (TextView) findViewById(R.id.op7ValidarPTextView);
        op8TextView = (TextView) findViewById(R.id.op8ValidarTextView);
        op9TextView = (TextView) findViewById(R.id.op9ValidarTextView);
        op10TextView = (TextView) findViewById(R.id.op10ValidarTextView);

        Intent intent = getIntent();

        usuario = intent.getStringExtra("usuario");
        puntosCliente = intent.getDoubleExtra("puntosCliente", 0);
        usuarioId = intent.getStringExtra("usuarioId");
        comercioId = intent.getStringExtra("comercioId");
        nombreComercio = intent.getStringExtra("nombreComercio");
        nombreCompletoAdmin = intent.getStringExtra("nombreCompletoAdmin");

        buttonDisabled();

        op1TextView.setText(usuario);
        op3TextView.setText(String.valueOf(puntosCliente));
        op5TextView.setText(String .valueOf(puntosCliente));
        op8TextView.setText("0 mxn");

        int maxLength = 4;
        validarEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        validarEditText.setOnKeyListener(this);
        validarEditText.addTextChangedListener(this);
        validarEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                changeView();

                return false;

            }
        });

        validarEditText.setKeyImeChangeListener(new ChatEditText.KeyImeChange() {
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
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == 0 && keyCode == 66){

            regresar();

        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        buttonDisabled();

        contador = 0;

        String referencia = validarEditText.getText().toString();

        if ( referencia.matches("") || referencia.isEmpty() || referencia.equals(".")) {

            op5TextView.setText(String.valueOf(puntosCliente));
            op8TextView.setText("0 mxn");
            op7TextView.setText("Equivalen a:");
            op8TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

        } else if (Double.valueOf(referencia) <= 0.0){

            op5TextView.setText(String.valueOf(puntosCliente));
            op8TextView.setText("0 mxn");
            op7TextView.setText("Equivalen a:");
            op8TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

        } else {

            puntos = puntosCliente - Double.valueOf(validarEditText.getText().toString());

            if (puntos <= 0){

                op5TextView.setText("Error");
                op7TextView.setText("Error");
                op8TextView.setText("Parece que ingresaste puntos de más");
                op8TextView.setTextColor(Color.RED);

            } else {

                buttonEnabled();

                op7TextView.setText("Equivalen a:");
                op8TextView.setText(validarEditText.getText().toString() + " mxn");
                op8TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                if ((puntos % 1) == 0){

                    op5TextView.setText(String.valueOf(puntos.intValue()));

                } else {

                    op5TextView.setText(String.format("%.2f", puntos));


                }
            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
