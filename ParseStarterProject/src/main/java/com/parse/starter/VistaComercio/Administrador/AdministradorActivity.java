package com.parse.starter.VistaComercio.Administrador;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.parse.starter.Inicio.inicio_Pando_Activity;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;
import com.parse.starter.VistaComercio.Colaborador.ColaboradorActivity;
import com.parse.starter.VistaComercio.EditarComercio.EditarComercioActivity;

import java.util.List;

public class AdministradorActivity extends AppCompatActivity {

    String nombreAdminCompleto;
    String nombreAdminSolo;
    String nombreComercio;
    String comercioId;

    String[] TITULOS = {"Reporte general", "Ir a modo Colaborador"};
    String[] DESCRIPCIONES = {"", "Enviar/Canjear puntos\nValidar compras"};

    int[] IMAGES = {R.drawable.bar_chart, R.drawable.ir_a_modo_colaborador};

    Boolean tieneLogo;

    Bitmap logoComercio;

    ListView adminListView;

    CustomAdapter customAdapter;

    ProgressDialog progressDialog;

    private void goToEditarComercio(){

        Intent intent = new Intent(getApplicationContext(), EditarComercioActivity.class);
        intent.putExtra("comercioId", comercioId);
        intent.putExtra("nombreAdminCompleto", nombreAdminCompleto);
        intent.putExtra("nombreComercio", nombreComercio);
        startActivity(intent);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        getSupportActionBar().hide();

        adminListView = (ListView) findViewById(R.id.administradorListView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        iniciarSppiner();

        if (ParseUser.getCurrentUser() == null){

            terminarSppiner();

            startActivity(new Intent(getApplicationContext(), inicio_Pando_Activity.class));

        } else {

            adminListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent;

                    if (position == 1){

                        intent = new Intent(getApplicationContext(), ReporteGeneralActivity.class);
                        intent.putExtra("comercioId", comercioId);
                        startActivity(intent);

                    }

                    if (position == 2){

                        intent = new Intent(getApplicationContext(), ColaboradorActivity.class);
                        startActivity(intent);

                    }

                }
            });

            customAdapter = new CustomAdapter();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestasAplicadas");
            query.whereEqualTo("colaboradorId", ParseUser.getCurrentUser().getObjectId());
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            nombreAdminCompleto = object.getString("nombreColaborador");
                            nombreAdminSolo = object.getString("nombre");
                            nombreComercio = object.getString("nombreComercio");
                            comercioId = object.getString("comercioId");

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

                                                        adminListView.setAdapter(customAdapter);

                                                        terminarSppiner();


                                                    } else {

                                                        terminarSppiner();

                                                        Toast.makeText(AdministradorActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }

                                    } else {

                                        tieneLogo = false;

                                        adminListView.setAdapter(customAdapter);

                                        terminarSppiner();

                                    }

                                } else {

                                    terminarSppiner();

                                    Toast.makeText(AdministradorActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    } else  {

                        terminarSppiner();

                        Toast.makeText(AdministradorActivity.this, "Tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

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

                if (itemViewType == 0){

                    convertView = mInflater.inflate(R.layout.administrador_cell_1, null);

                    ImageView logoImageView = (ImageView) convertView.findViewById(R.id.op1AdminCell1ImageView);
                    TextView op1textView = (TextView) convertView.findViewById(R.id.op1AdminCell1textView);
                    TextView op2textView = (TextView) convertView.findViewById(R.id.op2AdminCell1textView);
                    TextView op3textView = (TextView) convertView.findViewById(R.id.op3AdminCell1textView);

                    op2textView.setText(nombreComercio);
                    op3textView.setText(nombreAdminCompleto);

                    logoImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            goToEditarComercio();

                        }
                    });

                    op1textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            goToEditarComercio();

                        }
                    });

                    if (tieneLogo){

                        logoImageView.setImageBitmap(logoComercio);

                    } else {

                        logoImageView.setImageResource(R.drawable.store);

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

                    op1ImageView.setImageResource(IMAGES[pos]);
                    op1TextView.setText(TITULOS[pos]);
                    op2TextView.setText(DESCRIPCIONES[pos]);

                    if (pos == 1){

                        op1TextView.setTextColor(getResources().getColor(R.color.verde_Pando));
                        flechaTextView.setTextColor(getResources().getColor(R.color.verde_Pando));

                    }

                    return convertView;

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
