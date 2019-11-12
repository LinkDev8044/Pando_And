package com.parse.starter.registro_Usuario;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.starter.R;
import com.parse.starter.RegisrtroAdministrador.NombreComercioActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SexoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String nombreUsuario;
    String apellidoUsuario;
    String correoUsuario;
    String comercioId;
    String nombreComercio;

    ImageView manImageView;
    ImageView womanImageView;

    TextView mensajeTextView;
    TextView siguienteTextView;

    Date fechaNacimiento;

    Boolean fechaSelect;
    Boolean isMan;
    Boolean isWoman;
    Boolean esRegistroAdmin;
    Boolean esRegistroColaborador;

    ProgressDialog progressDialog;

    public void siguiente(View view){

        if (esRegistroColaborador){

            Intent intent = new Intent(getApplicationContext(), ContrasenaActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario);
            intent.putExtra("apellidoUsuario", apellidoUsuario);
            intent.putExtra("isMan", isMan);
            intent.putExtra("fechaNacimiento", fechaNacimiento.getTime());
            intent.putExtra("correoUsuario", correoUsuario);
            intent.putExtra("esRegistroAdmin", esRegistroAdmin);
            intent.putExtra("esRegistroColaborador", esRegistroColaborador);
            intent.putExtra("comercioId", comercioId);
            intent.putExtra("nombreComercio", nombreComercio);

            startActivity(intent);


        } else if (esRegistroAdmin){

            Intent intent = new Intent(getApplicationContext(), NombreComercioActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario);
            intent.putExtra("apellidoUsuario", apellidoUsuario);
            intent.putExtra("isMan", isMan);
            intent.putExtra("fechaNacimiento", fechaNacimiento.getTime());
            intent.putExtra("correoUsuario", correoUsuario);
            intent.putExtra("esRegistroAdmin", esRegistroAdmin);
            startActivity(intent);

        } else {

           Intent intent = new Intent(getApplicationContext(), CorreoUserActivity.class);
           intent.putExtra("nombreUsuario", nombreUsuario);
           intent.putExtra("apellidoUsuario", apellidoUsuario);
           intent.putExtra("isMan", isMan);
           intent.putExtra("fechaNacimiento", fechaNacimiento.getTime());
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

    public void back(View view){

        finish();

    }

    public void checkFecha(){

        if (fechaSelect){

            siguienteTextView.setVisibility(View.VISIBLE);

        }
    }

    private void setDate(Calendar calendar){

        fechaSelect = true;

        mensajeTextView.setVisibility(View.VISIBLE);

        mensajeTextView.setText(DateFormat.getDateInstance(2).format(calendar.getTime()));
        String i = String.valueOf(calendar.get(1));
        String j = String.valueOf(calendar.get(2) + 1);
        String k = String.valueOf(calendar.get(5));

        try {

            fechaNacimiento = new SimpleDateFormat("dd/MM/yyy").parse(k + "/" + j + "/" + i);

        } catch (ParseException e){

            e.printStackTrace();

        }

        if ((isMan && !isWoman || !isMan && isWoman)){

            siguienteTextView.setVisibility(View.VISIBLE);

        }

        //calendar = new SimpleDateFormat("dd/MM/yyyy");

    }

public void manClicked(View view){

        isMan = true;
        isWoman = false;

        manImageView.setImageResource(R.drawable.manclicked);
        womanImageView.setImageResource(R.drawable.woman);

        checkFecha();

}

public void womanClicked(View view){

    isMan = false;
    isWoman = true;

    manImageView.setImageResource(R.drawable.man);
    womanImageView.setImageResource(R.drawable.womanclicked);

    checkFecha();

}

public void datePicker(View view){

        new DatePickerFragment().show(getFragmentManager(), "date");

}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sexo);

        getSupportActionBar().hide();

        manImageView = (ImageView) findViewById(R.id.manImageView);
        womanImageView = (ImageView) findViewById(R.id.womanImageView);
        mensajeTextView = (TextView) findViewById(R.id.fechaTextView);
        siguienteTextView = (TextView) findViewById(R.id.sigSexoTexView);

        Intent intent = getIntent();
        nombreUsuario = intent.getStringExtra("nombreUsuario");
        apellidoUsuario = intent.getStringExtra("apellidoUsuario");
        esRegistroAdmin = intent.getBooleanExtra("esRegistroAdmin", false);
        esRegistroColaborador = intent.getBooleanExtra("esRegistroColaborador", false);

        if (esRegistroAdmin || esRegistroColaborador){

            correoUsuario = intent.getStringExtra("correoUsuario");
            nombreComercio = intent.getStringExtra("nombreComercio");
            comercioId = intent.getStringExtra("comercioId");

        }

        fechaSelect = false;
        isMan = false;
        isWoman = false;

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        setDate(new GregorianCalendar(i, i1, i2));

    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar.getInstance();
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity() , 2000, 7, 1);

        }
    }
}
