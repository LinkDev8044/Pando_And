package com.parse.starter.VistaClientes.CarritoPedidos.ConfirmacionPedido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;
import com.parse.starter.VistaClientes.VerNewMenu.VerNewMenuActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class ConfirmacionPedidoActivity extends AppCompatActivity {

    String comercioId;
    String numeroWhats;
    String nombreUsuario;
    String nombreComercio;

    Date fecha;

    TextView confirmacionButton;

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

    private void enviarWhats(){

        terminarSppiner();

        String phoneNumberWithCountryCode = "521" + numeroWhats;
        String message = "Hola " + nombreComercio + ", soy " + nombreUsuario + ".\nHice un pedido por la app Pando.";

        startActivity(
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse(
                                String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
                        )
                )
        );

    }

    public void confirmar(View view){

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

                        object.put("whatsEnviado", true);
                        object.saveInBackground();

                    }

                    enviarWhats();

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    private void reloadData(){

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("activo", true);
        query.whereEqualTo("whatsEnviado", true);
        query.orderByAscending("fechaCreacion");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        terminarSppiner();
                        Intent intent = new Intent(getApplicationContext(), DescripcionComercioActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);

                    } else {

                        terminarSppiner();

                    }

                } else {

                    terminarSppiner();

                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_pedido);

        getSupportActionBar().hide();

        confirmacionButton = (TextView) findViewById(R.id.op1ConfirmacionPTextView);

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");
        numeroWhats = intent.getStringExtra("numeroWhats");
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        nombreComercio = intent.getStringExtra("nombreComercio");

    }

    @Override
    protected void onStart() {
        super.onStart();

        confirmacionButton.setText("OK");
        confirmacionButton.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        confirmacionButton.setTextColor(Color.WHITE);
        confirmacionButton.setEnabled(true);

        reloadData();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
