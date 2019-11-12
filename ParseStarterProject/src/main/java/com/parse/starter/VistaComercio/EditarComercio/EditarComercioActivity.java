package com.parse.starter.VistaComercio.EditarComercio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.parse.starter.VistaComercio.Administrador.AdministradorActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class EditarComercioActivity extends AppCompatActivity implements TextWatcher {

    String comercioId;
    String descripcionCom;
    String mensaje;
    String nombreAdminCompleto;
    String nombreComercio;

    int contador;

    Boolean tieneDescripcion;
    Boolean tieneLogo;
    Boolean esNuevaDesc;
    Boolean esNuevaImagen;

    Bitmap logoComercio;
    Bitmap bitmap;

    TextView op1TextView;
    TextView siguienteTextView;

    EditText op1EditText;

    ImageView op1ImageView;

    java.util.Date fecha;

    ProgressDialog progressDialog;

    private void mostrarMensaje(){

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();

        op1ImageView.setEnabled(false);
        op1TextView.setEnabled(false);
        op1EditText.setEnabled(false);
        siguienteTextView.setText("LISTO");

        contador = 1;

    }

    private void guardarNuevoLogo(){

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

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] image = bos.toByteArray();
        final ParseFile file = new ParseFile("logoComercio.png", image);

        if (tieneLogo){

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ImagenComercio");
            query.whereEqualTo("comercioId", comercioId);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            object.put("imagenPerfil", file);
                            object.put("fechaModificacion", fecha);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null){

                                        terminarSppiner();

                                        mostrarMensaje();

                                    } else {

                                        terminarSppiner();

                                        Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else {

            ParseObject object = new ParseObject("ImagenComercio");
            object.put("nombreColaborador", nombreAdminCompleto);
            object.put("nombreComercio", nombreComercio);
            object.put("comercioId", comercioId);
            object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
            object.put("fechaCreacion", fecha);
            object.put("fechaModificacion", fecha);
            object.put("imagenPerfil", file);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null){

                        terminarSppiner();

                        mostrarMensaje();

                    } else {

                        terminarSppiner();

                        Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }

    private void guardarDescripcionNueva(){

        ParseObject object = new ParseObject("DescripcionComercio");
        object.put("nombreColaborador", nombreAdminCompleto);
        object.put("nombreComercio", nombreComercio);
        object.put("comercioId", comercioId);
        object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
        object.put("fechaCreacion", fecha);
        object.put("fechaModificacion", fecha);
        object.put("descripcion", op1EditText.getText().toString());
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){

                    terminarSppiner();

                    mostrarMensaje();

                } else {

                    terminarSppiner();

                    Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void modificarDescripcion(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DescripcionComercio");
        query.whereEqualTo("comercioId", comercioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                        object.put("descripcion", op1EditText.getText().toString());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {

                                    terminarSppiner();

                                    mostrarMensaje();

                                } else {

                                    terminarSppiner();

                                    Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }

                } else {

                    terminarSppiner();

                    Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void guardarAmbos(){

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

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] image = bos.toByteArray();
        final ParseFile file = new ParseFile("logoComercio.png", image);

        if (tieneLogo){

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ImagenComercio");
            query.whereEqualTo("comercioId", comercioId);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        for (ParseObject object : objects){

                            object.put("imagenPerfil", file);
                            object.put("fechaModificacion", fecha);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null){

                                        if (tieneDescripcion){

                                            modificarDescripcion();

                                        } else {

                                            guardarDescripcionNueva();

                                        }

                                    } else {

                                        terminarSppiner();

                                        Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        } else {

            ParseObject object = new ParseObject("ImagenComercio");
            object.put("nombreColaborador", nombreAdminCompleto);
            object.put("nombreComercio", nombreComercio);
            object.put("comercioId", comercioId);
            object.put("colaboradorId", ParseUser.getCurrentUser().getObjectId());
            object.put("fechaCreacion", fecha);
            object.put("fechaModificacion", fecha);
            object.put("imagenPerfil", file);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null){

                        if (tieneDescripcion){

                            modificarDescripcion();

                        } else {

                            guardarDescripcionNueva();

                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }

    public void guardarCambios(View view){

        if (contador == 0){

            iniciarSppiner();

            if (esNuevaImagen && esNuevaDesc){

                mensaje = "Descripción e imagen guardados con ÉXITO";

                guardarAmbos();

            } else if (esNuevaImagen && esNuevaDesc == false){

                mensaje = "Imagen guardada con ÉXITO";

                guardarNuevoLogo();

            } else if (esNuevaImagen == false && esNuevaDesc){

                mensaje = "Descripción guardada con ÉXITO";

                if (tieneDescripcion){

                    modificarDescripcion();

                } else {

                    guardarDescripcionNueva();

                }
            }

        } else {

            finish();

        }
    }

    private void guardarEnabled(){

        siguienteTextView.setVisibility(View.VISIBLE);
        siguienteTextView.setBackgroundColor(getResources().getColor(R.color.verde_Pando));
        siguienteTextView.setEnabled(true);

        if (esNuevaDesc && esNuevaImagen){

            siguienteTextView.setText("Guardar imagen y descripción");

        } else if (esNuevaDesc && esNuevaImagen == false){

            siguienteTextView.setText("Guardar descripción");

        } else if (esNuevaDesc == false && esNuevaImagen){

            siguienteTextView.setText("Guardar imagen");

        }
    }

    private void  guardarDisabled(){

        siguienteTextView.setVisibility(View.INVISIBLE);

    }

    public void buscarImagen(View view){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 1);

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

    public void back(View view){

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_comercio);

        getSupportActionBar().hide();
        getWindow().setSoftInputMode(18);

        esNuevaImagen = false;
        esNuevaDesc = false;
        contador = 0;

        op1TextView = (TextView) findViewById(R.id.op1EditarTextView);
        siguienteTextView = (TextView) findViewById(R.id.op2EditarTextView);
        op1EditText = (EditText) findViewById(R.id.op1EditarComEditText);
        op1ImageView = (ImageView) findViewById(R.id.op1EditarComImageView);

        op1TextView.setPaintFlags(op1TextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        op1EditText.addTextChangedListener(this);

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");
        nombreAdminCompleto = intent.getStringExtra("nombreAdminCompleto");
        nombreComercio = intent.getStringExtra("nombreComercio");

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DescripcionComercio");
        query.whereEqualTo("comercioId", comercioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0) {

                        tieneDescripcion = true;

                        for (ParseObject object : objects) {

                            descripcionCom = object.getString("descripcion");

                        }

                        op1EditText.setText(descripcionCom);

                    } else {

                        tieneDescripcion = false;

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

                                                    op1ImageView.setImageBitmap(logoComercio);

                                                    terminarSppiner();


                                                } else {

                                                    terminarSppiner();

                                                    Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    }

                                } else {

                                    tieneLogo = false;

                                    op1ImageView.setImageResource(R.drawable.store);

                                    terminarSppiner();

                                }

                            } else {

                                terminarSppiner();

                                Toast.makeText(EditarComercioActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    terminarSppiner();

                    Toast.makeText(EditarComercioActivity.this, "Tuvimos un problema - Intentalo de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (op1EditText.getText().toString().matches("")){

            esNuevaDesc = false;

            if (esNuevaImagen){

                guardarEnabled();

            } else {

                guardarDisabled();

            }

        } else {

            if (op1EditText.getText().toString().matches(descripcionCom)){

                esNuevaDesc = false;

                if (esNuevaImagen){

                    guardarEnabled();

                } else {

                    guardarDisabled();

                }

            } else {

                esNuevaDesc = true;

                guardarEnabled();

            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK && null != data){

            if (bitmap != null){

                bitmap.recycle();

            }

            try {
                InputStream stream = getContentResolver().openInputStream(data.getData());

                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                op1ImageView.setImageBitmap(bitmap);

                esNuevaImagen = true;

                guardarEnabled();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {


        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
