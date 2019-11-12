package com.parse.starter.VistaComercio.ProductosCliente;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaComercio.EnviarEncuesta.EnviarEncuestaActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class CanjearProductosActivity extends AppCompatActivity {

    String tituloSelec;
    String fechaSelec;
    String terminosSelec;
    String comercioId;
    String usuarioId;
    String productoIdClienteSelec;
    String productoIdTiendaSelec;

    Boolean activoSelec;

    int precioSelec;
    int contador;

    Date fecha;

    ListView canjearListView;

    TextView canjearTextView;

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

    public void canjear(View view){

        if (contador == 0){

            contador = 1;

            canjearTextView.setText("CONFIRMAR");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    buttonEnabled();

                }
            }, 5000);

        } else if (contador == 1){

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

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosCliente");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("usuarioId", usuarioId);
            query.whereEqualTo("objectId", productoIdClienteSelec);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            object.put("fechaModificacion", fecha);
                            object.put("activo", false);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e ==  null){

                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductosTienda");
                                        query.whereEqualTo("objectId", productoIdTiendaSelec);
                                        query.setLimit(1);
                                        query.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, ParseException e) {

                                                if (e == null) {

                                                    for (ParseObject object : objects){

                                                        int canjeados = object.getInt("canjeados");
                                                        object.put("fechaModificacion", fecha);
                                                        object.put("canjeados", canjeados + 1);
                                                        object.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {

                                                                if (e == null){

                                                                    contador = 2;

                                                                    canjearTextView.setText("LISTO");

                                                                    canjearListView.setAdapter(customAdapter);

                                                                    terminarSppiner();


                                                                } else {

                                                                    terminarSppiner();

                                                                    Toast.makeText(CanjearProductosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                                    }

                                                } else {

                                                    terminarSppiner();

                                                    Toast.makeText(CanjearProductosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                                    } else {

                                        terminarSppiner();

                                        Toast.makeText(CanjearProductosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(CanjearProductosActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else if (contador == 2){

            finish();

        }
    }

    private void buttonDisabled(){

        canjearTextView.setEnabled(false);
        canjearTextView.setTextColor(Color.WHITE);
        canjearTextView.setBackgroundColor(getResources().getColor(R.color.gris_oscuro_pando));
        canjearTextView.setText("Ya fue canjeado 👍");
    }

    private void buttonEnabled(){

        if (contador != 2) {

            contador = 0;

            canjearTextView.setEnabled(true);
            canjearTextView.setTextColor(Color.WHITE);
            canjearTextView.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
            canjearTextView.setText("Canjear");

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canjear_productos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        canjearListView = (ListView) findViewById(R.id.canjearListView);
        canjearTextView = (TextView) findViewById(R.id.canjearTextView);

        Intent intent = getIntent();
        tituloSelec = intent.getStringExtra("tituloSelec");
        fechaSelec = intent.getStringExtra("fechaSelec");
        precioSelec = intent.getIntExtra("precioSelec", 0);
        terminosSelec = intent.getStringExtra("terminosSelec");
        activoSelec = intent.getBooleanExtra("activoSelec", false);
        comercioId = intent.getStringExtra("comercioId");
        usuarioId = intent.getStringExtra("usuarioId");
        productoIdClienteSelec = intent.getStringExtra("productoIdClienteSelec");
        productoIdTiendaSelec = intent.getStringExtra("productoIdTiendaSelec");

        customAdapter = new CustomAdapter();

        if (activoSelec){

            buttonEnabled();

        } else {

            buttonDisabled();

        }

        canjearListView.setAdapter(customAdapter);

    }

    class CustomAdapter extends BaseAdapter implements Adapter{

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getViewTypeCount() {

            if (contador == 2){

                return 1;

            } else {

                return 2;

            }
        }

        @Override
        public int getItemViewType(int position) {

            if (contador == 2){

                return 2;

            } else {

                if (position == 0){

                    return 0;

                } else {

                    return 1;

                }
            }
        }

        @Override
        public int getCount() {

            if (contador == 2){

                return 1;

            } else {

                return 2;

            }
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

                    convertView = mInflater.inflate(R.layout.canjear_productos_cell_1, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1CanjearProdTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2CanjearProdTextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3CanjearProdTextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4CanjearProdTextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5CanjearProdTextView);

                    op1TextView.setText(tituloSelec);
                    op2TextView.setText("Fecha de compra");
                    op3TextView.setTextColor(Color.BLACK);
                    op3TextView.setText(fechaSelec);
                    op4TextView.setText("Precio en puntos");
                    op5TextView.setText(String.valueOf(precioSelec));
                    op5TextView.setTextColor(Color.BLACK);

                    return convertView;

                } else if (itemViewType == 1){

                    convertView = mInflater.inflate(R.layout.general_titulo_descripcion, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1GeneralTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2GeneralTextView);

                    op2TextView.setTextColor(Color.BLACK);
                    op1TextView.setText("Términos y condiciones");
                    op2TextView.setText(terminosSelec);

                    return convertView;

                } else if (itemViewType == 2){

                    convertView = mInflater.inflate(R.layout.canjear_productos_cell_2, null);

                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.op1CanjearProdImageView);

                    op1ImageView.setImageResource(R.drawable.success);

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
}
