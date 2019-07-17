package com.parse.starter.VistaClientes.HistorialPuntos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.parse.starter.R;
import com.parse.starter.VistaClientes.MisCompras.MisComprasActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistorialPuntosActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String comercioId;

    ArrayList<String> TIPO = new ArrayList();
    ArrayList<String> FECHA = new ArrayList();

    ArrayList<Double> PUNTOS = new ArrayList();

    Double puntosCliente;

    TextView puntosTextView;

    ListView historialListView;

    CustomAdapter customAdapter;

    ProgressDialog progressDialog;

    SwipeRefreshLayout swipeRefreshLayout;

    private void reloadData(){

        iniciarSppiner();

        TIPO.clear();
        FECHA.clear();
        PUNTOS.clear();

        customAdapter = new CustomAdapter();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        puntosCliente = object.getDouble("puntos");

                    }

                    puntosTextView.setText("Puntos disponibles: " + String.valueOf(puntosCliente));

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("HistorialPuntos");
                    query.whereEqualTo("comercioId", comercioId);
                    query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                    query.orderByDescending("fechaModificacion");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                for (ParseObject object : objects){

                                    TIPO.add(object.getString("tipo"));
                                    PUNTOS.add(object.getDouble("puntos"));

                                    DateFormat df = new SimpleDateFormat("dd-MMM-yy");
                                    String convertedDate = df.format(object.getDate("fechaModificacion"));
                                    FECHA.add(convertedDate);

                                }

                                historialListView.setAdapter(customAdapter);

                                swipeRefreshLayout.setRefreshing(false);

                                terminarSppiner();

                            } else {

                                terminarSppiner();

                                swipeRefreshLayout.setRefreshing(false);

                                Toast.makeText(HistorialPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    terminarSppiner();

                    swipeRefreshLayout.setRefreshing(false);

                    Toast.makeText(HistorialPuntosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_puntos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Historial");

        puntosTextView = (TextView) findViewById(R.id.puntosHistTextView);
        historialListView = (ListView) findViewById(R.id.historialListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.historialRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        Intent intent = getIntent();

        comercioId = intent.getStringExtra("comercioId");

        reloadData();

    }

    @Override
    public void onRefresh() {

        reloadData();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    class CustomAdapter extends BaseAdapter implements Adapter {

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getCount() {
            return TIPO.size();
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

            LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

            if (view == null){

                view = mInflater.inflate(R.layout.historial_puntos_cell_1, null);

                view.setEnabled(false);

                TextView fechaTextView = (TextView) view.findViewById(R.id.op1HistTextView);
                TextView tipoTextView = (TextView) view.findViewById(R.id.op2HistTextView);
                TextView puntosTextView = (TextView) view.findViewById(R.id.op3HistTextView);

                fechaTextView.setText(FECHA.get(i));

                Log.i("Prueba", TIPO.get(i));

                if (TIPO.get(i).matches("esCanjear")){

                    tipoTextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                    puntosTextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    tipoTextView.setText("Puntos canjeados");
                    puntosTextView.setText("-" + String.valueOf(PUNTOS.get(i)));


                } else if (TIPO.get(i).matches("esEnviar")){

                    tipoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    puntosTextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                    tipoTextView.setText("Puntos recibidos");
                    puntosTextView.setText("+" + String.valueOf(PUNTOS.get(i)));

                } else if (TIPO.get(i).matches("esComprar")){

                    tipoTextView.setTextColor(Color.RED);
                    puntosTextView.setTextColor(Color.RED);

                    tipoTextView.setText("Compra");
                    puntosTextView.setText("-" + String.valueOf(PUNTOS.get(i)));

                }


                return view;

            }

            view = mInflater.inflate(R.layout.historial_puntos_cell_1, null);

            view.setEnabled(false);

            TextView fechaTextView = (TextView) view.findViewById(R.id.op1HistTextView);
            TextView tipoTextView = (TextView) view.findViewById(R.id.op2HistTextView);
            TextView puntosTextView = (TextView) view.findViewById(R.id.op3HistTextView);

            fechaTextView.setText(FECHA.get(i));

            if (TIPO.get(i).matches("esCanjear")){

                tipoTextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                puntosTextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                tipoTextView.setText("Puntos canjeados");
                puntosTextView.setText("-" + String.valueOf(PUNTOS.get(i)));


            } else if (TIPO.get(i).matches("esEnviar")){

                tipoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                puntosTextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                tipoTextView.setText("Puntos recibidos");
                puntosTextView.setText("+" + String.valueOf(PUNTOS.get(i)));

            } else if (TIPO.get(i).matches("esComprar")){

                tipoTextView.setTextColor(Color.RED);
                puntosTextView.setTextColor(Color.RED);

                tipoTextView.setText("Compra");
                puntosTextView.setText("-" + String.valueOf(PUNTOS.get(i)));

            }


            return view;

        }
    }
}
