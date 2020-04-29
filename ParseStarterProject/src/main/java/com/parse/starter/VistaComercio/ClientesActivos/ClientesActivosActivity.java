package com.parse.starter.VistaComercio.ClientesActivos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.parse.starter.VistaComercio.Colaborador.ColaboradorActivity;
import com.parse.starter.VistaComercio.EnviarEncuesta.EnviarEncuestaActivity;
import com.parse.starter.VistaComercio.QRCodeReader.QRCodeReaderActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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

    ArrayList<String> USUARIOArray = new ArrayList();
    ArrayList<String> USUARIOIdArray = new ArrayList();
    ArrayList<String> CORREOUsuarioArray = new ArrayList();

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

    private void reloadData(){

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
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    USUARIOArray.clear();
                    USUARIOIdArray.clear();
                    CORREOUsuarioArray.clear();

                    for (ParseObject object : objects){

                        long diff = fecha.getTime() - object.getDate("fechaCreacion").getTime();
                        long diffInHours = TimeUnit.MILLISECONDS.toMinutes(diff);

                        if (diffInHours >= 40){

                            object.put("activo", false);
                            object.put("fechaEncuestaTerminada", fecha);
                            object.saveInBackground();

                        } else {

                            USUARIOArray.add(object.getString("nombreUsuario"));
                            USUARIOIdArray.add(object.getString("usuarioId"));
                            CORREOUsuarioArray.add(object.getString("email"));

                        }
                    }

                    clientesListView.setAdapter(customAdapter);

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

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

                convertView = mInflater.inflate(R.layout.una_opcion_con_flecha, null);

                final TextView op1TextView = (TextView) convertView.findViewById(R.id.opcion1FlechaTextView);

                op1TextView.setText(USUARIOArray.get(position));

                ParseQuery<ParseObject> query = ParseQuery.getQuery("VisitasCliente");
                query.whereEqualTo("comercioId", comercioId);
                query.whereEqualTo("usuarioId", USUARIOIdArray.get(position));
                query.setLimit(1);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null){

                            if (objects.size() > 0){

                                for (ParseObject object : objects){

                                    long diff = fecha.getTime() - object.getDate("fechaModificacion").getTime();
                                    long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);

                                    if (diffInHours <= 8){

                                        op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                                    } else {

                                        op1TextView.setTextColor(Color.DKGRAY);

                                    }
                                }

                            } else {

                                op1TextView.setTextColor(Color.DKGRAY);

                            }

                        } else {

                            terminarSppiner();

                            Toast.makeText(ClientesActivosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                return convertView;

            } else {

                convertView = mInflater.inflate(R.layout.una_opcion_con_flecha, null);

                final TextView op1TextView = (TextView) convertView.findViewById(R.id.opcion1FlechaTextView);

                op1TextView.setText(USUARIOArray.get(position));

                ParseQuery<ParseObject> query = ParseQuery.getQuery("VisitasCliente");
                query.whereEqualTo("comercioId", comercioId);
                query.whereEqualTo("usuarioId", USUARIOIdArray.get(position));
                query.setLimit(1);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null){

                            if (objects.size() > 0){

                                for (ParseObject object : objects){

                                    long diff = fecha.getTime() - object.getDate("fechaModificacion").getTime();
                                    long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);

                                    if (diffInHours <= 8){

                                        op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                                    } else {

                                        op1TextView.setTextColor(Color.DKGRAY);

                                    }
                                }

                            } else {

                                op1TextView.setTextColor(Color.DKGRAY);

                            }

                        } else {

                            terminarSppiner();

                            Toast.makeText(ClientesActivosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                return convertView;

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
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
