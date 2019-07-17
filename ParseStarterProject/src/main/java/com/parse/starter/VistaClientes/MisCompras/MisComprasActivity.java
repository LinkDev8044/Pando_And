package com.parse.starter.VistaClientes.MisCompras;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
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
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MisComprasActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String comercioId;

    ArrayList<String> TITULO = new ArrayList();
    ArrayList<String> TERMINOS = new ArrayList();
    ArrayList<String> FECHA = new ArrayList();

    ArrayList<Integer> PRECIO = new ArrayList();

    ArrayList<Boolean> ACTIVO = new ArrayList();

    ListView misComprasListView;

    SwipeRefreshLayout swipeRefreshLayout;

    CustomAdapter customAdapter;

    ProgressDialog progressDialog;

    private void reloadData(){

        TITULO.clear();
        TERMINOS.clear();
        PRECIO.clear();
        ACTIVO.clear();
        FECHA.clear();

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.orderByDescending("activo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        TITULO.add(object.getString("tituloProducto"));
                        ACTIVO.add(object.getBoolean("activo"));
                        PRECIO.add(object.getInt("precioPuntos"));
                        TERMINOS.add(object.getString("terminos"));

                        DateFormat df = new SimpleDateFormat("dd-MMM-yy");
                        String convertedDate = df.format(object.getDate("fechaModificacion"));
                        FECHA.add(convertedDate);

                    }

                    misComprasListView.setAdapter(customAdapter);

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

                } else {

                    terminarSppiner();

                    swipeRefreshLayout.setRefreshing(false);

                    Toast.makeText(MisComprasActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.activity_mis_compras);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Mis compras");

        misComprasListView = (ListView) findViewById(R.id.misComprasListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.misComprasRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);

        customAdapter = new CustomAdapter();
        Intent intent = getIntent();

        comercioId = intent.getStringExtra("comercioId");

        reloadData();

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

                view = mInflater.inflate(R.layout.mis_compras_cell_1, null);

                TextView tituloTextView = (TextView) view.findViewById(R.id.op1MisCompTextView);
                TextView canjeadoTextView = (TextView) view.findViewById(R.id.op2MisCompTextView);
                TextView fechaTextView = (TextView) view.findViewById(R.id.op3MisCompTextView);
                TextView precioTextView = (TextView) view.findViewById(R.id.op4MisCompTextView);
                TextView terminosTextView = (TextView) view.findViewById(R.id.op5MisCompTextView);

                tituloTextView.setText(TITULO.get(i));
                precioTextView.setText(String.valueOf(PRECIO.get(i)));
                terminosTextView.setText(TERMINOS.get(i));
                fechaTextView.setText(FECHA.get(i));

                if (ACTIVO.get(i)){

                    canjeadoTextView.setText("Si");
                    canjeadoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));


                } else {

                    canjeadoTextView.setText("No");
                    canjeadoTextView.setTextColor(Color.RED);

                }

                return view;

            }

            view = mInflater.inflate(R.layout.mis_compras_cell_1, null);

            TextView tituloTextView = (TextView) view.findViewById(R.id.op1MisCompTextView);
            TextView canjeadoTextView = (TextView) view.findViewById(R.id.op2MisCompTextView);
            TextView fechaTextView = (TextView) view.findViewById(R.id.op3MisCompTextView);
            TextView precioTextView = (TextView) view.findViewById(R.id.op4MisCompTextView);
            TextView terminosTextView = (TextView) view.findViewById(R.id.op5MisCompTextView);

            tituloTextView.setText(TITULO.get(i));
            precioTextView.setText(String.valueOf(PRECIO.get(i)));
            terminosTextView.setText(TERMINOS.get(i));
            fechaTextView.setText(FECHA.get(i));

            if (ACTIVO.get(i)){

                canjeadoTextView.setText("Si");
                canjeadoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));


            } else {

                canjeadoTextView.setText("No");
                canjeadoTextView.setTextColor(Color.RED);

            }

            return view;
        }
    }
}
