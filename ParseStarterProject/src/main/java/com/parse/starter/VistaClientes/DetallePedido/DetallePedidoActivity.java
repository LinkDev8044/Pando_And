package com.parse.starter.VistaClientes.DetallePedido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DetallePedidoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String comercioId;
    String tiempoString;
    String opcionEntrega;
    String hora;
    String tiempoEntrega;
    String horaRecoger;
    String fechaPedido;

    Double subTotal;
    Double precioConDesc;
    Double puntos;
    Double totalFinal;
    Double segundosPedido;

    ArrayList<String> nombrePlatilloArray = new ArrayList();
    ArrayList<Integer> cantidadArray = new ArrayList();
    ArrayList<Double> subTotalArray = new ArrayList();
    ArrayList<String> complementosArray = new ArrayList();
    ArrayList<String> comentariosArray = new ArrayList();

    int numDePedido;
    int etapa;

    Date fecha;

    Handler handler =  new Handler();

    Boolean pedidoActivo;

    CustomAdapter customAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    ListView detalleListView;

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

    private void updateView(int index){
        View v = detalleListView.getChildAt(index -
                detalleListView.getFirstVisiblePosition());

        if(v == null)
            return;

        TextView someText = (TextView) v.findViewById(R.id.op5ValidarPTextView);

        someText.setText(tiempoString);

    }

    private void runTimer(){

        segundosPedido += 1;

        Integer flooredCounter = Integer.valueOf((int) Math.floor(segundosPedido));
        Integer minute = (flooredCounter) / 60;
        String minuteString = String.valueOf(minute);
        if (minute < 10) {
            minuteString = "0" + minute;
        }

        Integer second = (flooredCounter % 3600) % 60;
        String secondString = String.valueOf(second);
        if (second < 10) {
            secondString = "0" + String.valueOf(second);
        }

        tiempoString = String.valueOf(minuteString) + ":" + String.valueOf(secondString);

        updateView(0);

        //customAdapter.notifyDataSetChanged();

    }

    private Runnable timerRunnableSeg = new Runnable() {
        @Override
        public void run() {

            runTimer();
            handler.postDelayed(this, 1000);

        }

        //handler.postDelayed(r, 1000);

    };

    private void cargarTotales(){

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

        subTotal = 0.0;
        precioConDesc = 0.0;
        puntos = 0.0;
        totalFinal = 0.0;
        etapa = 0;
        segundosPedido = 0.0;
        tiempoEntrega = "";
        opcionEntrega = "";
        horaRecoger = "";
        fechaPedido = "";
        pedidoActivo = false;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoConfirmado");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("numDePedido", numDePedido);
        query.orderByDescending("fechaCreacion");
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            opcionEntrega = object.getString("opcionEntrega");
                            etapa = object.getInt("etapa");
                            numDePedido = object.getInt("numDePedido");

                            //CARGAR TOTALES
                            subTotal = object.getDouble("subTotal");
                            precioConDesc = object.getDouble("precioConDescuento");
                            puntos = object.getDouble("puntos");
                            totalFinal = object.getDouble("totalFinal");

                            //Cargar hora recoger
                            horaRecoger = object.getString("horaRecoger");

                            //Revisar pedido activo para poner cronometro u fecha
                            pedidoActivo = object.getBoolean("activo");
                            tiempoEntrega = object.getString("tiempoEntrega");

                            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                            String convertedDate = dateFormat.format(object.getDate("fechaCreacion"));
                            fechaPedido = convertedDate;

                            if (pedidoActivo){

                                //Datos para validar el tiempo desde que se hizo el pedido
                                long diff = fecha.getTime() - object.getDate("fechaModificacion").getTime();
                                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
                                segundosPedido = Double.valueOf(diffInSeconds);

                                //Iniciar Timer
                                handler.removeCallbacks(timerRunnableSeg);
                                handler.postDelayed(timerRunnableSeg, 1000);

                            }
                        }

                        detalleListView.setAdapter(customAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                        terminarSppiner();

                    } else {

                        detalleListView.setAdapter(customAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                        terminarSppiner();

                    }

                } else {

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

                }
            }
        });
    }

    private void reloadData(){

        hora = "";
        nombrePlatilloArray.clear();
        cantidadArray.clear();
        subTotalArray.clear();
        complementosArray.clear();
        comentariosArray.clear();

        //Detener timers
        handler.removeCallbacks(timerRunnableSeg);

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("numDePedido", numDePedido);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            hora = object.getString("hora");
                            nombrePlatilloArray.add(object.getString("nombrePlatillo"));
                            cantidadArray.add(object.getInt("cantidad"));
                            subTotalArray.add(object.getDouble("subTotal"));
                            complementosArray.add(object.getString("complementos2"));
                            comentariosArray.add(object.getString("comentarios"));

                        }

                        cargarTotales();

                    } else {

                        cargarTotales();

                    }

                } else {

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detalleListView = (ListView) findViewById(R.id.detallePedidoListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.detallePRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");
        tiempoString = intent.getStringExtra("tiempoString");
        numDePedido = intent.getIntExtra("numDePedido", 0);

        customAdapter = new CustomAdapter();

    }

    @Override
    public boolean onSupportNavigateUp() {

        //Detener timers
        handler.removeCallbacks(timerRunnableSeg);

        finish();
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();

        reloadData();

    }

    @Override
    public void onRefresh() {

        reloadData();

    }

    class CustomAdapter extends BaseAdapter implements Adapter {

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getViewTypeCount() {

            return 3;

        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0){

                return 0;

            } else if (position > 0 && position < nombrePlatilloArray.size() + 1){

                return 1;

            } else {

                return 2;

            }
        }

        @Override
        public int getCount() {
            return 2 + nombrePlatilloArray.size();
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

            int itemViewType = getItemViewType(position);

            if (convertView == null){

                if (itemViewType == 0) {

                    //Grafica de seguimiento general

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_1, null);
                    convertView.setEnabled(false);

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

                    op1numPedidosTextView.setText("");
                    op2fechaTextView.setText(fechaPedido);
                    op4HoraTextView.setText(horaRecoger);
                    //op5TiempoTextView.setText(tiempoString);
                    op8TotalTextView.setText("");
                    op9FlechaTextView.setText("");

                    if (pedidoActivo) {

                        op5TiempoTextView.setText(tiempoString);

                    } else {

                        op5TiempoTextView.setText(tiempoEntrega);

                    }

                    if (opcionEntrega.matches("Domicilio")) {

                        op3OpcionEntregaTextView.setText("Pedido a domicilio");

                        op1ProgressBar.setMax(4);
                        op1ProgressBar.setProgress(etapa);

                        op1ImageView.setImageResource(R.drawable.food_delivery);

                        if (etapa == 1) {

                            op7EtapaTextView.setText("Pedido enviado");
                            op6AvanceTextView.setText("1/4");


                        } else if (etapa == 2) {

                            op7EtapaTextView.setText("En preparación");
                            op6AvanceTextView.setText("2/4");

                        } else if (etapa == 3) {

                            op7EtapaTextView.setText("En ruta");
                            op6AvanceTextView.setText("3/4");

                        } else if (etapa == 4) {

                            op7EtapaTextView.setText("Entregado");
                            op6AvanceTextView.setText("4/4");

                        }


                    } else if (opcionEntrega.matches("Recoger") || opcionEntrega.matches("Restaurante")) {

                        op1ProgressBar.setMax(3);
                        op1ProgressBar.setProgress(etapa);

                        if (opcionEntrega.matches("Recoger")) {

                            op3OpcionEntregaTextView.setText("Recoger pedido");
                            op1ImageView.setImageResource(R.drawable.take_away);

                        } else {

                            op3OpcionEntregaTextView.setText("Pedido en restaurante");
                            op1ImageView.setImageResource(R.drawable.spoon);

                        }

                        if (etapa == 1) {

                            op7EtapaTextView.setText("Pedido enviado");
                            op6AvanceTextView.setText("1/3");

                        } else if (etapa == 2) {

                            op7EtapaTextView.setText("En preparación");
                            op6AvanceTextView.setText("2/3");

                        } else if (etapa == 3) {

                            op7EtapaTextView.setText("Entregado");
                            op6AvanceTextView.setText("3/3");

                        }
                    }

                    if (tiempoEntrega.matches("CANCELADO")) {

                        op7EtapaTextView.setText("CANCELADO");

                    }

                    return convertView;

                } else if (itemViewType== 1){

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_3, null);
                    convertView.setEnabled(false);

                    TextView op1CantidadTextView = (TextView) convertView.findViewById(R.id.op1ValidarPC3TextView);
                    TextView op2NombreTextView = (TextView) convertView.findViewById(R.id.op2ValidarPC3TextView);
                    TextView op3PrecioTextView = (TextView) convertView.findViewById(R.id.op3ValidarPC3TextView);
                    TextView op4ComplementosTextView = (TextView) convertView.findViewById(R.id.op4ValidarPC3TextView);

                    op1CantidadTextView.setText(String.valueOf(cantidadArray.get(pos)));
                    op2NombreTextView.setText(nombrePlatilloArray.get(pos));
                    op3PrecioTextView.setText("$" + String.valueOf(subTotalArray.get(pos)));

                    if (complementosArray.get(pos).matches("")){

                        if (comentariosArray.get(pos).matches("") == false){

                            op4ComplementosTextView.setText(comentariosArray.get(pos));

                        }

                    } else {

                        if (comentariosArray.get(pos).matches("") == false){

                            op4ComplementosTextView.setText(complementosArray.get(pos) + "\n" + comentariosArray.get(pos));

                        } else {

                            op4ComplementosTextView.setText(complementosArray.get(pos));

                        }
                    }

                    return convertView;

                } else if (itemViewType == 2){

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_4, null);
                    convertView.setEnabled(false);

                    TextView op1SubTotalTextView = (TextView) convertView.findViewById(R.id.op1ValidarPC4TextView);
                    TextView op2PuntosTextView = (TextView) convertView.findViewById(R.id.op2ValidarPC4TextView);
                    TextView op3TotalTextView = (TextView) convertView.findViewById(R.id.op3ValidarPC4TextView);
                    TextView op4PrecioDescTextView = (TextView) convertView.findViewById(R.id.op4ValidarPC4TextView);

                    op3TotalTextView.setText("$" + String.valueOf(totalFinal));
                    op2PuntosTextView.setText("-" + String.valueOf(puntos));

                    if (subTotal.equals(precioConDesc)) {

                        op1SubTotalTextView.setText("$" + String.valueOf(subTotal));
                        op4PrecioDescTextView.setText("");

                    } else {

                        op1SubTotalTextView.setText("$" + String.valueOf(precioConDesc));
                        op4PrecioDescTextView.setText("$" + String.valueOf(subTotal));
                        op4PrecioDescTextView.setPaintFlags(op4PrecioDescTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    }

                    return convertView;

                }

            } else {

                if (itemViewType == 0) {

                    //Grafica de seguimiento general

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_1, null);
                    convertView.setEnabled(false);

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

                    op1numPedidosTextView.setText("");
                    op2fechaTextView.setText(fechaPedido);
                    op4HoraTextView.setText(horaRecoger);
                    //op5TiempoTextView.setText(tiempoString);
                    op8TotalTextView.setText("");
                    op9FlechaTextView.setText("");

                    if (pedidoActivo) {

                        op5TiempoTextView.setText(tiempoString);

                    } else {

                        op5TiempoTextView.setText(tiempoEntrega);

                    }

                    if (opcionEntrega.matches("Domicilio")) {

                        op3OpcionEntregaTextView.setText("Pedido a domicilio");

                        op1ProgressBar.setMax(4);
                        op1ProgressBar.setProgress(etapa);

                        op1ImageView.setImageResource(R.drawable.food_delivery);

                        if (etapa == 1) {

                            op7EtapaTextView.setText("Pedido enviado");
                            op6AvanceTextView.setText("1/4");


                        } else if (etapa == 2) {

                            op7EtapaTextView.setText("En preparación");
                            op6AvanceTextView.setText("2/4");

                        } else if (etapa == 3) {

                            op7EtapaTextView.setText("En ruta");
                            op6AvanceTextView.setText("3/4");

                        } else if (etapa == 4) {

                            op7EtapaTextView.setText("Entregado");
                            op6AvanceTextView.setText("4/4");

                        }


                    } else if (opcionEntrega.matches("Recoger") || opcionEntrega.matches("Restaurante")) {

                        op1ProgressBar.setMax(3);
                        op1ProgressBar.setProgress(etapa);

                        if (opcionEntrega.matches("Recoger")) {

                            op3OpcionEntregaTextView.setText("Recoger pedido");
                            op1ImageView.setImageResource(R.drawable.take_away);

                        } else {

                            op3OpcionEntregaTextView.setText("Pedido en restaurante");
                            op1ImageView.setImageResource(R.drawable.spoon);

                        }

                        if (etapa == 1) {

                            op7EtapaTextView.setText("Pedido enviado");
                            op6AvanceTextView.setText("1/3");

                        } else if (etapa == 2) {

                            op7EtapaTextView.setText("En preparación");
                            op6AvanceTextView.setText("2/3");

                        } else if (etapa == 3) {

                            op7EtapaTextView.setText("Entregado");
                            op6AvanceTextView.setText("3/3");

                        }
                    }

                    if (tiempoEntrega.matches("CANCELADO")) {

                        op7EtapaTextView.setText("CANCELADO");

                    }

                    return convertView;

                } else if (itemViewType== 1){

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_3, null);
                    convertView.setEnabled(false);

                    TextView op1CantidadTextView = (TextView) convertView.findViewById(R.id.op1ValidarPC3TextView);
                    TextView op2NombreTextView = (TextView) convertView.findViewById(R.id.op2ValidarPC3TextView);
                    TextView op3PrecioTextView = (TextView) convertView.findViewById(R.id.op3ValidarPC3TextView);
                    TextView op4ComplementosTextView = (TextView) convertView.findViewById(R.id.op4ValidarPC3TextView);

                    op1CantidadTextView.setText(String.valueOf(cantidadArray.get(pos)));
                    op2NombreTextView.setText(nombrePlatilloArray.get(pos));
                    op3PrecioTextView.setText("$" + String.valueOf(subTotalArray.get(pos)));

                    if (complementosArray.get(pos).matches("")){

                        if (comentariosArray.get(pos).matches("") == false){

                            op4ComplementosTextView.setText(comentariosArray.get(pos));

                        }

                    } else {

                        if (comentariosArray.get(pos).matches("") == false){

                            op4ComplementosTextView.setText(complementosArray.get(pos) + "\n" + comentariosArray.get(pos));

                        } else {

                            op4ComplementosTextView.setText(complementosArray.get(pos));

                        }
                    }

                    return convertView;

                } else if (itemViewType == 2){

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_4, null);
                    convertView.setEnabled(false);

                    TextView op1SubTotalTextView = (TextView) convertView.findViewById(R.id.op1ValidarPC4TextView);
                    TextView op2PuntosTextView = (TextView) convertView.findViewById(R.id.op2ValidarPC4TextView);
                    TextView op3TotalTextView = (TextView) convertView.findViewById(R.id.op3ValidarPC4TextView);
                    TextView op4PrecioDescTextView = (TextView) convertView.findViewById(R.id.op4ValidarPC4TextView);

                    op3TotalTextView.setText("$" + String.valueOf(totalFinal));
                    op2PuntosTextView.setText("-" + String.valueOf(puntos));

                    if (subTotal.equals(precioConDesc)) {

                        op1SubTotalTextView.setText("$" + String.valueOf(subTotal));
                        op4PrecioDescTextView.setText("");

                    } else {

                        op1SubTotalTextView.setText("$" + String.valueOf(precioConDesc));
                        op4PrecioDescTextView.setText("$" + String.valueOf(subTotal));
                        op4PrecioDescTextView.setPaintFlags(op4PrecioDescTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    }

                    return convertView;

                }

            }

            return convertView;

        }
    }
}
