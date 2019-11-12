package com.parse.starter.Inicio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
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
import com.parse.starter.R;
import com.parse.starter.VistaClientes.ClienteActivity;

import java.util.List;

public class LogInActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    String correo;
    String contrasena;

    EditText correoEditText;
    EditText contrasenaEditText;

    TextView siguienteTextView;

    ProgressDialog progressDialog;

    private void goToUsuario(){

        startActivity(new Intent(getApplicationContext(), ClienteActivity.class));

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

    public void validarCorreo(){

        if (isValidEmail(correo)){

            iniciarSppiner();

            ParseUser.logInInBackground(correo, contrasena, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if (e == null){

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestasAplicadas");
                        query.whereEqualTo("colaboradorId", ParseUser.getCurrentUser().getObjectId());
                        query.setLimit(1);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {

                                if (objects.size() > 0){

                                    Intent intent = new Intent(getApplicationContext(), SeleccionPerfilActivity.class);
                                    startActivity(intent);

                                } else {

                                    goToUsuario();

                                }

                                terminarSppiner();

                                return;

                            }
                        });

                    } else {

                        terminarSppiner();

                        Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else {

            Toast.makeText(this, "Espera - no parece un correo", Toast.LENGTH_SHORT).show();
        }
    }

    public void siguiente(View view){

        validarCorreo();

    }

    public static boolean isValidEmail(CharSequence paramCharSequence)
    {
        if (TextUtils.isEmpty(paramCharSequence)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(paramCharSequence).matches();
    }

    public void back(View view){

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        getSupportActionBar().hide();

        getWindow().setSoftInputMode(20);

        correoEditText = (EditText) findViewById(R.id.correoLogInEditText);
        contrasenaEditText = (EditText) findViewById(R.id.contraLogInEditText);
        siguienteTextView = (TextView) findViewById(R.id.sigLogInTextView);

        correoEditText.addTextChangedListener(this);
        contrasenaEditText.addTextChangedListener(this);
        contrasenaEditText.setOnKeyListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        correo = correoEditText.getText().toString();
        contrasena = contrasenaEditText.getText().toString();

        if (correo.matches("") || contrasena.matches("")){

            siguienteTextView.setVisibility(View.INVISIBLE);
            return;

        }

        if (isValidEmail(correo)){

            siguienteTextView.setVisibility(View.VISIBLE);

            return;
        }

        siguienteTextView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == 66 && keyEvent.getAction() == 0){

            correo = correoEditText.getText().toString();
            contrasena = contrasenaEditText.getText().toString();

            if (correo.matches("") || contrasena.matches("")){

                Toast.makeText(this, "Espera - se requiere llenar ambos campos", Toast.LENGTH_SHORT).show();

            } else {

                validarCorreo();

            }
        }
        return false;
    }
}
