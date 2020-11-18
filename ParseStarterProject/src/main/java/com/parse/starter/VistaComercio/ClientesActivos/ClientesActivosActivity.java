package com.parse.starter.VistaComercio.ClientesActivos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.transition.TransitionManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.parse.starter.R;
import com.parse.starter.VistaClientes.ClienteActivity;
import com.parse.starter.VistaComercio.Colaborador.ColaboradorActivity;
import com.parse.starter.VistaComercio.EnviarEncuesta.EnviarEncuestaActivity;
import com.parse.starter.VistaComercio.QRCodeReader.QRCodeReaderActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ClientesActivosActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String comercioId;
    String nombreComercio;
    String encuestaActiva;
    String encuestaActivaId;
    String nombreCompletoAdmin;
    String recompensaActiva;

    int numeroDePreguntas;
    int contador2;

    Boolean seActivaCronom;
    Boolean suenaCampana;

    Handler handlerReload =  new Handler();
    Handler handler =  new Handler();

    ArrayList<String> USUARIOArray = new ArrayList();
    ArrayList<String> USUARIOIdArray = new ArrayList();
    ArrayList<String> CORREOUsuarioArray = new ArrayList();
    ArrayList<String> distanciaComArray = new ArrayList();
    ArrayList<Boolean> esVecinoArray = new ArrayList();

    Map<Integer,String> usuarioIdDic =  new HashMap<Integer, String>();
    Map<Integer,Integer> tiempoDic =  new HashMap<Integer, Integer>();
    Map<Integer,Date> fechaDic =  new HashMap<Integer, Date>();
    Map<String,Boolean> usuarioIdBorrarDic =  new HashMap<String, Boolean>();
    Map<String,Boolean> letrasVerdesDic =  new HashMap<String, Boolean>();
    Map<String,Boolean> letrasMoradasDic =  new HashMap<String, Boolean>();
    Map<String,Boolean> letrasRojasDic =  new HashMap<String, Boolean>();
    Map<Integer,String> usuarioIdPedidosDic =  new HashMap<Integer, String>();
    Map<String,String> horaRecogerDic =  new HashMap<String, String>();
    Map<String,Double> cronometroSecDic =  new HashMap<String, Double>();
    Map<String,String> cronometroFinalDic =  new HashMap<String, String>();

    Date fecha;

    ListView clientesListView;

    ProgressDialog progressDialog;

    CustomAdapter customAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

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
        View v = clientesListView.getChildAt(index -
                clientesListView.getFirstVisiblePosition());

        if(v == null)
            return;

        TextView someText = (TextView) v.findViewById(R.id.op2DosOpcionesTextView);


        if (letrasRojasDic.get(USUARIOIdArray.get(index))){

            someText.setText(cronometroFinalDic.get(USUARIOIdArray.get(index)));

        } else if (letrasMoradasDic.get(USUARIOIdArray.get(index))){

            someText.setText(horaRecogerDic.get(USUARIOIdArray.get(index)));

        }
    }

    private void runTimer(){

        for (Map.Entry<Integer, String> entry : usuarioIdPedidosDic.entrySet()){

            final Integer key = entry.getKey();
            final String value = entry.getValue();

            double nuevoSegundo = cronometroSecDic.get(value) + 1;
            cronometroSecDic.put(value, nuevoSegundo);

            Integer flooredCounter = Integer.valueOf((int) Math.floor(cronometroSecDic.get(value)));
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

            String tiempoString = String.valueOf(minuteString) + ":" + String.valueOf(secondString);
            cronometroFinalDic.put(value, tiempoString);
            clientesListView.setEnabled(true);

            updateView(key);

        }
    }

    private Runnable timerRunnableSeg = new Runnable() {
        @Override
        public void run() {

            runTimer();
            handler.postDelayed(this, 1000);

        }

        //handler.postDelayed(r, 1000);

    };

    private Runnable timerRunnableReload = new Runnable() {
        @Override
        public void run() {

            reloadData();
            handlerReload.postDelayed(this, 300000);

        }

        //handler.postDelayed(r, 1000);

    };

    private void iniciarTimers(){

        handlerReload.removeCallbacks(timerRunnableReload);
        handlerReload.postDelayed(timerRunnableReload, 300000);

        if (seActivaCronom) {

            clientesListView.setEnabled(false);

            handler.removeCallbacks(timerRunnableSeg);
            handler.postDelayed(timerRunnableSeg, 1000);

            //5000 - 5 seg
        }
    }

    private void cargarTabla(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsuarioActivo");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("activo", true);
        query.orderByDescending("fechaCreacion");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    USUARIOArray.clear();
                    USUARIOIdArray.clear();
                    CORREOUsuarioArray.clear();
                    distanciaComArray.clear();
                    esVecinoArray.clear();

                    for (ParseObject object : objects){

                        Log.i("Prueba", "Cuantas");

                        USUARIOArray.add(object.getString("nombreUsuario"));
                        USUARIOIdArray.add(object.getString("usuarioId"));
                        CORREOUsuarioArray.add(object.getString("email"));

                        if (object.getString("distanciaComercio") == null){

                            distanciaComArray.add("N/A");
                            esVecinoArray.add(false);

                        } else  {

                            distanciaComArray.add(object.getString("distanciaComercio"));
                            esVecinoArray.add(object.getBoolean("esVecino"));

                        }
                    }

                    if (USUARIOArray.size() >= usuarioIdDic.size()){

                        if (suenaCampana){

                            MediaPlayer mp = MediaPlayer.create(ClientesActivosActivity.this , R.raw.pando_bell_sound);
                            mp.start();

                        }

                        iniciarTimers();

                        Log.i("Prueba", "Esta");

                        clientesListView.setAdapter(customAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                        terminarSppiner();

                    }

                } else {

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

                    Toast.makeText(ClientesActivosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void filtrarUsuarios(){

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

        contador2 = 0;

        if (usuarioIdDic.size() == 0){

            cargarTabla();

        } else {

            for (Map.Entry<Integer, String> entry : usuarioIdDic.entrySet()) {

                final Integer key = entry.getKey();
                final String value = entry.getValue();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("UsuarioActivo");
                query.whereEqualTo("comercioId", comercioId);
                query.whereEqualTo("activo", true);
                query.whereEqualTo("usuarioId", value);
                query.setLimit(1);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                            for (ParseObject object : objects) {

                                if (usuarioIdBorrarDic.get(value)) {

                                    object.put("activo", false);
                                    object.put("fechaEncuestaTerminada", fecha);
                                    object.saveInBackground();

                                }

                                contador2 = contador2 + 1;

                                if (contador2 >= usuarioIdDic.size()) {

                                    cargarTabla();

                                }
                            }

                        } else {

                            swipeRefreshLayout.setRefreshing(false);

                            terminarSppiner();

                            Toast.makeText(ClientesActivosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
    }

    private void revisarPedidos(){

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

        usuarioIdPedidosDic.clear();
        letrasRojasDic.clear();
        letrasMoradasDic.clear();
        horaRecogerDic.clear();
        cronometroSecDic.clear();
        cronometroFinalDic.clear();
        seActivaCronom = false;
        suenaCampana = false;

        if (usuarioIdDic.size() == 0){

            filtrarUsuarios();

        } else {

            for (Map.Entry<Integer, String> entry : usuarioIdDic.entrySet()) {

                final Integer key = entry.getKey();
                final String value = entry.getValue();


                ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoConfirmado");
                query.whereEqualTo("comercioId", comercioId);
                query.whereEqualTo("usuarioId", value);
                query.whereEqualTo("activo", true);
                query.orderByDescending("fechaCreacion");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                            if (objects.size() > 0) {

                                //Pedido por confirmar nuevo
                                usuarioIdBorrarDic.put(usuarioIdDic.get(key), false);
                                usuarioIdPedidosDic.put(key, value);
                                seActivaCronom = true;

                                for (ParseObject object : objects) {

                                    if (object.getInt("etapa") == 1) {

                                        suenaCampana = true;

                                    }

                                    //Datos para validar hora de pasar a recoger pedido
                                    String hora = object.getString("horaRecoger");
                                    horaRecogerDic.put(usuarioIdDic.get(key), hora);

                                    //Datos para validar el tiempo desde que se hizo el pedido
                                    long diff = fecha.getTime() - object.getDate("fechaModificacion").getTime();
                                    long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
                                    cronometroSecDic.put(usuarioIdDic.get(key), Double.valueOf(diffInSeconds));

                                    //revisar el tema de domicilio
                                    String opcionEntrega = object.getString("opcionEntrega");

                                    if (opcionEntrega.matches("Domicilio") || opcionEntrega.matches("Restaurante")) {

                                        letrasRojasDic.put(usuarioIdDic.get(key), true);
                                        letrasMoradasDic.put(usuarioIdDic.get(key), false);

                                    } else {

                                        letrasRojasDic.put(usuarioIdDic.get(key), false);
                                        letrasMoradasDic.put(usuarioIdDic.get(key), true);

                                    }

                                    //Revisar si ya se cargaron los datos de todos los usuarios
                                    if (letrasRojasDic.size() >= usuarioIdDic.size()) {
                                        filtrarUsuarios();
                                    }

                                }

                            } else {

                                letrasRojasDic.put(usuarioIdDic.get(key), false);
                                letrasMoradasDic.put(usuarioIdDic.get(key), false);

                                //Revisar si ya se cargaron los datos de todos los usuarios
                                if (letrasRojasDic.size() >= usuarioIdDic.size()) {
                                    filtrarUsuarios();
                                }

                            }

                        } else {

                            swipeRefreshLayout.setRefreshing(false);

                            terminarSppiner();

                            Toast.makeText(ClientesActivosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
    }

    private void validarPuntosEnviados(){

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

        usuarioIdBorrarDic.clear();
        letrasVerdesDic.clear();

        if (usuarioIdDic.size() == 0){

            revisarPedidos();

        } else {

            for (Map.Entry<Integer, String> entry : usuarioIdDic.entrySet()) {

                final Integer key = entry.getKey();
                String value = entry.getValue();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("VisitasCliente");
                query.whereEqualTo("usuarioId", value);
                query.whereEqualTo("comercioId", comercioId);
                query.whereGreaterThanOrEqualTo("fechaModificacion", fechaDic.get(key));
                query.orderByDescending("fechaModificacion");
                query.setLimit(1);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                            if (objects.size() > 0) {

                                letrasVerdesDic.put(usuarioIdDic.get(key), true);

                                for (ParseObject object : objects) {

                                    long diff = fecha.getTime() - object.getDate("fechaModificacion").getTime();
                                    long diffInHours = TimeUnit.MILLISECONDS.toMinutes(diff);

                                    if (diffInHours >= 60) {

                                        usuarioIdBorrarDic.put(usuarioIdDic.get(key), true);

                                    } else {

                                        usuarioIdBorrarDic.put(usuarioIdDic.get(key), false);

                                    }

                                    if (usuarioIdBorrarDic.size() >= usuarioIdDic.size()) {


                                        revisarPedidos();

                                    }
                                }

                            } else {

                                letrasVerdesDic.put(usuarioIdDic.get(key), false);

                                if (tiempoDic.get(key) >= 1440) {

                                    usuarioIdBorrarDic.put(usuarioIdDic.get(key), true);

                                } else {

                                    usuarioIdBorrarDic.put(usuarioIdDic.get(key), false);

                                }

                                if (usuarioIdBorrarDic.size() >= usuarioIdDic.size()) {

                                    revisarPedidos();

                                }
                            }

                        } else {

                            swipeRefreshLayout.setRefreshing(false);

                            terminarSppiner();

                            Toast.makeText(ClientesActivosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
    }

    private void reloadData(){

        //Detener timers
        handlerReload.removeCallbacks(timerRunnableReload);
        handler.removeCallbacks(timerRunnableSeg);

        customAdapter = new CustomAdapter();

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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsuarioActivo");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("activo", true);
        query.orderByDescending("fechaCreacion");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    usuarioIdDic.clear();
                    tiempoDic.clear();
                    fechaDic.clear();
                    int contador = 0;

                    for (ParseObject object : objects){

                        long diff = fecha.getTime() - object.getDate("fechaCreacion").getTime();
                        long diffInHours = TimeUnit.MILLISECONDS.toMinutes(diff);

                        usuarioIdDic.put(contador, object.getString("usuarioId"));
                        tiempoDic.put(contador, Integer.valueOf((int) diffInHours));
                        fechaDic.put(contador, object.getDate("fechaCreacion"));
                        contador = contador + 1;

                        /*if (contador >= usuarioIdDic.size()){



                        }*/
                    }

                    validarPuntosEnviados();

                } else {

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

                    Toast.makeText(ClientesActivosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_activos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Clientes activos");

        clientesListView = (ListView) findViewById(R.id.clientesActListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.clientesActrefreshLayout);

        Intent intent = getIntent();

        comercioId = intent.getStringExtra("comercioId");
        nombreComercio = intent.getStringExtra("nombreComercio");
        encuestaActiva = intent.getStringExtra("encuestaActiva");
        encuestaActivaId = intent.getStringExtra("encuestaActivaId");
        numeroDePreguntas = intent.getIntExtra("numeroDePreguntas", 0);
        nombreCompletoAdmin = intent.getStringExtra("nombreCompletoAdmin");
        recompensaActiva = intent.getStringExtra("recompensaActiva");

        swipeRefreshLayout.setOnRefreshListener(this);

        clientesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Detener timers
                handlerReload.removeCallbacks(timerRunnableReload);
                handler.removeCallbacks(timerRunnableSeg);

                Intent intent = new Intent(getApplicationContext(), EnviarEncuestaActivity.class);
                intent.putExtra("usuario", USUARIOArray.get(position));
                intent.putExtra("usuarioId", USUARIOIdArray.get(position));
                intent.putExtra("comercioId", comercioId);
                intent.putExtra("nombreComercio", nombreComercio);
                intent.putExtra("encuestaActiva", encuestaActiva);
                intent.putExtra("encuestaActivaId", encuestaActivaId);
                intent.putExtra("numeroDePreguntas", numeroDePreguntas);
                intent.putExtra("nombreCompletoAdmin", nombreCompletoAdmin);
                intent.putExtra("correoCliente", CORREOUsuarioArray.get(position));
                intent.putExtra("recompensaActiva", recompensaActiva);
                intent.putExtra("distanciaComSelec", distanciaComArray.get(position));
                intent.putExtra("esVecinoSelec", esVecinoArray.get(position));
                intent.putExtra("tiempoString", cronometroFinalDic.get(USUARIOIdArray.get(position)));
                startActivity(intent);

            }
        });

        //reloadData();

    }

    @Override
    protected void onStart() {

        reloadData();
        super.onStart();
    }

    @Override
    public void onRefresh() {

        reloadData();

    }

    class CustomAdapter extends BaseAdapter implements Adapter{

        @Override
        public int getCount() {
            return USUARIOArray.size();
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

                convertView = mInflater.inflate(R.layout.general_dos_opciones_con_flecha, null);

                final TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DosOpcionesTextView);
                final TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DosOpcionesTextView);

                op1TextView.setText(USUARIOArray.get(position));
                op2TextView.setText("");

                if (letrasVerdesDic.get(USUARIOIdArray.get(position))) {

                    op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                } else {

                    op1TextView.setTextColor(Color.DKGRAY);

                    if (letrasRojasDic.get(USUARIOIdArray.get(position))){

                        op1TextView.setTextColor(Color.RED);
                        op2TextView.setText(cronometroFinalDic.get(USUARIOIdArray.get(position)));

                    } else if (letrasMoradasDic.get(USUARIOIdArray.get(position))){

                        op1TextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                        op2TextView.setText(horaRecogerDic.get(USUARIOIdArray.get(position)));

                    }
                }

                return convertView;

            } else {

                convertView = mInflater.inflate(R.layout.general_dos_opciones_con_flecha, null);

                final TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DosOpcionesTextView);
                final TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DosOpcionesTextView);

                op1TextView.setText(USUARIOArray.get(position));
                op2TextView.setText("");

                if (letrasVerdesDic.get(USUARIOIdArray.get(position))) {

                    op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                } else {

                    op1TextView.setTextColor(Color.DKGRAY);

                    if (letrasRojasDic.get(USUARIOIdArray.get(position))){

                        op1TextView.setTextColor(Color.RED);
                        op2TextView.setText(cronometroFinalDic.get(USUARIOIdArray.get(position)));

                    } else if (letrasMoradasDic.get(USUARIOIdArray.get(position))){

                        op1TextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                        op2TextView.setText(horaRecogerDic.get(USUARIOIdArray.get(position)));

                    }
                }

                return convertView;

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        //Detener timers
        handlerReload.removeCallbacks(timerRunnableReload);
        handler.removeCallbacks(timerRunnableSeg);

        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_camera, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_icon:

                //Detener timers
                handlerReload.removeCallbacks(timerRunnableReload);
                handler.removeCallbacks(timerRunnableSeg);

                Intent intent = new Intent(getApplicationContext(), QRCodeReaderActivity.class);
                intent.putExtra("comercioId", comercioId);
                intent.putExtra("nombreComercio", nombreComercio);
                intent.putExtra("encuestaActiva", encuestaActiva);
                intent.putExtra("encuestaActivaId", encuestaActivaId);
                intent.putExtra("numeroDePreguntas", numeroDePreguntas);
                intent.putExtra("nombreCompletoAdmin", nombreCompletoAdmin);
                intent.putExtra("recompensaActiva", recompensaActiva);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
