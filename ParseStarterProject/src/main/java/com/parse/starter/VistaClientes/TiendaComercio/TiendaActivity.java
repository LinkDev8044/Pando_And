package com.parse.starter.VistaClientes.TiendaComercio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
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
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;

import java.util.ArrayList;
import java.util.List;

public class TiendaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String comercioId;
    String nombreComercio;
    String nombreCompletoCliente;

    ArrayList<String> TITULO = new ArrayList();
    ArrayList<String> TERMINOS = new ArrayList();
    ArrayList<String> PRODUCTOID = new ArrayList();

    ArrayList<Integer> PRECIO = new ArrayList();
    ArrayList<Integer> DISPONIBLES = new ArrayList();

    Double puntosCliente;

    TextView puntosTextView;

    ListView tiendaListView;

    CustomAdapter customAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    ProgressDialog progressDialog;

    private void reloadData(){

        TITULO.clear();
        PRECIO.clear();
        DISPONIBLES.clear();
        TERMINOS.clear();
        PRODUCTOID.clear();

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            puntosCliente = object.getDouble("puntos");
                            nombreCompletoCliente = object.getString("nombreUsuario");

                        }

                    } else {

                        puntosCliente = 0.0;

                    }

                    puntosTextView.setText("Puntos disponibles: " + String.valueOf(puntosCliente));

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosTienda");
                    query.whereEqualTo("comercioId", comercioId);
                    query.whereGreaterThan("cantidadDisponible", 0);
                    query.whereEqualTo("eliminado", false);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                for (ParseObject object : objects){

                                    TITULO.add(object.getString("titulo"));
                                    PRECIO.add(object.getInt("precioPuntos"));
                                    DISPONIBLES.add(object.getInt("cantidadDisponible"));
                                    TERMINOS.add(object.getString("terminosYCondiciones"));
                                    PRODUCTOID.add(object.getObjectId());
                                    nombreComercio = object.getString("nombreComercio");

                                }

                                tiendaListView.setAdapter(customAdapter);

                                swipeRefreshLayout.setRefreshing(false);

                                terminarSppiner();

                            } else {

                                swipeRefreshLayout.setRefreshing(false);

                                terminarSppiner();

                                Toast.makeText(TiendaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

                    Toast.makeText(TiendaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

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
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        comercioId = intent.getStringExtra("comercioId");

        customAdapter = new CustomAdapter();

        reloadData();

        tiendaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), CompraTiendaActivity.class);
                intent.putExtra("tituloSelec", TITULO.get(i));
                intent.putExtra("precioSelec", PRECIO.get(i));
                intent.putExtra("disponiblesSelec", DISPONIBLES.get(i));
                intent.putExtra("terminosSelec", TERMINOS.get(i));
                intent.putExtra("puntosCliente", puntosCliente);
                intent.putExtra("comercioId", comercioId);
                intent.putExtra("productoId", PRODUCTOID.get(i));
                intent.putExtra("nombreComercio", nombreComercio);
                intent.putExtra("nombreCompletoCliente", nombreCompletoCliente);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Tienda");

        puntosTextView = (TextView) findViewById(R.id.puntosClienteTextView);
        tiendaListView = (ListView) findViewById(R.id.tiendaListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.tiendaRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        puntosTextView.setTextColor(getResources().getColor(R.color.verde_Pando));

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onRefresh() {

        reloadData();

    }

    class CustomAdapter extends BaseAdapter implements Adapter{

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getCount() {
            return TITULO.size();
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

                view = mInflater.inflate(R.layout.tienda_cell_1, null);

                TextView tituloTextView = (TextView) view.findViewById(R.id.op1TiendaTextview);
                TextView precioTextView = (TextView) view.findViewById(R.id.op2TiendaTextView);
                TextView disponiblesTextView = (TextView) view.findViewById(R.id.op3TiendaTextView);

                tituloTextView.setText(TITULO.get(i));
                precioTextView.setText(String.valueOf(PRECIO.get(i)));
                disponiblesTextView.setText(String.valueOf(DISPONIBLES.get(i)));

                return view;

            }

            view = mInflater.inflate(R.layout.tienda_cell_1, null);

            TextView tituloTextView = (TextView) view.findViewById(R.id.op1TiendaTextview);
            TextView precioTextView = (TextView) view.findViewById(R.id.op2TiendaTextView);
            TextView disponiblesTextView = (TextView) view.findViewById(R.id.op3TiendaTextView);

            tituloTextView.setText(TITULO.get(i));
            precioTextView.setText(String.valueOf(PRECIO.get(i)));
            disponiblesTextView.setText(String.valueOf(DISPONIBLES.get(i)));

            return view;
        }
    }
}
