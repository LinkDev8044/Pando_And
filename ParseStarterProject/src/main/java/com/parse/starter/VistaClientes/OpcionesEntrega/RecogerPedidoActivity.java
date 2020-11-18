package com.parse.starter.VistaClientes.OpcionesEntrega;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.CarritoPedidos.CarritoPedidosActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class RecogerPedidoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String comercioId;
    String horaGuardar;
    String minInicial;
    String horaInicial;

    Boolean horaSeleccionada;

    List<String> listHoras = new ArrayList<String>();
    List<String> listMinutos = new ArrayList<String>();

    Spinner horaSpinner;
    Spinner minutosSpinner;

    TextView guardarButton;

    Date fecha;

    ProgressDialog progressDialog;

    public void guardar(View view){

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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("activo", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        object.put("opcionEntrega", "Recoger");
                        object.put("hora", horaGuardar);
                        object.put("fechaModificacion", fecha);
                        object.saveInBackground();
                    }

                    terminarSppiner();
                    Intent intent = new Intent(getApplicationContext(), CarritoPedidosActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);


                } else {
                    terminarSppiner();

                }
            }
        });

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

    private void buttonEnabled(){

        guardarButton.setText("Confirmar hora");
        guardarButton.setTextColor(Color.WHITE);
        guardarButton.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        guardarButton.setEnabled(true);

    }

    private void buttonDisabled(){

        guardarButton.setText("Confirmar hora");
        guardarButton.setTextColor(Color.WHITE);
        guardarButton.setBackgroundColor(Color.DKGRAY);
        guardarButton.setEnabled(false);

    }

    private void reloadData(){

        horaGuardar = "";
        minInicial = "00";
        horaInicial = "13";


        for (int i = 1; i <  25; i++){

            listHoras.add(String.valueOf(i));
        }

        listMinutos.add("00");
        listMinutos.add("15");
        listMinutos.add("30");
        listMinutos.add("45");

        ArrayAdapter<String> adapterHoras = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listHoras);
        adapterHoras.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        horaSpinner.setAdapter(adapterHoras);
        horaSpinner.setSelection(12);
        horaSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapterMinutos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listMinutos);
        adapterMinutos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutosSpinner.setAdapter(adapterMinutos);
        //horaSpinner.setSelection(12);
        minutosSpinner.setOnItemSelectedListener(this);

        buttonEnabled();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoger_pedido);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Recoger pedido");

        horaSpinner = (Spinner) findViewById(R.id.op1RecogerPSpinner);
        minutosSpinner = (Spinner) findViewById(R.id.op2RecogerPSpinner);
        guardarButton = (TextView) findViewById(R.id.op1RecogerPTextView);

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");

    }

    @Override
    protected void onStart() {
        super.onStart();

        reloadData();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent == horaSpinner){

            horaInicial = horaSpinner.getItemAtPosition(position).toString();

        }

        if (parent == minutosSpinner){

            minInicial = minutosSpinner.getItemAtPosition(position).toString();

        }

        horaGuardar = horaInicial + ":" + minInicial + " hrs";

        Log.i("Prueba", horaGuardar);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
