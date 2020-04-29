package com.parse.starter.VistaClientes.DescripcionComercio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.Encuestas.PreguntaCaritasActivity;
import com.parse.starter.VistaClientes.HistorialPuntos.HistorialPuntosActivity;
import com.parse.starter.VistaClientes.MisCompras.MisComprasActivity;
import com.parse.starter.VistaClientes.TiendaComercio.TiendaActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DescripcionComercioActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String nombreComercio;
    String comercioId;
    String nombreCliente;
    String apellidoCliente;
    String correoCliente;
    String descripcionCom;
    String encuestaActiva;
    String encuestaActivaId;
    String recompensaActiva;
    String nombreColaborador;
    String colaboradorId;
    String correoColaborador;
    String direccion;
    String horario;

    Boolean ofreceVIP;
    Boolean esVIP;
    Boolean visa;
    Boolean american;
    Boolean mastercard;

    Calendar fechaInicioMes;

    ArrayList<String> consumoEnviadoArray = new ArrayList();

    String[] TITULOS = {"Ver tienda", "Ver mis compras", "Ver historial de puntos"};
    String[] DESCRIPCIONES = {"Compra productos o servicios con tus puntos aquí", "", ""};

    Double puntosCliente;

    ArrayList<Double> puntosEnviadosArray = new ArrayList();

    int porcentajeNivel1;
    int porcentajeNivel2;
    int porcentajeNivel3;
    int numeroDePreguntas;
    int contadorPuntos;
    int nivel1;
    int nivel2;
    int nivel3;
    int visitasCliente;
    int consumoPromedio;
    int numeroContacto;

    int[] IMAGES = {R.drawable.shop, R.drawable.configurar_recompensas, R.drawable.list};

    Boolean tieneLogo;
    Boolean usuarioActivo;
    Boolean ofreceRecompensa;
    Boolean ofrecePuntos;
    Boolean tieneDescripcion;
    Boolean tieneTienda;
    Boolean clienteTieneProduc;
    Boolean tieneHistorial;
    Boolean encuestaPendiente;
    Boolean visitaRegistrada;
    Boolean usarQR;
    Boolean usuarioActivoQR;

    Bitmap logoComercio;
    Bitmap qrClienteImage;

    Date fecha;
    Date fechaComparacion;
    Date fechaComparacionVisitas;

    ListView descripcionListView;

    TextView estoyAquiTextView;

    CustomAdapter customAdapter;

    ProgressDialog progressDialog;

    SwipeRefreshLayout swipeRefreshLayout;

    private void buttonClienteActivo(){

        estoyAquiTextView.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        estoyAquiTextView.setTextColor(Color.WHITE);
        estoyAquiTextView.setText("Visible para el comercio 👍");

    }

    private void buttonClienteInactivo(){

        estoyAquiTextView.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        estoyAquiTextView.setTextColor(Color.WHITE);
        estoyAquiTextView.setText("Indicar que estoy aquí 👋️");

    }

    public void estoyAqui(View view){

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

        if (usuarioActivo){

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UsuarioActivo");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("activo", true);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            object.put("activo", false);
                            object.put("encuestaAplicada", false);
                            object.put("fechaEncuestaTerminada", fecha);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null){

                                        usuarioActivo = false;
                                        buttonClienteInactivo();

                                        terminarSppiner();

                                        reloadData();

                                    } else {

                                        terminarSppiner();

                                        Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else {

            ParseObject object = new ParseObject("UsuarioActivo");
            object.put("nombreComercio", nombreComercio);
            object.put("comercioId", comercioId);
            object.put("email", correoCliente);
            object.put("activo", true);
            object.put("encuestaAplicada", false);
            object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
            object.put("fechaCreacion", fecha);
            object.put("fechaEncuestaTerminada", fecha);
            object.put("nombreUsuario", nombreCliente + " " + apellidoCliente);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null){

                        usuarioActivo = true;
                        buttonClienteActivo();

                        terminarSppiner();

                    } else {

                        terminarSppiner();

                        Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void cargaDatosComercio(){

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
            this.fechaComparacion = dateFormat.parse(valueOf4 + "/" + valueOf3 + "/" + valueOf2 + " " + valueOf + ":" + valueOf5 + ":" + valueOf6);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        //fecha 30 días antes
        fechaInicioMes = Calendar.getInstance();
        fechaInicioMes.set(Calendar.DATE, -30);

        tieneLogo = false;
        usuarioActivo = false;
        ofreceRecompensa = false;
        tieneDescripcion = false;
        tieneTienda = false;
        clienteTieneProduc = false;
        tieneHistorial = false;
        encuestaPendiente = false;
        visitaRegistrada = false;
        contadorPuntos = 0;
        ofreceVIP = false;
        esVIP = false;
        visitasCliente = 0;
        ofrecePuntos = false;
        porcentajeNivel1 = 0;
        consumoPromedio = 0;
        american = false;
        visa = false;
        mastercard = false;
        numeroContacto = 0;
        direccion = "";
        horario = "";

        consumoEnviadoArray.clear();
        puntosEnviadosArray.clear();

        descripcionCom = "(Sin descripción)";

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comercios");
        query.whereEqualTo("nombreComercio", nombreComercio);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        comercioId = object.getObjectId();
                        consumoPromedio = object.getInt("consumoPromedio");
                        visa = object.getBoolean("visa");
                        mastercard = object.getBoolean("mastercard");
                        american = object.getBoolean("american");
                        numeroContacto = object.getInt("numeroContacto");
                        direccion = object.getString("direccion");
                        horario = object.getString("horario");

                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UsuarioActivo");
                    query.whereEqualTo("comercioId", comercioId);
                    query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                    query.whereEqualTo("activo", true);
                    query.setLimit(1);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                if (objects.size() > 0){

                                    for (ParseObject object : objects){

                                        long diff = fechaComparacion.getTime() - object.getDate("fechaCreacion").getTime();
                                        long diffInHours = TimeUnit.MILLISECONDS.toMinutes(diff);

                                        if (diffInHours >= 40){

                                            object.put("activo", false);
                                            object.put("fechaEncuestaTerminada", fechaComparacion);
                                            object.saveInBackground();

                                            usuarioActivo = false;

                                            buttonClienteInactivo();

                                        } else {

                                            usuarioActivo = true;
                                            buttonClienteActivo();

                                        }
                                    }

                                } else {

                                    usuarioActivo = false;

                                    buttonClienteInactivo();

                                }

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestaPendiente");
                                query.whereEqualTo("comercioId", comercioId);
                                query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                query.whereEqualTo("activo", true);
                                query.setLimit(1);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {

                                        if (e == null){

                                            if (objects.size() > 0){

                                                for (ParseObject object : objects) {

                                                    long diff = fechaComparacion.getTime() - object.getDate("fechaCreacion").getTime();
                                                    long diffInDays = TimeUnit.MILLISECONDS.toHours(diff);

                                                    if (diffInDays >= 8){

                                                        object.put("activo", false);
                                                        object.put("fechaModificacion", fechaComparacion);
                                                        object.saveInBackground();

                                                        encuestaPendiente = false;

                                                    } else {

                                                        encuestaPendiente = true;

                                                        encuestaActiva = object.getString("nombreEncuesta");
                                                        encuestaActivaId = object.getString("encuestaId");
                                                        numeroDePreguntas = object.getInt("numeroDePreguntas");
                                                        recompensaActiva = object.getString("recompensaActiva");
                                                        nombreColaborador = object.getString("nombreColaborador");
                                                        colaboradorId = object.getString("colaboradorId");
                                                        correoColaborador = object.getString("correoColaborador");

                                                    }
                                                }

                                            } else {

                                                encuestaPendiente = false;
                                            }


                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
                                            query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                            query.whereEqualTo("comercioId", comercioId);
                                            query.setLimit(1);
                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {

                                                    if (e == null){

                                                        if (objects.size() > 0){

                                                            for (ParseObject object : objects){

                                                                puntosCliente = object.getDouble("puntos");

                                                            }

                                                        } else {

                                                            puntosCliente = 0.0;

                                                        }

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

                                                                    Log.i("prueba", String.valueOf(ofreceVIP));

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

                                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("DescripcionComercio");
                                                                                query.whereEqualTo("comercioId", comercioId);
                                                                                query.setLimit(1);
                                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                                    @Override
                                                                                    public void done(List<ParseObject> objects, ParseException e) {

                                                                                        if (e == null){

                                                                                            if (objects.size() > 0){

                                                                                                tieneDescripcion = true;

                                                                                                for (ParseObject object : objects){

                                                                                                    descripcionCom = object.getString("descripcion");

                                                                                                }
                                                                                            }

                                                                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosTienda");
                                                                                            query.whereEqualTo("comercioId", comercioId);
                                                                                            query.whereGreaterThan("cantidadDisponible", 0);
                                                                                            query.whereEqualTo("eliminado", false);
                                                                                            query.setLimit(1);
                                                                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                                                                @Override
                                                                                                public void done(List<ParseObject> objects, ParseException e) {

                                                                                                    if (e == null){

                                                                                                        if (objects.size() >0){

                                                                                                            tieneTienda = true;

                                                                                                        }

                                                                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosCliente");
                                                                                                        query.whereEqualTo("comercioId", comercioId);
                                                                                                        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                                                                                        query.setLimit(1);
                                                                                                        query.findInBackground(new FindCallback<ParseObject>() {
                                                                                                            @Override
                                                                                                            public void done(List<ParseObject> objects, ParseException e) {

                                                                                                                if (e == null){

                                                                                                                    if (objects.size() > 0){

                                                                                                                        clienteTieneProduc = true;

                                                                                                                    }

                                                                                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("HistorialPuntos");
                                                                                                                    query.whereEqualTo("comercioId", comercioId);
                                                                                                                    query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                                                                                                    query.setLimit(1);
                                                                                                                    query.findInBackground(new FindCallback<ParseObject>() {
                                                                                                                        @Override
                                                                                                                        public void done(List<ParseObject> objects, ParseException e) {

                                                                                                                            if (e == null){

                                                                                                                                if (objects.size() > 0){

                                                                                                                                    tieneHistorial = true;

                                                                                                                                }

                                                                                                                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                                                                                                                query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                                                                                                                                query.setLimit(1);
                                                                                                                                query.findInBackground(new FindCallback<ParseUser>() {
                                                                                                                                    @Override
                                                                                                                                    public void done(List<ParseUser> objects, ParseException e) {

                                                                                                                                        if (e == null){

                                                                                                                                            for (ParseObject object : objects){

                                                                                                                                                nombreCliente = object.getString("nombre");
                                                                                                                                                apellidoCliente = object.getString("apellido");
                                                                                                                                                correoCliente = object.getString("email");

                                                                                                                                            }

                                                                                                                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosEnviados");
                                                                                                                                            query.whereEqualTo("comercioId", comercioId);
                                                                                                                                            query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                                                                                                                            query.orderByDescending("fechaCreacion");
                                                                                                                                            query.setLimit(5);
                                                                                                                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                                                                                                                @Override
                                                                                                                                                public void done(List<ParseObject> objects, ParseException e) {


                                                                                                                                                    if (e == null){

                                                                                                                                                        for (ParseObject object : objects){

                                                                                                                                                            long diff = fechaComparacion.getTime() - object.getDate("fechaCreacion").getTime();
                                                                                                                                                            long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);

                                                                                                                                                            if (diffInHours <= 8){

                                                                                                                                                                visitaRegistrada = true;

                                                                                                                                                                consumoEnviadoArray.add(object.getString("consumo"));
                                                                                                                                                                puntosEnviadosArray.add(object.getDouble("puntosEnviados"));

                                                                                                                                                                contadorPuntos = contadorPuntos + 1;

                                                                                                                                                            }
                                                                                                                                                        }

                                                                                                                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("CodigoQRCliente");
                                                                                                                                                        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                                                                                                                                        query.setLimit(1);
                                                                                                                                                        query.findInBackground(new FindCallback<ParseObject>() {
                                                                                                                                                            @Override
                                                                                                                                                            public void done(List<ParseObject> objects, ParseException e) {

                                                                                                                                                                if (e == null){

                                                                                                                                                                    for (ParseObject object : objects){

                                                                                                                                                                        ParseFile parseFile = (ParseFile) object.get("codigoQRCliente");
                                                                                                                                                                        parseFile.getDataInBackground(new GetDataCallback() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void done(byte[] data, ParseException e) {

                                                                                                                                                                                if (e == null){

                                                                                                                                                                                    qrClienteImage = BitmapFactory.decodeByteArray(data, 0, data.length);


                                                                                                                                                                                } else {

                                                                                                                                                                                    swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                                    terminarSppiner();

                                                                                                                                                                                    Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                                                                                }
                                                                                                                                                                            }
                                                                                                                                                                        });

                                                                                                                                                                    }

                                                                                                                                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosEnviados");
                                                                                                                                                                    query.whereEqualTo("comercioId", comercioId);
                                                                                                                                                                    query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
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

                                                                                                                                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("ImagenComercio");
                                                                                                                                                                                query.whereEqualTo("comercioId", comercioId);
                                                                                                                                                                                query.setLimit(1);
                                                                                                                                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                                                                                                                                    @Override
                                                                                                                                                                                    public void done(List<ParseObject> objects, ParseException e) {

                                                                                                                                                                                        if (e == null){

                                                                                                                                                                                            if (objects.size() > 0){

                                                                                                                                                                                                tieneLogo = true;

                                                                                                                                                                                                for (ParseObject object : objects){

                                                                                                                                                                                                    ParseFile parseFile = (ParseFile) object.get("imagenPerfil");
                                                                                                                                                                                                    parseFile.getDataInBackground(new GetDataCallback() {
                                                                                                                                                                                                        @Override
                                                                                                                                                                                                        public void done(byte[] data, ParseException e) {

                                                                                                                                                                                                            if (e == null){

                                                                                                                                                                                                                logoComercio = BitmapFactory.decodeByteArray(data, 0, data.length);

                                                                                                                                                                                                                descripcionListView.setAdapter(customAdapter);

                                                                                                                                                                                                                swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                                                                terminarSppiner();


                                                                                                                                                                                                            } else {

                                                                                                                                                                                                                swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                                                                terminarSppiner();

                                                                                                                                                                                                                Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                                                                                                            }
                                                                                                                                                                                                        }
                                                                                                                                                                                                    });
                                                                                                                                                                                                }

                                                                                                                                                                                            } else {

                                                                                                                                                                                                descripcionListView.setAdapter(customAdapter);

                                                                                                                                                                                                swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                                                terminarSppiner();

                                                                                                                                                                                            }

                                                                                                                                                                                        } else {

                                                                                                                                                                                            swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                                            terminarSppiner();

                                                                                                                                                                                            Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                });

                                                                                                                                                                            } else {

                                                                                                                                                                                swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                                terminarSppiner();

                                                                                                                                                                                Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();


                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    });

                                                                                                                                                                } else {

                                                                                                                                                                    descripcionListView.setAdapter(customAdapter);

                                                                                                                                                                    swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                    terminarSppiner();

                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        });

                                                                                                                                                    } else {

                                                                                                                                                        swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                        terminarSppiner();

                                                                                                                                                        Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });


                                                                                                                                        } else {

                                                                                                                                            swipeRefreshLayout.setRefreshing(false);

                                                                                                                                            terminarSppiner();

                                                                                                                                            Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });

                                                                                                                            } else {

                                                                                                                                swipeRefreshLayout.setRefreshing(false);

                                                                                                                                terminarSppiner();

                                                                                                                                Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                            }
                                                                                                                        }
                                                                                                                    });

                                                                                                                } else {

                                                                                                                    swipeRefreshLayout.setRefreshing(false);

                                                                                                                    terminarSppiner();

                                                                                                                    Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                }
                                                                                                            }
                                                                                                        });

                                                                                                    } else {

                                                                                                        swipeRefreshLayout.setRefreshing(false);

                                                                                                        terminarSppiner();

                                                                                                        Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                        } else {

                                                                                            swipeRefreshLayout.setRefreshing(false);

                                                                                            terminarSppiner();

                                                                                            Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    }
                                                                                });

                                                                            } else {

                                                                                swipeRefreshLayout.setRefreshing(false);

                                                                                terminarSppiner();

                                                                                Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();


                                                                            }
                                                                        }
                                                                    });

                                                                } else {

                                                                    swipeRefreshLayout.setRefreshing(false);

                                                                    terminarSppiner();

                                                                    Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });

                                                    } else {

                                                        swipeRefreshLayout.setRefreshing(false);

                                                        terminarSppiner();

                                                        Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });

                                        } else {

                                            swipeRefreshLayout.setRefreshing(false);

                                            terminarSppiner();

                                            Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            } else {

                                swipeRefreshLayout.setRefreshing(false);

                                terminarSppiner();

                                Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    swipeRefreshLayout.setRefreshing(false);

                    terminarSppiner();

                    Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void reloadData(){

        customAdapter = new CustomAdapter();

        if (usarQR){

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
                this.fechaComparacion = dateFormat.parse(valueOf4 + "/" + valueOf3 + "/" + valueOf2 + " " + valueOf + ":" + valueOf5 + ":" + valueOf6);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UsuarioActivo");
            query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("activo", true);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        if (objects.size() > 0){

                            usuarioActivoQR = true;

                            for (ParseObject object : objects){

                                long diff = fechaComparacion.getTime() - object.getDate("fechaCreacion").getTime();
                                long diffInHours = TimeUnit.MILLISECONDS.toMinutes(diff);

                                if (diffInHours >= 40){

                                    usuarioActivoQR = false;
                                    estoyAquiTextView.setVisibility(View.INVISIBLE);

                                } else {

                                    nombreComercio = object.getString("nombreComercio");
                                    estoyAquiTextView.setVisibility(View.VISIBLE);

                                }

                            }

                            cargaDatosComercio();

                        } else {

                            usuarioActivoQR = false;
                            estoyAquiTextView.setVisibility(View.INVISIBLE);

                            cargaDatosComercio();

                        }

                    } else {



                        swipeRefreshLayout.setRefreshing(false);

                        terminarSppiner();

                        Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else {

            cargaDatosComercio();

        }
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

        descripcionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent;

                if (i == 2){

                    intent = new Intent(getApplicationContext(), PreguntaCaritasActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    intent.putExtra("encuestaActiva", encuestaActiva);
                    intent.putExtra("encuestaActivaId", encuestaActivaId);
                    intent.putExtra("numeroDePreguntas", numeroDePreguntas);
                    intent.putExtra("recompensaActiva", recompensaActiva);
                    intent.putExtra("nombreColaborador", nombreColaborador);
                    intent.putExtra("colaboradorId", colaboradorId);
                    intent.putExtra("correoColaborador", correoColaborador);
                    intent.putExtra("nombreCliente", nombreCliente);
                    intent.putExtra("nombreComercio", nombreComercio);
                    intent.putExtra("apellidoCliente", apellidoCliente);
                    startActivity(intent);

                }

                if (i == 8){

                    intent = new Intent(getApplicationContext(), TiendaActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }

                if (i == 9){

                    intent = new Intent(getApplicationContext(), MisComprasActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }

                if (i == 10){

                    intent = new Intent(getApplicationContext(), HistorialPuntosActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }
            }
        });

        reloadData();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descripcion_comercio);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        estoyAquiTextView = (TextView) findViewById(R.id.estoyAquiTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshDescLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        Intent intent = getIntent();

        nombreComercio = intent.getStringExtra("nombreComercio");
        usarQR = intent.getBooleanExtra("usarQR", false);

        descripcionListView = (ListView) findViewById(R.id.descripcionListView);

    }

    class CustomAdapter extends BaseAdapter implements Adapter {

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getViewTypeCount() {
            return 8;

        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0){

                if (usarQR){

                    return 6;
                }

                return 3;

            }

            if (position == 1){

                if (usarQR){

                    if (usuarioActivoQR){

                        return 0;
                    }

                    return 3;

                }

                return 0;

            }

            if (position == 2){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (encuestaPendiente){

                            return 4;

                        }

                        return 3;

                    }

                    return 3;

                }

                if (encuestaPendiente){

                    return 4;

                }

                return 3;

            }

            if (position == 3){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (visitaRegistrada){

                            if (contadorPuntos > 0){

                                return 5;

                            }
                        }

                        return 3;

                    }

                    return 3;

                }

                if (visitaRegistrada){

                    if (contadorPuntos > 0){

                        return 5;

                    }
                }

                return 3;

            }

            if (position == 4){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (visitaRegistrada){

                            if (contadorPuntos > 1){

                                return 5;

                            }
                        }

                        return 3;

                    }

                    return 3;
                }

                if (visitaRegistrada){

                    if (contadorPuntos > 1){

                        return 5;

                    }
                }

                return 3;
            }

            if (position == 5){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (visitaRegistrada){

                            if (contadorPuntos > 2){

                                return 5;

                            }
                        }

                        return 3;
                    }

                    return 3;

                }

                if (visitaRegistrada){

                    if (contadorPuntos > 2){

                        return 5;

                    }
                }

                return 3;

            }

            if (position == 6){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (visitaRegistrada){

                            if (contadorPuntos > 3){

                                return 5;

                            }
                        }

                        return 3;

                    }

                    return 3;
                }

                if (visitaRegistrada){

                    if (contadorPuntos > 3){

                        return 5;

                    }
                }

                return 3;

            }

            if (position == 7){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (visitaRegistrada){

                            if (contadorPuntos > 4){

                                return 5;

                            }
                        }

                        return 3;

                    }

                    return 3;

                }

                if (visitaRegistrada){

                    if (contadorPuntos > 4){

                        return 5;

                    }
                }

                return 3;

            }

            if (position == 8){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (tieneTienda) {

                            return 1;

                        }

                        return 3;

                    }

                    return 3;

                }

                if (tieneTienda) {

                    return 1;

                }

                return 3;

            }

            if (position == 9){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (clienteTieneProduc){

                            return 1;

                        }

                        return 3;
                    }

                    return 3;

                }

                if (clienteTieneProduc){

                    return 1;

                }

                return 3;

            }

            if (position == 10){

                if (usarQR){

                    if (usuarioActivoQR){

                        if (tieneHistorial){

                            return 1;

                        }

                        return 3;

                    }

                    return 3;

                }

                if (tieneHistorial){

                    return 1;

                }

                return 3;

            }

            if (position == 11){

                if (usarQR){

                    if (usuarioActivoQR){

                        return 7;

                    }

                    return 3;

                }

                return 7;

            }

            if (position == 12){

                if (usarQR){

                    if (usuarioActivoQR){

                        return 2;

                    }

                    return 3;

                }

                return 2;
            }

            return 1;

        }

        @Override
        public int getCount() {
            return 13;
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

            int itemViewType = getItemViewType(i);

            if (view == null) {

                if (itemViewType == 0) {

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_1, null);

                    view.setEnabled(false);

                    TextView nombreComTexetView = (TextView) view.findViewById(R.id.op1DescTexxtView);
                    TextView puntosClienteTextView = (TextView) view.findViewById(R.id.op2GeneralTextView);
                    ImageView logoImageView = (ImageView) view.findViewById(R.id.op1DescImageView);
                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op2DescImageView);
                    TextView op1TextView = (TextView) view.findViewById(R.id.op4DescTextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op5DescTextView);
                    TextView op3TextView = (TextView) view.findViewById(R.id.op6DescTextView);
                    TextView op4TextView = (TextView) view.findViewById(R.id.op7DescTextView);
                    TextView op5TextView = (TextView) view.findViewById(R.id.op8DescTextView);

                    nombreComTexetView.setText(nombreComercio);
                    puntosClienteTextView.setText(String.valueOf(puntosCliente));
                    op5TextView.setText(String.valueOf(visitasCliente));

                    if (ofreceVIP){

                        if (esVIP) {

                            if (visitasCliente >= nivel3){

                                op1ImageView.setImageResource(R.drawable.pando_nivel3);
                                op1TextView.setText("VIP Nivel 3 Titanium");
                                op3TextView.setText(String.valueOf(porcentajeNivel3) + "%");


                            } else {

                                if (visitasCliente >= nivel2){

                                    op1ImageView.setImageResource(R.drawable.pando_nivel2);
                                    op1TextView.setText("VIP Nivel 2 Platino");
                                    op3TextView.setText(String.valueOf(porcentajeNivel2) + "%");

                                } else {

                                    op1ImageView.setImageResource(R.drawable.pando_nivel1);
                                    op1TextView.setText("VIP Nivel 1 Oro");
                                    op3TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                                }
                            }


                        } else {

                            op1ImageView.setImageResource(R.drawable.pando_nivel1);
                            op1TextView.setText("VIP Nivel 1 Oro");
                            op3TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                        }

                    } else {

                        op1ImageView.setImageResource(R.drawable.nop);
                        op1TextView.setText("Programa niveles VIP");
                        op3TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                    }

                    if (ofrecePuntos) {

                        op2TextView.setText("Puntos por consumo");

                        if (porcentajeNivel3 > 0) {


                        } else {

                            op3TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                        }

                    } else {

                        op2TextView.setText("No programa de puntos");
                        op3TextView.setText("0 %");

                    }

                    if (tieneLogo) {

                        logoImageView.setImageBitmap(logoComercio);

                    } else {

                        logoImageView.setImageResource(R.drawable.store);

                    }

                    return view;

                } else if (itemViewType == 1){

                    int pos = i - 8;

                    view = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

                    TextView opcion1TextView = (TextView) view.findViewById(R.id.opc1ImaTextView);
                    TextView opcion2TextView = (TextView) view.findViewById(R.id.opc2ImaTextView);
                    ImageView opcion1ImageView = (ImageView) view.findViewById(R.id.opc1ImaImageView);

                    opcion1TextView.setText(TITULOS[pos]);
                    opcion2TextView.setText(DESCRIPCIONES[pos]);
                    opcion1ImageView.setImageResource(IMAGES[pos]);

                    return view;

                } else if (itemViewType == 2){

                    view = mInflater.inflate(R.layout.general_titulo_descripcion, null);

                    view.setEnabled(false);

                    TextView opcion1TextView = (TextView) view.findViewById(R.id.op1GeneralTextView);
                    TextView opcion2TextView = (TextView) view.findViewById(R.id.op2GeneralTextView);

                    opcion1TextView.setText("Descripción");
                    opcion2TextView.setText(descripcionCom);

                    if (!tieneDescripcion){

                        opcion2TextView.setTypeface(null, Typeface.ITALIC);

                    }
                } else if (itemViewType == 3){

                    view = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return view;

                } else if (itemViewType == 4){

                    view = mInflater.inflate(R.layout.general_una_opcion_sola, null);

                    TextView tituloTextView = (TextView) view.findViewById(R.id.opcionSolaTextView);

                    tituloTextView.setText("Contestar ENCUESTA de servicio AQUÍ");
                    tituloTextView.setBackgroundColor(getResources().getColor(R.color.morado_Pando));
                    tituloTextView.setTextColor(Color.WHITE);

                    return view;

                } else if (itemViewType == 5){

                    int pos = i - 3;

                    view = mInflater.inflate(R.layout.historial_puntos_cell_1, null);

                    TextView op1TextView = (TextView) view.findViewById(R.id.op1HistTextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op4HistTextView);
                    TextView op3TextView = (TextView) view.findViewById(R.id.op2HistTextView);
                    TextView op4TextView = (TextView) view.findViewById(R.id.op5HistTextView);
                    TextView op5TextView = (TextView) view.findViewById(R.id.op3HistTextView);

                    op1TextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                    op1TextView.setTypeface(null, Typeface.BOLD);
                    op3TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op5TextView.setTextColor(Color.BLACK);

                    op1TextView.setText("Nuevo");
                    op2TextView.setText("Puntos recibidos");
                    op3TextView.setText(String.valueOf(puntosEnviadosArray.get(pos)));
                    op4TextView.setText("Consumo");
                    op5TextView.setText("$ " + consumoEnviadoArray.get(pos));

                    return view;

                } else if (itemViewType == 6){

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_2, null);

                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1QRImageView);
                    ImageView op2ImageView = (ImageView) view.findViewById(R.id.op2QRImageView);

                    op1ImageView.setImageBitmap(qrClienteImage);
                    op2ImageView.setImageResource(R.drawable.download);

                    return view;

                } else if (itemViewType == 7){

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_3, null);

                    TextView op1TextView = (TextView) view.findViewById(R.id.op1DescCell3TextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op2DescCell3TextView);
                    TextView op3TextView = (TextView) view.findViewById(R.id.op3DescCell3TextView);
                    TextView op4TextView = (TextView) view.findViewById(R.id.op4DescCell3TextView);
                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1DescCell3ImageView);
                    ImageView op2ImageView = (ImageView) view.findViewById(R.id.op2DescCell3ImageView);
                    ImageView op3ImageView = (ImageView) view.findViewById(R.id.op3DescCell3ImageView);

                    if (consumoPromedio > 0){

                        op1TextView.setText(String.valueOf(consumoPromedio));

                    } else {

                        op1TextView.setText("N/A");

                    }

                    if (!visa && !mastercard && !american){

                        op3ImageView.setImageResource(R.drawable.nop);

                    } else {

                        if (american){

                            op1ImageView.setImageResource(R.drawable.american_express);

                        }

                        if (mastercard){

                            op2ImageView.setImageResource(R.drawable.mastercard);

                        }

                        if (visa){

                            op3ImageView.setImageResource(R.drawable.visa);

                        }
                    }

                    if (numeroContacto > 0){

                        op2TextView.setText(String.valueOf(numeroContacto));

                    } else {

                        op2TextView.setText("N/A");

                    }

                    if (direccion.matches("")){

                        op3TextView.setText("Sin dirección");

                    } else {

                        op3TextView.setText(direccion);

                    }

                    if (horario.matches("")){

                        op4TextView.setText("N/A");

                    } else {

                        op4TextView.setText(horario);

                    }

                    return view;

                }

            } else {

                if (itemViewType == 0) {

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_1, null);

                    view.setEnabled(false);

                    TextView nombreComTexetView = (TextView) view.findViewById(R.id.op1DescTexxtView);
                    TextView puntosClienteTextView = (TextView) view.findViewById(R.id.op2GeneralTextView);
                    ImageView logoImageView = (ImageView) view.findViewById(R.id.op1DescImageView);
                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op2DescImageView);
                    TextView op1TextView = (TextView) view.findViewById(R.id.op4DescTextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op5DescTextView);
                    TextView op3TextView = (TextView) view.findViewById(R.id.op6DescTextView);
                    TextView op4TextView = (TextView) view.findViewById(R.id.op7DescTextView);
                    TextView op5TextView = (TextView) view.findViewById(R.id.op8DescTextView);

                    nombreComTexetView.setText(nombreComercio);
                    puntosClienteTextView.setText(String.valueOf(puntosCliente));
                    op5TextView.setText(String.valueOf(visitasCliente));

                    if (ofreceVIP){

                        if (esVIP) {

                            if (visitasCliente >= nivel3){

                                op1ImageView.setImageResource(R.drawable.pando_nivel3);
                                op1TextView.setText("VIP Nivel 3 Titanium");
                                op3TextView.setText(String.valueOf(porcentajeNivel3) + "%");


                            } else {

                                if (visitasCliente >= nivel2){

                                    op1ImageView.setImageResource(R.drawable.pando_nivel2);
                                    op1TextView.setText("VIP Nivel 2 Platino");
                                    op3TextView.setText(String.valueOf(porcentajeNivel2) + "%");

                                } else {

                                    op1ImageView.setImageResource(R.drawable.pando_nivel1);
                                    op1TextView.setText("VIP Nivel 1 Oro");
                                    op3TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                                }
                            }


                        } else {

                            op1ImageView.setImageResource(R.drawable.pando_nivel1);
                            op1TextView.setText("VIP Nivel 1 Oro");
                            op3TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                        }

                    } else {

                        op1ImageView.setImageResource(R.drawable.nop);
                        op1TextView.setText("Programa niveles VIP");
                        op3TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                    }

                    if (ofrecePuntos) {

                        op2TextView.setText("Puntos por consumo");

                        if (porcentajeNivel3 > 0) {


                        } else {

                            op3TextView.setText(String.valueOf(porcentajeNivel1) + "%");

                        }

                    } else {

                        op2TextView.setText("No rograma de puntos");
                        op3TextView.setText("0 %");

                    }

                    if (tieneLogo) {

                        logoImageView.setImageBitmap(logoComercio);

                    } else {

                        logoImageView.setImageResource(R.drawable.store);

                    }

                    return view;

                } else if (itemViewType == 1){

                    int pos = i - 8;

                    view = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

                    TextView opcion1TextView = (TextView) view.findViewById(R.id.opc1ImaTextView);
                    TextView opcion2TextView = (TextView) view.findViewById(R.id.opc2ImaTextView);
                    ImageView opcion1ImageView = (ImageView) view.findViewById(R.id.opc1ImaImageView);

                    opcion1TextView.setText(TITULOS[pos]);
                    opcion2TextView.setText(DESCRIPCIONES[pos]);
                    opcion1ImageView.setImageResource(IMAGES[pos]);

                    return view;

                } else if (itemViewType == 2){

                    view = mInflater.inflate(R.layout.general_titulo_descripcion, null);

                    view.setEnabled(false);

                    TextView opcion1TextView = (TextView) view.findViewById(R.id.op1GeneralTextView);
                    TextView opcion2TextView = (TextView) view.findViewById(R.id.op2GeneralTextView);

                    opcion1TextView.setText("Descripción");
                    opcion2TextView.setText(descripcionCom);

                    if (!tieneDescripcion){

                        opcion2TextView.setTypeface(null, Typeface.ITALIC);

                    }
                } else if (itemViewType == 3){

                    view = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return view;

                } else if (itemViewType == 4){

                    view = mInflater.inflate(R.layout.general_una_opcion_sola, null);

                    TextView tituloTextView = (TextView) view.findViewById(R.id.opcionSolaTextView);

                    tituloTextView.setText("Contestar ENCUESTA de servicio AQUÍ");
                    tituloTextView.setBackgroundColor(getResources().getColor(R.color.morado_Pando));
                    tituloTextView.setTextColor(Color.WHITE);

                    return view;

                } else if (itemViewType == 5){

                    int pos = i - 3;

                    view = mInflater.inflate(R.layout.historial_puntos_cell_1, null);

                    TextView op1TextView = (TextView) view.findViewById(R.id.op1HistTextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op4HistTextView);
                    TextView op3TextView = (TextView) view.findViewById(R.id.op2HistTextView);
                    TextView op4TextView = (TextView) view.findViewById(R.id.op5HistTextView);
                    TextView op5TextView = (TextView) view.findViewById(R.id.op3HistTextView);

                    op1TextView.setTextColor(getResources().getColor(R.color.morado_Pando));
                    op1TextView.setTypeface(null, Typeface.BOLD);
                    op3TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op5TextView.setTextColor(Color.BLACK);

                    op1TextView.setText("Nuevo");
                    op2TextView.setText("Puntos recibidos");
                    op3TextView.setText(String.valueOf(puntosEnviadosArray.get(pos)));
                    op4TextView.setText("Consumo");
                    op5TextView.setText("$ " + consumoEnviadoArray.get(pos));

                    return view;

                } else if (itemViewType == 6){

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_2, null);

                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1QRImageView);
                    ImageView op2ImageView = (ImageView) view.findViewById(R.id.op2QRImageView);

                    op1ImageView.setImageBitmap(qrClienteImage);
                    op2ImageView.setImageResource(R.drawable.download);

                    return view;

                } else if (itemViewType == 7){

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_3, null);

                    TextView op1TextView = (TextView) view.findViewById(R.id.op1DescCell3TextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op2DescCell3TextView);
                    TextView op3TextView = (TextView) view.findViewById(R.id.op3DescCell3TextView);
                    TextView op4TextView = (TextView) view.findViewById(R.id.op4DescCell3TextView);
                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1DescCell3ImageView);
                    ImageView op2ImageView = (ImageView) view.findViewById(R.id.op2DescCell3ImageView);
                    ImageView op3ImageView = (ImageView) view.findViewById(R.id.op3DescCell3ImageView);

                    if (consumoPromedio > 0){

                        op1TextView.setText(String.valueOf(consumoPromedio));

                    } else {

                        op1TextView.setText("N/A");

                    }

                    if (!visa && !mastercard && !american){

                        op3ImageView.setImageResource(R.drawable.nop);

                    } else {

                        if (american){

                            op1ImageView.setImageResource(R.drawable.american_express);

                        }

                        if (mastercard){

                            op2ImageView.setImageResource(R.drawable.mastercard);

                        }

                        if (visa){

                            op3ImageView.setImageResource(R.drawable.visa);

                        }
                    }

                    if (numeroContacto > 0){

                        op2TextView.setText(String.valueOf(numeroContacto));

                    } else {

                        op2TextView.setText("N/A");

                    }

                    if (direccion.matches("")){

                        op3TextView.setText("Sin dirección");

                    } else {

                        op3TextView.setText(direccion);

                    }

                    if (horario.matches("")){

                        op4TextView.setText("N/A");

                    } else {

                        op4TextView.setText(horario);

                    }

                    return view;

                }
            }

            return view;

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
