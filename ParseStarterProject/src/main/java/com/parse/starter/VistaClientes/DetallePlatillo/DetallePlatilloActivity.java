package com.parse.starter.VistaClientes.DetallePlatillo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.VerNewMenu.VerNewMenuActivity;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

public class DetallePlatilloActivity extends AppCompatActivity {

    String comercioId;
    String nombreComercio;
    String nombreComCliente;
    String platilloId;
    String platilloSelec;
    String descPlatilloSelec;
    String complementosServidor;
    String complementosServidor2;
    String comentarios;

    int horaAbierto;
    int horaCerrado;
    int minutoAbierto;
    int minutoCerrado;
    int cantidad;

    ArrayList<String> nomAdicionalArray = new ArrayList();
    ArrayList<Boolean> esEncabezadoArray = new ArrayList();
    ArrayList<Integer> numOpcionesArray = new ArrayList();
    ArrayList<Double> seccionArray = new ArrayList();
    ArrayList<Double> costoArray = new ArrayList();

    Map<Integer,Integer> limiteOpciones =  new HashMap<Integer,Integer>();
    Map<Integer,Boolean> rowsSelected =  new HashMap<Integer,Boolean>();
    Map<Integer, String> complementosDic = new HashMap<Integer, String>();

    Boolean platilloLunes;
    Boolean platilloMartes;
    Boolean platilloMiercoles;
    Boolean platilloJueves;
    Boolean platilloViernes;
    Boolean platilloSabado;
    Boolean platilloDomingo;
    Boolean activarButton;
    Boolean tieneAdicionales;
    Boolean pedidoPendiente;
    Boolean restAbierto;
    Boolean platilloDisp;

    Double precioSelec;
    Double precioFinal;
    Double subTotalAdicionales;

    Bitmap imagenSelec = null;

    ListView detallePedidoListView;
    TextView agregarButton;
    TextView label1;

    Date fecha;

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

