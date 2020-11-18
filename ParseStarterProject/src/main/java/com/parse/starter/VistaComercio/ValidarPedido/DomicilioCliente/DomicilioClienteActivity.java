
package com.parse.starter.VistaComercio.ValidarPedido.DomicilioCliente;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.parse.starter.R;

public class DomicilioClienteActivity extends AppCompatActivity {

    String nomUsuarioCom;
    String calle;
    String numeroExterior;
    String colonia;
    String delegacion;
    String codigoPostal;
    String entreCalles;

    TextView op1CalleTextView;
    TextView op2NumTextView;
    TextView op3ColoniaTextView;
    TextView op4DelTextView;
    TextView op5CodigoTextView;
    TextView op6EntreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domicilio_cliente);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        nomUsuarioCom = intent.getStringExtra("nomUsuarioCom");
        calle = intent.getStringExtra("calle");
        numeroExterior = intent.getStringExtra("numeroExterior");
        colonia = intent.getStringExtra("colonia");
        delegacion = intent.getStringExtra("delegacion");
        codigoPostal = intent.getStringExtra("codigoPostal");
        entreCalles = intent.getStringExtra("entreCalles");

        op1CalleTextView = (TextView) findViewById(R.id.op1DomicilioTextView);
        op2NumTextView = (TextView) findViewById(R.id.op2DomicilioTextView);
        op3ColoniaTextView = (TextView) findViewById(R.id.op3DomicilioTextView);
        op4DelTextView = (TextView) findViewById(R.id.op4DomicilioTextView);
        op5CodigoTextView = (TextView) findViewById(R.id.op5DomicilioTextView);
        op6EntreTextView = (TextView) findViewById(R.id.op6DomicilioTextView);

        setTitle(nomUsuarioCom);

        op1CalleTextView.setText(calle);
        op2NumTextView.setText(numeroExterior);
        op3ColoniaTextView.setText(colonia);
        op4DelTextView.setText(delegacion);
        op5CodigoTextView.setText(codigoPostal);
        op6EntreTextView.setText(entreCalles);

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }
}
