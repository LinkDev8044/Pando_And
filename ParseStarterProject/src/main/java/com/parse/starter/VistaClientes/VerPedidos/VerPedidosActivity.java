package com.parse.starter.VistaClientes.VerPedidos;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.DetallePedido.DetallePedidoActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class VerPedidosActivity extends AppCompatActivity {

    String comercioId;

    ArrayList<String> opcionEntregaArray = new ArrayList();
    ArrayList<String> horaRecogerArray = new ArrayList();
    ArrayList<String> tiempoEntregaArray = new ArrayList();
    ArrayList<String> fechaPedidoArray = new ArrayList();
    ArrayList<Integer> etapaArray = new ArrayList();
    ArrayList<Integer> numPedidoArray = new ArrayList();
    ArrayList<Double> totalFinalArray = new ArrayList();

    ListView verPedidosListView;

    CustomAdapter customAdapter;

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

        opcionEntregaArray.clear();
        horaRecogerArray.clear();
        tiempoEntregaArray.clear();
        fechaPedidoArray.clear();
        etapaArray.clear();
        numPedidoArray.clear();
        totalFinalArray.clear();

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoConfirmado");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("activo", false);
        query.orderByDescending("fechaCreacion");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        opcionEntregaArray.add(object.getString("opcionEntrega"));
                        etapaArray.add(object.getInt("etapa"));
                        numPedidoArray.add(object.getInt("numDePedido"));
                        totalFinalArray.add(object.getDouble("totalFinal"));
                        horaRecogerArray.add(object.getString("horaRecoger"));
                        tiempoEntregaArray.add(object.getString("tiempoEntrega"));

                        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                        String convertedDate = dateFormat.format(object.getDate("fechaCreacion"));
                        fechaPedidoArray.add(convertedDate);

                    }

                    terminarSppiner();
                    verPedidosListView.setAdapter(customAdapter);

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_pedidos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Historial pedidos");

        verPedidosListView = (ListView) findViewById(R.id.verPedidosListView);

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");

        customAdapter = new CustomAdapter();

        verPedidosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent1 = new Intent(getApplicationContext(), DetallePedidoActivity.class);
                intent1.putExtra("numDePedido", numPedidoArray.get(position));
                intent1.putExtra("comercioId", comercioId);
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

    class CustomAdapter extends BaseAdapter implements Adapter {

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getCount() {
            return totalFinalArray.size();
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

            if (convertView == null){

                //Grafica de seguimiento general

                convertView = mInflater.inflate(R.layout.validar_pedido_cell_1, null);
                convertView.setEnabled(true);

                TextView op1numPedidosTextView = (TextView) convertView.findViewById(R.id.op1ValidarPTextView);
                TextView op2fechaTextView = (TextView) convertView.findViewById(R.id.op2ValidarPTextView);
                TextView op3OpcionEntregaTextView = (TextView) convertView.findViewById(R.id.op3ValidarPTextView);
                TextView op4HoraTextView = (TextView) convertView.findViewById(R.id.op4ValidarPTextView);
                TextView op5TiempoTextView = (TextView) convertView.findViewById(R.id.op5ValidarPTextView);
                TextView op6AvanceTextView = (TextView) convertView.findViewById(R.id.op6ValidarPTextView);
                TextView op7EtapaTextView = (TextView) convertView.findViewById(R.id.op7ValidarPTextView);
                TextView op8TotalTextView = (TextView) convertView.findViewById(R.id.op8ValidarPTextView);
                TextView op9FlechaTextView = (TextView) convertView.findViewById(R.id.op9ValidarPTextView);
                ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1ValidarPImageView);
                ProgressBar op1ProgressBar = (ProgressBar) convertView.findViewById(R.id.op1ValidarPProgressBar);

                op1numPedidosTextView.setText("#" + String.valueOf(numPedidoArray.get(position)));
                op2fechaTextView.setText(fechaPedidoArray.get(position));
                op8TotalTextView.setText("Total $" + String.valueOf(totalFinalArray.get(position)));
                //op9FlechaTextView.setText("");
                op4HoraTextView.setText(horaRecogerArray.get(position));
                op5TiempoTextView.setText(tiempoEntregaArray.get(position));


                if (opcionEntregaArray.get(position).matches("Domicilio")) {

                    op3OpcionEntregaTextView.setText("Pedido a domicilio");

                    op1ProgressBar.setMax(4);
                    op1ProgressBar.setProgress(etapaArray.get(position));

                    op1ImageView.setImageResource(R.drawable.food_delivery);

                    if (etapaArray.get(position) == 1) {

                        op7EtapaTextView.setText("Pedido enviado");
                        op6AvanceTextView.setText("1/4");


                    } else if (etapaArray.get(position) == 2) {

                        op7EtapaTextView.setText("En preparación");
                        op6AvanceTextView.setText("2/4");

                    } else if (etapaArray.get(position) == 3) {

                        op7EtapaTextView.setText("En ruta");
                        op6AvanceTextView.setText("3/4");

                    } else if (etapaArray.get(position) == 4) {

                        op7EtapaTextView.setText("Entregado");
                        op6AvanceTextView.setText("4/4");

                    }


                } else if (opcionEntregaArray.get(position).matches("Recoger") || opcionEntregaArray.get(position).matches("Restaurante")) {

                    op1ProgressBar.setMax(3);
                    op1ProgressBar.setProgress(etapaArray.get(position));

                    if (opcionEntregaArray.get(position).matches("Recoger")) {

                        op3OpcionEntregaTextView.setText("Recoger pedido");
                        op1ImageView.setImageResource(R.drawable.take_away);

                    } else {

                        op3OpcionEntregaTextView.setText("Pedido en restaurante");
                        op1ImageView.setImageResource(R.drawable.spoon);

                    }

                    if (etapaArray.get(position) == 1) {

                        op7EtapaTextView.setText("Pedido enviado");
                        op6AvanceTextView.setText("1/3");

                    } else if (etapaArray.get(position) == 2) {

                        op7EtapaTextView.setText("En preparación");
                        op6AvanceTextView.setText("2/3");

                    } else if (etapaArray.get(position) == 3) {

                        op7EtapaTextView.setText("Entregado");
                        op6AvanceTextView.setText("3/3");

                    }
                }

                if (tiempoEntregaArray.get(position).matches("CANCELADO")) {

                    op7EtapaTextView.setText("CANCELADO");

                }

                return convertView;

            } else {

                //Grafica de seguimiento general

                convertView = mInflater.inflate(R.layout.validar_pedido_cell_1, null);
                convertView.setEnabled(true);

                TextView op1numPedidosTextView = (TextView) convertView.findViewById(R.id.op1ValidarPTextView);
                TextView op2fechaTextView = (TextView) convertView.findViewById(R.id.op2ValidarPTextView);
                TextView op3OpcionEntregaTextView = (TextView) convertView.findViewById(R.id.op3ValidarPTextView);
                TextView op4HoraTextView = (TextView) convertView.findViewById(R.id.op4ValidarPTextView);
                TextView op5TiempoTextView = (TextView) convertView.findViewById(R.id.op5ValidarPTextView);
                TextView op6AvanceTextView = (TextView) convertView.findViewById(R.id.op6ValidarPTextView);
                TextView op7EtapaTextView = (TextView) convertView.findViewById(R.id.op7ValidarPTextView);
                TextView op8TotalTextView = (TextView) convertView.findViewById(R.id.op8ValidarPTextView);
                TextView op9FlechaTextView = (TextView) convertView.findViewById(R.id.op9ValidarPTextView);
                ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1ValidarPImageView);
                ProgressBar op1ProgressBar = (ProgressBar) convertView.findViewById(R.id.op1ValidarPProgressBar);

                op1numPedidosTextView.setText("#" + String.valueOf(numPedidoArray.get(position)));
                op2fechaTextView.setText(fechaPedidoArray.get(position));
                op8TotalTextView.setText("Total $" + String.valueOf(totalFinalArray.get(position)));
                //op9FlechaTextView.setText("");
                op4HoraTextView.setText(horaRecogerArray.get(position));
                op5TiempoTextView.setText(tiempoEntregaArray.get(position));


                if (opcionEntregaArray.get(position).matches("Domicilio")) {

                    op3OpcionEntregaTextView.setText("Pedido a domicilio");

                    op1ProgressBar.setMax(4);
                    op1ProgressBar.setProgress(etapaArray.get(position));

                    op1ImageView.setImageResource(R.drawable.food_delivery);

                    if (etapaArray.get(position) == 1) {

                        op7EtapaTextView.setText("Pedido enviado");
                        op6AvanceTextView.setText("1/4");


                    } else if (etapaArray.get(position) == 2) {

                        op7EtapaTextView.setText("En preparación");
                        op6AvanceTextView.setText("2/4");

                    } else if (etapaArray.get(position) == 3) {

                        op7EtapaTextView.setText("En ruta");
                        op6AvanceTextView.setText("3/4");

                    } else if (etapaArray.get(position) == 4) {

                        op7EtapaTextView.setText("Entregado");
                        op6AvanceTextView.setText("4/4");

                    }


                } else if (opcionEntregaArray.get(position).matches("Recoger") || opcionEntregaArray.get(position).matches("Restaurante")) {

                    op1ProgressBar.setMax(3);
                    op1ProgressBar.setProgress(etapaArray.get(position));

                    if (opcionEntregaArray.get(position).matches("Recoger")) {

                        op3OpcionEntregaTextView.setText("Recoger pedido");
                        op1ImageView.setImageResource(R.drawable.take_away);

                    } else {

                        op3OpcionEntregaTextView.setText("Pedido en restaurante");
                        op1ImageView.setImageResource(R.drawable.spoon);

                    }

                    if (etapaArray.get(position) == 1) {

                        op7EtapaTextView.setText("Pedido enviado");
                        op6AvanceTextView.setText("1/3");

                    } else if (etapaArray.get(position) == 2) {

                        op7EtapaTextView.setText("En preparación");
                        op6AvanceTextView.setText("2/3");

                    } else if (etapaArray.get(position) == 3) {

                        op7EtapaTextView.setText("Entregado");
                        op6AvanceTextView.setText("3/3");

                    }
                }

                if (tiempoEntregaArray.get(position).matches("CANCELADO")) {

                    op7EtapaTextView.setText("CANCELADO");

                }

                return convertView;

            }
        }
    }
}