    public void agregarPlatillo(View view){

        complementosServidor = "";
        complementosServidor2 = "";

        SortedSet<Integer> keys = new TreeSet<>(complementosDic.keySet());
        for (Integer key : keys) {
            String value = complementosDic.get(key);
            // do something

            if (complementosServidor.matches("")){

                complementosServidor = value;
                complementosServidor2 = value;

            } else {

                complementosServidor = complementosServidor + "\n" + value;
                complementosServidor2 = complementosServidor2 + "·" + value;
            }
        }

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

        ParseObject object = new ParseObject("PedidoCliente");
        object.put("comercioId", comercioId);
        object.put("nombreComercio", nombreComercio);
        object.put("usuarioId", ParseUser.getCurrentUser().getObjectId());
        object.put("nombreCliente", nombreComCliente);
        object.put("fechaCreacion", fecha);
        object.put("fechaModificacion", fecha);
        object.put("activo", true);
        object.put("nombrePlatillo", platilloSelec);
        object.put("complementos",  complementosServidor); // Complementos  seperados por un \n
        object.put("complementos2", complementosServidor2); //Complementos separados por un  ·
        object.put("precioPlatillo", precioSelec);
        object.put("costoAdicionales", subTotalAdicionales);
        object.put("subTotal", precioFinal);
        object.put("comentarios", comentarios);
        object.put("opcionEntrega", "");
        object.put("hora", "");
        object.put("cantidad", cantidad);
        object.put("usarPuntos", false);
        object.put("pedidoConfirmado", false);
        object.put("whatsEnviado", false);
        object.put("etapa", 0);
        object.put("puntos", 0);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    terminarSppiner();
                    Intent intent = new Intent(getApplicationContext(), VerNewMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                } else {

                    terminarSppiner();

                }
            }
        });

    }

    private void calculoTotal(){

        precioFinal = (precioSelec + subTotalAdicionales) * Double.valueOf(cantidad);

        label1.setText("$" + String.valueOf(precioFinal));

        activarButton = true;

        for (Map.Entry<Integer, Integer> entry : limiteOpciones.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            if (entry.getValue() > 0){

                activarButton = false;

            }
        }

        if (activarButton){

            agregarButtonEnabled();
        } else {

            agregarButtonDisabled();
        }

        customAdapter.notifyDataSetChanged();

    }

    private void addButtonClicked(){

        cantidad += 1;

        calculoTotal();

    }

    private void minusButtonClicked(){

        if (cantidad > 1){

            cantidad = cantidad - 1;

            calculoTotal();
        }
    }

    private void agregarButtonEnabled(){

        if (pedidoPendiente || restAbierto == false || platilloDisp == false){

            agregarButtonDisabled();

        } else {

            agregarButton.setText("Agregar " + String.valueOf(cantidad));

            agregarButton.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
            agregarButton.setTextColor(Color.WHITE);
            agregarButton.setEnabled(true);
            label1.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
            label1.setTextColor(Color.WHITE);
            label1.setEnabled(true);

        }
    }

    private void agregarButtonDisabled(){

        agregarButton.setBackgroundColor(Color.DKGRAY);
        agregarButton.setTextColor(Color.WHITE);
        agregarButton.setEnabled(false);
        label1.setBackgroundColor(Color.DKGRAY);
        label1.setTextColor(Color.WHITE);
        label1.setEnabled(false);

        if (pedidoPendiente){

            agregarButton.setText("Tienes pedido pendiente");

        } else if (restAbierto == false){

            agregarButton.setText("Restaurante CERRADO");

        } else if (platilloDisp == false){

            agregarButton.setText("No disponible hoy");

        } else {

            agregarButton.setText("Agregar " + String.valueOf(cantidad));

        }
    }

    private void cargarImagenPlatillo(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PlatillosMenu");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("objectId", platilloId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {

                            ParseFile parseFile1 = (ParseFile) object.get("imagenPlatillo");
                            parseFile1.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {

                                    if (e == null) {

                                        imagenSelec = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        terminarSppiner();
                                        detallePedidoListView.setAdapter(customAdapter);

                                    } else {

                                        terminarSppiner();

                                    }
                                }
                            });
                        }

                    } else {

                        terminarSppiner();
                        detallePedidoListView.setAdapter(customAdapter);

                    }

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    private void revisarDispPlatillo(){

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("America/Mexico_City"));
        //final int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
        //final int minutoActual = calendar.get(Calendar.MINUTE);
        int numDeDia = calendar.get(Calendar.DAY_OF_WEEK);
        String dia = null;

        if (numDeDia == 1){
            dia = "domingo";
        } else if (numDeDia == 2){

            dia = "lunes";

        } else if (numDeDia == 3){

            dia = "martes";

        } else if (numDeDia == 4){

            dia = "miercoles";

        } else if (numDeDia == 5){

            dia = "jueves";

        } else if (numDeDia == 6){

            dia = "viernes";

        } else if (numDeDia == 7){

            dia = "sabado";

        }

        platilloDisp = false;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PlatillosMenu");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("objectId", platilloId);
        query.whereEqualTo(dia, true);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        platilloDisp = true;

                    }

                    if (activarButton){

                        agregarButtonEnabled();

                    } else {

                        agregarButtonDisabled();
                    }

                    cargarImagenPlatillo();

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    private void revisarHorarioCom(){

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("America/Mexico_City"));
        final int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
        final int minutoActual = calendar.get(Calendar.MINUTE);
        int numDeDia = calendar.get(Calendar.DAY_OF_WEEK);

        horaAbierto = 0;
        horaCerrado = 0;
        minutoAbierto = 0;
        minutoAbierto = 0;
        restAbierto = false;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("HorarioComercio");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("numDia", numDeDia);
        query.whereEqualTo("abierto", true);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            horaAbierto = object.getInt("horaAbierto");
                            horaCerrado = object.getInt("horaCerrado");
                            minutoAbierto = object.getInt("minutoAbierto");
                            minutoCerrado = object.getInt("minutoCerrado");

                            if (horaActual > horaAbierto){

                                if (horaActual < horaCerrado){

                                    restAbierto = true;

                                } else if (horaActual == horaCerrado){

                                    if (minutoActual <= minutoCerrado){

                                        restAbierto = true;

                                    }
                                }

                            } else if (horaActual == horaAbierto){

                                if (minutoActual >= minutoAbierto){

                                    restAbierto = true;

                                }
                            }
                        }

                        revisarDispPlatillo();

                    } else {

                        revisarDispPlatillo();

                    }

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    private void revisarPedidoPendiente(){

        pedidoPendiente = false;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoConfirmado");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("activo", true);
        query.orderByDescending("fechaCreacion");
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        pedidoPendiente = true;

                        agregarButtonDisabled();

                    }

                    revisarHorarioCom();

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    private void reloadData(){

        label1.setText("$" + String.valueOf(precioSelec));
        label1.setTextColor(Color.WHITE);

        nomAdicionalArray.clear();
        esEncabezadoArray.clear();
        numOpcionesArray.clear();
        limiteOpciones.clear();
        seccionArray.clear();
        costoArray.clear();
        //Registro de opciones seleccionados por el cliente
        rowsSelected.clear();

        activarButton = false;
        tieneAdicionales = false;

        complementosDic.clear();
        complementosServidor = "";
        comentarios = "";
        precioFinal = precioSelec;
        cantidad = 1;
        subTotalAdicionales = 0.0;

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("AdicionalesPlatillo");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("nombrePlatillo", platilloSelec);
        query.orderByAscending("orden");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    int contadorRows = 0;

                    if (objects.size() > 0){

                        tieneAdicionales = true;

                        for (ParseObject object : objects){

                            rowsSelected.put(contadorRows, false);

                            contadorRows += 1;

                            nomAdicionalArray.add(object.getString("nombreAdicional"));
                            esEncabezadoArray.add(object.getBoolean("esEncabezado"));
                            numOpcionesArray.add(object.getInt("numeroOpciones"));
                            seccionArray.add(object.getDouble("orden"));
                            costoArray.add(object.getDouble("costoAdicional"));

                            if (object.getBoolean("esEncabezado")){

                                limiteOpciones.put(object.getInt("orden"), object.getInt("numeroOpciones"));

                                Log.i("Prueba", String.valueOf(limiteOpciones));

                            }
                        }

                        revisarPedidoPendiente();

                    } else {

                        activarButton = true;

                        revisarPedidoPendiente();

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
        setContentView(R.layout.activity_detalle_platillo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detallePedidoListView = (ListView) findViewById(R.id.detallePedidoListView);
        agregarButton = (TextView) findViewById(R.id.op1DetallePTextView);
        label1 = (TextView) findViewById(R.id.op2DetallePTextView);
        //detallePedidoListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        customAdapter = new CustomAdapter();

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");
        platilloSelec = intent.getStringExtra("platilloSelec");
        descPlatilloSelec = intent.getStringExtra("descPlatilloSelec");
        precioSelec = intent.getDoubleExtra("precioSelec", 0);
        nombreComercio = intent.getStringExtra("nombreComercio");
        nombreComCliente = intent.getStringExtra("nombreComCliente");
        platilloId = intent.getStringExtra("platilloId");
        platilloLunes = intent.getBooleanExtra("platilloLunes", false);
        platilloMartes = intent.getBooleanExtra("platilloMartes", false);
        platilloMiercoles = intent.getBooleanExtra("platilloMiercoles", false);
        platilloJueves = intent.getBooleanExtra("platilloJueves", false);
        platilloViernes = intent.getBooleanExtra("platilloViernes", false);
        platilloSabado = intent.getBooleanExtra("platilloSabado", false);
        platilloDomingo = intent.getBooleanExtra("platilloDomingo", false);

        detallePedidoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int pos = position - 1;

                if (position > 0 && (position < nomAdicionalArray.size() + 1)) {

                    if (tieneAdicionales) {

                        if (esEncabezadoArray.get(pos) == false) {

                            int i = Integer.valueOf(seccionArray.get(pos).intValue());

                            if (limiteOpciones.get(i) > 0) {

                                if (rowsSelected.get(pos) == false) {

                                    rowsSelected.put(pos, true);

                                    int limite = limiteOpciones.get(i) - 1;

                                    limiteOpciones.put(i, limite);

                                    complementosDic.put(pos, nomAdicionalArray.get(pos));

                                    if (costoArray.get(pos) > 0){

                                        complementosDic.put(pos, complementosDic.get(pos) + " ($" + String.valueOf(costoArray.get(pos)) + ")");

                                        subTotalAdicionales = subTotalAdicionales + costoArray.get(pos);

                                    }

                                    //Agregar loas adicionales al diccionario

                                    calculoTotal();

                                } else {

                                    rowsSelected.put(pos, false);

                                    int limite = limiteOpciones.get(i) + 1;

                                    limiteOpciones.put(i, limite);

                                    complementosDic.remove(pos);

                                    if (costoArray.get(pos) > 0){

                                        subTotalAdicionales = subTotalAdicionales - costoArray.get(pos);
                                    }

                                    calculoTotal();

                                }

                            } else {

                                if (rowsSelected.get(pos)) {

                                    rowsSelected.put(pos, false);

                                    int limite = limiteOpciones.get(i) + 1;

                                    limiteOpciones.put(i, limite);

                                    complementosDic.remove(pos);

                                    if (costoArray.get(pos) > 0){

                                        subTotalAdicionales = subTotalAdicionales - costoArray.get(pos);
                                    }

                                    calculoTotal();

                                }
                            }

                            customAdapter.notifyDataSetChanged();


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

    class CustomAdapter extends BaseAdapter implements Adapter {

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0){

                return 0;

            } else if (position > 0 && (position < nomAdicionalArray.size() + 1)){

                if (esEncabezadoArray.get(position - 1)){

                    return 1;

                } else {

                    return 2;

                }

            } else {

                return 3;

            }

            //return super.getItemViewType(position);

        }

        @Override
        public int getCount() {
            return 2 + nomAdicionalArray.size();
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

                if (itemViewType == 0){

                    convertView = mInflater.inflate(R.layout.detalle_platillo_cell_1, null);

                    convertView.setEnabled(false);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DetallePC1TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DetallePC1TextView);
                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1DetallePC1ImageView);

                    detallePedidoListView.setItemChecked(position, true);

                    op1ImageView.setImageBitmap(imagenSelec);
                    op1TextView.setText(platilloSelec);

                    if (platilloDisp == false){

                        String mensaje = "Disponible ";

                        if (platilloLunes){

                            mensaje = mensaje + "·Lunes ";

                        }

                        if (platilloMartes){

                            mensaje = mensaje + "·Martes ";

                        }

                        if (platilloMiercoles){

                            mensaje = mensaje + "·Miércoles ";

                        }

                        if (platilloJueves){

                            mensaje = mensaje + "·Jueves ";

                        }

                        if (platilloViernes){

                            mensaje = mensaje + "·Viernes ";

                        }

                        if (platilloSabado){

                            mensaje = mensaje + "·Sábado ";

                        }

                        if (platilloDomingo){

                            mensaje = mensaje + "·Domingo ";

                        }

                        op2TextView.setText(mensaje);
                        op2TextView.setTextColor(Color.RED);

                    } else {

                        op2TextView.setText(descPlatilloSelec);
                        op2TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                    }

                    return convertView;

                } else if (itemViewType == 1){

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.detalle_platillo_cell_2, null);
                    convertView.setEnabled(false);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DetallePC2TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DetallePC2TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3DetallePC2TextView);

                    op1TextView.setText(nomAdicionalArray.get(pos));
                    op2TextView.setText("Límite " + String.valueOf(numOpcionesArray.get(pos)));

                    return convertView;

                } else if (itemViewType == 2){

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.detalle_platillo_cell_3, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DetallePC3TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DetallePC3TextView);
                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1DetallePC3ImageView);

                    op1TextView.setText(nomAdicionalArray.get(pos));
                    op2TextView.setText("");

                    if (costoArray.get(pos) > 0){

                        op2TextView.setText("+$" + String.valueOf(costoArray.get(pos)));
                    }

                    //Boolean isRowChecked = detallePedidoListView.isItemChecked(pos);

                    if (rowsSelected.get(pos)){

                        op1ImageView.setImageResource(R.drawable.one_option_check);

                        if (numOpcionesArray.get(pos) > 1){

                            op1ImageView.setImageResource(R.drawable.checkbox_uncheck);

                        }

                    } else {

                        op1ImageView.setImageResource(R.drawable.one_option_uncheck);

                        if (numOpcionesArray.get(pos) > 1){

                            op1ImageView.setImageResource(R.drawable.checkbox_check);

                        }
                    }

                    return convertView;

                } else if (itemViewType == 3){

                    convertView = mInflater.inflate(R.layout.detalle_platillo_cell_4, null);
                    convertView.setEnabled(true);

                    TextView cantidadTextView = (TextView) convertView.findViewById(R.id.op1DetallePC4TextView);
                    ImageView minusImageView = (ImageView) convertView.findViewById(R.id.op1DetallePC4ImageView);
                    ImageView addImageView = (ImageView) convertView.findViewById(R.id.op2DetallePC4ImageView);

                    cantidadTextView.setText(String.valueOf(cantidad));

                    minusImageView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            minusButtonClicked();

                            return false;
                        }
                    });

                    addImageView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            addButtonClicked();

                            return false;
                        }
                    });

                    return convertView;

                }

            } else {

                if (itemViewType == 0){

                    convertView = mInflater.inflate(R.layout.detalle_platillo_cell_1, null);

                    convertView.setEnabled(false);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DetallePC1TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DetallePC1TextView);
                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1DetallePC1ImageView);

                    op1ImageView.setImageBitmap(imagenSelec);
                    op1TextView.setText(platilloSelec);

                    if (platilloDisp == false){

                        String mensaje = "Disponible ";

                        if (platilloLunes){

                            mensaje = mensaje + "·Lunes ";

                        }

                        if (platilloMartes){

                            mensaje = mensaje + "·Martes ";

                        }

                        if (platilloMiercoles){

                            mensaje = mensaje + "·Miércoles ";

                        }

                        if (platilloJueves){

                            mensaje = mensaje + "·Jueves ";

                        }

                        if (platilloViernes){

                            mensaje = mensaje + "·Viernes ";

                        }

                        if (platilloSabado){

                            mensaje = mensaje + "·Sábado ";

                        }

                        if (platilloDomingo){

                            mensaje = mensaje + "·Domingo ";

                        }

                        op2TextView.setText(mensaje);
                        op2TextView.setTextColor(Color.RED);

                    } else {

                        op2TextView.setText(descPlatilloSelec);
                        op2TextView.setTextColor(getResources().getColor(R.color.gris_oscuro_pando));
                    }

                    return convertView;

                } else if (itemViewType == 1){

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.detalle_platillo_cell_2, null);
                    convertView.setEnabled(false);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DetallePC2TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DetallePC2TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3DetallePC2TextView);

                    op1TextView.setText(nomAdicionalArray.get(pos));
                    op2TextView.setText("Límite " + String.valueOf(numOpcionesArray.get(pos)));

                    return convertView;

                } else if (itemViewType == 2){

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.detalle_platillo_cell_3, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1DetallePC3TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2DetallePC3TextView);
                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1DetallePC3ImageView);

                    op1TextView.setText(nomAdicionalArray.get(pos));
                    op2TextView.setText("");

                    if (costoArray.get(pos) > 0){

                        op2TextView.setText("+$" + String.valueOf(costoArray.get(pos)));

                    }

                    Boolean isRowChecked = detallePedidoListView.isItemChecked(pos);

                    if (rowsSelected.get(pos)){

                        op1ImageView.setImageResource(R.drawable.one_option_check);

                        if (numOpcionesArray.get(pos) > 1){

                            op1ImageView.setImageResource(R.drawable.checkbox_uncheck);

                        }

                    } else {

                        op1ImageView.setImageResource(R.drawable.one_option_uncheck);

                        if (numOpcionesArray.get(pos) > 1){

                            op1ImageView.setImageResource(R.drawable.checkbox_check);

                        }
                    }

                    return convertView;

                } else if (itemViewType == 3){

                    convertView = mInflater.inflate(R.layout.detalle_platillo_cell_4, null);
                    convertView.setEnabled(true);

                    TextView cantidadTextView = (TextView) convertView.findViewById(R.id.op1DetallePC4TextView);
                    ImageView minusImageView = (ImageView) convertView.findViewById(R.id.op1DetallePC4ImageView);
                    ImageView addImageView = (ImageView) convertView.findViewById(R.id.op2DetallePC4ImageView);

                    cantidadTextView.setText(String.valueOf(cantidad));

                    minusImageView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            minusButtonClicked();

                            return false;
                        }
                    });

                    addImageView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            addButtonClicked();

                            return false;
                        }
                    });

                    return convertView;

                }
            }

            return convertView;

        }
    }
}
