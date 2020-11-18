package com.parse.starter.VistaClientes.VerNewMenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.parse.starter.R;
import com.parse.starter.VistaClientes.CarritoPedidos.CarritoPedidosActivity;
import com.parse.starter.VistaClientes.Platillos.PlatillosActivity;

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

public class VerNewMenuActivity extends AppCompatActivity {

    String comercioId;
    String nombreComercio;
    String nombreComCliente;
    String numeroWhats;
    String nombreUsuario;
    String distanciaComGuardar;
    String categoriaSelec;

    Boolean envioDisponible;
    Boolean esVecino;
    Boolean usuarioVisible;
    Boolean tienePedido;
    Boolean restAbierto;
    Double subTotalPedido;

    int contadorPlatillos;
    int horaAbierto;
    int horaCerrado;
    int minutoAbierto;
    int minutoCerrado;

    Date fecha;

    ArrayList<String> categoriaIdArray = new ArrayList();
    ArrayList<String> nomCategoriaArray = new ArrayList();

    Map<String,Bitmap> imagenesDic =  new HashMap<String,Bitmap>();

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet1 = new ConstraintSet();
    ConstraintSet constraintSet2 = new ConstraintSet();

    ListView verNewMenuListView;

    TextView verCarritoButton;
    TextView label1;
    TextView label2;

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

    private void regresar (){

        if (getCurrentFocus() != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }

        TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet1.applyTo(constraintLayout);

    }

    public void verCarrito(View view){

        Intent intent = new Intent(getApplicationContext(), CarritoPedidosActivity.class);
        intent.putExtra("nombreComCliente", nombreComCliente);
        intent.putExtra("comercioId", comercioId);
        intent.putExtra("envioDisponible", envioDisponible);
        intent.putExtra("numeroWhats", numeroWhats);
        intent.putExtra("nombreUsuario", nombreUsuario);
        intent.putExtra("nombreComercio", nombreComercio);

        //Datos adicionales para guardar "UsuarioActivo"
        intent.putExtra("esVecino", esVecino);
        intent.putExtra("distanciaComGuardar", distanciaComGuardar);
        intent.putExtra("usuarioVisible", usuarioVisible);
        startActivity(intent);

    }

