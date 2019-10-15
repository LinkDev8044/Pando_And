package com.parse.starter.VistaClientes.TiendaComercio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class CompraTiendaActivity extends AppCompatActivity {

    String tituloSelec;
    String terminosSelec;
    String comercioId;
    String productoIdSelec;
    String nombreComercio;
    String nombreCompletoCliente;

    int precioSelec;
    int disponiblesSelec;
    int contador;

    Double puntosCliente;

    TextView siguienteTextView;
    TextView mensajeTextView;

    ListView compraListView;

    java.util.Date fecha;

    CustomAdapter customAdapter;

    ProgressDialog progressDialog;
    
    public void comprar(View view){

        if (contador == 0) {

            siguienteTextView.setText("CONFIRMAR aquí");

            contador = 1;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (contador == 1){

                        buttonEnabled();

                    }
                }
            }, 5000);

            return;
        }

        if (contador == 1){

            iniciarSppiner();

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

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosTienda");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("objectId", productoIdSelec);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            object.put("comprados", object.getInt("comprados") + 1);
                            object.put("fechaModificacion", fecha);
                            object.put("cantidadDisponible", object.getInt("cantidadDisponible") - 1);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null){

                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
                                        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                        query.whereEqualTo("comercioId", comercioId);
                                        query.setLimit(1);
                                        query.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, ParseException e) {

                                                if (e == null) {

                                                    for (ParseObject object : objects) {

                                                        object.put("fechaModificacion", fecha);
                                                        Double puntosForSave = Double.valueOf(String.format("%.2f", object.getDouble("puntos") - Double.valueOf(precioSelec)));
                                                        object.put("puntos", puntosForSave);
                                                        object.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {

                                                                if (e == null){

                                                                    ParseObject object = new ParseObject("HistorialPuntos");
                                                                    object.put("nombreComercio", nombreComercio);
                                                                    object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                                                    object.put("fechaCreacion", fecha);
                                                                    object.put("fechaModificacion", fecha);
                                                                    object.put("puntos", precioSelec);
                                                                    object.put("tipo", "esComprar");
                                                                    object.put("comercioId", comercioId);
                                                                    object.put("correoUsuario", ParseUser.getCurrentUser().getEmail());
                                                                    object.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {

                                                                            if (e == null){

                                                                                ParseObject object = new ParseObject("ProductosCliente");
                                                                                object.put("nombreComercio", nombreComercio);
                                                                                object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                                                                object.put("fechaCreacion", fecha);
                                                                                object.put("fechaModificacion", fecha);
                                                                                object.put("nombreCompleto", nombreCompletoCliente);
                                                                                object.put("activo", true);
                                                                                object.put("terminos", terminosSelec);
                                                                                object.put("precioPuntos", precioSelec);
                                                                                object.put("comercioId", comercioId);
                                                                                object.put("tituloProducto", tituloSelec);
                                                                                object.put("productoId", productoIdSelec);
                                                                                object.saveInBackground(new SaveCallback() {
                                                                                    @Override
                                                                                    public void done(ParseException e) {

                                                                                        if (e == null){

                                                                                            terminarSppiner();

                                                                                            mensajeTextView.setText("COMPRA EXITOSA\n\nConsulta tus compras en la opción ´Ver mis compras´ debajo de la opción ´Ver tienda´");
                                                                                            siguienteTextView.setText("LISTO");
                                                                                            contador = 2;

                                                                                            //compraListView.setAdapter(customAdapter);

                                                                                            return;

                                                                                        } else {

                                                                                            terminarSppiner();

                                                                                            Toast.makeText(CompraTiendaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    }
                                                                                });

                                                                            } else {

                                                                                terminarSppiner();

                                                                                Toast.makeText(CompraTiendaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }
                                                                    });

                                                                } else {

                                                                    terminarSppiner();

                                                                    Toast.makeText(CompraTiendaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                                    }

                                                } else {

                                                    terminarSppiner();

                                                    Toast.makeText(CompraTiendaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                                    } else {

                                        terminarSppiner();

                                        Toast.makeText(CompraTiendaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(CompraTiendaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }

        if (contador == 2){

            finish();

        }
    }

    private void buttonEnabled(){

        contador = 0;

        siguienteTextView.setText("Comprar️");
        siguienteTextView.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        siguienteTextView.setEnabled(true);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_tienda);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Tienda");

        compraListView = (ListView) findViewById(R.id.compraListView);
        siguienteTextView = (TextView) findViewById(R.id.siguienteCompraTextView);
        mensajeTextView = (TextView) findViewById(R.id.mensajeCompraTextView);

        Intent intent = getIntent();

        tituloSelec = intent.getStringExtra("tituloSelec");
        precioSelec = intent.getIntExtra("precioSelec", 0);
        disponiblesSelec = intent.getIntExtra("disponiblesSelec", 0);
        terminosSelec = intent.getStringExtra("terminosSelec");
        puntosCliente = intent.getDoubleExtra("puntosCliente", 0);
        comercioId = intent.getStringExtra("comercioId");
        productoIdSelec = intent.getStringExtra("productoId");
        nombreComercio = intent.getStringExtra("nombreComercio");
        nombreCompletoCliente = intent.getStringExtra("nombreCompletoCliente");

        customAdapter = new CustomAdapter();

        compraListView.setAdapter(customAdapter);

        if (precioSelec > puntosCliente){

            siguienteTextView.setText("Puntos insuficientes ☹️");
            siguienteTextView.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            siguienteTextView.setEnabled(false);

        } else {

            buttonEnabled();
        }

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
            return 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null){

                view = mInflater.inflate(R.layout.compra_tienda_cell_1, null);
                TextView tituloTextView = (TextView) view.findViewById(R.id.op1CompraTextView);
                TextView precioTextView = (TextView) view.findViewById(R.id.op2CompraTextView);
                TextView disponiblesTextView = (TextView) view.findViewById(R.id.op3CompraTextView);
                TextView terminosTextView = (TextView) view.findViewById(R.id.op4CompraTextView);

                tituloTextView.setText(tituloSelec);
                precioTextView.setText(String.valueOf(precioSelec));
                disponiblesTextView.setText(String.valueOf(disponiblesSelec));
                terminosTextView.setText(terminosSelec);

                return view;

            }

            view = mInflater.inflate(R.layout.compra_tienda_cell_1, null);
            TextView tituloTextView = (TextView) view.findViewById(R.id.op1CompraTextView);
            TextView precioTextView = (TextView) view.findViewById(R.id.op2CompraTextView);
            TextView disponiblesTextView = (TextView) view.findViewById(R.id.op3CompraTextView);
            TextView terminosTextView = (TextView) view.findViewById(R.id.op4CompraTextView);

            tituloTextView.setText(tituloSelec);
            precioTextView.setText(String.valueOf(precioSelec));
            disponiblesTextView.setText(String.valueOf(disponiblesSelec));
            terminosTextView.setText(terminosSelec);

            return view;

        }
    }
}
