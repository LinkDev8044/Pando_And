package com.parse.starter.VistaClientes.OpcionesEntrega.DetalleDomicilio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

public class DomicilioGeneralActivity extends AppCompatActivity {

    String nombreComCliente;
    String comercioId;
    String calle;
    String numExterior;
    String colonia;
    String delegacion;
    String codigoPostal;
    String entreCalles;

    String[] opcionesDomicilio = {"Calle", "Num exterior e interior", "Colonia", "Delegación", "Código postal", "Entre calles..."};
    String[] opcionesDescripcion = {"Agrega aquí tu calle", "Agrega aquí tu numero exterior e interior", "Agrega aquí tu colonia", "Agrega aquí tu delegación", "Agrega aquí tu código postal", "Agrega aquí entre que calles se encuentra tu domicilio"};

    ArrayList<String> domicilioServidor = new ArrayList();

    CustomAdapter customAdapter;

    ListView domicilioListView;

   TextView domicilioButton;

    Date fecha;

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

   public void domicilioCompleto(View view){

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

                       object.put("opcionEntrega", "Domicilio");
                       object.put("hora", "");
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

    private void buttonEnabled(){

        domicilioButton.setText("Utilizar esta dirección");
        domicilioButton.setTextColor(Color.WHITE);
        domicilioButton.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        domicilioButton.setEnabled(true);

    }

   private void buttonDisabled(){

       domicilioButton.setText("Utilizar esta dirección");
       domicilioButton.setTextColor(Color.WHITE);
       domicilioButton.setBackgroundColor(Color.DKGRAY);
       domicilioButton.setEnabled(false);

   }

   private void crearDomicilio(){

        ParseObject object = new ParseObject("DomicilioCliente");
        object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
        object.put("nombreCliente", nombreComCliente);
        object.put("calle", "");
        object.put("numExtInt", "");
        object.put("colonia", "");
        object.put("delegacion", "");
        object.put("codigoPostal", "");
        object.put("entreCalles", "");
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    terminarSppiner();
                    domicilioListView.setAdapter(customAdapter);

                } else {

                    terminarSppiner();

                }
            }
        });

   }

   private void reloadData(){

        domicilioServidor.clear();
        calle = "";
       numExterior = "";
       colonia = "";
       delegacion = "";
       codigoPostal = "";
       entreCalles = "";

        iniciarSppiner();

       ParseQuery<ParseObject> query = ParseQuery.getQuery("DomicilioCliente");
       query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
       query.setLimit(1);
       query.findInBackground(new FindCallback<ParseObject>() {
           @Override
           public void done(List<ParseObject> objects, ParseException e) {

               if (e == null){

                   if (objects.size() > 0){

                       for (ParseObject object : objects){

                           calle = object.getString("calle");
                           domicilioServidor.add(calle);
                           numExterior = object.getString("numExtInt");
                           domicilioServidor.add(numExterior);
                           colonia = object.getString("colonia");
                           domicilioServidor.add(colonia);
                           delegacion = object.getString("delegacion");
                           domicilioServidor.add(delegacion);
                           codigoPostal = object.getString("codigoPostal");
                           domicilioServidor.add(codigoPostal);
                           entreCalles = object.getString("entreCalles");
                           domicilioServidor.add(entreCalles);

                       }

                       if (domicilioServidor.contains("")){

                           buttonDisabled();

                       } else {

                           buttonEnabled();

                       }

                       terminarSppiner();
                       domicilioListView.setAdapter(customAdapter);

                   } else {

                       crearDomicilio();
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
        setContentView(R.layout.activity_domicilio_general);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Mi domicilio");

        domicilioListView = (ListView) findViewById(R.id.domicilioListView);
        domicilioButton = (TextView) findViewById(R.id.op1DomicilioGTextView);

        Intent intent = getIntent();
        nombreComCliente = intent.getStringExtra("nombreComCliente");
        comercioId = intent.getStringExtra("comercioId");

        customAdapter = new CustomAdapter();

        domicilioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent1 = new Intent(getApplicationContext(), DetalleDomicilioActivity.class);
                intent1.putExtra("tituloPregunta", opcionesDomicilio[position]);
                intent1.putExtra("opcionSelec", position);
                intent1.putExtra("domicilioSelec", domicilioServidor.get(position));
                startActivity(intent1);

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

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getCount() {
            return 6;
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

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.domicilio_general_cell_1, null);

                TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DomicilioGC1TextView);
                TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DomicilioGC1TextView);

                op1TextView.setText(opcionesDomicilio[position]);
                op2TextView.setTextColor(Color.GRAY);
                op2TextView.setText(opcionesDescripcion[position]);

                if (domicilioServidor.get(position).matches("") == false) {

                    op2TextView.setText(domicilioServidor.get(position));
                    op2TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));

                }

                return convertView;

            } else {

                convertView = mInflater.inflate(R.layout.domicilio_general_cell_1, null);

                TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DomicilioGC1TextView);
                TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DomicilioGC1TextView);

                op1TextView.setText(opcionesDomicilio[position]);
                op2TextView.setTextColor(Color.GRAY);
                op2TextView.setText(opcionesDescripcion[position]);

                if (domicilioServidor.get(position).matches("") == false) {

                    op2TextView.setText(domicilioServidor.get(position));
                    op2TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));

                }

                return convertView;

            }

        }
    }
}