    private void changeView(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet2.applyTo(constraintLayout);

                //Se carga aqui la tabla para evitar error que no deja hacer touch en ListView
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        terminarSppiner();
                        verNewMenuListView.setAdapter(customAdapter);

                    }
                }, 150);
            }
        }, 150);



        if (restAbierto == false){

            verCarritoButton.setBackgroundColor(Color.DKGRAY);
            verCarritoButton.setTextColor(Color.WHITE);
            verCarritoButton.setEnabled(false);
            verCarritoButton.setText("Restaurante CERRADO");
            label1.setBackgroundColor(Color.DKGRAY);
            label1.setTextColor(Color.WHITE);
            label1.setEnabled(false);
            label1.setText("");
            label2.setBackgroundColor(Color.DKGRAY);
            label2.setTextColor(Color.WHITE);
            label2.setEnabled(false);
            label2.setText("");

        } else {

            //Agregar labels al button (precio y # articulos)
            label1.setText(String.valueOf(contadorPlatillos));
            label1.setTextColor(Color.WHITE);
            label1.setEnabled(true);
            label1.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
            label2.setText("$" + String.valueOf(subTotalPedido));
            label2.setTextColor(Color.WHITE);
            label2.setEnabled(true);
            label2.setBackgroundColor(getResources().getColor(R.color.verde_Pando));

            verCarritoButton.setEnabled(true);
            verCarritoButton.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
            verCarritoButton.setTextColor(Color.WHITE);
            verCarritoButton.setText("Ver carrito");

        }

    }

    private void cargarTabla(){

        if (tienePedido){

            changeView();

        } else {

            regresar();

            terminarSppiner();
            verNewMenuListView.setAdapter(customAdapter);

        }
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

                        cargarTabla();

                    } else {



                        cargarTabla();

                    }

                } else {

                    terminarSppiner();

                }
            }
        });
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

        tienePedido = false;
        subTotalPedido = 0.0;
        contadorPlatillos = 0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PedidoCliente");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("activo", true);
        query.whereEqualTo("pedidoConfirmado", false);
        query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0) {

                        for (ParseObject object : objects){

                            long diff = fecha.getTime() - object.getDate("fechaCreacion").getTime();
                            long diffminutes = TimeUnit.MILLISECONDS.toMinutes(diff);

                            if (diffminutes >=180) {

                                object.put("activo", false);
                                object.saveInBackground();

                            } else {

                                tienePedido = true;
                                Double nuevoTotal = object.getDouble("subTotal");
                                subTotalPedido = subTotalPedido + nuevoTotal;
                                contadorPlatillos = contadorPlatillos + object.getInt("cantidad");

                            }
                        }

                        revisarHorarioCom();

                    } else {

                        revisarHorarioCom();
                    }

                } else {

                    terminarSppiner();

                }
            }
        });
    }

    private void reloadData(){

        categoriaIdArray.clear();
        imagenesDic.clear();
        nomCategoriaArray.clear();

        /*verCarritoButton.setEnabled(false);
        label1.setEnabled(false);
        label2.setEnabled(false);*/

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("CategoriasMenu");
        query.whereEqualTo("comercioId", comercioId);
        query.whereEqualTo("activo", true);
        query.orderByAscending("orden");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0) {

                        final int contadorImagenes = objects.size();

                        for (ParseObject object : objects) {

                            final String categoriaId = object.getObjectId();
                            categoriaIdArray.add(categoriaId);
                            nomCategoriaArray.add(object.getString("nombreCategoria"));

                            ParseFile parseFile1 = (ParseFile) object.get("imagenCategoria");
                            parseFile1.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {

                                    if (e == null) {

                                        imagenesDic.put(categoriaId, BitmapFactory.decodeByteArray(data, 0, data.length));

                                    } else {

                                        terminarSppiner();

                                    }

                                    if (imagenesDic.size() == contadorImagenes) {

                                        revisarPedidos();

                                    }
                                }
                            });
                        }

                    } else {

                        revisarPedidos();

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
        setContentView(R.layout.activity_ver_new_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Menú");

        constraintLayout = (ConstraintLayout) findViewById(R.id.verNewMenuConstraintLayout);
        constraintSet1.clone(constraintLayout);
        constraintSet2.clone(this, R.layout.detalle_ver_new_menu);

        verNewMenuListView = (ListView) findViewById(R.id.verNewMenuListView);
        verCarritoButton = (TextView) findViewById(R.id.op1VerCarritoVNMTextView);
        label1 = (TextView) findViewById(R.id.op2VerCarritoVNMTextView);
        label2 = (TextView) findViewById(R.id.op3VerCarritoVNMTextView);

        customAdapter = new CustomAdapter();

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");
        nombreComercio = intent.getStringExtra("nombreComercio");
        nombreComCliente = intent.getStringExtra("nombreComCliente");
        envioDisponible = intent.getBooleanExtra("envioDisponible", false);
        numeroWhats = intent.getStringExtra("numeroWhats");
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        esVecino = intent.getBooleanExtra("esVecino", false);
        distanciaComGuardar = intent.getStringExtra("distanciaComGuardar");
        usuarioVisible = intent.getBooleanExtra("usuarioVisible", false);

        verNewMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                categoriaSelec = nomCategoriaArray.get(position);

                Intent intent = new Intent(getApplicationContext(), PlatillosActivity.class);
                intent.putExtra("categoriaSelec", categoriaSelec);
                intent.putExtra("comercioId", comercioId);
                intent.putExtra("nombreComercio", nombreComercio);
                intent.putExtra("nombreComCliente", nombreComCliente);
                intent.putExtra("envioDisponible", envioDisponible);
                intent.putExtra("numeroWhats", numeroWhats);
                intent.putExtra("nombreUsuario", nombreUsuario);
                intent.putExtra("esVecino", esVecino);
                intent.putExtra("distanciaComGuardar", distanciaComGuardar);
                intent.putExtra("usuarioVisible", usuarioVisible);
                startActivity(intent);

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
        public int getCount() {
            return nomCategoriaArray.size();
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

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.ver_new_menu_cell_1, null);


                TextView op1TextView = (TextView) convertView.findViewById(R.id.op1VerNewMTextView);
                ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1VerNMImageView);

                op1ImageView.setImageBitmap(imagenesDic.get(categoriaIdArray.get(position)));
                op1TextView.setText(nomCategoriaArray.get(position));

                return convertView;

            } else {

                convertView = mInflater.inflate(R.layout.ver_new_menu_cell_1, null);


                TextView op1TextView = (TextView) convertView.findViewById(R.id.op1VerNewMTextView);
                ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1VerNMImageView);

                op1ImageView.setImageBitmap(imagenesDic.get(categoriaIdArray.get(position)));
                op1TextView.setText(nomCategoriaArray.get(position));

                return convertView;

            }
        }
    }
}
