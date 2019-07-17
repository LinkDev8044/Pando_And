package com.parse.starter.registro_Usuario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.starter.Inicio.inicio_Pando_Activity;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.ClienteActivity;

public class InfoInicialActivity extends AppCompatActivity {

    int contador;

    ImageView logoImageView;

    TextView tituloTextView;
    TextView descipcionTextView;
    TextView siguienteTextView;

    Boolean mensajeAyuda;
    Boolean esAnonimo;

    public void siguiente(View view){

        if (contador == 0){

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

        if (mensajeAyuda){

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
