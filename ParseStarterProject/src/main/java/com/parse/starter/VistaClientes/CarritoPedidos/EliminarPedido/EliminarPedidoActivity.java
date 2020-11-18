package com.parse.starter.VistaClientes.CarritoPedidos.EliminarPedido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class EliminarPedidoActivity extends AppCompatActivity {

    String pedidoIdSelec;
    String nomPlatilloSelec;
    String complementosSelec;
    String comentariosSelec;
    String comercioId;

    int cantidadSelec;
    int contador;

    Double subTotalSelec;

    Date fecha;

    TextView op1CantidadTextView;
    TextView op2NombreTextView;
    TextView op3PrecioTextView;
    TextView op4ComTextView;
    TextView op5eliminarButton;

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

    public void eliminar(View view){

        if (contador == 0){

            op5eliminarButton.setText("Confirmar");
            op5eliminarButton.setBackgroundColor(Color.RED);
            op5eliminarButton.setTextColor(Color.WHITE);
            op5eliminarButton.setEnabled(true);

            contador += 1;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (contador == 1){

                        eliminarButtonEnabled();

                    }

                }
            }, 5000);

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

            ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("objectId", pedidoIdSelec);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        contador += 1;

                        for (ParseObject object : objects){

                            object.put("fechaModificacion", fecha);
                            object.put("activo", false);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null){

                                        terminarSppiner();
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
    }

    private void eliminarButtonEnabled(){

        contador = 0;

        op5eliminarButton.setText("Eliminar");
        op5eliminarButton.setBackgroundColor(Color.RED);
        op5eliminarButton.setTextColor(Color.WHITE);
        op5eliminarButton.setEnabled(true);

    }

    private void reloadData(){

        eliminarButtonEnabled();

        contador = 0;

        op1CantidadTextView.setText(String.valueOf(cantidadSelec));
        op2NombreTextView.setText(nomPlatilloSelec);
        op3PrecioTextView.setText("$" + String.valueOf(subTotalSelec));

        if (complementosSelec.matches("")){

            if (comentariosSelec.matches("") == false){

                op4ComTextView.setText(comentariosSelec);

            }

        } else {

            if (comentariosSelec.matches("") == false){

                op4ComTextView.setText(complementosSelec + "\n" + comentariosSelec);

            } else {

                op4ComTextView.setText(complementosSelec);

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_pedido);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        pedidoIdSelec = intent.getStringExtra("pedidoIdSelec");
        nomPlatilloSelec = intent.getStringExtra("nomPlatilloSelec");
        complementosSelec = intent.getStringExtra("complementosSelec");
        comentariosSelec = intent.getStringExtra("comentariosSelec");
        cantidadSelec = intent.getIntExtra("cantidadSelec", 0);
        subTotalSelec = intent.getDoubleExtra("subTotalSelec", 0);
        comercioId = intent.getStringExtra("comercioId");

        op1CantidadTextView = findViewById(R.id.op1EliminarPTextView);
        op2NombreTextView = findViewById(R.id.op2EliminarPTextView);
        op3PrecioTextView = findViewById(R.id.op3EliminarPTextView);
        op4ComTextView = findViewById(R.id.op4EliminarPTextView);
        op5eliminarButton = findViewById(R.id.op5EliminarPTextView);

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
}
