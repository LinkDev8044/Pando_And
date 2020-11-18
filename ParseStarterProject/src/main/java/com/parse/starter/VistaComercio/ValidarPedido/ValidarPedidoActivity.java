package com.parse.starter.VistaComercio.ValidarPedido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaComercio.ValidarPedido.DomicilioCliente.DomicilioClienteActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ValidarPedidoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String nomUsuarioCom;
    String comercioId;
    String usuarioId;
    String tiempoString;
    String hora;
    String horaRecoger;
    String tiempoEntrega;
    String opcionEntrega;
    String fechaPedido;
    String calle;
    String numeroExterior;
    String colonia;
    String delegacion;
    String codigoPostal;
    String entreCalles;
    String numeroCliente;
    String nombreComercio;
    String nombreCompletoAdmin;
    String usuario;
    String correoCliente;
    String encuestaActiva;
    String encuestaActivaId;
    String recompensaActiva;

    ArrayList<String> nombrePlatilloArray = new ArrayList();
    ArrayList<String> complementosArray = new ArrayList();
    ArrayList<String> comentariosArray = new ArrayList();

    int numDePedido;
    int etapa;
    int numeroDePreguntas;

    Double puntos;
    Double subTotal;
    Double precioConDesc;
    Double puntosDescontar;
    Double totalFinal;
    Double segundosPedido;
    Double porcentaje;


    Boolean pedidoActivo;
    Boolean seCancela;
    Boolean seActualizaPlatillos;
    Boolean ofrecePuntos;
    Boolean encuestaEnviada;


    Date fecha;

    Handler handler =  new Handler();
    Handler handlerButton =  new Handler();

    ArrayList<Integer> cantidadArray = new ArrayList();

    ArrayList<Double> subTotalArray = new ArrayList();

    ListView validarPedidoListView;

    CustomAdapter customAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    ProgressDialog progressDialog;

    TextView validarButton;
    TextView estatusTextView;

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

    private void descontarPuntos(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
        query.whereEqualTo("usuarioId", usuarioId);
        query.whereEqualTo("comercioId", comercioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    for (ParseObject object : objects){

                        Double puntosRestantes = object.getDouble("puntos") - puntosDescontar;
                        final String puntosGuardar;

                        if (puntosRestantes - Math.floor(puntosRestantes) > 0.000001){

                            puntosGuardar = String.format("%.2f", puntosRestantes);

                        } else {

                            int i = Integer.valueOf(puntosRestantes.intValue());
                            puntosGuardar = String.valueOf(i);

                        }

                        object.put("puntos", Double.valueOf(puntosGuardar));
                        object.put("fechaModificacion", fecha);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null){

                                    ParseObject object = new ParseObject("HistorialPuntos");
                                    object.put("nombreComercio", nombreComercio);
                                    object.put("usuarioId", usuarioId);
                                    object.put("fechaModificacion", fecha);
                                    object.put("fechaCreacion", fecha);
                                    object.put("puntos", Double.valueOf(puntosGuardar));
                                    object.put("tipo", "esCanjear");
                                    object.put("comercioId", comercioId);
                                    object.put("correoUsuario", correoCliente);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            if (e == null){

                                                ParseObject object = new ParseObject("PuntosCanjeados");
                                                object.put("nombreComercio", nombreComercio);
                                                object.put("comercioId", comercioId);
                                                object.put("nombreColaborador", nombreCompletoAdmin);
                                                object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
                                                object.put("usuarioId", usuarioId);
                                                object.put("nombreUsuario", usuario);
                                                object.put("fechaCreacion", fecha);
                                                object.put("fechaModificacion", fecha);
                                                object.put("puntosDisponibles", 0.0);
                                                object.put("puntosRestantes", 0.0);
                                                object.put("puntosCanjeados", Double.valueOf(puntosGuardar));
                                                object.put("equivalencia", Double.valueOf(puntosGuardar));
                                                object.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {

                                                        if (e == null){

                                                            terminarSppiner();
                                                            reloadData();

                                                        } else {

                                                            terminarSppiner();
                                                            Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });

                                            } else {

                                                terminarSppiner();
                                                Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                } else {

                                    terminarSppiner();
                                    Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }

                } else {

                    terminarSppiner();
                    Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void enviarEncuesta(){

        if (encuestaEnviada){

            if (puntosDescontar > 0){

                descontarPuntos();

            } else {

                terminarSppiner();
                reloadData();
            }

        } else {

            ParseObject object = new ParseObject("EncuestaPendiente");
            object.put("nombreComercio", nombreComercio);
            object.put("comercioId", comercioId);
            object.put("nombreEncuesta", encuestaActiva);
            object.put("encuestaId", encuestaActivaId);
            object.put("fechaCreacion", fecha);
            object.put("fechaModificacion", fecha);
            object.put("numeroDePreguntas", numeroDePreguntas);
            object.put("recompensaActiva", recompensaActiva);
            object.put("usuario", usuario);
            object.put("usuarioId", usuarioId);
            object.put("activo", true);
            object.put("encuestaAplicada", false);
            object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
            object.put("nombreColaborador", nombreCompletoAdmin);
            object.put("correoColaborador", ParseUser.getCurrentUser().getEmail());
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null){

                        if (puntosDescontar > 0){

                            descontarPuntos();

                        } else {

                            terminarSppiner();
                            reloadData();
                        }

                    } else {

                        terminarSppiner();
                        Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();


                    }
                }
            });

        }
    }

    private void registrarVisita(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("VisitasCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", usuarioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            long diff = fecha.getTime() - object.getDate("fechaModificacion").getTime();
                            long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);

                            if (diffInHours >= 8){

                                int nuevoValor = object.getInt("numeroDeVisitas") + 1;
                                object.put("numeroDeVisitas", nuevoValor);
                                object.put("fechaModificacion", fecha);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        if (e == null) {

                                            enviarEncuesta();

                                        } else {

                                            terminarSppiner();
                                            Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            } else {

                                enviarEncuesta();

                            }
                        }

                    } else {

                        ParseObject object = new ParseObject("VisitasCliente");
                        object.put("nombreComercio", nombreComercio);
                        object.put("comercioId", comercioId);
                        object.put("nombreUsuario", usuario);
                        object.put("usuarioId", usuarioId);
                        object.put("fechacreacion", fecha);
                        object.put("fechaModificacion", fecha);
                        object.put("numeroDeVisitas", 1);
                        object.put("nombreCompleto", nombreCompletoAdmin);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {

                                    enviarEncuesta();

                                } else {

                                    terminarSppiner();
                                    Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }

                } else {

                    terminarSppiner();
                    Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void enviarPuntos(){

        //Calculo de puntos a enviar
        final Double puntosGuardar;
        Double valorConversion = porcentaje / 100;

        puntos = totalFinal * Double.valueOf(porcentaje / 100);

        if (puntos - Math.floor(puntos) > 0.000001){

            String puntosString = String.format("%.2f", puntos);
            puntosGuardar = Double.valueOf(puntosString);

        } else {

            int i = Integer.valueOf(puntos.intValue());
            puntosGuardar = Double.valueOf(i);

        }

        ParseObject object = new ParseObject("PuntosEnviados");

        object.put("nombreComercio", nombreComercio);
        object.put("comercioId", comercioId);
        object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
        object.put("nombreColaborador", nombreCompletoAdmin);
        object.put("usuarioId", usuarioId);
        object.put("nombreUsuario", usuario);
        object.put("fechaCreacion", fecha);
        object.put("fechaModificacion", fecha);
        object.put("consumo", String.valueOf(totalFinal));
        object.put("puntosEnviados", puntosGuardar);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    final ParseObject object = new ParseObject("HistorialPuntos");

                    object.put("nombreComercio", nombreComercio);
                    object.put("usuarioId", usuarioId);
                    object.put("fechaModificacion", fecha);
                    object.put("fechaCreacion", fecha);
                    object.put("puntos", puntosGuardar);
                    object.put("tipo", "esEnviar");
                    object.put("comercioId", comercioId);
                    object.put("correoUsuario", correoCliente);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null){

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
                                query.whereEqualTo("comercioId", comercioId);
                                query.whereEqualTo("usuarioId", usuarioId);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {

                                        if (e == null){

                                            if (objects.size() > 0){

                                                for (ParseObject object : objects){

                                                    object.put("fechaModificacion", fecha);

                                                    Double puntosNuevos = puntosGuardar;
                                                    Double puntosViejos = Double.valueOf(object.getDouble("puntos"));
                                                    Double puntosForSave = Double.valueOf(String.format("%.2f", puntosViejos + puntosNuevos));
                                                    object.put("puntos", puntosForSave);

                                                    Double consumoNuevo = totalFinal;
                                                    Double consumoViejo = Double.valueOf(object.getDouble("consumo"));
                                                    Double consumoForSave = Double.valueOf(String.format("%.2f", consumoViejo + consumoNuevo));
                                                    object.put("consumo", consumoForSave);

                                                    object.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {

                                                            if (e == null){

                                                                registrarVisita();

                                                            } else {

                                                                terminarSppiner();
                                                                Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });
                                                }

                                            } else {

                                                ParseObject object = new ParseObject("PuntosCliente");
                                                object.put("usuarioId", usuarioId);
                                                object.put("nombreUsuario", usuario);
                                                object.put("fechaCreacion", fecha);
                                                object.put("fechaModificacion", fecha);
                                                object.put("consumo", totalFinal);
                                                object.put("puntos", puntosGuardar);
                                                object.put("nombreComercio", nombreComercio);
                                                object.put("comercioId", comercioId);

                                                object.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {

                                                        if (e == null){

                                                            registrarVisita();

                                                        } else {

                                                            terminarSppiner();
                                                            Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });

                                            }

                                        } else {

                                            terminarSppiner();
                                            Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            } else {

                                terminarSppiner();
                                Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    terminarSppiner();
                    Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void actualizarPlatillos(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", usuarioId);
        query.whereEqualTo("activo", true);
        query.whereEqualTo("pedidoConfirmado", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        object.put("activo", false);
                        object.put("fechaModificacion", fecha);
                        object.saveInBackground();

                    }

                    if (seCancela){

                        terminarSppiner();
                        reloadData();

                    } else {

                        if (ofrecePuntos){

                            enviarPuntos();

                        } else {

                            registrarVisita();

                        }
                    }

                } else {

                    terminarSppiner();
                    Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    public void validar(View view){

        if (seCancela){

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

            ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoConfirmado");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("usuarioId", usuarioId);
            query.whereEqualTo("numDePedido", numDePedido);
            query.orderByDescending("fechaCreacion");
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            if (opcionEntrega.matches("Domicilio")){

                                object.put("etapa", 4);

                            } else {

                                object.put("etapa", 3);
                            }
                            object.put("activo", false);
                            object.put("entregado", false);
                            object.put("tiempoEntrega", "CANCELADO");
                            object.put("fechaModificacion", fecha);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null){

                                        actualizarPlatillos();

                                    } else {

                                        terminarSppiner();
                                        Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }

                    } else {

                        terminarSppiner();
                        Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else {

            if (validarButton.getText().toString().matches("LISTO")){

                //Detener timers
                handler.removeCallbacks(timerRunnableSeg);
                handlerButton.removeCallbacks(timerRunnableButton);

                finish();

            } else {

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

                iniciarSppiner();


                seActualizaPlatillos = false;

                ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoConfirmado");
                query.whereEqualTo("comercioId", comercioId);
                query.whereEqualTo("usuarioId", usuarioId);
                query.whereEqualTo("numDePedido", numDePedido);
                query.orderByDescending("fechaCreacion");
                query.setLimit(1);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null){

                            for (ParseObject object : objects){

                                object.put("etapa", etapa + 1);

                                if (opcionEntrega.matches("Domicilio")){

                                    if (etapa == 3){

                                        seActualizaPlatillos = true;
                                        object.put("activo", false);
                                        object.put("entregado", true);
                                        object.put("tiempoEntrega", tiempoString);
                                        object.put("fechaModificacion", fecha);

                                    }

                                } else {

                                    if (etapa == 2){

                                        seActualizaPlatillos = true;
                                        object.put("activo", false);
                                        object.put("entregado", true);
                                        object.put("tiempoEntrega", tiempoString);
                                        object.put("fechaModificacion", fecha);


                                    }
                                }

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        if (e == null){

                                            //Detener timers
                                            handler.removeCallbacks(timerRunnableSeg);

                                            if (seActualizaPlatillos){

                                                actualizarPlatillos();

                                            } else {

                                                terminarSppiner();
                                                reloadData();

                                            }

                                        } else {

                                            terminarSppiner();
                                            Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }

                        } else {

                            terminarSppiner();
                            Toast.makeText(ValidarPedidoActivity.this, "Tuvimos un error - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        }
    }

    private void buttonEnabled(){

        handlerButton.removeCallbacks(timerRunnableButton);
        seCancela = false;

        validarButton.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        validarButton.setTextColor(Color.WHITE);
        validarButton.setEnabled(true);

        if (opcionEntrega.matches("Domicilio")){

            if (etapa == 1){

                validarButton.setText("CONFIRMAR Pedido");
                estatusTextView.setText("Etapa 1: Pedido Enviado");

            } else if (etapa == 2){

                validarButton.setText("Pedido ENVIADO");
                estatusTextView.setText("Etapa 2: En Preparación");

            } else if (etapa == 3){

                validarButton.setText("Pedido ENTREGADO");
                estatusTextView.setText("Etapa 3: Enviado");

            } else if (etapa == 4){

                validarButton.setText("OK");
                estatusTextView.setText("Etapa 4: Entregado");

            }

        } else {

            if (etapa == 1) {

                validarButton.setText("CONFIRMAR Pedido");
                estatusTextView.setText("Etapa 1: Pedido Enviado");

            } else if (etapa == 2){

                validarButton.setText("Pedido ENTREGADO");
                estatusTextView.setText("Etapa 2: En Preparación");

            } else if (etapa == 3) {

                validarButton.setText("OK");
                estatusTextView.setText("Etapa 3: Entregado");

            }
        }
    }

    private void buttonRed(){

        validarButton.setText("CANCELAR Pedido");

        validarButton.setBackgroundColor(Color.RED);
        validarButton.setTextColor(Color.WHITE);
        validarButton.setEnabled(true);

    }

    private void updateView(int index){
        View v = validarPedidoListView.getChildAt(index -
                validarPedidoListView.getFirstVisiblePosition());

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

    }

    private Runnable timerRunnableButton = new Runnable() {
        @Override
        public void run() {

            buttonEnabled();

        }

        //handler.postDelayed(r, 1000);

    };

    private Runnable timerRunnableSeg = new Runnable() {
        @Override
        public void run() {

            runTimer();
            handler.postDelayed(this, 1000);

        }

        //handler.postDelayed(r, 1000);

    };

    private void cargarContactoCliente(){

        numeroCliente = "";

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactoCliente");
        query.whereEqualTo("usuarioId", usuarioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            numeroCliente = object.getString("numeroCliente");

                        }

                        swipeRefreshLayout.setRefreshing(false);
                        terminarSppiner();
                        validarPedidoListView.setAdapter(customAdapter);

                    } else {

                        swipeRefreshLayout.setRefreshing(false);
                        terminarSppiner();
                        validarPedidoListView.setAdapter(customAdapter);

                    }

                } else {

                    swipeRefreshLayout.setRefreshing(false);
                    terminarSppiner();

                }
            }
        });
    }

    private void cargarDomicilio(){

        calle = "";
        numeroExterior = "";
        colonia = "";
        delegacion = "";
        codigoPostal = "";
        entreCalles = "";

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DomicilioCliente");
        query.whereEqualTo("usuarioId", usuarioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            calle = object.getString("calle");
                            numeroExterior = object.getString("numExtInt");
                            colonia = object.getString("colonia");
                            delegacion = object.getString("delegacion");
                            codigoPostal = object.getString("codigoPostal");
                            entreCalles = object.getString("entreCalles");

                        }

                        cargarContactoCliente();

                    } else  {

                        cargarContactoCliente();

                    }

                } else {

                    swipeRefreshLayout.setRefreshing(false);
                    terminarSppiner();

                }
            }
        });
    }

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


        puntos = 0.0;

        etapa = 0;
        subTotal = 0.0;
        precioConDesc = 0.0;
        puntosDescontar = 0.0;
        totalFinal = 0.0;
        segundosPedido = 0.0;
        horaRecoger = "";
        tiempoEntrega = "";
        opcionEntrega = "";
        fechaPedido = "";
        pedidoActivo = false;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoConfirmado");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", usuarioId);
        query.whereEqualTo("numDePedido",  numDePedido);
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

                            //CARGAR TOTALES
                            subTotal = object.getDouble("subTotal");
                            precioConDesc = object.getDouble("precioConDescuento");
                            puntosDescontar = object.getDouble("puntos");
                            totalFinal = object.getDouble("totalFinal");

                            //CARGAR HORA RECOGER
                            horaRecoger = object.getString("horaRecoger");

                            //REVISAR PEDIDO ACTIVO PARA PONER CRONOMETRO O FECHA
                            pedidoActivo = object.getBoolean("activo");
                            tiempoEntrega = object.getString("tiempoEntrega");

                            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                            String convertedDate = dateFormat.format(object.getDate("fechaCreacion"));
                            fechaPedido = convertedDate;

                            //ACTIVAR BUTTON
                            buttonEnabled();

                            if (pedidoActivo) {

                                //Datos para validar el tiempo desde que se hizo el pedido
                                long diff = fecha.getTime() - object.getDate("fechaModificacion").getTime();
                                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
                                segundosPedido = Double.valueOf(diffInSeconds);

                                //Iniciar Timer
                                handler.removeCallbacks(timerRunnableSeg);
                                handler.postDelayed(timerRunnableSeg, 1000);

                            }
                        }

                        cargarDomicilio();

                    } else {

                        cargarDomicilio();

                    }

                } else {

                    swipeRefreshLayout.setRefreshing(false);
                    terminarSppiner();

                }
            }
        });
    }

    private void reloadData(){

        iniciarSppiner();

        hora = "";
        nombrePlatilloArray.clear();
        cantidadArray.clear();
        subTotalArray.clear();
        complementosArray.clear();
        comentariosArray.clear();

        //Detener timers
        handler.removeCallbacks(timerRunnableSeg);
        handlerButton.removeCallbacks(timerRunnableButton);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", usuarioId);
        query.whereEqualTo("numDePedido", numDePedido);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            //Cargar info de pedido
                            hora = object.getString("hora");
                            nombrePlatilloArray.add(object.getString("nombrePlatillo"));
                            cantidadArray.add(object.getInt("cantidad"));
                            subTotalArray.add(object.getDouble("subTotal"));
                            complementosArray.add(object.getString("complementos"));
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
        setContentView(R.layout.activity_validar_pedido);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        validarPedidoListView= (ListView) findViewById(R.id.validarPedidoListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.validarPedidoRefreshLayout);
        validarButton = (TextView) findViewById(R.id.op1ValidarPButtonTextView);
        estatusTextView = (TextView) findViewById(R.id.op2EstatusValidarPTextView);
        swipeRefreshLayout.setOnRefreshListener(this);

        final Intent intent = getIntent();
        nomUsuarioCom = intent.getStringExtra("nomUsuarioCom");
        comercioId = intent.getStringExtra("comercioId");
        numDePedido = intent.getIntExtra("numDePedido", 0);
        usuarioId = intent.getStringExtra("usuarioId");
        tiempoString = intent.getStringExtra("tiempoString");
        porcentaje = intent.getDoubleExtra("porcentaje", 0);
        ofrecePuntos = intent.getBooleanExtra("ofrecePuntos", false);
        nombreComercio = intent.getStringExtra("nombreComercio");
        nombreCompletoAdmin = intent.getStringExtra("nombreCompleto");
        usuario = intent.getStringExtra("usuario");
        correoCliente = intent.getStringExtra("correoCliente");
        encuestaEnviada = intent.getBooleanExtra("encuestaEnviada", false);
        encuestaActiva = intent.getStringExtra("encuestaActiva");
        encuestaActivaId = intent.getStringExtra("encuestaActivaId");
        recompensaActiva = intent.getStringExtra("recompensaActiva");
        numeroDePreguntas = intent.getIntExtra("numeroDePreguntas", 0);

        customAdapter = new CustomAdapter();

        setTitle(nomUsuarioCom);

        validarPedidoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {

                    //Detener timers
                    handler.removeCallbacks(timerRunnableSeg);

                    Intent intent1 = new Intent(getApplicationContext(), DomicilioClienteActivity.class);
                    intent1.putExtra("calle", calle);
                    intent1.putExtra("numeroExterior", numeroExterior);
                    intent1.putExtra("colonia", colonia);
                    intent1.putExtra("delegacion", delegacion);
                    intent1.putExtra("codigoPostal", codigoPostal);
                    intent1.putExtra("entreCalles", entreCalles);
                    intent1.putExtra("nomUsuarioCom", nomUsuarioCom);
                    startActivity(intent1);

                }
            }
        });
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

    class CustomAdapter extends BaseAdapter implements Adapter {

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getViewTypeCount() {
            return 6;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0){

                return 1;

            } else if (position == 1){

                if (opcionEntrega.matches("Domicilio")){

                    return 2;

                }

                return 0;

            } else if (position == 2){

                if (opcionEntrega.matches("Domicilio") || opcionEntrega.matches("Recoger")) {

                    return 3;

                }

                return 0;

            } else if (position > 2 && position < (nombrePlatilloArray.size() + 3)){

                return 4;

            } else {

                return 5;

            }
        }

        @Override
        public int getCount() {
            return 4 + nombrePlatilloArray.size();
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

            //LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
            int itemViewType = getItemViewType(position);

            if (convertView == null) {

                if (itemViewType == 0){

                    convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return convertView;


                } else if (itemViewType == 1) {

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

                    if (pedidoActivo){

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

                    if (tiempoEntrega.matches("CANCELADO")){

                        op7EtapaTextView.setText("CANCELADO");
                        estatusTextView.setText("CANCELADO");

                    }

                    return convertView;

                } else if (itemViewType == 2){

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_2, null);

                    ImageView op1IconoImageView = (ImageView) convertView.findViewById(R.id.op1ValidarPCell2ImageView);
                    TextView op1TituloTextView = (TextView) convertView.findViewById(R.id.op1ValidarPCell2TextView);
                    TextView op2DescTextView = (TextView) convertView.findViewById(R.id.op2ValidarPCell2TextView);

                    op1IconoImageView.setImageResource(R.drawable.estoy_aqui_navigation);
                    op1TituloTextView.setText(calle + ", " + numeroExterior + ", ");
                    op2DescTextView.setText(colonia + ", " + delegacion + ", " + codigoPostal + ", " + entreCalles);

                    return convertView;

                } else if (itemViewType == 3){

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_2, null);
                    convertView.setEnabled(false);

                    ImageView op1IconoImageView = (ImageView) convertView.findViewById(R.id.op1ValidarPCell2ImageView);
                    TextView op1TituloTextView = (TextView) convertView.findViewById(R.id.op1ValidarPCell2TextView);
                    TextView op2DescTextView = (TextView) convertView.findViewById(R.id.op2ValidarPCell2TextView);
                    TextView op3FlechaTextView = (TextView) convertView.findViewById(R.id.op3ValidarPCell2TextView);

                    op1IconoImageView.setImageResource(R.drawable.call);
                    op1TituloTextView.setText(numeroCliente);
                    op2DescTextView.setText("");
                    op3FlechaTextView.setText("");

                    return convertView;

                } else if (itemViewType == 4){

                    int pos = position - 3;

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

                } else if (itemViewType == 5){

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_4, null);
                    convertView.setEnabled(false);

                    TextView op1SubTotalTextView = (TextView) convertView.findViewById(R.id.op1ValidarPC4TextView);
                    TextView op2PuntosTextView = (TextView) convertView.findViewById(R.id.op2ValidarPC4TextView);
                    TextView op3TotalTextView = (TextView) convertView.findViewById(R.id.op3ValidarPC4TextView);
                    TextView op4PrecioDescTextView = (TextView) convertView.findViewById(R.id.op4ValidarPC4TextView);

                    op3TotalTextView.setText("$" + String.valueOf(totalFinal));
                    op2PuntosTextView.setText("-" + String.valueOf(puntosDescontar));

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

                if (itemViewType == 0){

                    convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return convertView;


                } else if (itemViewType == 1) {

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

                    if (pedidoActivo){

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

                    if (tiempoEntrega.matches("CANCELADO")){

                        op7EtapaTextView.setText("CANCELADO");
                        estatusTextView.setText("CANCELADO");

                    }

                    return convertView;

                } else if (itemViewType == 2){

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_2, null);

                    ImageView op1IconoImageView = (ImageView) convertView.findViewById(R.id.op1ValidarPCell2ImageView);
                    TextView op1TituloTextView = (TextView) convertView.findViewById(R.id.op1ValidarPCell2TextView);
                    TextView op2DescTextView = (TextView) convertView.findViewById(R.id.op2ValidarPCell2TextView);

                    op1IconoImageView.setImageResource(R.drawable.estoy_aqui_navigation);
                    op1TituloTextView.setText(calle + ", " + numeroExterior + ", ");
                    op2DescTextView.setText(colonia + ", " + delegacion + ", " + codigoPostal + ", " + entreCalles);

                    return convertView;

                } else if (itemViewType == 3){

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_2, null);
                    convertView.setEnabled(false);

                    ImageView op1IconoImageView = (ImageView) convertView.findViewById(R.id.op1ValidarPCell2ImageView);
                    TextView op1TituloTextView = (TextView) convertView.findViewById(R.id.op1ValidarPCell2TextView);
                    TextView op2DescTextView = (TextView) convertView.findViewById(R.id.op2ValidarPCell2TextView);
                    TextView op3FlechaTextView = (TextView) convertView.findViewById(R.id.op3ValidarPCell2TextView);

                    op1IconoImageView.setImageResource(R.drawable.call);
                    op1TituloTextView.setText(numeroCliente);
                    op2DescTextView.setText("");
                    op3FlechaTextView.setText("");

                    return convertView;

                } else if (itemViewType == 4){

                    int pos = position - 3;

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

                } else if (itemViewType == 5){

                    convertView = mInflater.inflate(R.layout.validar_pedido_cell_4, null);
                    convertView.setEnabled(false);

                    TextView op1SubTotalTextView = (TextView) convertView.findViewById(R.id.op1ValidarPC4TextView);
                    TextView op2PuntosTextView = (TextView) convertView.findViewById(R.id.op2ValidarPC4TextView);
                    TextView op3TotalTextView = (TextView) convertView.findViewById(R.id.op3ValidarPC4TextView);
                    TextView op4PrecioDescTextView = (TextView) convertView.findViewById(R.id.op4ValidarPC4TextView);

                    op3TotalTextView.setText("$" + String.valueOf(totalFinal));
                    op2PuntosTextView.setText("-" + String.valueOf(puntosDescontar));

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

    @Override
    public boolean onSupportNavigateUp() {

        //Detener timers
        handler.removeCallbacks(timerRunnableSeg);
        handlerButton.removeCallbacks(timerRunnableButton);

        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_trash, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.trash_icon:

                if (validarButton.getText().toString().matches("LISTO") == false){

                    if (seCancela){

                        buttonEnabled();

                    } else {

                        seCancela = true;

                        buttonRed();

                        handlerButton.removeCallbacks(timerRunnableButton);
                        handlerButton.postDelayed(timerRunnableButton, 8000);

                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
