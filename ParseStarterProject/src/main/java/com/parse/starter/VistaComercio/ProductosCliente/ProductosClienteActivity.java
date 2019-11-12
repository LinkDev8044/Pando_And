package com.parse.starter.VistaComercio.ProductosCliente;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;
import com.parse.starter.VistaComercio.EnviarEncuesta.EnviarEncuestaActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductosClienteActivity extends AppCompatActivity {

    String usuario;
    String comercioId;
    String usuarioId;

    ArrayList<String> tituloProductoArray = new ArrayList();
    ArrayList<String> terminosArray = new ArrayList();
    ArrayList<String> productoIdTiendaArray = new ArrayList();
    ArrayList<String> productoIdClienteArray = new ArrayList();
    ArrayList<String> fechaCompraArray = new ArrayList();

    ArrayList<Boolean> productoActivoArray = new ArrayList();
    ArrayList<Integer> precioPuntosArray = new ArrayList();

    ListView productosListView;

    CustomAdapter customAdapter;

    ProgressDialog progressDialog;

    private void reloadData(){

        iniciarSppiner();

        customAdapter = new CustomAdapter();

        tituloProductoArray.clear();
        productoActivoArray.clear();
        precioPuntosArray.clear();
        terminosArray.clear();
        productoIdTiendaArray.clear();
        productoIdClienteArray.clear();
        fechaCompraArray.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", usuarioId);
        query.orderByDescending("activo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        tituloProductoArray.add(object.getString("tituloProducto"));
                        productoActivoArray.add(object.getBoolean("activo"));
                        precioPuntosArray.add(object.getInt("precioPuntos"));
                        terminosArray.add(object.getString("terminos"));
                        productoIdTiendaArray.add(object.getString("productoId"));
                        productoIdClienteArray.add(object.getObjectId());

                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                        String convertedDate = dateFormat.format(object.getDate("fechaModificacion"));
                        fechaCompraArray.add(convertedDate);

                    }

                    productosListView.setAdapter(customAdapter);

                    terminarSppiner();

                } else {

                    terminarSppiner();

                    Toast.makeText(ProductosClienteActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.activity_productos_cliente);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        usuario = intent.getStringExtra("usuario");
        comercioId = intent.getStringExtra("comercioId");
        usuarioId = intent.getStringExtra("usuarioId");

        setTitle(usuario);

        productosListView = (ListView) findViewById(R.id.productosListView);

        productosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), CanjearProductosActivity.class);
                intent.putExtra("tituloSelec", tituloProductoArray.get(position));
                intent.putExtra("fechaSelec", fechaCompraArray.get(position));
                intent.putExtra("precioSelec", precioPuntosArray.get(position));
                intent.putExtra("terminosSelec", terminosArray.get(position));
                intent.putExtra("activoSelec", productoActivoArray.get(position));
                intent.putExtra("comercioId", comercioId);
                intent.putExtra("usuarioId", usuarioId);
                intent.putExtra("productoIdClienteSelec", productoIdClienteArray.get(position));
                intent.putExtra("productoIdTiendaSelec", productoIdTiendaArray.get(position));
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {

        reloadData();

        super.onStart();
    }

    class CustomAdapter extends BaseAdapter implements Adapter{
        @Override
        public int getCount() {
            return tituloProductoArray.size();
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

            if (convertView == null){

                convertView = mInflater.inflate(R.layout.productos_cliente_cell_1, null);

                TextView op1TextView = (TextView) convertView.findViewById(R.id.op1ProdCteTextView);
                TextView op2TextView = (TextView) convertView.findViewById(R.id.op2ProdCteTextView);
                TextView op3TextView = (TextView) convertView.findViewById(R.id.op3ProdCteTextView);
                TextView op4TextView = (TextView) convertView.findViewById(R.id.op4ProdCteTextView);
                TextView op5TextView = (TextView) convertView.findViewById(R.id.op5ProdCteTextView);
                TextView op6TextView = (TextView) convertView.findViewById(R.id.op6ProdCteTextView);
                ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1ProdCteImageView);

                op1TextView.setText(tituloProductoArray.get(position));
                op2TextView.setText("Disponible para canjear");
                op3TextView.setText("Fecha de compra");
                op4TextView.setText(fechaCompraArray.get(position));
                op4TextView.setTextColor(Color.BLACK);
                op5TextView.setText("Precio en puntos");
                op6TextView.setText(String.valueOf(precioPuntosArray.get(position)));
                op6TextView.setTextColor(Color.BLACK);

                if (productoActivoArray.get(position)){

                    op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op1ImageView.setImageResource(R.drawable.success);

                } else {

                    op1TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                    op1ImageView.setImageResource(R.drawable.nop);

                }

                return convertView;

            } else {

                convertView = mInflater.inflate(R.layout.productos_cliente_cell_1, null);

                TextView op1TextView = (TextView) convertView.findViewById(R.id.op1ProdCteTextView);
                TextView op2TextView = (TextView) convertView.findViewById(R.id.op2ProdCteTextView);
                TextView op3TextView = (TextView) convertView.findViewById(R.id.op3ProdCteTextView);
                TextView op4TextView = (TextView) convertView.findViewById(R.id.op4ProdCteTextView);
                TextView op5TextView = (TextView) convertView.findViewById(R.id.op5ProdCteTextView);
                TextView op6TextView = (TextView) convertView.findViewById(R.id.op6ProdCteTextView);
                ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1ProdCteImageView);

                op1TextView.setText(tituloProductoArray.get(position));
                op2TextView.setText("Disponible para canjear");
                op3TextView.setText("Fecha de compra");
                op4TextView.setText(fechaCompraArray.get(position));
                op4TextView.setTextColor(Color.BLACK);
                op5TextView.setText("Precio en puntos");
                op6TextView.setText(String.valueOf(precioPuntosArray.get(position)));
                op6TextView.setTextColor(Color.BLACK);

                if (productoActivoArray.get(position)){

                    op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op1ImageView.setImageResource(R.drawable.success);

                } else {

                    op1TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                    op1ImageView.setImageResource(R.drawable.nop);

                }

                return convertView;

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
