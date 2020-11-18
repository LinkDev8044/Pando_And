package com.parse.starter.VistaClientes.OpcionesEntrega;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.OpcionesEntrega.DetalleDomicilio.DomicilioGeneralActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class OpcionesEntregaActivity extends AppCompatActivity {

    String nombreComCliente;
    String comercioId;
    String opcionEntregaSelec;

    Boolean envioDisponible;

    String[] opcionesEntrega = {"A domicilio", "Recoger pedido", "Consumo en restaurante"};
    String[] opcionesNoDisp = {"A domicilio (no disponible)", "Recoger pedido", "Consumo en restaurante (no disponible)"};

    int[] imagenesOpciones = {R.drawable.food_delivery, R.drawable.take_away, R.drawable.spoon};

    Date fecha;

    CustomAdapter customAdapter;

    ListView opcionesListView;

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

    private void reloadData(){

        opcionEntregaSelec = "";

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("activo", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){


                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            opcionEntregaSelec = object.getString("opcionEntrega");

                        }

                        terminarSppiner();
                        opcionesListView.setAdapter(customAdapter);

                    } else {

                        terminarSppiner();
                        opcionesListView.setAdapter(customAdapter);
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
        setContentView(R.layout.activity_opciones_entrega);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Opciones entrega");

        opcionesListView = (ListView) findViewById(R.id.opcionesEListView);

        Intent intent = getIntent();
        nombreComCliente = intent.getStringExtra("nombreComCliente");
        comercioId = intent.getStringExtra("comercioId");
        envioDisponible = intent.getBooleanExtra("envioDisponible", false);

        customAdapter = new CustomAdapter();

        opcionesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){

                    if (envioDisponible) {

                        Intent intent = new Intent(getApplicationContext(), DomicilioGeneralActivity.class);
                        intent.putExtra("nombreComCliente", nombreComCliente);
                        intent.putExtra("comercioId", comercioId);
                        startActivity(intent);

                    }

                } else if (position == 1){

                    Intent intent = new Intent(getApplicationContext(), RecogerPedidoActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                } else if (position == 2){

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
                        fecha = dateFormat.parse(valueOf4 + "/" + valueOf3 + "/" + valueOf2 + " " + valueOf + ":" + valueOf5 + ":" + valueOf6);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                    iniciarSppiner();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
                    query.whereEqualTo("comercioId",  comercioId);
                    query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                    query.whereEqualTo("activo", true);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                for (ParseObject object : objects){

                                    object.put("opcionEntrega", "Restaurante");
                                    object.put("hora", "");
                                    object.put("fechaModificacion", fecha);
                                    object.saveInBackground();

                                }

                                terminarSppiner();
                                finish();

                            } else {

                                terminarSppiner();
                            }
                        }
                    });

                }
            }
        });

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

    class CustomAdapter extends BaseAdapter implements Adapter{


        @Override
        public int getCount() {
            return opcionesEntrega.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

            convertView = mInflater.inflate(R.layout.general_una_opcion_con__imagen_flecha, null);

            convertView.setEnabled(false);

            TextView op1TextView = (TextView) convertView.findViewById(R.id.op1General1oifTextView);
            ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1General1oifImageView);

            op1ImageView.setImageResource(imagenesOpciones[position]);
            op1TextView.setText(opcionesEntrega[position]);

            if (position == 0){

                if (envioDisponible == false){

                    op1TextView.setText(opcionesNoDisp[position]);

                }
            }

            if (opcionEntregaSelec.matches("Domicilio")){

                if (position == 0){

                    op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                }

            } else if (opcionEntregaSelec.matches("Recoger")){

                if (position == 1){

                    op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                }

            } else if (opcionEntregaSelec.matches("Restaurante")){

                if (position == 2){

                    op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                }

            }

            return convertView;
        }
    }

}
