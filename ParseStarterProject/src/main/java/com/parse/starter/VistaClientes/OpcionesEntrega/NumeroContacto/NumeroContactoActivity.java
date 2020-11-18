package com.parse.starter.VistaClientes.OpcionesEntrega.NumeroContacto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.util.List;

public class NumeroContactoActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    String nombreComCliente;
    String numeroCliente;

    TextView guardarButton;

    EditText numeroEditText;

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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactoCliente");
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        object.put("numeroCliente", numeroEditText.getText().toString());
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

        buttonDisabled();

        numeroEditText.setHint("Escribe aquí tu número...");

        if (numeroCliente.matches("") == false){

            numeroEditText.setText(numeroCliente);
            numeroEditText.setSelection(numeroEditText.getText().length());

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numero_contacto);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(20);
        setTitle("Contacto");

        guardarButton = (TextView) findViewById(R.id.op1NumeroCTextView);
        numeroEditText = (EditText) findViewById(R.id.op1NumeroCEditText);

        Intent intent = getIntent();
        nombreComCliente = intent.getStringExtra("nombreComCliente");
        numeroCliente = intent.getStringExtra("numeroCliente");

        numeroEditText.addTextChangedListener(this);
        numeroEditText.setOnKeyListener(this);

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

        if (numeroEditText.getText().toString().matches("")){

            buttonDisabled();

        } else {

            if (numeroEditText.getText().toString().length() > 9){

                buttonEnabled();

            } else {

                buttonDisabled();

            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }
}
