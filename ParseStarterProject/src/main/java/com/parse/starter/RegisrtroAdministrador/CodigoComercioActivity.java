package com.parse.starter.RegisrtroAdministrador;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;
import com.parse.starter.registro_Usuario.CorreoUserActivity;

import java.util.List;

public class CodigoComercioActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    String codigoComercio;
    String nombreComercio;

    EditText codigoEditText;

    TextView siguienteTextView;

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

    private void checkComercio(){

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comercios");
        query.whereEqualTo("objectId", codigoComercio);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (objects.size() > 0){

                    for (ParseObject object : objects){

                        nombreComercio = object.getString("nombreComercio");

                    }

                    Intent intent = new Intent(getApplicationContext(), CorreoUserActivity.class);
                    intent.putExtra("esRegistroColaborador", true);
                    intent.putExtra("comercioId", codigoComercio);
                    intent.putExtra("nombreComercio", nombreComercio);

                    terminarSppiner();

                    startActivity(intent);

                } else {

                    terminarSppiner();

                    Toast.makeText(CodigoComercioActivity.this, "No encontramos tu codigo en la base de datos. Intentalo de nuevo.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void siguiente(View view){

        checkComercio();

    }

    public void back(View view){

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_comercio);

        getSupportActionBar().hide();
        getWindow().setSoftInputMode(20);

        codigoEditText = (EditText) findViewById(R.id.op1CodComEditText);
        siguienteTextView = (TextView) findViewById(R.id.op1CodComTextView);

        codigoEditText.addTextChangedListener(this);
        codigoEditText.setOnKeyListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        codigoComercio = codigoEditText.getText().toString();

        if (codigoComercio.matches("")){

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

            codigoComercio = codigoEditText.getText().toString();

            if (codigoComercio.matches("")){

                Toast.makeText(this, "Se requiere el código para continuar", Toast.LENGTH_SHORT).show();

            } else {

                checkComercio();

            }

        }
        return false;
    }
}
