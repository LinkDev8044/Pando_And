package com.parse.starter.registro_Usuario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.Inicio.inicio_Pando_Activity;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.ClienteActivity;
import com.parse.starter.VistaClientes.ListaComercios.ListaComerciosFragment;
import com.parse.starter.VistaClientes.TiendaComercio.CompraTiendaActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class InfoInicialActivity extends AppCompatActivity {

    String comercioId;
    String nombreCliente;
    String encuestaActivaId;

    int contador;

    ImageView logoImageView;

    TextView tituloTextView;
    TextView descipcionTextView;
    TextView siguienteTextView;

    Boolean mensajeAyuda;
    Boolean esAnonimo;
    Boolean esAgradecimiento;

    java.util.Date fecha;

    ProgressDialog progressDialog;

    public void siguiente(View view){

        if (contador == 0){

            if (esAgradecimiento){

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

                ParseQuery<ParseObject> query = ParseQuery.getQuery("EncuestaPendiente");
                query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("comercioId", comercioId);
                query.whereEqualTo("activo", true);
                query.setLimit(1);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        for (ParseObject object : objects){

                            object.put("activo", false);
                            object.put("encuestaAplicada", true);
                            object.put("fechaModificacion", fecha);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null){

                                        terminarSppiner();

                                        startActivity(new Intent(getApplicationContext(), ClienteActivity.class));

                                        return;

                                    } else {

                                        terminarSppiner();

                                        Toast.makeText(InfoInicialActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                        return;

                                    }
                                }
                            });
                        }
                    }
                });


            }

            if (mensajeAyuda){

                if (esAnonimo){

                    startActivity(new Intent(getApplicationContext(), inicio_Pando_Activity.class));

                } else {

                    startActivity(new Intent(getApplicationContext(), ClienteActivity.class));

                }

                return;
            }

            logoImageView.setImageResource(R.drawable.loyalty);
            tituloTextView.setText("¿Cómo acumular puntos?");
            descipcionTextView.setText("Selecciona un comercio y presiona “Estoy aquí” y listo. El staff te hará llegar tus puntos.\n\nPara utilizar tus puntos selecciona un comercio y presiona la opción “Ver tienda”.\n\n\n\nLo hacemos con mucho cariño y amor\n❤️\nEl equipo Pando");
            siguienteTextView.setText("Listo");

            contador = 1;
            return;
        }

        if (contador == 1){

            Intent intent = new Intent(getApplicationContext(), ClienteActivity.class);
            startActivity(intent);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_inicial);

        getSupportActionBar().hide();

        contador = 0;

        logoImageView = (ImageView) findViewById(R.id.logoBienImageView);
        tituloTextView = (TextView) findViewById(R.id.tituloBienTextView);
        descipcionTextView = (TextView) findViewById(R.id.descBienTextView);
        siguienteTextView = (TextView) findViewById(R.id.siguienteBienTextView);

        Intent intent = getIntent();
        mensajeAyuda = intent.getBooleanExtra("mensajeAyuda", false);
        esAnonimo = intent.getBooleanExtra("esAnonimo", false);
        esAgradecimiento = intent.getBooleanExtra("esAgradecimiento", false);
        comercioId = intent.getStringExtra("comercioId");
        nombreCliente = intent.getStringExtra("nombreCliente");
        encuestaActivaId = intent.getStringExtra("encuestaActivaId");

        if (esAgradecimiento){

            ParseQuery<ParseObject> query = ParseQuery.getQuery("MensajeAgradecimiento");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("encuestaId", encuestaActivaId);
            query.whereEqualTo("mensajeEliminado", false);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    for (ParseObject object : objects){

                        descipcionTextView.setText(object.getString("mensaje"));

                    }
                }
            });

            logoImageView.setImageResource(R.drawable.chef);
            tituloTextView.setText("Muchas gracias " + nombreCliente);
            siguienteTextView.setText("Listo");

        } else if (mensajeAyuda){

            logoImageView.setImageResource(R.drawable.success);
            tituloTextView.setText("Mensaje enviado con éxito");
            descipcionTextView.setText("Gracias a tus comentarios podremos mejorar para brindarte una mejor experiencia.\n\nEn menos de 48 hrs nos estaremos comunicando contigo por correo para dar seguimiento a tu mensaje.\n\nNo olvides revisar la bandeja de spam.\n\n\n\nCon pasión y amor\n❤️\nEl equipo Pando");
            siguienteTextView.setText("Listo");

        } else {

            logoImageView.setImageResource(R.drawable.welcome);
            tituloTextView.setText("Bienvenido a Pando");
            descipcionTextView.setText("Pando es un programa de lealtad creado para recompensar la preferencia de los clientes frecuentes.\n\nCon Pando podrás acumular puntos por cada compra que realices.");

        }
    }

    @Override
    public void onBackPressed() {

        if (contador == 0){

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (contador == 1){

            logoImageView.setImageResource(R.drawable.welcome);
            tituloTextView.setText("Bienvenido a Pando");
            descipcionTextView.setText("Pando es un programa de lealtad creado para recompensar la preferencia de los clientes frecuentes.\n\nCon Pando podrás acumular puntos por cada compra que realices.");
            siguienteTextView.setText("Siguiente");

            contador = 0;

        }
    }
}
