package com.parse.starter.VistaClientes.CarritoPedidos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.CarritoPedidos.ConfirmacionPedido.ConfirmacionPedidoActivity;
import com.parse.starter.VistaClientes.CarritoPedidos.EliminarPedido.EliminarPedidoActivity;
import com.parse.starter.VistaClientes.OpcionesEntrega.NumeroContacto.NumeroContactoActivity;
import com.parse.starter.VistaClientes.OpcionesEntrega.OpcionesEntregaActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class CarritoPedidosActivity extends AppCompatActivity {

    String nombreComCliente;
    String comercioId;
    String numeroWhats;
    String nombreUsuario;
    String nombreComercio;
    String distanciaComGuardar;
    String opcionEntrega;
    String hora;
    String calle;
    String numeroExterior;
    String colonia;
    String delegacion;
    String codigoPostal;
    String entreCalles;
    String numeroCliente;

    ArrayList<String> nombrePlatilloArray = new ArrayList();
    ArrayList<Integer> cantidadArray = new ArrayList();
    ArrayList<Double> subTotalArray = new ArrayList();
    ArrayList<String> complementosArray = new ArrayList();
    ArrayList<String> comentariosArray = new ArrayList();
    ArrayList<String> pedidoIdArray = new ArrayList();

    Boolean envioDisponible;
    Boolean esVecino;
    Boolean usuarioVisible;
    Boolean usarPuntos;
    Boolean tienePagoConPts;

    Double subTotal;
    Double puntosCliente;
    Double precioConDescuento;
    Double cantidadADescontar;
    Double puntosAplicados;
    Double totalFinal;
    Double precioEnButton;

    int numVisitasCliente;
    int descuento;
    int contador;
    int numDePedido;

    Date fecha;

    CustomAdapter customAdapter;

    ListView carritoListView;

    TextView pedidoButton;
    TextView label1;

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

    private void goToConfirmacion(){

        Intent intent = new Intent(getApplicationContext(), ConfirmacionPedidoActivity.class);
        intent.putExtra("comercioId", comercioId);
        intent.putExtra("numeroWhats", numeroWhats);
        intent.putExtra("nombreUsuario", nombreUsuario);
        intent.putExtra("nombreComercio", nombreComercio);
        startActivity(intent);

    }

    private void estoyAqui(){

        if (usuarioVisible){

            terminarSppiner();

            goToConfirmacion();

        } else {

            ParseObject object = new ParseObject("UsuarioActivo");
            object.put("nombreComercio", nombreComercio);
            object.put("comercioId", comercioId);
            object.put("activo", true);
            object.put("encuestaAplicada", false);
            object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
            object.put("fechaCreacion", fecha);
            object.put("fechaEncuestaTerminada", fecha);
            object.put("nombreUsuario", nombreComCliente);
            object.put("email", ParseUser.getCurrentUser().getEmail());
            object.put("esVecino", esVecino);
            object.put("distanciaComercio", distanciaComGuardar);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null){

                        terminarSppiner();

                        goToConfirmacion();

                    } else {

                        terminarSppiner();
                        buttonEnabled();

                    }
                }
            });
        }
    }

    private void actualizarPlatillos(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("activo", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        object.put("fechaModificacion", fecha);
                        object.put("pedidoConfirmado", true);
                        object.put("numDePedido", numDePedido + 1);
                        object.saveInBackground();

                    }

                    estoyAqui();

                } else {

                    terminarSppiner();
                    buttonEnabled();
                }
            }
        });
    }

    private void guardarPedido(){

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

        ParseObject object = new ParseObject("PedidoConfirmado");
        object.put("comercioId", comercioId);
        object.put("nombreComercio", nombreComercio);
        object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
        object.put("nombreCliente", nombreComCliente);
        object.put("fechaCreacion", fecha);
        object.put("fechaModificacion", fecha);
        object.put("etapa", 1);
        object.put("numDePedido", numDePedido + 1);
        object.put("opcionEntrega", opcionEntrega);
        object.put("porcentajeDescuento", descuento);
        object.put("precioConDescuento", precioConDescuento);
        object.put("subTotal", subTotal);
        object.put("puntos", puntosAplicados);
        object.put("totalFinal", totalFinal);
        object.put("activo", true);
        object.put("horaRecoger", hora);
        object.put("entregado", false);
        object.put("tiempoEntrega", "");
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    actualizarPlatillos();

                } else {

                    terminarSppiner();
                    buttonEnabled();

                }
            }
        });

    }

    private void revisarNumPedido(){

        numDePedido = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoConfirmado");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.orderByDescending("fechaCreacion");
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            numDePedido = object.getInt("numDePedido");

                        }

                        guardarPedido();

                    } else {

                        guardarPedido();

                    }

                } else {

                    terminarSppiner();
                    buttonEnabled();

                }
            }
        });
    }

    public void hacerPedido(View view){

        if (contador == 0){

            pedidoButton.setText("Confirmar");

            contador += 1;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (contador == 1){

                        buttonEnabled();

                    }

                }
            }, 5000);

        } else {

            contador += 1;

            iniciarSppiner();

            revisarNumPedido();

        }
    }

    private void buttonEnabled(){

        contador = 0;

        pedidoButton.setText("Realizar pedido");
        pedidoButton.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        pedidoButton.setTextColor(Color.WHITE);
        pedidoButton.setEnabled(true);

        label1.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        label1.setEnabled(true);

    }

    private void buttonDisabled(){

        pedidoButton.setText("Realizar pedido");
        pedidoButton.setBackgroundColor(Color.DKGRAY);
        pedidoButton.setTextColor(Color.WHITE);
        pedidoButton.setEnabled(false);

        label1.setBackgroundColor(Color.DKGRAY);
        label1.setEnabled(false);

    }

    private void reloadPuntos(){

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("activo", true);
        query.orderByAscending("fechaCreacion");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        object.put("usarPuntos", usarPuntos);
                        object.saveInBackground();

                    }

                    terminarSppiner();
                    actualizarPrecios();

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    private void reloadButton(){

        label1.setText("$" + String.valueOf(precioEnButton));
        label1.setTextColor(Color.WHITE);

    }

    private void reloadCelda(){

        customAdapter.notifyDataSetChanged();

    }

    private void actualizarPrecios(){

        puntosAplicados = 0.0;
        totalFinal = 0.0;
        precioEnButton = 0.0;

        if (descuento == 0){

            totalFinal = subTotal;
            precioEnButton = totalFinal;

            if (usarPuntos){

                puntosAplicados = puntosCliente;

                if (puntosCliente >= subTotal){

                    puntosAplicados = subTotal;

                }

                totalFinal = precioConDescuento - puntosAplicados;

                String puntosGuardar = null;

                if (totalFinal - Math.floor(totalFinal) > 0.000001){

                    puntosGuardar = String.format("%.2f", totalFinal);

                } else {

                    int i = Integer.valueOf(totalFinal.intValue());
                    puntosGuardar = String.valueOf(i);

                }

                precioEnButton = Double.valueOf(puntosGuardar);
                totalFinal = Double.valueOf(puntosGuardar);

            }

            reloadButton();

        } else {

            totalFinal = precioConDescuento;
            precioEnButton = precioConDescuento;

            if (usarPuntos){

                puntosAplicados = puntosCliente;

                if (puntosCliente >= precioConDescuento){

                    puntosAplicados = precioConDescuento;

                }

                totalFinal = precioConDescuento - puntosAplicados;

                String puntosGuardar = null;

                if (totalFinal - Math.floor(totalFinal) > 0.000001){

                    puntosGuardar = String.format("%.2f", totalFinal);

                } else {

                    int i = Integer.valueOf(totalFinal.intValue());
                    puntosGuardar = String.valueOf(i);

                }

                precioEnButton = Double.valueOf(puntosGuardar);
                totalFinal = Double.valueOf(puntosGuardar);

            }

            reloadButton();

        }

        reloadCelda();

    }

    private void verificarButton(){

        buttonDisabled();

        if (opcionEntrega.matches("Domicilio") || opcionEntrega.matches("Recoger")){

            if (numeroCliente.matches("") == false){

                buttonEnabled();

            }

        } else if (opcionEntrega.matches("Restaurante")){

            buttonEnabled();

        }

        terminarSppiner();
        carritoListView.setAdapter(customAdapter);

    }

    private void calcularPrecios(){

        precioConDescuento = subTotal;
        cantidadADescontar = 0.0;

        if (descuento > 0){

            cantidadADescontar = (Double.valueOf(descuento) * subTotal) / 100;
            precioConDescuento = subTotal - cantidadADescontar;

        }

        verificarButton();

        actualizarPrecios();

    }

    private void verificarDescuentos(){

        descuento = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DescuentosPando");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("numVisitas", numVisitasCliente);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            if (envioDisponible){

                                descuento = object.getInt("esVecino");

                            } else {

                                descuento = object.getInt("noVecino");

                            }
                        }

                        calcularPrecios();

                    } else {

                        calcularPrecios();

                    }

                } else {

                    terminarSppiner();

                }
            }
        });

    }

    private void verificarVisitas(){

        numVisitasCliente = 0;

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

                            numVisitasCliente = object.getInt("numeroDeVisitas");

                        }

                        verificarDescuentos();

                    } else {

                        verificarDescuentos();

                    }

                } else {

                    terminarSppiner();

                }
            }
        });

    }

    private void verPuntosCliente(){

        puntosCliente = 0.0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            puntosCliente = object.getDouble("puntos");

                        }

                        verificarVisitas();

                    } else {

                        verificarVisitas();
                    }

                } else {

                    terminarSppiner();

                }
            }
        });

    }

    private void validarPagoConPuntos(){

        tienePagoConPts = false;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ConfiguracionComercio");
        query.whereEqualTo("comercioId", comercioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            Boolean tienePago = object.getBoolean("pagarConPuntos");
                            if (tienePago != null){

                                tienePagoConPts = object.getBoolean("pagarConPuntos");

                            }
                        }

                        verPuntosCliente();

                    } else {

                        verPuntosCliente();

                    }

                } else {

                    terminarSppiner();

                }
            }
        });

    }

    private void cargarContactoCliente(){

        numeroCliente = "";

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactoCliente");
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            numeroCliente = object.getString("numeroCliente");

                        }

                        validarPagoConPuntos();

                    } else {

                        ParseObject object = new ParseObject("ContactoCliente");
                        object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
                        object.put("nombreCliente", nombreComCliente);
                        object.put("numeroCliente", "");
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null){

                                    validarPagoConPuntos();

                                } else {

                                    terminarSppiner();

                                }
                            }
                        });

                    }

                } else {

                    terminarSppiner();

                }
            }
        });

    }

    private void cargarDomicilioCliente(){

        calle = "";
        numeroExterior = "";
        colonia = "";
        delegacion = "";
        codigoPostal = "";
        entreCalles = "";

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DomicilioCliente");
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        calle = object.getString("calle");
                        numeroExterior = object.getString("numExtInt");
                        colonia = object.getString("colonia");
                        delegacion = object.getString("delegacion");
                        codigoPostal = object.getString("codigoPostal");
                        entreCalles = object.getString("entreCalles");

                    }

                    cargarContactoCliente();

                } else {

                    terminarSppiner();

                }
            }
        });

    }

    private void reloadData(){

        iniciarSppiner();

        opcionEntrega = "";
        hora = "";
        nombrePlatilloArray.clear();
        cantidadArray.clear();
        subTotalArray.clear();
        complementosArray.clear();
        comentariosArray.clear();
        pedidoIdArray.clear();
        usarPuntos = false;
        subTotal = 0.0;
        contador = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("activo", true);
        query.orderByAscending("fechaCreacion");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        opcionEntrega = object.getString("opcionEntrega");
                        hora = object.getString("hora");
                        nombrePlatilloArray.add(object.getString("nombrePlatillo"));
                        cantidadArray.add(object.getInt("cantidad"));
                        subTotalArray.add(object.getDouble("subTotal"));
                        complementosArray.add(object.getString("complementos2"));
                        comentariosArray.add(object.getString("comentarios"));
                        subTotal = subTotal + (object.getDouble("subTotal"));
                        usarPuntos = object.getBoolean("usarPuntos");
                        pedidoIdArray.add(object.getObjectId());

                    }

                    if (opcionEntrega.matches("Domicilio")){

                        cargarDomicilioCliente();

                    } else {

                        cargarContactoCliente();

                    }

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_pedidos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Mi carrito");

        carritoListView = (ListView) findViewById(R.id.carritoListView);
        pedidoButton = (TextView) findViewById(R.id.op1CarritoTextView);
        label1 = (TextView) findViewById(R.id.op2CarritoTextView);

        Intent intent = getIntent();
        nombreComCliente = intent.getStringExtra("nombreComCliente");
        comercioId = intent.getStringExtra("comercioId");
        envioDisponible = intent.getBooleanExtra("envioDisponible", false);
        numeroWhats = intent.getStringExtra("numeroWhats");
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        nombreComercio = intent.getStringExtra("nombreComercio");
        esVecino = intent.getBooleanExtra("esVecino", false);
        distanciaComGuardar = intent.getStringExtra("distanciaComGuardar");
        usuarioVisible = intent.getBooleanExtra("usuarioVisible", false);

        customAdapter = new CustomAdapter();

        carritoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1){

                    Intent intent = new Intent(getApplicationContext(), OpcionesEntregaActivity.class);
                    intent.putExtra("nombreComCliente", nombreComCliente);
                    intent.putExtra("comercioId", comercioId);
                    intent.putExtra("envioDisponible", envioDisponible);
                    startActivity(intent);

                }

                if (position == 2){

                    Intent intent = new Intent(getApplicationContext(), NumeroContactoActivity.class);
                    intent.putExtra("nombreComCliente", nombreComCliente);
                    intent.putExtra("numeroCliente", numeroCliente);
                    startActivity(intent);

                }

                if (position > 3 && position < (nombrePlatilloArray.size() + 4)){

                    int pos = position - 4;

                    Intent intent1 = new Intent(getApplicationContext(), EliminarPedidoActivity.class);
                    intent1.putExtra("comercioId", comercioId);
                    intent1.putExtra("pedidoIdSelec", pedidoIdArray.get(pos));
                    intent1.putExtra("nomPlatilloSelec", nombrePlatilloArray.get(pos));
                    intent1.putExtra("complementosSelec", complementosArray.get(pos));
                    intent1.putExtra("comentariosSelec", comentariosArray.get(pos));
                    intent1.putExtra("cantidadSelec", cantidadArray.get(pos));
                    intent1.putExtra("subTotalSelec", subTotalArray.get(pos));
                    startActivity(intent1);

                }

                if (position == nombrePlatilloArray.size() + 4){

                    if (tienePagoConPts){

                        if (puntosCliente > 1){

                            if (usarPuntos == false){

                                usarPuntos = true;

                                reloadPuntos();

                            } else {

                                if (usarPuntos){

                                    usarPuntos = false;

                                    reloadPuntos();

                                }
                            }
                        }
                    }

                }

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

    class CustomAdapter extends BaseAdapter implements Adapter{

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getViewTypeCount() {

            return 7;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0){

                return 6;

            } else if (position == 1){

                return 0;

            } else if (position == 2){

                return 1;

            } else if (position == 3){

                return 2;

            } else if (position > 3 && position < (nombrePlatilloArray.size() + 4)){

                return 3;

            } else if (position == (nombrePlatilloArray.size() + 4)){

                return 4;

            }  else if (position == (nombrePlatilloArray.size() + 5)){

                return 5;

            } else {

                return 0;
            }
        }

        @Override
        public int getCount() {
            return 6 + nombrePlatilloArray.size();
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

            if (convertView == null) {

                if (itemViewType == 0) {

                    convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_1, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1CarritoPC1TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2CarritoPC1TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3CarritoPC1TextView);
                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1CarritoPC1ImageView);

                    if (opcionEntrega.matches("")) {

                        op1ImageView.setImageResource(R.drawable.food_delivery);
                        op1TextView.setText("Opciones de entrega");
                        op2TextView.setText("A domicilio, recoger o en restaurante");
                        op3TextView.setText("Selecciona una opción aquí");
                        op3TextView.setTextColor(Color.RED);
                        op2TextView.setTypeface(Typeface.DEFAULT);

                        op2TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);


                    } else if (opcionEntrega.matches("Domicilio")) {

                        op1ImageView.setImageResource(R.drawable.food_delivery);
                        op1TextView.setText("A domicilio");
                        op2TextView.setText(calle + " " + numeroExterior);
                        op3TextView.setText(colonia + ", " + delegacion + ", " + codigoPostal + ", " + entreCalles);
                        op3TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                        op2TextView.setTypeface(Typeface.DEFAULT_BOLD);

                    } else if (opcionEntrega.matches("Recoger")) {

                        op1ImageView.setImageResource(R.drawable.take_away);
                        op1TextView.setText("Recoger pedido");
                        op2TextView.setText(hora);
                        op3TextView.setText("");
                        op3TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                        op2TextView.setTypeface(Typeface.DEFAULT_BOLD);
                        op3TextView.getLayoutParams().height = 0;

                    } else if (opcionEntrega.matches("Restaurante")) {

                        op1ImageView.setImageResource(R.drawable.spoon);
                        op1TextView.setText("");
                        op2TextView.setText("Consumo en restaurante");
                        op3TextView.setText("");
                        op3TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                        op2TextView.setTypeface(Typeface.DEFAULT_BOLD);

                        op1TextView.getLayoutParams().height = 0;
                        op3TextView.getLayoutParams().height = 0;

                    }

                    return convertView;

                } else if (itemViewType == 1){

                    if (opcionEntrega.matches("Domicilio") || opcionEntrega.matches("Recoger")){

                        convertView = mInflater.inflate(R.layout.validar_pedido_cell_2, null);

                        ImageView op1IconoImageView = (ImageView) convertView.findViewById(R.id.op1ValidarPCell2ImageView);
                        TextView op1TituloTextView = (TextView) convertView.findViewById(R.id.op1ValidarPCell2TextView);
                        TextView op2DescTextView = (TextView) convertView.findViewById(R.id.op2ValidarPCell2TextView);

                        op1IconoImageView.setImageResource(R.drawable.call);
                        op1TituloTextView.setText("");
                        op2DescTextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                        op2DescTextView.setText(numeroCliente);
                        op2DescTextView.setTypeface(Typeface.DEFAULT_BOLD);

                        if (numeroCliente.matches("")) {

                            op2DescTextView.setTextColor(Color.RED);
                            op2DescTextView.setText("Agrega tu número de contacto");
                            op2DescTextView.setTypeface(Typeface.DEFAULT);

                        }

                        return convertView;

                    } else {

                        //Celda vacia

                        convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                        return convertView;

                    }

                } else if (itemViewType == 2){

                    convertView = mInflater.inflate(R.layout.una_opcion_con_flecha, null);
                    convertView.setEnabled(false);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opcion1FlechaTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.opcion2FlechaTextView);

                    op1TextView.setText("Tu pedido");
                    op2TextView.setText("");
                    op1TextView.setTypeface(Typeface.DEFAULT_BOLD);


                } else if (itemViewType == 3){

                    int pos = position - 4;

                    convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_2, null);

                    TextView op1CantidadTextView = (TextView) convertView.findViewById(R.id.op1CarritoPC2TextView);
                    TextView op2NombreTextView = (TextView) convertView.findViewById(R.id.op2CarritoPC2TextView);
                    TextView op3PrecioTextView = (TextView) convertView.findViewById(R.id.op3CarritoPC2TextView);
                    TextView op4ComplementosTextView = (TextView) convertView.findViewById(R.id.op4CarritoPC2TextView);

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

                } else if (itemViewType == 4){

                    convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_3, null);

                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1CarritoPC3ImageView);
                    ImageView op2ImageView = (ImageView) convertView.findViewById(R.id.op2CarritoPC3ImageView);
                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1CarritoPC3TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2CarritoPC3TextView);

                    op1TextView.setText(String.valueOf(puntosCliente) + " Puntos");
                    op1TextView.setTextColor(Color.BLACK);
                    op2TextView.setTextColor(Color.BLACK);
                    op2TextView.setText("Aplicar");
                    op1ImageView.setImageResource(R.drawable.bitcoin);
                    op2ImageView.setImageResource(R.drawable.one_option_uncheck);

                    if (puntosCliente <= 1){

                        op1TextView.setTextColor(Color.LTGRAY);
                        op2TextView.setTextColor(Color.LTGRAY);
                        op2TextView.setText("(No puntos suficientes)");
                    }

                    if (tienePagoConPts == false){

                        op1TextView.setTextColor(Color.LTGRAY);
                        op2TextView.setTextColor(Color.LTGRAY);
                        op1TextView.setText("Pago con puntos no disponible");
                        op2TextView.setText("");
                        op2ImageView.setImageResource(0);

                    }

                    if (usarPuntos){

                        if (tienePagoConPts){

                            op2ImageView.setImageResource(R.drawable.one_option_check);

                            if (puntosCliente <= 1){

                                op2ImageView.setImageResource(0);

                            }
                        }
                    }

                    return convertView;

                } else if (itemViewType == 5){

                    convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_4, null);

                    convertView.setEnabled(false);

                    TextView op1SubTotalTextView = (TextView) convertView.findViewById(R.id.op1CarritoPC4TextView);
                    TextView op2PuntosTextView = (TextView) convertView.findViewById(R.id.op2CarritoPC4TextView);
                    TextView op3TotalTextView = (TextView) convertView.findViewById(R.id.op3CarritoPC4TextView);
                    TextView op4PrecioDescTextView = (TextView) convertView.findViewById(R.id.op4CarritoPC4TextView);

                    if (descuento == 0){

                        op1SubTotalTextView.setText("$" + String.valueOf(subTotal));
                        op2PuntosTextView.setText("-" + String.valueOf(puntosAplicados));
                        op3TotalTextView.setText("$" + String.valueOf(totalFinal));
                        op4PrecioDescTextView.setText("");

                        precioEnButton = totalFinal;

                    } else {

                        op1SubTotalTextView.setText("$" + String.valueOf(precioConDescuento));
                        op2PuntosTextView.setText("-" + String.valueOf(puntosAplicados));
                        op3TotalTextView.setText("$" + String.valueOf(totalFinal));
                        op4PrecioDescTextView.setText("$" + String.valueOf(subTotal));
                        op4PrecioDescTextView.setPaintFlags(op4PrecioDescTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


                        precioEnButton = totalFinal;

                    }

                    return convertView;

                } else if (itemViewType == 6){

                    if (opcionEntrega.matches("Domicilio")){

                        convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_5, null);

                        TextView op1TextView = (TextView) convertView.findViewById(R.id.op1CarritoPC5TextView);

                        op1TextView.setText("Sólo efectivo en servicio a domicilio");
                        op1TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                        return convertView;

                    } else {

                        //Celda vacia

                        convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                        return convertView;

                    }
                }

            } else {

                if (itemViewType == 0) {

                    convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_1, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1CarritoPC1TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2CarritoPC1TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3CarritoPC1TextView);
                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1CarritoPC1ImageView);

                    if (opcionEntrega.matches("")) {

                        op1ImageView.setImageResource(R.drawable.food_delivery);
                        op1TextView.setText("Opciones de entrega");
                        op2TextView.setText("A domicilio, recoger o en restaurante");
                        op3TextView.setText("Selecciona una opción aquí");
                        op3TextView.setTextColor(Color.RED);
                        op2TextView.setTypeface(Typeface.DEFAULT);

                        op2TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);


                    } else if (opcionEntrega.matches("Domicilio")) {

                        op1ImageView.setImageResource(R.drawable.food_delivery);
                        op1TextView.setText("A domicilio");
                        op2TextView.setText(calle + " " + numeroExterior);
                        op3TextView.setText(colonia + ", " + delegacion + ", " + codigoPostal + ", " + entreCalles);
                        op3TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                        op2TextView.setTypeface(Typeface.DEFAULT_BOLD);

                    } else if (opcionEntrega.matches("Recoger")) {

                        op1ImageView.setImageResource(R.drawable.take_away);
                        op1TextView.setText("Recoger pedido");
                        op2TextView.setText(hora);
                        op3TextView.setText("");
                        op3TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                        op2TextView.setTypeface(Typeface.DEFAULT_BOLD);
                        op3TextView.getLayoutParams().height = 0;

                    } else if (opcionEntrega.matches("Restaurante")) {

                        op1ImageView.setImageResource(R.drawable.spoon);
                        op1TextView.setText("");
                        op2TextView.setText("Consumo en restaurante");
                        op3TextView.setText("");
                        op3TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                        op2TextView.setTypeface(Typeface.DEFAULT_BOLD);

                        op1TextView.getLayoutParams().height = 0;
                        op3TextView.getLayoutParams().height = 0;

                    }

                    return convertView;

                } else if (itemViewType == 1){

                    if (opcionEntrega.matches("Domicilio") || opcionEntrega.matches("Recoger")){

                        convertView = mInflater.inflate(R.layout.validar_pedido_cell_2, null);

                        ImageView op1IconoImageView = (ImageView) convertView.findViewById(R.id.op1ValidarPCell2ImageView);
                        TextView op1TituloTextView = (TextView) convertView.findViewById(R.id.op1ValidarPCell2TextView);
                        TextView op2DescTextView = (TextView) convertView.findViewById(R.id.op2ValidarPCell2TextView);

                        op1IconoImageView.setImageResource(R.drawable.call);
                        op1TituloTextView.setText("");
                        op2DescTextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                        op2DescTextView.setText(numeroCliente);
                        op2DescTextView.setTypeface(Typeface.DEFAULT_BOLD);

                        if (numeroCliente.matches("")) {

                            op2DescTextView.setTextColor(Color.RED);
                            op2DescTextView.setText("Agrega tu número de contacto");
                            op2DescTextView.setTypeface(Typeface.DEFAULT);

                        }

                        return convertView;

                    } else {

                        //Celda vacia

                        convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                        return convertView;

                    }

                } else if (itemViewType == 2){

                    convertView = mInflater.inflate(R.layout.una_opcion_con_flecha, null);
                    convertView.setEnabled(false);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opcion1FlechaTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.opcion2FlechaTextView);

                    op1TextView.setText("Tu pedido");
                    op2TextView.setText("");
                    op1TextView.setTypeface(Typeface.DEFAULT_BOLD);


                } else if (itemViewType == 3){

                    int pos = position - 4;

                    convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_2, null);

                    TextView op1CantidadTextView = (TextView) convertView.findViewById(R.id.op1CarritoPC2TextView);
                    TextView op2NombreTextView = (TextView) convertView.findViewById(R.id.op2CarritoPC2TextView);
                    TextView op3PrecioTextView = (TextView) convertView.findViewById(R.id.op3CarritoPC2TextView);
                    TextView op4ComplementosTextView = (TextView) convertView.findViewById(R.id.op4CarritoPC2TextView);

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

                } else if (itemViewType == 4){

                    convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_3, null);

                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1CarritoPC3ImageView);
                    ImageView op2ImageView = (ImageView) convertView.findViewById(R.id.op2CarritoPC3ImageView);
                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1CarritoPC3TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2CarritoPC3TextView);

                    op1TextView.setText(String.valueOf(puntosCliente) + " Puntos");
                    op1TextView.setTextColor(Color.BLACK);
                    op2TextView.setTextColor(Color.BLACK);
                    op2TextView.setText("Aplicar");
                    op1ImageView.setImageResource(R.drawable.bitcoin);
                    op2ImageView.setImageResource(R.drawable.one_option_uncheck);

                    if (puntosCliente <= 1){

                        op1TextView.setTextColor(Color.LTGRAY);
                        op2TextView.setTextColor(Color.LTGRAY);
                        op2TextView.setText("(No puntos suficientes)");
                    }

                    if (tienePagoConPts == false){

                        op1TextView.setTextColor(Color.LTGRAY);
                        op2TextView.setTextColor(Color.LTGRAY);
                        op1TextView.setText("Pago con puntos no disponible");
                        op2TextView.setText("");
                        op2ImageView.setImageResource(0);

                    }

                    if (usarPuntos){

                        if (tienePagoConPts){

                            op2ImageView.setImageResource(R.drawable.one_option_check);

                            if (puntosCliente <= 1){

                                op2ImageView.setImageResource(0);

                            }
                        }
                    }

                    return convertView;

                } else if (itemViewType == 5){

                    convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_4, null);

                    convertView.setEnabled(false);

                    TextView op1SubTotalTextView = (TextView) convertView.findViewById(R.id.op1CarritoPC4TextView);
                    TextView op2PuntosTextView = (TextView) convertView.findViewById(R.id.op2CarritoPC4TextView);
                    TextView op3TotalTextView = (TextView) convertView.findViewById(R.id.op3CarritoPC4TextView);
                    TextView op4PrecioDescTextView = (TextView) convertView.findViewById(R.id.op4CarritoPC4TextView);

                    if (descuento == 0){

                        op1SubTotalTextView.setText("$" + String.valueOf(subTotal));
                        op2PuntosTextView.setText("-" + String.valueOf(puntosAplicados));
                        op3TotalTextView.setText("$" + String.valueOf(totalFinal));
                        op4PrecioDescTextView.setText("");

                        precioEnButton = totalFinal;

                    } else {

                        op1SubTotalTextView.setText("$" + String.valueOf(precioConDescuento));
                        op2PuntosTextView.setText("-" + String.valueOf(puntosAplicados));
                        op3TotalTextView.setText("$" + String.valueOf(totalFinal));
                        op4PrecioDescTextView.setText("$" + String.valueOf(subTotal));
                        op4PrecioDescTextView.setPaintFlags(op4PrecioDescTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


                        precioEnButton = totalFinal;

                    }


                    return convertView;

                } else if (itemViewType == 6){

                    if (opcionEntrega.matches("Domicilio")){

                        convertView = mInflater.inflate(R.layout.carrito_pedidos_cell_5, null);

                        TextView op1TextView = (TextView) convertView.findViewById(R.id.op1CarritoPC5TextView);

                        op1TextView.setText("Sólo efectivo en servicio a domicilio");
                        op1TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                        return convertView;

                    } else {

                        //Celda vacia

                        convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                        return convertView;

                    }
                }
            }

            return convertView;

        }
    }
}
