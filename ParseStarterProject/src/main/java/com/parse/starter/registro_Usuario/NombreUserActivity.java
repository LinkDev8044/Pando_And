package com.parse.starter.registro_Usuario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.starter.R;

public class NombreUserActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener {

    EditText nombreEditText;
    EditText apellidoEditText;

    TextView siguienteTextView;

    String nombreUsuario;
    String apellidoUsuario;

    public void siguiente(View view){

        Intent intent = new Intent(getApplicationContext(), SexoActivity.class);
        intent.putExtra("nombreUsuario", nombreUsuario);
        intent.putExtra("apellidoUsuario", apellidoUsuario);
        startActivity(intent);

    }

    public void back(View view){

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombre_user);

        getSupportActionBar().hide();

        getWindow().setSoftInputMode(20);

        nombreEditText = (EditText) findViewById(R.id.op1_Reg_EditText);
        apellidoEditText = (EditText) findViewById(R.id.op2_Reg_EditText);
        siguienteTextView = (TextView) findViewById(R.id.sig_nom_TextView);

        nombreEditText.addTextChangedListener(this);
        apellidoEditText.addTextChangedListener(this);
        apellidoEditText.setOnKeyListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        nombreUsuario = nombreEditText.getText().toString();
        apellidoUsuario = apellidoEditText.getText().toString();

        if (nombreUsuario.matches("") || apellidoUsuario.matches("")){

            siguienteTextView.setVisibility(View.INVISIBLE);

        } else {

            siguienteTextView.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (keyEvent.getAction() == 0 && i == 66) {

            nombreUsuario = nombreEditText.getText().toString();
            apellidoUsuario = apellidoEditText.getText().toString();

            if (nombreUsuario.matches("") || apellidoUsuario.matches("")){

                Toast.makeText(this, "Espera - se requiere llenar ambos campos para continuar", Toast.LENGTH_SHORT).show();

            } else {

                Intent intent = new Intent(getApplicationContext(), SexoActivity.class);
                intent.putExtra("nombreUsuario", nombreUsuario);
                intent.putExtra("apellidoUsuario", apellidoUsuario);
                startActivity(intent);

            }

            return true;

        } else {

            return false;

        }
    }
}
