package com.parse.starter.VistaClientes.OpcionesEntrega.DetalleDomicilio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.util.List;

public class DetalleDomicilioActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    String tituloPregunta;
    String domicilioSelec;

    int opcionSelec;

    TextView tituloPreguntaTextView;
    TextView guardarButton;

    EditText domicilioEditText;

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

    private void guardar(){

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DomicilioCliente");
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        if (opcionSelec == 0){

                            object.put("calle", domicilioEditText.getText().toString());

                        } else if (opcionSelec == 1){

                            object.put("numExtInt", domicilioEditText.getText().toString());

                        } else if (opcionSelec == 2){

                            object.put("colonia", domicilioEditText.getText().toString());

                        } else if (opcionSelec == 3){

                            object.put("delegacion", domicilioEditText.getText().toString());

                        } else if (opcionSelec == 4){

                            object.put("codigoPostal", domicilioEditText.getText().toString());

                        } else if (opcionSelec == 5){

                            object.put("entreCalles", domicilioEditText.getText().toString());

                        }

                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null){

                                    terminarSppiner();
                                    View view = getCurrentFocus();
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                                    finish();

                                } else {

                                    terminarSppiner();

                                }
                            }
                        });

                    }

                } else {

                    terminarSppiner();
                }
            }
        });

    }

    public void irAGuardar(View view){

        guardar();

    }

    private void buttonEnabled(){

        guardarButton.setText("Guardar");
        guardarButton.setTextColor(Color.WHITE);
        guardarButton.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        guardarButton.setEnabled(true);

    }

    private void buttonDisabled(){

        guardarButton.setText("Guardar");
        guardarButton.setTextColor(Color.WHITE);
        guardarButton.setBackgroundColor(Color.DKGRAY);
        guardarButton.setEnabled(false);

    }

    private void reloadData(){



        tituloPreguntaTextView.setText(tituloPregunta);

        if (opcionSelec == 5){

            domicilioEditText.setHint("Escribe aquí entre que calles estas...");

        } else {

            domicilioEditText.setHint("Escribe tu " + tituloPregunta + " aquí...");

        }

        if (domicilioSelec.matches("") == false){

            domicilioEditText.setText(domicilioSelec);
            domicilioEditText.setSelection(domicilioEditText.getText().length());

        }

        buttonDisabled();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_domicilio);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(20);

        Intent intent = getIntent();
        tituloPregunta = intent.getStringExtra("tituloPregunta");
        domicilioSelec = intent.getStringExtra("domicilioSelec");
        opcionSelec = intent.getIntExtra("opcionSelec", 0);

        tituloPreguntaTextView = (TextView) findViewById(R.id.op1DetalleDTextView);
        guardarButton = (TextView) findViewById(R.id.op2DetalleDTextView);
        domicilioEditText = (EditText) findViewById(R.id.op1DetalleDEditText);

        domicilioEditText.addTextChangedListener(this);
        domicilioEditText.setOnKeyListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        reloadData();

    }

    @Override
    public boolean onSupportNavigateUp() {

        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        finish();
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (domicilioEditText.getText().toString().matches("")){

            buttonDisabled();

        } else {

            buttonEnabled();

        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == 0 && keyCode == 66) {

            if (domicilioEditText.getText().toString().matches("")){

                Toast.makeText(this, "Espera - se requiere llenar el campo para continuar", Toast.LENGTH_SHORT).show();

            } else {

                guardar();

            }

            return true;

        } else {

            return false;

        }


    }
}
