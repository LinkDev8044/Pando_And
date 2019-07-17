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
import com.parse.starter.VistaClientes.HistorialPuntos.HistorialPuntosActivity;
import com.parse.starter.VistaClientes.MisCompras.MisComprasActivity;
import com.parse.starter.VistaClientes.TiendaComercio.TiendaActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DescripcionComercioActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String nombreComercio;
    String comercioId;
    String nombreCliente;
    String apellidoCliente;
    String correoCliente;
    String descripcionCom;

    String[] TITULOS = {"Ver tienda", "Ver mis compras", "Ver historial de puntos"};
    String[] DESCRIPCIONES = {"Compra productos o servicios con tus puntos aquí", "", ""};

    Double puntosCliente;

    int porcentajeValor;

    int[] IMAGES = {R.drawable.shop, R.drawable.configurar_recompensas, R.drawable.list};

    Boolean tieneLogo;
    Boolean usuarioActivo;
    Boolean ofreceRecompensa;
    Boolean ofrecePuntos;
    Boolean tieneDescripcion;
    Boolean tieneTienda;
    Boolean clienteTieneProduc;
    Boolean tieneHistorial;

    Bitmap logoComercio;

    Date fecha;

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

                    } else {

                        terminarSppiner();

                        Toast.makeText(DescripcionComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void reloadData(){

        tieneLogo = false;
        usuarioActivo = false;
        ofreceRecompensa = false;
        tieneDescripcion = false;
        tieneTienda = false;
        clienteTieneProduc = false;
        tieneHistorial = false;

        descripcionCom = "(Sin descripción)";

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comercios");
        query.whereEqualTo("nombreComercio", nombreComercio);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        comercioId = object.getObjectId();

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

                                    usuarioActivo = true;
                                    buttonClienteActivo();

                                } else {

                                    usuarioActivo = false;

                                    buttonClienteInactivo();

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

                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("RecompensaActiva");
                                            query.whereEqualTo("comercioId", comercioId);
                                            query.whereNotEqualTo("recompensaActiva", "noRecompensa");
                                            query.setLimit(1);
                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {

                                                    if (e == null){

                                                        if (objects.size() > 0){

                                                            ofreceRecompensa = true;

                                                        } else {

                                                            ofreceRecompensa = false;

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

                                                                                porcentajeValor = object.getInt("porcentaje");

                                                                            } else {

                                                                                porcentajeValor = 0;

                                                                            }
                                                                        }

                                                                    } else {

                                                                        ofrecePuntos = false;
                                                                        porcentajeValor = 0;

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

        customAdapter = new CustomAdapter();

        reloadData();

        descripcionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent;

                if (i == 1){

                    intent = new Intent(getApplicationContext(), TiendaActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }

                if (i == 2){

                    intent = new Intent(getApplicationContext(), MisComprasActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }

                if (i == 3){

                    intent = new Intent(getApplicationContext(), HistorialPuntosActivity.class);
                    intent.putExtra("comercioId", comercioId);
                    startActivity(intent);

                }
            }
        });

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

        descripcionListView = (ListView) findViewById(R.id.descripcionListView);

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
            }

            if (position == 1){

                if (tieneTienda) {

                  return 1;

                }

                return 3;
            }

            if (position == 2){

                if (clienteTieneProduc){

                    return 1;

                }

                return 3;
            }

            if (position == 3){

                if (tieneHistorial){

                    return 1;

                }

                return 3;
            }

            if (position == 4){

                return 2;
            }

            return 1;

        }

        @Override
        public int getCount() {
            return 5;
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
                    TextView porcentajeTextView = (TextView) view.findViewById(R.id.op3DescTextView);
                    ImageView logoImageView = (ImageView) view.findViewById(R.id.op1DescImageView);
                    ImageView recompensaImageView = (ImageView) view.findViewById(R.id.op2DescImageView);
                    ImageView puntosImageView = (ImageView) view.findViewById(R.id.op3DescImageView);

                    nombreComTexetView.setText(nombreComercio);
                    puntosClienteTextView.setText(String.valueOf(puntosCliente));
                    porcentajeTextView.setText(String.valueOf(porcentajeValor) + "%");

                    if (ofreceRecompensa) {

                        recompensaImageView.setImageResource(R.drawable.success);

                    } else {

                        recompensaImageView.setImageResource(R.drawable.nop);

                    }

                    if (ofrecePuntos) {

                        puntosImageView.setImageResource(R.drawable.success);

                    } else {

                        puntosImageView.setImageResource(R.drawable.nop);

                    }

                    if (tieneLogo) {

                        logoImageView.setImageBitmap(logoComercio);

                    } else {

                        logoImageView.setImageResource(R.drawable.store);
                    }

                    return view;

                } else if (itemViewType == 1){

                    int pos = i - 1;

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

                }
            }

            if (itemViewType == 0) {

                view = mInflater.inflate(R.layout.descripcion_comercio_cell_1, null);

                view.setEnabled(false);

                TextView nombreComTexetView = (TextView) view.findViewById(R.id.op1DescTexxtView);
                TextView puntosClienteTextView = (TextView) view.findViewById(R.id.op2GeneralTextView);
                TextView porcentajeTextView = (TextView) view.findViewById(R.id.op3DescTextView);
                ImageView logoImageView = (ImageView) view.findViewById(R.id.op1DescImageView);
                ImageView recompensaImageView = (ImageView) view.findViewById(R.id.op2DescImageView);
                ImageView puntosImageView = (ImageView) view.findViewById(R.id.op3DescImageView);

                nombreComTexetView.setText(nombreComercio);
                puntosClienteTextView.setText(String.valueOf(puntosCliente));
                porcentajeTextView.setText(String.valueOf(porcentajeValor) + "%");

                if (ofreceRecompensa) {

                    recompensaImageView.setImageResource(R.drawable.success);

                } else {

                    recompensaImageView.setImageResource(R.drawable.nop);

                }

                if (ofrecePuntos) {

                    puntosImageView.setImageResource(R.drawable.success);

                } else {

                    puntosImageView.setImageResource(R.drawable.nop);

                }

                if (tieneLogo) {

                    logoImageView.setImageBitmap(logoComercio);

                } else {

                    logoImageView.setImageResource(R.drawable.store);
                }

                return view;

            } else if (itemViewType == 1){

                int pos = i - 1;

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
