package com.parse.starter.VistaClientes.DescripcionComercio;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.MainActivity;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.Encuestas.PreguntaCaritasActivity;
import com.parse.starter.VistaClientes.HistorialPuntos.HistorialPuntosActivity;
import com.parse.starter.VistaClientes.MisCompras.MisComprasActivity;
import com.parse.starter.VistaClientes.TiendaComercio.TiendaActivity;
import com.parse.starter.VistaClientes.VerMenu.VerMenuActivity;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
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
    String numeroWhats;
    String numeroLlamada;
    String distanciaComGuardar;
    String promoVecino;
    String promoNoVecino;

    Boolean ofreceVIP;
    Boolean esVIP;
    Boolean visa;
    Boolean american;
    Boolean mastercard;
    Boolean esVecinoGuardar;
    Boolean tieneMenu;

    LocationManager locationManager;
    LocationListener locationListener;

    Calendar fechaInicioMes;

    ArrayList<String> consumoEnviadoArray = new ArrayList();

    String[] TITULOS = {"Ver menú", "Enviar WhatsApp", "Llamar", "Comprar cupones", "Ver mis cupones", "Ver historial de puntos"};
    String[] DESCRIPCIONES = {"", "", "", "Compra productos o servicios con puntos aquí", "", ""};

    int[] IMAGES = {R.drawable.menu, R.drawable.whatsapp_2, R.drawable.call, R.drawable.shop, R.drawable.configurar_recompensas, R.drawable.list};

    Double puntosCliente;
    Double distanceKm;

    ArrayList<Double> puntosEnviadosArray = new ArrayList();

    ArrayList<Bitmap> imagenesPortadaArray = new ArrayList();
    ArrayList<ParseFile> parseFileArray = new ArrayList();

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
    int totalDeVisitas;
    int distanciaEnvio;

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
    Boolean tieneImagenPortada;
    Boolean cargaCompleta;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location parseLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                    Log.i("Prueba", String.valueOf(parseLocation));

                    reloadData();

                }
            }
        }
    }

    private void buttonClienteActivo() {

        estoyAquiTextView.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        estoyAquiTextView.setTextColor(Color.WHITE);
        estoyAquiTextView.setText("Visible para el comercio 👍");

    }

    private void buttonClienteInactivo() {

        estoyAquiTextView.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        estoyAquiTextView.setTextColor(Color.WHITE);
        estoyAquiTextView.setText("Para recibir puntos, presiona aquí 👈️");

    }

    public void estoyAqui(View view) {

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

        if (usuarioActivo) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UsuarioActivo");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("activo", true);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {

                        for (ParseObject object : objects) {

                            object.put("activo", false);
                            object.put("encuestaAplicada", false);
                            object.put("fechaEncuestaTerminada", fecha);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null) {

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
            object.put("esVecino", esVecinoGuardar);
            object.put("distanciaComercio", distanciaComGuardar);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {

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

    private void updateUserLocation(final Location location){

        Log.i("Prueba", "Cuantas");

        distanceKm = 0.0;

        final ParseGeoPoint geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comercios");
        query.whereEqualTo("objectId", comercioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                Log.i("Prueba", "Veces");

                if (e == null) {

                    if (objects.size() > 0){

                        for (ParseObject object : objects) {

                            distanceKm = geoPointLocation.distanceInKilometersTo(object.getParseGeoPoint("location"));
                            String str = String.format("%1.2f", distanceKm);
                            Log.i("Prueba distancia Km:", str);

                            locationManager.removeUpdates(locationListener);

                            if (cargaCompleta){

                                Log.i("Prueba", "Pasa");

                                descripcionListView.setAdapter(customAdapter);

                                swipeRefreshLayout.setRefreshing(false);

                                terminarSppiner();

                            }
                        }
                    }
                }
            }
        });
    }

    private void cargaDatosComercio() {

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
        ofreceVIP = false;
        esVIP = false;
        ofrecePuntos = false;
        american = false;
        visa = false;
        mastercard = false;
        tieneImagenPortada = false;
        esVecinoGuardar = false;
        tieneMenu = false;
        cargaCompleta = false;
        contadorPuntos = 0;
        visitasCliente = 0;
        porcentajeNivel1 = 0;
        consumoPromedio = 0;
        numeroContacto = 0;
        totalDeVisitas = 0;
        distanciaEnvio = 0;
        consumoEnviadoArray.clear();
        puntosEnviadosArray.clear();
        imagenesPortadaArray.clear();
        parseFileArray.clear();
        direccion = "";
        horario = "";
        numeroWhats = "";
        numeroLlamada = "";
        distanciaComGuardar = "";
        promoVecino = "";
        promoNoVecino = "";
        descripcionCom = "(Sin descripción)";

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                updateUserLocation(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Log.i("Prueba", "PAN");

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

                        if (object.get("distanciaEnvio") != null) {

                            distanciaEnvio = object.getInt("distanciaEnvio");
                            numeroWhats = object.getString("numeroWhats");
                            numeroLlamada = object.getString("numeroLlamada");

                        }
                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ImagenMenu");
                    query.whereEqualTo("comercioId", comercioId);
                    query.setLimit(1);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                if (objects.size() > 0){

                                    tieneMenu = true;

                                }

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("VisitasCliente");
                                query.whereEqualTo("comercioId", comercioId);
                                query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                                query.setLimit(1);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {

                                        if (e == null){

                                            if (objects.size() > 0){

                                                for (ParseObject object : objects){

                                                    totalDeVisitas = object.getInt("numeroDeVisitas");

                                                }
                                            }

                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("PromocionesPando");
                                            query.whereEqualTo("comercioId", comercioId);
                                            query.setLimit(1);
                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {

                                                    if (e == null){

                                                        if (objects.size() > 0){

                                                            for (ParseObject object : objects){

                                                                promoVecino = object.getString("promoVecino");
                                                                promoNoVecino = object.getString("promoNoVecino");

                                                            }
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

                                                                            if (diffInHours >= 1440){

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

                                                                                                                                                                                                                                        final ParseFile parseFile = (ParseFile) object.get("imagenPerfil");
                                                                                                                                                                                                                                        parseFile.getDataInBackground(new GetDataCallback() {
                                                                                                                                                                                                                                            @Override
                                                                                                                                                                                                                                            public void done(byte[] data, ParseException e) {

                                                                                                                                                                                                                                                if (e == null){

                                                                                                                                                                                                                                                    logoComercio = BitmapFactory.decodeByteArray(data, 0, data.length);

                                                                                                                                                                                                                                                } else {

                                                                                                                                                                                                                                                    swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                                                                                                    terminarSppiner();

                                                                                                                                                                                                                                                    Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                        });
                                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ImagenPortada");
                                                                                                                                                                                                                                    query.whereEqualTo("comercioId", comercioId);
                                                                                                                                                                                                                                    query.orderByAscending("orden");
                                                                                                                                                                                                                                    query.setLimit(5);
                                                                                                                                                                                                                                    query.findInBackground(new FindCallback<ParseObject>() {
                                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                                        public void done(List<ParseObject> objects, ParseException e) {

                                                                                                                                                                                                                                            Log.i("Prueba", "CON JAMON");

                                                                                                                                                                                                                                            if (e == null){

                                                                                                                                                                                                                                                if (objects.size() > 0){

                                                                                                                                                                                                                                                    final int contadorImagenes = objects.size();

                                                                                                                                                                                                                                                    tieneImagenPortada = true;

                                                                                                                                                                                                                                                    for (ParseObject object : objects){

                                                                                                                                                                                                                                                        ParseFile parseFile1 = (ParseFile) object.get("imagenPortada");
                                                                                                                                                                                                                                                        parseFileArray.add(parseFile1);
                                                                                                                                                                                                                                                        parseFile1.getDataInBackground(new GetDataCallback() {
                                                                                                                                                                                                                                                            @Override
                                                                                                                                                                                                                                                            public void done(byte[] data, ParseException e) {

                                                                                                                                                                                                                                                                if (e == null){

                                                                                                                                                                                                                                                                    imagenesPortadaArray.add(BitmapFactory.decodeByteArray(data, 0, data.length));

                                                                                                                                                                                                                                                                } else  {

                                                                                                                                                                                                                                                                    swipeRefreshLayout.setRefreshing(false);

                                                                                                                                                                                                                                                                    terminarSppiner();

                                                                                                                                                                                                                                                                    Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                                                                                                                                                                                                                }

                                                                                                                                                                                                                                                                if (imagenesPortadaArray.size() == contadorImagenes){

                                                                                                                                                                                                                                                                    try {

                                                                                                                                                                                                                                                                        if (ContextCompat.checkSelfPermission(DescripcionComercioActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                                                                                                                                                                                                                                                                            Log.i("Prueba", "Capitan");
                                                                                                                                                                                                                                                                            ActivityCompat.requestPermissions(DescripcionComercioActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                                                                                                                                                                                                                                                                        } else {

                                                                                                                                                                                                                                                                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                                                                                                                                                                                                                                                                            Location parseLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                                                                                                                                                                                                                                                                            Log.i("Prueba", "America");

                                                                                                                                                                                                                                                                            cargaCompleta = true;
                                                                                                                                                                                                                                                                            updateUserLocation(parseLocation);

                                                                                                                                                                                                                                                                        }

                                                                                                                                                                                                                                                                    } catch (Exception m) {

                                                                                                                                                                                                                                                                        Log.i("GeoPoint Logging Error", m.getMessage());

                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                }

                                                                                                                                                                                                                                                            }

                                                                                                                                                                                                                                                        });
                                                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                                } else {

                                                                                                                                                                                                                                                    try {

                                                                                                                                                                                                                                                        if (ContextCompat.checkSelfPermission(DescripcionComercioActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                                                                                                                                                                                                                                            Log.i("Prueba", "Scarlet");
                                                                                                                                                                                                                                                            ActivityCompat.requestPermissions(DescripcionComercioActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                                                                                                                                                                                                                                                        } else {

                                                                                                                                                                                                                                                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                                                                                                                                                                                                                                                            Location parseLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                                                                                                                                                                                                                                                            Log.i("Prueba", "Johanson");
                                                                                                                                                                                                                                                            cargaCompleta = true;
                                                                                                                                                                                                                                                            updateUserLocation(parseLocation);

                                                                                                                                                                                                                                                        }

                                                                                                                                                                                                                                                    } catch (Exception m) {

                                                                                                                                                                                                                                                        Log.i("GeoPoint Logging Error", m.getMessage());

                                                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                                }

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
    protected void onPause() {
        locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

        descripcionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent;

                if (i == 5){

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

                if (i == 11){

                    intent = new Intent(getApplicationContext(), VerMenuActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }

                if (i == 12){

                    String phoneNumberWithCountryCode = "521" + numeroWhats;
                    String message = "";

                    startActivity(
                            new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(
                                            String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
                                    )
                            )
                    );

                }

                if (i == 13){

                    iniciarSppiner();

                    Intent callintent = new Intent(Intent.ACTION_DIAL);
                    callintent.setData(Uri.parse("tel:" + numeroLlamada));
                    terminarSppiner();
                    startActivity(callintent);

                }

                if (i == 14){

                    intent = new Intent(getApplicationContext(), TiendaActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }

                if (i == 15){

                    intent = new Intent(getApplicationContext(), MisComprasActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }

                if (i == 16){

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
            return 11;

        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0){

                //Codigo QR

                if (usarQR){

                    return 6;
                }

                return 3;

            }

            if (position == 1){

                //Imagenes portada

                if (usarQR){

                    if (usuarioActivoQR){

                        if (tieneImagenPortada){

                            return 8;

                        }

                        return 3;

                    }

                    return 3;

                }

                if (tieneImagenPortada){

                    return 8;

                }

                return 3;



            }

            if (position == 2){

                //Nombre comercio y distancia

                if (usarQR){

                    if (usuarioActivoQR){

                        return 9;

                    }

                    return 3;

                }

                return 9;

            }

            if (position == 3){

                //Promociones Pando

                if (usarQR){

                    if (usuarioActivoQR){

                        if (totalDeVisitas >= 2){

                            return 3;

                        }

                        if (promoVecino.matches("")){

                            return 3;

                        }

                        return 10;

                    }

                    return 3;

                }

                if (totalDeVisitas >= 2){

                    return 3;

                }

                if (promoVecino.matches("")){

                    return 3;

                }

                return 10;

            }

            if (position == 4){

                //Programa de Lealtad

                if (usarQR){

                    if (usuarioActivoQR){

                        return 0;
                    }

                    return 3;

                }

                return 0;

            }

            if (position == 5){

                //Encuesta pendiente

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

            if (position == 6){

                //Puntos recien enviados 1

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

            if (position == 7){

                //Puntos recien enviados 2

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

            if (position == 8){

                //Puntos recien enviados 3

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

            if (position == 9){

                //Puntos recien enviados 4

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

            if (position == 10){

                //Puntos recien enviados 5

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

            if (position == 11){

                //Ver Menú

                if (usarQR){

                    if (usuarioActivoQR){

                        if (tieneMenu) {

                            return 1;

                        }

                        return 3;

                    }

                    return 3;

                }

                if (tieneMenu) {

                    return 1;

                }

                return 3;

            }

            if (position == 12){

                //Enviar Whats

                if (usarQR){

                    if (usuarioActivoQR){

                        if (numeroWhats.matches("")) {

                            return 3;

                        }

                        return 1;

                    }

                    return 3;

                }

                if (numeroWhats.matches("")) {

                    return 3;

                }

                return 1;


            }

            if (position == 13){

                //Llamar

                if (usarQR){

                    if (usuarioActivoQR){

                        if (numeroLlamada.matches("")) {

                            return 3;

                        }

                        return 1;

                    }

                    return 3;

                }

                if (numeroLlamada.matches("")) {

                    return 3;

                }

                return 1;

            }

            if (position == 14){

                //Comprar cupones

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

            if (position == 15){

                //Cupones usuario

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

            if (position == 16){

                //Historial de puntos

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

            if (position == 17){

                //Info general

                if (usarQR){

                    if (usuarioActivoQR){

                        return 7;

                    }

                    return 3;

                }

                return 7;

            }

            if (position == 18){

                //Descripcion comercio

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
            return 19;
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

                    //Programa Lealtad

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_1, null);

                    view.setEnabled(false);

                    //TextView nombreComTexetView = (TextView) view.findViewById(R.id.op1DescTexxtView);
                    TextView puntosClienteTextView = (TextView) view.findViewById(R.id.op2GeneralTextView);
                    //ImageView logoImageView = (ImageView) view.findViewById(R.id.op1DescImageView);
                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op2DescImageView);
                    TextView op1TextView = (TextView) view.findViewById(R.id.op4DescTextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op5DescTextView);
                    TextView op3TextView = (TextView) view.findViewById(R.id.op6DescTextView);
                    TextView op4TextView = (TextView) view.findViewById(R.id.op7DescTextView);
                    TextView op5TextView = (TextView) view.findViewById(R.id.op8DescTextView);
                    TextView totalTextView = (TextView) view.findViewById(R.id.op9DescTextView);

                    puntosClienteTextView.setText(String.valueOf(puntosCliente));
                    op5TextView.setText(String.valueOf(visitasCliente));
                    totalTextView.setText(String.valueOf(totalDeVisitas));

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

                    return view;

                } else if (itemViewType == 1){

                    //Opciones: Ver menú - Enviar whats - Llamar - Comprar cupones

                    int pos = i - 11;

                    view = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

                    TextView opcion1TextView = (TextView) view.findViewById(R.id.opc1ImaTextView);
                    TextView opcion2TextView = (TextView) view.findViewById(R.id.opc2ImaTextView);
                    ImageView opcion1ImageView = (ImageView) view.findViewById(R.id.opc1ImaImageView);

                    opcion1TextView.setText(TITULOS[pos]);
                    opcion2TextView.setText(DESCRIPCIONES[pos]);
                    opcion1ImageView.setImageResource(IMAGES[pos]);

                    return view;

                } else if (itemViewType == 2){

                    //Descripcion comercio

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

                    //Celda vacia

                    view = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return view;

                } else if (itemViewType == 4){

                    //Contestar encuesta

                    view = mInflater.inflate(R.layout.general_una_opcion_sola, null);

                    TextView tituloTextView = (TextView) view.findViewById(R.id.opcionSolaTextView);

                    tituloTextView.setText("Contestar ENCUESTA de servicio AQUÍ");
                    tituloTextView.setBackgroundColor(getResources().getColor(R.color.morado_Pando));
                    tituloTextView.setTextColor(Color.WHITE);

                    return view;

                } else if (itemViewType == 5){

                    //Puntos recien enviados

                    int pos = i - 6;

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

                    //Codigo QR

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_2, null);

                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1QRImageView);
                    ImageView op2ImageView = (ImageView) view.findViewById(R.id.op2QRImageView);

                    op1ImageView.setImageBitmap(qrClienteImage);
                    op2ImageView.setImageResource(R.drawable.download);

                    return view;

                } else if (itemViewType == 7){

                    // Info general comercio

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

                } else if (itemViewType == 8) {

                    //Imagenes Portada

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_4, null);

                    view.setEnabled(true);

                    LinearLayout gallery = view.findViewById(R.id.galerry);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

                    for (int x = 0; x < imagenesPortadaArray.size(); x++){

                        View view1 = inflater.inflate(R.layout.descripcion_comercio_item_gallery, null);

                        final ImageView imagenPortada = view1.findViewById(R.id.itemGalleryImageView);

                        parseFileArray.get(x).getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {

                                if (e == null){

                                    imagenPortada.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));

                                }

                            }

                        });

                        gallery.addView(view1);

                    }

                    return view;

                } else if (itemViewType == 9){

                    // Nueva Logo comercio, distancia y envio gratis

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_5, null);

                    ImageView logoImageView = (ImageView) view.findViewById(R.id.op1DescCell5ImageView);
                    TextView nombreComTextView = (TextView) view.findViewById(R.id.op1DescCell5TextView);
                    TextView distKmTextView = (TextView) view.findViewById(R.id.op2DescCell5TextView);
                    TextView vecinoTextView = (TextView) view.findViewById(R.id.op3DescCell5TextView);
                    TextView envioTextView = (TextView) view.findViewById(R.id.op4DescCell5TextView);

                    nombreComTextView.setText(nombreComercio);
                    distKmTextView.setText(String.format("%1.2f", distanceKm) + " Km");
                    distanciaComGuardar = distKmTextView.getText().toString();

                    if (tieneLogo) {

                        logoImageView.setImageBitmap(logoComercio);

                    } else {

                        logoImageView.setImageResource(R.drawable.store);

                    }

                    if (distanciaEnvio > 0){

                        if (distanceKm > distanciaEnvio){

                            //No vecino
                            esVecinoGuardar = false;
                            vecinoTextView.setText("No vecin@");
                            envioTextView.setText("📞Haz tu pedido, pasa por el y ¡Disfruta!\n☝️Envío gratis en distancia menor a " + String.valueOf(distanciaEnvio) + " Km");

                        } else {

                            //Vecino
                            esVecinoGuardar = true;
                            vecinoTextView.setText("Vecin@");
                            vecinoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                            envioTextView.setText("🛵 ¡Envío gratis!");

                        }

                    } else  {

                        esVecinoGuardar = false;
                        vecinoTextView.setText("");
                        envioTextView.setText("📞Haz tu pedido, pasa por el y ¡Disfruta!");

                    }

                    return view;

                } else if (itemViewType == 10){

                    //Promociones Pando

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_6, null);
                    TextView titVecinoTextView = (TextView) view.findViewById(R.id.op1DescCell6TextView);
                    TextView descVecinoTextView = (TextView) view.findViewById(R.id.op2DescCell6TextView);
                    TextView titNoVecinoTextView = (TextView) view.findViewById(R.id.op3DescCell6TextView);
                    TextView descNoVecinoTextView = (TextView) view.findViewById(R.id.op4DescCell6TextView);

                    descVecinoTextView.setText(promoVecino);
                    descNoVecinoTextView.setText(promoNoVecino);

                    if (distanceKm > distanciaEnvio){

                        //No vecino
                        titNoVecinoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                        descNoVecinoTextView.setTextColor(Color.BLACK);

                    } else  {

                        //Vecino
                        titVecinoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                        descVecinoTextView.setTextColor(Color.BLACK);

                    }
                }

            } else {

                if (itemViewType == 0) {

                    //Programa Lealtad

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_1, null);

                    view.setEnabled(false);

                    //TextView nombreComTexetView = (TextView) view.findViewById(R.id.op1DescTexxtView);
                    TextView puntosClienteTextView = (TextView) view.findViewById(R.id.op2GeneralTextView);
                    //ImageView logoImageView = (ImageView) view.findViewById(R.id.op1DescImageView);
                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op2DescImageView);
                    TextView op1TextView = (TextView) view.findViewById(R.id.op4DescTextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op5DescTextView);
                    TextView op3TextView = (TextView) view.findViewById(R.id.op6DescTextView);
                    TextView op4TextView = (TextView) view.findViewById(R.id.op7DescTextView);
                    TextView op5TextView = (TextView) view.findViewById(R.id.op8DescTextView);
                    TextView totalTextView = (TextView) view.findViewById(R.id.op9DescTextView);

                    puntosClienteTextView.setText(String.valueOf(puntosCliente));
                    op5TextView.setText(String.valueOf(visitasCliente));
                    totalTextView.setText(String.valueOf(totalDeVisitas));

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

                    return view;

                } else if (itemViewType == 1){

                    //Opciones: Ver menú - Enviar whats - Llamar - Comprar cupones

                    int pos = i - 11;

                    view = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

                    TextView opcion1TextView = (TextView) view.findViewById(R.id.opc1ImaTextView);
                    TextView opcion2TextView = (TextView) view.findViewById(R.id.opc2ImaTextView);
                    ImageView opcion1ImageView = (ImageView) view.findViewById(R.id.opc1ImaImageView);

                    opcion1TextView.setText(TITULOS[pos]);
                    opcion2TextView.setText(DESCRIPCIONES[pos]);
                    opcion1ImageView.setImageResource(IMAGES[pos]);

                    return view;

                } else if (itemViewType == 2){

                    //Descripcion comercio

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

                    //Celda vacia

                    view = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return view;

                } else if (itemViewType == 4){

                    //Contestar encuesta

                    view = mInflater.inflate(R.layout.general_una_opcion_sola, null);

                    TextView tituloTextView = (TextView) view.findViewById(R.id.opcionSolaTextView);

                    tituloTextView.setText("Contestar ENCUESTA de servicio AQUÍ");
                    tituloTextView.setBackgroundColor(getResources().getColor(R.color.morado_Pando));
                    tituloTextView.setTextColor(Color.WHITE);

                    return view;

                } else if (itemViewType == 5){

                    //Puntos recien enviados

                    int pos = i - 6;

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

                    //Codigo QR

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_2, null);

                    ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1QRImageView);
                    ImageView op2ImageView = (ImageView) view.findViewById(R.id.op2QRImageView);

                    op1ImageView.setImageBitmap(qrClienteImage);
                    op2ImageView.setImageResource(R.drawable.download);

                    return view;

                } else if (itemViewType == 7){

                    // Info general comercio

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

                } else if (itemViewType == 8) {

                    //Imagenes Portada

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_4, null);

                    view.setEnabled(true);

                    LinearLayout gallery = view.findViewById(R.id.galerry);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

                    for (int x = 0; x < imagenesPortadaArray.size(); x++){

                        View view1 = inflater.inflate(R.layout.descripcion_comercio_item_gallery, null);

                        final ImageView imagenPortada = view1.findViewById(R.id.itemGalleryImageView);

                        parseFileArray.get(x).getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {

                                if (e == null){

                                    imagenPortada.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));

                                }

                            }

                        });

                        gallery.addView(view1);

                    }

                    return view;

                } else if (itemViewType == 9){

                    // Nueva Logo comercio, distancia y envio gratis

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_5, null);

                    ImageView logoImageView = (ImageView) view.findViewById(R.id.op1DescCell5ImageView);
                    TextView nombreComTextView = (TextView) view.findViewById(R.id.op1DescCell5TextView);
                    TextView distKmTextView = (TextView) view.findViewById(R.id.op2DescCell5TextView);
                    TextView vecinoTextView = (TextView) view.findViewById(R.id.op3DescCell5TextView);
                    TextView envioTextView = (TextView) view.findViewById(R.id.op4DescCell5TextView);

                    nombreComTextView.setText(nombreComercio);
                    distKmTextView.setText(String.format("%1.2f", distanceKm) + " Km");
                    distanciaComGuardar = distKmTextView.getText().toString();

                    if (tieneLogo) {

                        logoImageView.setImageBitmap(logoComercio);

                    } else {

                        logoImageView.setImageResource(R.drawable.store);

                    }

                    if (distanciaEnvio > 0){

                        if (distanceKm > distanciaEnvio){

                            //No vecino
                            esVecinoGuardar = false;
                            vecinoTextView.setText("No vecin@");
                            envioTextView.setText("📞Haz tu pedido, pasa por el y ¡Disfruta!\n☝️Envío gratis en distancia menor a " + String.valueOf(distanciaEnvio) + "Km");

                        } else {

                            //Vecino
                            esVecinoGuardar = true;
                            vecinoTextView.setText("Vecin@");
                            vecinoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                            envioTextView.setText("🛵 ¡Envío gratis!");

                        }

                    } else  {

                        esVecinoGuardar = false;
                        vecinoTextView.setText("");
                        envioTextView.setText("📞Haz tu pedido, pasa por el y ¡Disfruta!");

                    }

                    return view;

                } else if (itemViewType == 10){

                    //Promociones Pando

                    view = mInflater.inflate(R.layout.descripcion_comercio_cell_6, null);
                    TextView titVecinoTextView = (TextView) view.findViewById(R.id.op1DescCell6TextView);
                    TextView descVecinoTextView = (TextView) view.findViewById(R.id.op2DescCell6TextView);
                    TextView titNoVecinoTextView = (TextView) view.findViewById(R.id.op3DescCell6TextView);
                    TextView descNoVecinoTextView = (TextView) view.findViewById(R.id.op4DescCell6TextView);

                    descVecinoTextView.setText(promoVecino);
                    descNoVecinoTextView.setText(promoNoVecino);

                    if (distanceKm > distanciaEnvio){

                        //No vecino
                        titNoVecinoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                        descNoVecinoTextView.setTextColor(Color.BLACK);

                    } else  {

                        //Vecino
                        titVecinoTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                        descVecinoTextView.setTextColor(Color.BLACK);

                    }
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
