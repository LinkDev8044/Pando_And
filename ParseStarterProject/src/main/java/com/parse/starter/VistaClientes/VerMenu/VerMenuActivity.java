package com.parse.starter.VistaClientes.VerMenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;

import java.util.ArrayList;
import java.util.List;

public class VerMenuActivity extends AppCompatActivity {

    String comercioId;

    ArrayList<ParseFile> parseFileArray = new ArrayList();

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
        setContentView(R.layout.activity_ver_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Menú");

        final LinearLayout gallery = findViewById(R.id.galleryMenu);
        final LayoutInflater inflater = LayoutInflater.from(this);

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ImagenMenu");
        query.whereEqualTo("comercioId", comercioId);
        query.orderByAscending("contador");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        final int contadorImagenes = objects.size();

                        for (ParseObject object : objects){

                            ParseFile parseFile1 = (ParseFile) object.get("imagenMenu");
                            parseFileArray.add(parseFile1);

                            if (parseFileArray.size() == contadorImagenes){

                                for (int x = 0; x < parseFileArray.size(); x++){

                                    View view1 = inflater.inflate(R.layout.ver_menu_item, null);

                                    final ImageView imagenPortada = view1.findViewById(R.id.op1VerMenuImageView);

                                    parseFileArray.get(x).getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {

                                            if (e == null){

                                                imagenPortada.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));

                                            }
                                        }
                                    });

                                    gallery.addView(view1);

                                }

                                terminarSppiner();

                            }
                        }

                    } else {

                        terminarSppiner();

                    }

                } else {

                    terminarSppiner();

                    Toast.makeText(VerMenuActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
