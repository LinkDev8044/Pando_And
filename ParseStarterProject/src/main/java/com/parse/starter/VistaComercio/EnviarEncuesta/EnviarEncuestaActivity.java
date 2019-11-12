package com.parse.starter.VistaComercio.EnviarEncuesta;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.parse.starter.R;
import com.parse.starter.VistaComercio.ClientesActivos.ClientesActivosActivity;
import com.parse.starter.VistaComercio.EnviarPuntos.EnviarPuntosActivity;
import com.parse.starter.VistaComercio.ProductosCliente.ProductosClienteActivity;

import java.text.SimpleDateFormat;
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

    int numeroDePreguntas;
    int totalDeVisitas;

    Boolean tieneProductosCliente;
    Boolean encuestaEnviada;

    String[] TITULOS = {"Enviar puntos", "Canjear compras", "Canjear puntos"};

    int[] IMAGES = {R.drawable.enviar_puntos, R.drawable.nop, R.drawable.puntos_de_recompensa};

    Double puntosCliente;

    ListView enviarEncuestaListView;

    Date fecha;

    SwipeRefreshLayout swipeRefreshLayout;

    CustomAdapter customAdapter;

    ProgressDialog progressDialog;

    private void reloadData(){

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

        customAdapter = new CustomAdapter();

        iniciarSppiner();

        puntosCliente = 0.0;
        totalDeVisitas = 0;
        tieneProductosCliente = false;
        encuestaEnviada = false;

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

                                                    } else {

                                                        swipeRefreshLayout.setRefreshing(false);

                                                        terminarSppiner();

                                                        Toast.makeText(EnviarEncuestaActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });

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

                if (position == 1){

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
                    startActivity(intent);

                }

                if (position == 2){

                    Intent intent = new Intent(getApplicationContext(), ProductosClienteActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("comercioId", comercioId);
                    intent.putExtra("usuarioId", usuarioId);
                    startActivity(intent);

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

            return 2;

        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0) {

                return 0;

            } else {

                return 1;
            }
        }

        @Override
        public int getCount() {

            return 4;

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
                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1EnviarEncTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2EnviarEncTextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3EnviarEncTextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4EnviarEncTextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5EnviarEncTextView);

                    op2TextView.setText("Puntos disponibles");
                    op4TextView.setText("Total de visitas");
                    op2TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op3TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op4TextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                    op5TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    op1ImageView.setImageResource(R.drawable.perfil_provisional);
                    op1TextView.setText(usuario);
                    op3TextView.setText(String.valueOf(puntosCliente));
                    op5TextView.setText(String.valueOf(totalDeVisitas));

                    return convertView;

                } else if (itemViewType == 1){

                    int pos = position - 1;

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

                }

            } else {

                if (itemViewType == 0) {

                    convertView = mInflater.inflate(R.layout.enviar_encuesta_cell_1, null);

                    convertView.setEnabled(false);

                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1EnviarEncImageView);
                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1EnviarEncTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2EnviarEncTextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3EnviarEncTextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4EnviarEncTextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5EnviarEncTextView);

                    op2TextView.setText("Puntos disponibles");
                    op4TextView.setText("Total de visitas");
                    op2TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op3TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op4TextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                    op5TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    op1ImageView.setImageResource(R.drawable.perfil_provisional);
                    op1TextView.setText(usuario);
                    op3TextView.setText(String.valueOf(puntosCliente));
                    op5TextView.setText(String.valueOf(totalDeVisitas));

                    return convertView;

                } else if (itemViewType == 1){

                    int pos = position - 1;

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
