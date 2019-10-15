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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;

public class ContrasenaActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    String contrasena;
    String confirmacion;
    String correoUsuario;
    String nombreUsuario;
    String apellidoUsuario;

    Boolean isMan;

    java.util.Date fechaNacimiento = new java.util.Date();

    TextView siguienteTextView;

    EditText contrasena1EditText;
    EditText contrasena2EditText;

    ProgressDialog progressDialog;

    private void goToInfoBienvenida(){

        terminarSppiner();
        Intent intent = new Intent(getApplicationContext(), InfoInicialActivity.class);
        startActivity(intent);

    }

    private void registroUsuario(){

        Log.i("Prueba", "Que");

        iniciarSppiner();
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

                Log.i("Prueba", "Aqui");

                if (e == null){

                    Log.i("Prueba", "Pasa");

                    goToInfoBienvenida();

                } else {

                    terminarSppiner();

                    Toast.makeText(ContrasenaActivity.this, "Parece que hubo un error - Intena de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void siguiente(View view){

        if (contrasena.matches(confirmacion)){

            registroUsuario();

        } else {

            Toast.makeText(this, "Espera - la contraseña y la confirmación son diferentes", Toast.LENGTH_SHORT).show();

            return;

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

        contrasena1EditText.addTextChangedListener(this);
        contrasena2EditText.addTextChangedListener(this);
        contrasena2EditText.setOnKeyListener(this);

        Intent intent = getIntent();
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        apellidoUsuario = intent.getStringExtra("apellidoUsuario");
        isMan = Boolean.valueOf(intent.getBooleanExtra("isMan", false));
        fechaNacimiento.setTime(intent.getLongExtra("fechaNacimiento", -1));
        correoUsuario = intent.getStringExtra("correoUsuario");

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        contrasena = contrasena1EditText.getText().toString();
        confirmacion = contrasena2EditText.getText().toString();

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

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == 66 && keyEvent.getAction() == 0){

            contrasena = contrasena1EditText.getText().toString();
            confirmacion = contrasena2EditText.getText().toString();

            if (contrasena.matches("") || confirmacion.matches("")){

                Toast.makeText(this, "Espera - se requiere llenar ambos campos", Toast.LENGTH_SHORT).show();

            } else {

                if (contrasena.matches(confirmacion)){

                    registroUsuario();

                } else {

                    Toast.makeText(this, "Espera - la contraseña y la confirmación son diferentes", Toast.LENGTH_SHORT).show();

                }
            }
        } else {

            return false;

        }
        return false;
    }
}
