package com.parse.starter.VistaComercio.Administrador;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.starter.R;
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetalleReporteActivity extends AppCompatActivity {

    String titulo;

    ArrayList<String> NOMBREClienteArray = new ArrayList<>();
    ArrayList<String> NOMBREIngresoArray = new ArrayList<>();

    ArrayList<Integer> VISITASClienteArray = new ArrayList<>();
    ArrayList<Double> CANTIDADIngresoArray = new ArrayList<>();

    Map<String,Boolean> clientesRepDic =  new HashMap<String,Boolean>();

    Boolean reporteVisitas;

    CustomAdapter customAdapter;

    ListView detalleRepListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reporte);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detalleRepListView = (ListView) findViewById(R.id.detalleRepListView);

        Intent intent = getIntent();
        reporteVisitas = intent.getBooleanExtra("reporteVisitas", false);
        titulo = intent.getStringExtra("titulo");
        clientesRepDic = (HashMap<String, Boolean>)intent.getSerializableExtra("clientesRepDic");

        if (reporteVisitas){

            NOMBREClienteArray = intent.getStringArrayListExtra("NOMBREClienteArray");
            VISITASClienteArray = intent.getIntegerArrayListExtra("VISITASClienteArray");

        } else {

            NOMBREIngresoArray = intent.getStringArrayListExtra("NOMBREIngresoArray");
            CANTIDADIngresoArray = (ArrayList<Double>) getIntent().getSerializableExtra("CANTIDADIngresoArray");

        }

        setTitle(titulo);

        customAdapter = new CustomAdapter();

        detalleRepListView.setAdapter(customAdapter);

    }

    class CustomAdapter extends BaseAdapter implements Adapter{

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getCount() {

            if (reporteVisitas){

                return NOMBREClienteArray.size();

            } else {

                return NOMBREIngresoArray.size();

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

            if (convertView == null){

                convertView = mInflater.inflate(R.layout.reporte_general_cell_2, null);

                TextView op1TextView = (TextView) convertView.findViewById(R.id.op1RepCel2TextView);
                TextView op2TextView = (TextView) convertView.findViewById(R.id.op2RepCel2TextView);
                TextView op3TextView = (TextView) convertView.findViewById(R.id.op3RepCel2TextView);
                TextView op4TextView = (TextView) convertView.findViewById(R.id.op4RepCel2TextView);
                TextView op5TextView = (TextView) convertView.findViewById(R.id.op5RepCel2TextView);
                TextView op6TextView = (TextView) convertView.findViewById(R.id.op6RepCel2TextView);

                op1TextView.setTextColor(Color.BLACK);
                op2TextView.setTextColor((getResources().getColor(R.color.verde_Pando)));
                op2TextView.setTypeface(null, Typeface.BOLD);
                op4TextView.setTextColor(Color.BLACK);
                op6TextView.setTextColor(Color.BLACK);

                op2TextView.setText("# " + String.valueOf(position + 1));
                op5TextView.setText("Cliente nuevo");
                op6TextView.setText("No");

                if (reporteVisitas){

                    op1TextView.setText(NOMBREClienteArray.get(position));

                    op3TextView.setText("# Visitas");
                    op4TextView.setText(String.valueOf(VISITASClienteArray.get(position)));


                    if (!clientesRepDic.get(NOMBREClienteArray.get(position))){

                        op6TextView.setText("Si");
                        op6TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    }

                } else {

                    op1TextView.setText(NOMBREIngresoArray.get(position));
                    op3TextView.setText("Consumo");
                    op4TextView.setText(String.valueOf(CANTIDADIngresoArray.get(position)));

                    if (!clientesRepDic.get(NOMBREIngresoArray.get(position))){

                        op6TextView.setText("Si");
                        op6TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    }
                }

                return convertView;

            } else {

                convertView = mInflater.inflate(R.layout.reporte_general_cell_2, null);

                TextView op1TextView = (TextView) convertView.findViewById(R.id.op1RepCel2TextView);
                TextView op2TextView = (TextView) convertView.findViewById(R.id.op2RepCel2TextView);
                TextView op3TextView = (TextView) convertView.findViewById(R.id.op3RepCel2TextView);
                TextView op4TextView = (TextView) convertView.findViewById(R.id.op4RepCel2TextView);
                TextView op5TextView = (TextView) convertView.findViewById(R.id.op5RepCel2TextView);
                TextView op6TextView = (TextView) convertView.findViewById(R.id.op6RepCel2TextView);

                op1TextView.setTextColor(Color.BLACK);
                op2TextView.setTextColor((getResources().getColor(R.color.verde_Pando)));
                op2TextView.setTypeface(null, Typeface.BOLD);
                op4TextView.setTextColor(Color.BLACK);
                op6TextView.setTextColor(Color.BLACK);

                op2TextView.setText("# " + String.valueOf(position + 1));
                op5TextView.setText("Cliente nuevo");
                op6TextView.setText("No");

                if (reporteVisitas){

                    op1TextView.setText(NOMBREClienteArray.get(position));

                    op3TextView.setText("# Visitas");
                    op4TextView.setText(String.valueOf(VISITASClienteArray.get(position)));


                    if (!clientesRepDic.get(NOMBREClienteArray.get(position))){

                        op6TextView.setText("Si");
                        op6TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    }

                } else {

                    op1TextView.setText(NOMBREIngresoArray.get(position));
                    op3TextView.setText("Consumo");
                    op4TextView.setText(String.valueOf(CANTIDADIngresoArray.get(position)));

                    if (!clientesRepDic.get(NOMBREIngresoArray.get(position))){

                        op6TextView.setText("Si");
                        op6TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    }
                }

                return convertView;

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
