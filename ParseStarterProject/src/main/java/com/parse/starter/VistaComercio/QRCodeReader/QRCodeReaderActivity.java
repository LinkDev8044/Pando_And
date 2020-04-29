package com.parse.starter.VistaComercio.QRCodeReader;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaComercio.EnviarEncuesta.EnviarEncuestaActivity;
import com.parse.starter.registro_Usuario.CorreoUserActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeReaderActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    String usuario;
    String usuarioId;
    String correoCliente;
    String comercioId;
    String nombreComercio;
    String encuestaActiva;
    String encuestaActivaId;
    String nombreCompletoAdmin;
    String recompensaActiva;

    int numeroDePreguntas;

    Date fecha;

    int MY_PERMISSIONS_REQUEST_CAMERA=0;

    private ZXingScannerView mScannerView;

    ProgressDialog progressDialog;

    private void goToEnviarEncuesta(){

        terminarSppiner();

        Intent intent = new Intent(getApplicationContext(), EnviarEncuestaActivity.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("usuarioId", usuarioId);
        intent.putExtra("comercioId", comercioId);
        intent.putExtra("nombreComercio", nombreComercio);
        intent.putExtra("encuestaActiva", encuestaActiva);
        intent.putExtra("encuestaActivaId", encuestaActivaId);
        intent.putExtra("numeroDePreguntas", numeroDePreguntas);
        intent.putExtra("nombreCompletoAdmin", nombreCompletoAdmin);
        intent.putExtra("correoCliente", correoCliente);
        intent.putExtra("recompensaActiva", recompensaActiva);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");
        nombreComercio = intent.getStringExtra("nombreComercio");
        encuestaActiva = intent.getStringExtra("encuestaActiva");
        encuestaActivaId = intent.getStringExtra("encuestaActivaId");
        numeroDePreguntas = intent.getIntExtra("numeroDePreguntas", 0);
        nombreCompletoAdmin = intent.getStringExtra("nombreCompletoAdmin");
        recompensaActiva = intent.getStringExtra("recompensaActiva");

        mScannerView = new ZXingScannerView(this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, Manifest.permission.CAMERA)) {

                //Show permission dialog
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity)this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        setContentView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void handleResult(Result rawResult) {

        Log.i("Prueba", rawResult.getText());

        mScannerView.resumeCameraPreview(this);

        usuarioId = rawResult.getText();

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

        mScannerView.stopCamera();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", usuarioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        for (ParseObject object : objects){

                            usuario = object.getString("nombre") + " " + object.getString("apellido");

                        }

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("CodigoQRCliente");
                        query.whereEqualTo("usuarioId", usuarioId);
                        query.setLimit(1);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {

                                if (e == null){

                                    for (ParseObject object : objects){

                                        correoCliente = object.getString("correoCliente");

                                    }

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UsuarioActivo");
                                    query.whereEqualTo("comercioId", comercioId);
                                    query.whereEqualTo("usuarioId", usuarioId);
                                    query.whereEqualTo("activo", true);
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {

                                            if (e == null){

                                                if (objects.size() > 0){

                                                    for (ParseObject object : objects){

                                                        long diff = fecha.getTime() - object.getDate("fechaCreacion").getTime();
                                                        long diffInHours = TimeUnit.MILLISECONDS.toMinutes(diff);

                                                        if (diffInHours >= 40){

                                                            object.put("fechaCreacion", fecha);
                                                            object.saveInBackground();

                                                        }

                                                        goToEnviarEncuesta();

                                                    }

                                                } else {

                                                    ParseObject object = new ParseObject("UsuarioActivo");
                                                    object.put("nombreComercio", nombreComercio);
                                                    object.put("comercioId", comercioId);
                                                    object.put("activo", true);
                                                    object.put("encuestaAplicada", false);
                                                    object.put("usuarioId", usuarioId);
                                                    object.put("fechaCreacion", fecha);
                                                    object.put("fechaEncuestaTerminada", fecha);
                                                    object.put("nombreUsuario", usuario);
                                                    object.put("email", correoCliente);
                                                    object.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {

                                                            if (e == null){

                                                                goToEnviarEncuesta();

                                                            } else {

                                                                terminarSppiner();

                                                                Toast.makeText(QRCodeReaderActivity.this, "Tuvimos un problema, intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });

                                                }

                                            } else {

                                                terminarSppiner();

                                                Toast.makeText(QRCodeReaderActivity.this, "Tuvimos un problema, intenta de nuevo", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                } else {

                                    terminarSppiner();

                                    Toast.makeText(QRCodeReaderActivity.this, "Tuvimos un problema, intenta de nuevo", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    } else {

                        terminarSppiner();

                        AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeReaderActivity.this);
                        builder.setTitle("ERROR");
                        builder.setMessage("No se encuentra registrado");
                        builder.setNeutralButton("Intentar de nuevo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mScannerView.startCamera();

                            }
                        });

                        AlertDialog alert= builder.create();
                        alert.show();

                    }

                } else {

                    terminarSppiner();

                    Toast.makeText(QRCodeReaderActivity.this, "Tuvimos un problema, intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
