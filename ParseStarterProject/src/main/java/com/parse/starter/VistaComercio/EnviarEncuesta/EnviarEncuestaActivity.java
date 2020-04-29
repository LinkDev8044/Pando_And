package com.parse.starter.VistaComercio.EnviarEncuesta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.ValidarPuntos.ValidarPuntosActivity;
import com.parse.starter.VistaComercio.ClientesActivos.ClientesActivosActivity;
import com.parse.starter.VistaComercio.EnviarPuntos.EnviarPuntosActivity;
import com.parse.starter.VistaComercio.ProductosCliente.ProductosClienteActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class EnviarEncuestaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String usuario;
    String usuarioId;
    String comercioId;
    String nombreComercio;
    String encuestaActiva;
    String encuestaActivaId;
    String nombreCompletoAdmin;
    String correoCliente;
    String recompensaActiva;

    Boolean ofreceVIP;
    Boolean ofrecePuntos;
    Boolean esVIP;
    Boolean tieneProductosCliente;
    Boolean encuestaEnviada;

    ArrayList<String> consumoEnviadoArray = new ArrayList();

    ArrayList<Double> puntosEnviadosArray = new ArrayList();

    int numeroDePreguntas;
    int totalDeVisitas;
    int contadorPuntos;
    int nivel1;
    int nivel2;
    int nivel3;
    int porcentajeNivel1;
    int porcentajeNivel2;
    int porcentajeNivel3;
    int visitasCliente;

    Double porcentaje;
    Double puntosCliente;

    Calendar fechaInicioMes;

    Date fecha;
    Date fechaComparacionVisitas;

    String[] TITULOS = {"Enviar puntos", "Canjear compras", "Canjear puntos"};

    int[] IMAGES = {R.drawable.enviar_puntos, R.drawable.nop, R.drawable.puntos_de_recompensa};

    ListView enviarEncuestaListView;

    SwipeRefreshLayout swipeRefreshLayout;

    CustomAdapter customAdapter;

    ProgressDialog progressDialog;

    private void reloadData(){

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

        //fecha 30 días antes
        fechaInicioMes = Calendar.getInstance();
        fechaInicioMes.set(Calendar.DATE, -30);

        customAdapter = new CustomAdapter();

        puntosCliente = 0.0;
        totalDeVisitas = 0;
        contadorPuntos = 0;
        tieneProductosCliente = false;
        encuestaEnviada = false;
        consumoEnviadoArray.clear();
        puntosEnviadosArray.clear();
        ofreceVIP = false;
        nivel1 = 0;
        nivel2 = 0;
        nivel3 = 0;
        ofrecePuntos = false;
        porcentajeNivel1 = 0;
        porcentajeNivel2 = 0;
        porcentajeNivel3 = 0;
        esVIP = false;
        visitasCliente = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", usuarioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        puntosCliente = object.getDouble("puntos");

                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("VisitasCliente");
                    query.whereEqualTo("comercioId", comercioId);
                    query.whereEqualTo("usuarioId", usuarioId);
                    query.setLimit(1);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                for (ParseObject object : objects){

                                    totalDeVisitas = object.getInt("numeroDeVisitas");

                                }

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosCliente");
                                query.whereEqualTo("comercioId", comercioId);
                                query.whereEqualTo("usuarioId", usuarioId);
                                query.whereEqualTo("activo", true);
                                query.setLimit(1);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {

                                        if (e == null){

                                            if (objects.size() > 0){

                                                tieneProductosCliente = true;

                                            }

                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestaPendiente");
                                            query.whereEqualTo("comercioId", comercioId);
                                            query.whereEqualTo("usuarioId", usuarioId);
                                            query.whereEqualTo("activo", true);
                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {

                                                    if (e == null){

                                                        if (objects.size() > 0){

                                                            for (ParseObject object : objects){

                                                                long diff = fecha.getTime() - object.getDate("fechaCreacion").getTime();
                                                                long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);

                                                                if (diffInHours >= 8){

                                                                    object.put("activo", false);
                                                                    object.put("fechaModificacion", fecha);
                                                                    object.saveInBackground();

                                                                } else {

                                                                    encuestaEnviada = true;

                                                                }
                                                            }

                                                        }



                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosEnviados");
                                                        query.whereEqualTo("comercioId", comercioId);
                                                        query.whereEqualTo("usuarioId", usuarioId);
                                                        query.orderByDescending("fechaCreacion");
                                                        query.setLimit(5);
                                                        query.findInBackground(new FindCallback<ParseObject>() {
                                                            @Override
                                                            public void done(List<ParseObject> objects, ParseException e) {

                                                                if (e == null){

                                                                    for (ParseObject object : objects){

                                                                        long diff = fecha.getTime() - object.getDate("fechaCreacion").getTime();
                                                                        long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);

                                                                        if (diffInHours <= 8 ){

                                                                            consumoEnviadoArray.add(object.getString("consumo"));
                                                                            puntosEnviadosArray.add(object.getDouble("puntosEnviados"));

                                                                        }
                                                                    }

                                                                    contadorPuntos = consumoEnviadoArray.size();

                                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ClubVIP");
                                                                    query.whereEqualTo("comercioId", comercioId);
                                                                    query.whereEqualTo("activo", true);
                                                                    query.setLimit(1);
                                                                    query.findInBackground(new FindCallback<ParseObject>() {
                                                                        @Override
                                                                        public void done(List<ParseObject> objects, ParseException e) {

                                                                            if (e == null){

                                                                                if (objects.size() > 0){

                                                                                    ofreceVIP = true;

                                                                                    for (ParseObject object : objects){

                                                                                        nivel1 = object.getInt("nivel1");
                                                                                        nivel2 = object.getInt("nivel2");
                                                                                        nivel3 = object.getInt("nivel3");

                                                                                    }
                                                                                }

                                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosActivos");
                                                                                query.whereEqualTo("comercioId", comercioId);
                                                                                query.whereEqualTo("eliminado", false);
                                                                                query.setLimit(1);
                                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                                    @Override
                                                                                    public void done(List<ParseObject> objects, ParseException e) {

                                                                                        if (e == null){

                                                                                            if (objects.size() > 0){

                                                                                                for (ParseObject object : objects){

                                                                                                    ofrecePuntos = object.getBoolean("activo");

                                                                                                    if (ofrecePuntos){

                                                                                                        porcentajeNivel1 = object.getInt("porcentaje");
                                                                                                        porcentajeNivel2 = object.getInt("porcentaje2");
                                                                                                        porcentajeNivel3 = object.getInt("porcentaje3");

                                                                                                    }
                                                                                                }
                                                                                            }

                                                                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosEnviados");
                                                                                            query.whereEqualTo("comercioId", comercioId);
                                                                                            query.whereEqualTo("usuarioId", usuarioId);
                                                                                            query.whereGreaterThanOrEqualTo("fechaCreacion", fechaInicioMes.getTime());
                                                                                            query.orderByDescending("fechaCreacion");
                                                                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                                                                @Override
                                                                                                public void done(List<ParseObject> objects, ParseException e) {

                                                                                                    if (e == null){

                                                                                                        if (objects.size() > 0){

                                                                                                            esVIP = true;

                                                                                                            for (ParseObject object : objects){

                                                                                                                if (visitasCliente == 0){

                                                                                                                    fechaComparacionVisitas = object.getDate("fechaCreacion");

                                                                                                                    visitasCliente = visitasCliente + 1;

                                                                                                                } else {

                                                                                                                    long diff = fechaComparacionVisitas.getTime() - object.getDate("fechaCreacion").getTime();
                                                                                                                    long diffInDays = TimeUnit.MILLISECONDS.toHours(diff);

                                                                                                                    if (diffInDays >= 8){

                                                                                                                        visitasCliente = visitasCliente + 1;

                                                                                                                    }

                                                                                                                    fechaComparacionVisitas = object.getDate("fechaCreacion");

                                                                                                                }
                                                                                                            }
                                                                                                        }

                                                                                                        enviarEncuestaListView.setAdapter(customAdapter);

                                                                                                        swipeRefreshLayout.setRefreshing(false);

                                                                                                        terminarSppiner();

                                                                                                    } else {

                                                                                                        swipeRefreshLayout.setRefreshing(false);

                                                                                                        terminarSppiner();

                                                                                                        Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                        } else {

                                                                                            swipeRefreshLayout.setRefreshing(false);

                                                                                            terminarSppiner();

                                                                                            Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    }
                                                                                });

                                                                            } else {

                                                                                swipeRefreshLayout.setRefreshing(false);

                                                                                terminarSppiner();

                                                                                Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }
                                                                    });

                                                                } else {

                                                                    swipeRefreshLayout.setRefreshing(false);

                                                                    terminarSppiner();

                                                                    Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });



                                                    } else {

                                                        swipeRefreshLayout.setRefreshing(false);

                                                        terminarSppiner();

                                                        Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });

                                        } else {

                                            swipeRefreshLayout.setRefreshing(false);

                                            terminarSppiner();

                                            Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            } else {

                                swipeRefreshLayout.setRefreshing(false);

                                terminarSppiner();

                                Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

                    Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.activity_enviar_encuesta);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        enviarEncuestaListView = (ListView) findViewById(R.id.enviarEncuestaListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.enviarEncRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);

        Intent intent = getIntent();
        usuario = intent.getStringExtra("usuario");
        usuarioId = intent.getStringExtra("usuarioId");
        comercioId = intent.getStringExtra("comercioId");
        nombreComercio = intent.getStringExtra("nombreComercio");
        encuestaActiva = intent.getStringExtra("encuestaActiva");
        encuestaActivaId = intent.getStringExtra("encuestaActivaId");
        numeroDePreguntas = intent.getIntExtra("numeroDePreguntas", 0);
        nombreCompletoAdmin = intent.getStringExtra("nombreCompletoAdmin");
        correoCliente = intent.getStringExtra("correoCliente");
        recompensaActiva = intent.getStringExtra("recompensaActiva");

        enviarEncuestaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 6){

                    Intent intent = new Intent(getApplicationContext(), EnviarPuntosActivity.class);
                    intent.putExtra("encuestaEnviada", encuestaEnviada);
                    intent.putExtra("comercioId", comercioId);
                    intent.putExtra("nombreComercio", nombreComercio);
                    intent.putExtra("encuestaActiva", encuestaActiva);
                    intent.putExtra("encuestaActivaId", encuestaActivaId);
                    intent.putExtra("numeroDePreguntas", numeroDePreguntas);
                    intent.putExtra("nombreCompletoAdmin", nombreCompletoAdmin);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("usuarioId", usuarioId);
                    intent.putExtra("correoCliente", correoCliente);
                    intent.putExtra("recompensaActiva", recompensaActiva);
                    intent.putExtra("porcentaje", porcentaje);
                    startActivity(intent);

                }

                if (position == 7){

                    Intent intent = new Intent(getApplicationContext(), ProductosClienteActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("comercioId", comercioId);
                    intent.putExtra("usuarioId", usuarioId);
                    startActivity(intent);

                }

                if (position == 8){

                    if (puntosCliente > 0){

                        Intent intent = new Intent(getApplicationContext(), ValidarPuntosActivity.class);
                        intent.putExtra("usuario", usuario);
                        intent.putExtra("puntosCliente", puntosCliente);
                        intent.putExtra("usuarioId", usuarioId);
                        intent.putExtra("comercioId", comercioId);
                        intent.putExtra("nombreComercio", nombreComercio);
                        intent.putExtra("nombreCompletoAdmin", nombreCompletoAdmin);
                        startActivity(intent);

                    } else {

                        Toast.makeText(EnviarEncuestaActivity.this, "El cliente NO tiene puntos para canjear", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        //reloadData();

    }

    @Override
    protected void onStart() {

        reloadData();

        super.onStart();

    }

    class CustomAdapter extends BaseAdapter implements Adapter {

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getViewTypeCount() {

            return 4;

        }

        @Override
        public int getItemViewType(int position) {

            /*if (position == 0) {

                return 0;

            } else if (position >= 0 && position <= 3){

                return 1;

            } else {

                return 2;

            }*/

            if (position == 0) {

                return 0;

            } else if (position == 1){

                if (contadorPuntos > 0){

                    return 2;

                }

                return 3;

            } else if (position == 2){

                if (contadorPuntos > 1){

                    return 2;

                }

                return 3;

            } else if (position == 3){

                if (contadorPuntos > 2){

                    return 2;

                }

                return 3;

            } else if (position == 4){

                if (contadorPuntos > 3){

                    return 2;

                }

                return 3;

            } else if (position == 5){

                if (contadorPuntos > 4){

                    return 2;

                }

                return 3;

            } else {

                return 1;

            }

            /*if (position == 0){

                 return 0;

            } else {

                if (consumoEnviadoArray.size() > 0){

                    if (position == 1){

                        return 2;

                    }

                    if (position == 2){

                        if (consumoEnviadoArray.size() > 1){


                        } else {


                        }

                    }



                } else {

                    return 1;

                }
            }*/
        }

        @Override
        public int getCount() {

            return 9;

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

                    convertView = mInflater.inflate(R.layout.enviar_encuesta_cell_1, null);

                    convertView.setEnabled(false);

                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1EnviarEncImageView);
                    ImageView op1ImageViewNew = (ImageView) convertView.findViewById(R.id.op1EnvEncImageView);
                    ImageView op2ImageView = (ImageView) convertView.findViewById(R.id.op2EnvEncImageView);
                    ImageView op3ImageView = (ImageView) convertView.findViewById(R.id.op3EnvEncImageView);
                    ImageView op4ImageView = (ImageView) convertView.findViewById(R.id.op4EnvEncImageView);
                    ImageView op5ImageView = (ImageView) convertView.findViewById(R.id.op5EnvEncImageView);
                    ImageView op6ImageView = (ImageView) convertView.findViewById(R.id.op6EnvEncImageView);
                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1EnviarEncTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2EnviarEncTextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3EnviarEncTextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4EnviarEncTextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5EnviarEncTextView);
                    TextView op6TextView = (TextView) convertView.findViewById(R.id.op6EnvEncTextView);
                    TextView op7TextView = (TextView) convertView.findViewById(R.id.op7EnvEncTextView);
                    TextView op8TextView = (TextView) convertView.findViewById(R.id.op8EnvEncTextView);
                    TextView op9TextView = (TextView) convertView.findViewById(R.id.op9EnvEncTextView);
                    TextView op10TextView = (TextView) convertView.findViewById(R.id.op10EnvEncTextView);


                    op2TextView.setText("Puntos disponibles");
                    op9TextView.setText("# Visitas | Últimos 30 días");
                    op4TextView.setText("Total de visitas");
                    op10TextView.setText(String.valueOf(visitasCliente));
                    op3TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op8TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op10TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op5TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                    op1ImageView.setImageResource(R.drawable.perfil_provisional);
                    op1ImageViewNew.setImageResource(R.drawable.puntos_de_recompensa);
                    op2ImageView.setImageResource(R.drawable.vip);
                    op4ImageView.setImageResource(R.drawable.percentage);
                    op5ImageView.setImageResource(R.drawable.placeholder);
                    op6ImageView.setImageResource(R.drawable.list);
                    op1TextView.setText(usuario);
                    op3TextView.setText(String.valueOf(puntosCliente));
                    op5TextView.setText(String.valueOf(totalDeVisitas));

                    porcentaje = Double.valueOf(porcentajeNivel1);

                    if (ofreceVIP){


                        if (esVIP) {

                            if (visitasCliente >= nivel3){

                                op3ImageView.setImageResource(R.drawable.pando_nivel3);
                                op6TextView.setText("VIP Nivel 3 Titanium");
                                op8TextView.setText(String.valueOf(porcentajeNivel3) + "%");
                                porcentaje = Double.valueOf(porcentajeNivel3);


                            } else {

                                if (visitasCliente >= nivel2){

                                    op3ImageView.setImageResource(R.drawable.pando_nivel2);
                                    op6TextView.setText("VIP Nivel 2 Platino");
                                    op8TextView.setText(String.valueOf(porcentajeNivel2) + "%");
                                    porcentaje = Double.valueOf(porcentajeNivel2);

                                } else {

                                    op3ImageView.setImageResource(R.drawable.pando_nivel1);
                                    op6TextView.setText("VIP Nivel 1 Oro");
                                    op8TextView.setText(String.valueOf(porcentajeNivel1) + "%");
                                    porcentaje = Double.valueOf(porcentajeNivel1);

                                }
                            }


                        } else {

                            op3ImageView.setImageResource(R.drawable.pando_nivel1);
                            op6TextView.setText("VIP Nivel 1 Oro");
                            op8TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                        }

                    } else {

                        op3ImageView.setImageResource(R.drawable.nop);
                        op6TextView.setText("Programa VIP");
                        op8TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                    }

                    if (ofrecePuntos) {

                        op7TextView.setText("Puntos por consumo");

                        if (porcentajeNivel3 > 0) {


                        } else {

                            op8TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                        }

                    } else {

                        op7TextView.setText("No programa de puntos");
                        op8TextView.setText("0 %");

                    }

                    return convertView;

                } else if (itemViewType == 1){

                    int pos = position - 6;

                    convertView = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opc1ImaTextView);
                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.opc1ImaImageView);

                    op1TextView.setText(TITULOS[pos]);
                    op1ImageView.setImageResource(IMAGES[pos]);

                    if (position == 2) {

                        if (tieneProductosCliente) {

                            op1ImageView.setImageResource(R.drawable.success);

                        }
                    }

                    return convertView;

                } else if (itemViewType == 2){

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.enviar_encuesta_cell_2, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1EnvEncCell2TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2EnvEncCell2TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3EnvEncCell2TextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4EnvEncCell2TextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5EnvEncCell2TextView);

                    op1TextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                    op3TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op5TextView.setTextColor(Color.BLACK);

                    op1TextView.setText("NUEVO");
                    op2TextView.setText("Puntos enviados");
                    op3TextView.setText(String.valueOf(puntosEnviadosArray.get(pos)));
                    op4TextView.setText("Consumo");
                    op5TextView.setText("$ " + consumoEnviadoArray.get(pos));

                    return convertView;

                } else if (itemViewType == 3){

                    convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return convertView;

                }

            } else {

                if (itemViewType == 0) {

                    convertView = mInflater.inflate(R.layout.enviar_encuesta_cell_1, null);

                    convertView.setEnabled(false);

                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1EnviarEncImageView);
                    ImageView op1ImageViewNew = (ImageView) convertView.findViewById(R.id.op1EnvEncImageView);
                    ImageView op2ImageView = (ImageView) convertView.findViewById(R.id.op2EnvEncImageView);
                    ImageView op3ImageView = (ImageView) convertView.findViewById(R.id.op3EnvEncImageView);
                    ImageView op4ImageView = (ImageView) convertView.findViewById(R.id.op4EnvEncImageView);
                    ImageView op5ImageView = (ImageView) convertView.findViewById(R.id.op5EnvEncImageView);
                    ImageView op6ImageView = (ImageView) convertView.findViewById(R.id.op6EnvEncImageView);
                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1EnviarEncTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2EnviarEncTextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3EnviarEncTextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4EnviarEncTextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5EnviarEncTextView);
                    TextView op6TextView = (TextView) convertView.findViewById(R.id.op6EnvEncTextView);
                    TextView op7TextView = (TextView) convertView.findViewById(R.id.op7EnvEncTextView);
                    TextView op8TextView = (TextView) convertView.findViewById(R.id.op8EnvEncTextView);
                    TextView op9TextView = (TextView) convertView.findViewById(R.id.op9EnvEncTextView);
                    TextView op10TextView = (TextView) convertView.findViewById(R.id.op10EnvEncTextView);


                    op2TextView.setText("Puntos disponibles");
                    op9TextView.setText("# Visitas | Últimos 30 días");
                    op4TextView.setText("Total de visitas");
                    op10TextView.setText(String.valueOf(visitasCliente));
                    op3TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op8TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op10TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op5TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                    op1ImageView.setImageResource(R.drawable.perfil_provisional);
                    op1ImageViewNew.setImageResource(R.drawable.puntos_de_recompensa);
                    op2ImageView.setImageResource(R.drawable.vip);
                    op4ImageView.setImageResource(R.drawable.percentage);
                    op5ImageView.setImageResource(R.drawable.placeholder);
                    op6ImageView.setImageResource(R.drawable.list);
                    op1TextView.setText(usuario);
                    op3TextView.setText(String.valueOf(puntosCliente));
                    op5TextView.setText(String.valueOf(totalDeVisitas));

                    if (esVIP) {

                        if (visitasCliente >= nivel3){

                            op3ImageView.setImageResource(R.drawable.pando_nivel3);
                            op6TextView.setText("VIP Nivel 3 Titanium");
                            op8TextView.setText(String.valueOf(porcentajeNivel3) + "%");
                            porcentaje = Double.valueOf(porcentajeNivel3);


                        } else {

                            if (visitasCliente >= nivel2){

                                op3ImageView.setImageResource(R.drawable.pando_nivel2);
                                op6TextView.setText("VIP Nivel 2 Platino");
                                op8TextView.setText(String.valueOf(porcentajeNivel2) + "%");
                                porcentaje = Double.valueOf(porcentajeNivel2);

                            } else {

                                op3ImageView.setImageResource(R.drawable.pando_nivel1);
                                op6TextView.setText("VIP Nivel 1 Oro");
                                op8TextView.setText(String.valueOf(porcentajeNivel1) + "%");
                                porcentaje = Double.valueOf(porcentajeNivel1);

                            }
                        }


                    } else {

                        porcentaje = Double.valueOf(porcentajeNivel1);

                        if (ofreceVIP){

                            op3ImageView.setImageResource(R.drawable.pando_nivel1);
                            op6TextView.setText("VIP Nivel 1 Oro");
                            op8TextView.setText(String.valueOf(porcentajeNivel1) + "%");


                        } else {

                            op3ImageView.setImageResource(R.drawable.nop);
                            op6TextView.setText("Programa VIP");
                            op8TextView.setText("0 %");

                        }
                    }

                    if (ofrecePuntos) {

                        op7TextView.setText("Puntos por consumo");

                        if (porcentajeNivel3 > 0) {


                        } else {

                            op8TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                        }

                    } else {

                        op7TextView.setText("No programa de puntos");
                        op8TextView.setText("0 %");

                    }

                    return convertView;

                } else if (itemViewType == 1){

                    int pos = position - 6;

                    convertView = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opc1ImaTextView);
                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.opc1ImaImageView);

                    op1TextView.setText(TITULOS[pos]);
                    op1ImageView.setImageResource(IMAGES[pos]);

                    if (position == 2) {

                        if (tieneProductosCliente) {

                            op1ImageView.setImageResource(R.drawable.success);

                        }
                    }

                    return convertView;

                } else if (itemViewType == 2){

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.enviar_encuesta_cell_2, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1EnvEncCell2TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2EnvEncCell2TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3EnvEncCell2TextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4EnvEncCell2TextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5EnvEncCell2TextView);

                    op1TextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                    op3TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op5TextView.setTextColor(Color.BLACK);

                    op1TextView.setText("NUEVO");
                    op2TextView.setText("Puntos enviados");
                    op3TextView.setText(String.valueOf(puntosEnviadosArray.get(pos)));
                    op4TextView.setText("Consumo");
                    op5TextView.setText("$ " + consumoEnviadoArray.get(pos));

                    return convertView;

                } else if (itemViewType == 3){

                    convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return convertView;

                }
            }

            return convertView;

        }
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
}
