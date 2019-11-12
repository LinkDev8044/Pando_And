package com.parse.starter.VistaComercio.Colaborador;

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
import com.parse.ParseUser;
import com.parse.starter.Inicio.inicio_Pando_Activity;
import com.parse.starter.R;
import com.parse.starter.VistaComercio.Administrador.AdministradorActivity;
import com.parse.starter.VistaComercio.ClientesActivos.ClientesActivosActivity;

import java.util.List;

public class ColaboradorActivity extends AppCompatActivity {

    String nombreCompletoAdmin;
    String nombreAdministrador;
    String nombreComercio;
    String comercioId;
    String encuestaActiva;
    String encuestaActivaId;
    String recompensaActiva;

    int numeroDePreguntas;

    Boolean esAdministrador;
    Boolean isLogOut;

    String[] TITULOS = {"Ver clientes activos"};
    String[] DESCRIPCIONES = {"Enviar/Canjear puntos\nValidar compras"};

    int[] IMAGES = {R.drawable.enviar_encuesta};

    ListView colaboradorListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colaborador);

        getSupportActionBar().hide();

        if (ParseUser.getCurrentUser() == null){

            startActivity(new Intent(getApplicationContext(), inicio_Pando_Activity.class));

        } else {

            colaboradorListView = (ListView) findViewById(R.id.colaboradorListView);

            customAdapter = new CustomAdapter();

            colaboradorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 1){

                        Intent intent = new Intent(getApplicationContext(), ClientesActivosActivity.class);
                        intent.putExtra("comercioId", comercioId);
                        intent.putExtra("nombreComercio", nombreComercio);
                        intent.putExtra("encuestaActiva", encuestaActiva);
                        intent.putExtra("encuestaActivaId", encuestaActivaId);
                        intent.putExtra("numeroDePreguntas", numeroDePreguntas);
                        intent.putExtra("nombreCompletoAdmin", nombreCompletoAdmin);
                        intent.putExtra("recompensaActiva", recompensaActiva);
                        startActivity(intent);

                    }

                    if (position == 2){

                        if (esAdministrador){

                            startActivity(new Intent(getApplicationContext(), AdministradorActivity.class));

                        } else {

                            if (isLogOut){

                                ParseUser.logOut();
                                startActivity(new Intent(getApplicationContext(), inicio_Pando_Activity.class));

                            } else {

                                isLogOut = true;

                                Toast.makeText(ColaboradorActivity.this, "Presiona de nuevo para CONFIRMAR cerrar sesión", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        isLogOut = false;

                                    }
                                }, 5000);
                            }
                        }
                    }
                }
            });

            iniciarSppiner();

            nombreCompletoAdmin= "";
            nombreAdministrador= "";
            nombreComercio= "";
            comercioId= "";
            esAdministrador = false;
            isLogOut = false;

            ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestasAplicadas");
            query.whereEqualTo("colaboradorId", ParseUser.getCurrentUser().getObjectId());
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            nombreCompletoAdmin = object.getString("nombreColaborador");
                            nombreAdministrador = object.getString("nombre");
                            nombreComercio = object.getString("nombreComercio");
                            esAdministrador = object.getBoolean("esAdministrador");
                            comercioId = object.getString("comercioId");

                        }

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestaActiva");
                        query.whereEqualTo("comercioId", comercioId);
                        query.setLimit(1);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {

                                if (e == null){

                                    for (ParseObject object : objects){

                                        encuestaActiva = object.getString("nombreEncuesta");
                                        encuestaActivaId = object.getString("encuestaId");
                                        numeroDePreguntas = object.getInt("numeroDePreguntas");

                                    }

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("RecompensaActiva");
                                    query.whereEqualTo("comercioId", comercioId);
                                    query.setLimit(1);
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {

                                            if (e == null){

                                                for (ParseObject object : objects){

                                                    recompensaActiva = object.getString("recompensaActiva");

                                                }

                                            } else {

                                                terminarSppiner();

                                                Toast.makeText(ColaboradorActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                } else {

                                    terminarSppiner();

                                    Toast.makeText(ColaboradorActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                        terminarSppiner();

                        colaboradorListView.setAdapter(customAdapter);

                    } else {

                        terminarSppiner();

                        Toast.makeText(ColaboradorActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    class CustomAdapter extends BaseAdapter implements Adapter{

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0){

                return 0;

            } else {

                return 1;

            }

        }

        @Override
        public int getCount() {
            return 3;
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

            int itemViewType = getItemViewType(position);

            if (convertView == null){

                if (itemViewType == 0) {

                    convertView = mInflater.inflate(R.layout.colaborador_cell_1, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1ColCell1TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2ColCell1TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3ColCell1TextView);

                    op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                    op3TextView.setTextColor(Color.BLACK);

                    op1TextView.setText(nombreCompletoAdmin);
                    op2TextView.setText(nombreComercio);
                    op3TextView.setText("Colaborador");

                    if (esAdministrador) {

                        op3TextView.setText("Administrador");

                    }

                    return convertView;

                } else if (itemViewType == 1) {

                    int pos = position - 1;

                    convertView = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

                    ImageView op1ImageView = (ImageView) convertView.findViewById(R.id.opc1ImaImageView);
                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opc1ImaTextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.opc2ImaTextView);
                    TextView flechaTextView = (TextView) convertView.findViewById(R.id.textView16);
                    op2TextView.setTextColor(Color.GRAY);

                    if (pos == 0) {

                        op1ImageView.setImageResource(IMAGES[pos]);
                        op1TextView.setText(TITULOS[pos]);
                        op2TextView.setText(DESCRIPCIONES[pos]);

                    }

                    if (pos == 1){

                        if (esAdministrador){

                            op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                            flechaTextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                            op1ImageView.setImageResource(R.drawable.ir_a_administrador);
                            op1TextView.setText("Ir a Administrador");

                        } else {

                            op1ImageView.setImageResource(R.drawable.cerrar_sesion);
                            op1TextView.setText("Cerrar sesión");

                        }
                    }
                }
            }

            return convertView;

        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
