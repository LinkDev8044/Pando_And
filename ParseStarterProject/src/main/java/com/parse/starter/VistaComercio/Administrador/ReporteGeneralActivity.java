package com.parse.starter.VistaComercio.Administrador;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.LocaleData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReporteGeneralActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String comercioId;
    String nombreReferencia;
    String titulo;

    Integer clientesTotal;
    Integer clientesRepetidos;
    Integer clientesNuevos;

    Double ingresoTotal;
    Double ingresoViejo;
    Double ingresoNuevo;

    Boolean busquedaPorMes;

    Calendar calRef;
    Calendar fechaInicioMes;
    Calendar fechaFinalMes;

    Date daysAgo;

    ArrayList<String> CLIENTESArray = new ArrayList<>();
    ArrayList<String> NOMBREClienteArray = new ArrayList<>();
    ArrayList<String> NOMBREIngresoArray = new ArrayList<>();
    ArrayList<String> NOMBRESViejos = new ArrayList<>();

    ArrayList<Integer> VISITASClienteArray = new ArrayList<>();
    ArrayList<Double> CANTIDADIngresoArray = new ArrayList<>();

    Map<String,Double> ingresoDic =  new HashMap<String,Double>();

    Map<String,Integer> counts =  new HashMap<String,Integer>();
    Map<String,Integer> sortedKeys =  new HashMap<String,Integer>();

    Map<String,Double> sortedKeys2 =  new HashMap<String,Double>();

    Map<String,Boolean> clientesRepDic =  new HashMap<String,Boolean>();

    ArrayList<PieEntry> entries = new ArrayList<>();

    ArrayList<Integer> colors = new ArrayList<>();

    private DecimalFormat mFormat;

    Spinner periodoSpinner;

    CustomAdapter customAdapter;

    List<String> list = new ArrayList<String>();

    ListView reporteListView;

    ProgressDialog progressDialog;

    private void iniciarSppiner() {

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("Cargando...");
        this.progressDialog.show();
        getWindow().setFlags(16, 16);

    }

    private void terminarSppiner() {

        getWindow().clearFlags(16);
        this.progressDialog.dismiss();

    }

    private void reloadData(){

        iniciarSppiner();

        clientesTotal = 0;
        ingresoTotal = 0.0;
        clientesRepetidos = 0;
        clientesNuevos = 0;
        ingresoViejo = 0.0;
        ingresoNuevo = 0.0;
        CLIENTESArray.clear();
        ingresoDic.clear();
        counts.clear();
        NOMBREClienteArray.clear();
        VISITASClienteArray.clear();
        NOMBREIngresoArray.clear();
        CANTIDADIngresoArray.clear();
        NOMBRESViejos.clear();
        clientesRepDic.clear();
        nombreReferencia = "";

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosEnviados");
        query.whereEqualTo("comercioId", comercioId);

        if (busquedaPorMes){

            query.whereLessThanOrEqualTo("fechaCreacion", fechaFinalMes.getTime());
            query.whereGreaterThanOrEqualTo("fechaCreacion", fechaInicioMes.getTime());

        } else {

            query.whereGreaterThanOrEqualTo("fechaCreacion", daysAgo);

        }

        query.orderByAscending("usuarioId");
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException error) {

                if (error == null){

                    for (ParseObject object : objects){

                        String nombreEnServidor = object.getString("nombreUsuario");
                        Double valorEnServidor = Double.valueOf(object.getString("consumo"));
                        clientesTotal = clientesTotal + 1;
                        ingresoTotal = ingresoTotal + valorEnServidor;
                        CLIENTESArray.add(object.getString("nombreUsuario"));

                        if (nombreEnServidor.matches(nombreReferencia)){

                            Double valorReferencia = ingresoDic.get(nombreReferencia);
                            ingresoDic.put(nombreReferencia, valorReferencia + valorEnServidor);

                        } else {

                            nombreReferencia = nombreEnServidor;
                            ingresoDic.put(nombreReferencia, valorEnServidor);

                        }
                    }

                    Map<String, Integer> hm = new HashMap<String, Integer>();
                    for (String i : CLIENTESArray) {
                        Integer j = hm.get(i);
                        hm.put(i, (j == null) ? 1 : j + 1);
                    }

                    for (Map.Entry<String, Integer> val : hm.entrySet()) {
                        counts.put(val.getKey(), val.getValue());
                    }

                    sortedKeys = sortByValue(counts);

                    for (Map.Entry<String, Integer> e : sortedKeys.entrySet()) {
                        //to get key
                        NOMBREClienteArray.add(e.getKey());
                        //and to get value
                        VISITASClienteArray.add(e.getValue());
                        e.getValue();
                    }

                    sortedKeys2 = sortByValue2(ingresoDic);

                    for (Map.Entry<String, Double> e : sortedKeys2.entrySet()) {
                        //to get key
                        NOMBREIngresoArray.add(e.getKey());
                        //and to get value
                        CANTIDADIngresoArray.add(e.getValue());
                        e.getValue();
                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
                    query.whereEqualTo("comercioId", comercioId);

                    if (busquedaPorMes){

                        query.whereLessThan("fechaCreacion", fechaInicioMes.getTime());

                    } else {

                        query.whereLessThan("fechaCreacion", daysAgo);

                    }

                    query.setLimit(1000);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null){

                                for (ParseObject object : objects){

                                    NOMBRESViejos.add(object.getString("nombreUsuario"));

                                }

                                for (String data : CLIENTESArray){

                                    if (NOMBRESViejos.contains(data)){

                                        clientesRepDic.put(data, true);
                                        clientesRepetidos = clientesRepetidos + 1;

                                    } else {

                                        clientesRepDic.put(data, false);
                                        clientesNuevos = clientesNuevos + 1;

                                    }
                                }

                                for (String data : NOMBREIngresoArray){

                                    if (NOMBRESViejos.contains(data)){

                                        ingresoViejo = ingresoViejo + ingresoDic.get(data);

                                    } else {

                                        ingresoNuevo = ingresoNuevo + ingresoDic.get(data);

                                    }
                                }

                                Log.i("Prueba", String.valueOf(clientesRepDic));

                                reporteListView.setAdapter(customAdapter);

                                terminarSppiner();

                            } else {

                                Log.i("Prueba", e.getMessage().toString());

                                Toast.makeText(ReporteGeneralActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    terminarSppiner();

                    Log.i("Prueba", error.getMessage().toString());

                    Toast.makeText(ReporteGeneralActivity.this, "Parece que tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public static HashMap<String, Double> sortByValue2(Map<String, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static HashMap<String, Integer> sortByValue(Map<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_general);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        periodoSpinner = (Spinner) findViewById(R.id.periodoSpinner);
        reporteListView = (ListView) findViewById(R.id.reporteListView);

        Intent intent = getIntent();
        comercioId = intent.getStringExtra("comercioId");

        busquedaPorMes = false;

        customAdapter = new CustomAdapter();

        calRef = Calendar.getInstance();
        calRef.add(Calendar.DATE, -15);
        daysAgo = calRef.getTime();
        Log.i("Prueba", String.valueOf(daysAgo));

        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.MONTH, 0);
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.MONTH, -1);
        Calendar cal3 = Calendar.getInstance();
        cal3.add(Calendar.MONTH, -2);
        Calendar cal4 = Calendar.getInstance();
        cal4.add(Calendar.MONTH, -3);
        Calendar cal5 = Calendar.getInstance();
        cal5.add(Calendar.MONTH, -4);
        Calendar cal6 = Calendar.getInstance();
        cal6.add(Calendar.MONTH, -5);

        list.add("Últimos 15 días");
        list.add("Últimos 30 días");
        list.add("Últimos 45 días");
        list.add("Últimos 60 días");
        list.add(format.format(cal1.getTime()));
        list.add(format.format(cal2.getTime()));
        list.add(format.format(cal3.getTime()));
        list.add(format.format(cal4.getTime()));
        list.add(format.format(cal5.getTime()));
        list.add(format.format(cal6.getTime()));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodoSpinner.setAdapter(adapter);
        periodoSpinner.setOnItemSelectedListener(this);

        reporteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;

                Log.i("Prueba", String.valueOf(position));

                if (position == 7){

                    intent = new Intent(getApplicationContext(), DetalleReporteActivity.class);
                    intent.putExtra("reporteVisitas", true);
                    intent.putExtra("titulo", titulo);
                    intent.putExtra("NOMBREClienteArray", NOMBREClienteArray);
                    intent.putExtra("VISITASClienteArray", VISITASClienteArray);
                    intent.putExtra("NOMBREIngresoArray", NOMBREIngresoArray);
                    intent.putExtra("CANTIDADIngresoArray", CANTIDADIngresoArray);
                    intent.putExtra("clientesRepDic", (Serializable)clientesRepDic);
                    startActivity(intent);

                }

                if (position == 15){

                    intent = new Intent(getApplicationContext(), DetalleReporteActivity.class);
                    intent.putExtra("reporteVisitas", false);
                    intent.putExtra("titulo", titulo);
                    intent.putExtra("NOMBREClienteArray", NOMBREClienteArray);
                    intent.putExtra("VISITASClienteArray", VISITASClienteArray);
                    intent.putExtra("NOMBREIngresoArray", NOMBREIngresoArray);
                    intent.putExtra("CANTIDADIngresoArray", CANTIDADIngresoArray);
                    intent.putExtra("clientesRepDic", (Serializable)clientesRepDic);
                    startActivity(intent);

                }
            }
        });

        //reloadData(); Se inicia en onItemSelected()

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        titulo = periodoSpinner.getItemAtPosition(position).toString();

        if (position <= 3){

            busquedaPorMes = false;

            calRef = Calendar.getInstance();
            calRef.add(Calendar.DATE, -(15 * (position + 1)));
            daysAgo = calRef.getTime();
            Log.i("Prueba", String.valueOf(daysAgo));

        } else {

            fechaInicioMes = Calendar.getInstance();
            fechaInicioMes.add(Calendar.MONTH, -(position - 4));

            //INICIO
            fechaInicioMes.set(Calendar.DAY_OF_MONTH, 1);
            fechaInicioMes.set(Calendar.HOUR_OF_DAY, 0);
            fechaInicioMes.set(Calendar.MINUTE, 0);
            fechaInicioMes.set(Calendar.SECOND, 0);
            fechaInicioMes.set(Calendar.MILLISECOND, 0);
            Log.i("Prueba", String.valueOf(fechaInicioMes.getTime()));

            //FIN
            fechaFinalMes = Calendar.getInstance();
            fechaFinalMes.add(Calendar.MONTH, -(position - 4));
            fechaFinalMes.set(Calendar.DATE, fechaFinalMes.getActualMaximum(Calendar.DATE));
            fechaFinalMes.set(Calendar.HOUR_OF_DAY, 23);
            fechaFinalMes.set(Calendar.MINUTE, 59);
            fechaFinalMes.set(Calendar.SECOND, 59);
            Log.i("Prueba", String.valueOf(fechaFinalMes.getTime()));

            busquedaPorMes = true;

        }

        reloadData();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class CustomAdapter extends BaseAdapter implements Adapter{

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());

        @Override
        public int getViewTypeCount() {
            return 7;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0){

                return 0;

            }

            if (position == 1){

                return 1;

            }

            if (position == 2){

                if (NOMBREClienteArray.size() > 0){

                    return  2;

                } else {

                    return  3;

                }
            }

            if (position == 3){

                if (NOMBREClienteArray.size() > 1){

                    return  2;

                } else {

                    return  3;

                }

            }

            if (position == 4){

                if (NOMBREClienteArray.size() > 2){

                    return  2;

                } else {

                    return  3;

                }

            }

            if (position == 5) {

                if (NOMBREClienteArray.size() > 3){

                    return  2;

                } else {

                    return  3;

                }


            }

            if (position == 6){

                if (NOMBREClienteArray.size() > 4){

                    return  2;

                } else {

                    return  3;

                }

            }

            if (position == 7){

                return 4;

            }

            if (position == 8){

                return 5;

            }

            if (position == 9){

                return 1;

            }

            if (position == 10){

                if (NOMBREIngresoArray.size() > 0){

                    return 6;

                } else {

                    return 3;

                }
            }

            if (position == 11){

                if (NOMBREIngresoArray.size() > 1){

                    return 6;

                } else {

                    return 3;

                }
            }

            if (position == 12){

                if (NOMBREIngresoArray.size() > 2){

                    return 6;

                } else {

                    return 3;

                }

            }

            if (position == 13){

                if (NOMBREIngresoArray.size() > 3){

                    return 6;

                } else {

                    return 3;

                }
            }

            if (position == 14){

                if (NOMBREIngresoArray.size() > 4){

                    return 6;

                } else {

                    return 3;

                }

            }

            if (position == 15){

                return 4;

            }

            return 3;
        }

        @Override
        public int getCount() {
            return 16;
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

            if (convertView == null) {

                if (itemViewType == 0) {

                    convertView = mInflater.inflate(R.layout.reporte_general_cell_1, null);

                    TextView opcion1TextView = (TextView) convertView.findViewById(R.id.op1ReporteTextView);
                    PieChart pieChart1 = (PieChart) convertView.findViewById(R.id.op1PieChart);

                    opcion1TextView.setText("Visitas: " + String.valueOf(clientesTotal));

                    colors.clear();
                    colors.add(getResources().getColor(R.color.rojo_pie));
                    colors.add(getResources().getColor(R.color.azul_pie));

                    entries.clear();
                    entries.add(new PieEntry(clientesRepetidos, "Rep"));
                    entries.add(new PieEntry(clientesNuevos, "New"));

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(colors);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.valueOf((int) Math.floor(value));
                        }
                    });

                    PieData data = new PieData(dataSet);
                    data.setValueTextColor(Color.WHITE);
                    data.setValueTextSize(18f);
                    pieChart1.setData(data);
                    pieChart1.getDescription().setEnabled(false);
                    pieChart1.animateX(1500);
                    pieChart1.animateY(1500);
                    pieChart1.animateXY(1500, 1500);
                    pieChart1.highlightValues(null);
                    pieChart1.invalidate();

                    return convertView;

                } else if (itemViewType == 1){

                    convertView = mInflater.inflate(R.layout.general_una_opcion_sola, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opcionSolaTextView);
                    op1TextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    op1TextView.setText("TOP 5");

                } else if (itemViewType == 2){

                    Integer pos = position - 2;

                    convertView = mInflater.inflate(R.layout.reporte_general_cell_2, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1RepCel2TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2RepCel2TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3RepCel2TextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4RepCel2TextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5RepCel2TextView);
                    TextView op6TextView = (TextView) convertView.findViewById(R.id.op6RepCel2TextView);

                    op1TextView.setText(NOMBREClienteArray.get(pos));

                    op1TextView.setTextColor(Color.BLACK);
                    op2TextView.setTextColor((getResources().getColor(R.color.verde_Pando)));
                    op2TextView.setTypeface(null, Typeface.BOLD);
                    op4TextView.setTextColor(Color.BLACK);
                    op6TextView.setTextColor(Color.BLACK);

                    op1TextView.setText(NOMBREClienteArray.get(pos));
                    op2TextView.setText("# " + String.valueOf(pos + 1));
                    op3TextView.setText("# Visitas");
                    op4TextView.setText(String.valueOf(VISITASClienteArray.get(pos)));
                    op5TextView.setText("Cliente nuevo");
                    op6TextView.setText("No");

                    if (!clientesRepDic.get(NOMBREClienteArray.get(pos))){

                        op6TextView.setText("Si");
                        op6TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    }

                    return convertView;

                } else  if (itemViewType == 3){

                    convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return convertView;

                } else if (itemViewType == 4) {

                    convertView = mInflater.inflate(R.layout.una_opcion_con_flecha, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opcion1FlechaTextView);

                    op1TextView.setText(" Ver todos");

                    return convertView;

                } else if (itemViewType == 5){

                    convertView = mInflater.inflate(R.layout.reporte_general_cell_1, null);

                    TextView opcion1TextView = (TextView) convertView.findViewById(R.id.op1ReporteTextView);
                    PieChart pieChart1 = (PieChart) convertView.findViewById(R.id.op1PieChart);

                    opcion1TextView.setText("Ingresos: $" + String.valueOf(ingresoTotal));

                    colors.clear();
                    colors.add(getResources().getColor(R.color.rojo_pie));
                    colors.add(getResources().getColor(R.color.azul_pie));

                    entries.clear();
                    entries.add(new PieEntry(ingresoViejo.intValue(), "Rep"));
                    entries.add(new PieEntry(ingresoNuevo.intValue(), "New"));

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(colors);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.valueOf((int) Math.floor(value));
                        }
                    });

                    PieData data = new PieData(dataSet);
                    data.setValueTextColor(Color.WHITE);
                    data.setValueTextSize(18f);
                    pieChart1.setData(data);
                    pieChart1.getDescription().setEnabled(false);
                    pieChart1.animateX(1500);
                    pieChart1.animateY(1500);
                    pieChart1.animateXY(1500, 1500);
                    pieChart1.highlightValues(null);
                    pieChart1.invalidate();

                    return convertView;

                } else if (itemViewType ==6){

                    Integer pos = position - 10;

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

                    op1TextView.setText(NOMBREIngresoArray.get(pos));
                    op2TextView.setText("# " + String.valueOf(pos + 1));
                    op3TextView.setText("Consumo");
                    op4TextView.setText(String.valueOf(CANTIDADIngresoArray.get(pos)));
                    op5TextView.setText("Cliente nuevo");
                    op6TextView.setText("No");

                    if (!clientesRepDic.get(NOMBREIngresoArray.get(pos))){

                        op6TextView.setText("Si");
                        op6TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    }

                    return convertView;
                }

            } else {

                if (itemViewType == 0) {

                    convertView = mInflater.inflate(R.layout.reporte_general_cell_1, null);

                    TextView opcion1TextView = (TextView) convertView.findViewById(R.id.op1ReporteTextView);
                    PieChart pieChart1 = (PieChart) convertView.findViewById(R.id.op1PieChart);

                    opcion1TextView.setText("Visitas: " + String.valueOf(clientesTotal));

                    colors.clear();
                    colors.add(getResources().getColor(R.color.rojo_pie));
                    colors.add(getResources().getColor(R.color.azul_pie));

                    entries.clear();
                    entries.add(new PieEntry(clientesRepetidos, "Rep"));
                    entries.add(new PieEntry(clientesNuevos, "New"));

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(colors);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.valueOf((int) Math.floor(value));
                        }
                    });

                    PieData data = new PieData(dataSet);
                    data.setValueTextColor(Color.WHITE);
                    data.setValueTextSize(18f);
                    pieChart1.setData(data);
                    pieChart1.getDescription().setEnabled(false);
                    pieChart1.animateX(1500);
                    pieChart1.animateY(1500);
                    pieChart1.animateXY(1500, 1500);
                    pieChart1.highlightValues(null);
                    pieChart1.invalidate();

                    return convertView;

                } else if (itemViewType == 1){

                    convertView = mInflater.inflate(R.layout.general_una_opcion_sola, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opcionSolaTextView);
                    op1TextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    op1TextView.setText("TOP 5");

                } else if (itemViewType == 2){

                    Integer pos = position - 2;

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

                    op1TextView.setText(NOMBREClienteArray.get(pos));
                    op2TextView.setText("# " + String.valueOf(pos + 1));
                    op3TextView.setText("# Visitas");
                    op4TextView.setText(String.valueOf(VISITASClienteArray.get(pos)));
                    op5TextView.setText("Cliente nuevo");
                    op6TextView.setText("No");

                    if (!clientesRepDic.get(NOMBREClienteArray.get(pos))){

                        op6TextView.setText("Si");
                        op6TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    }

                    return convertView;

                } else  if (itemViewType == 3){

                    convertView = mInflater.inflate(R.layout.general_celda_vacia, null);

                    return convertView;

                } else if (itemViewType == 4) {

                    convertView = mInflater.inflate(R.layout.una_opcion_con_flecha, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.opcion1FlechaTextView);

                    op1TextView.setText(" Ver todos");

                    return convertView;

                } else if (itemViewType == 5){

                    convertView = mInflater.inflate(R.layout.reporte_general_cell_1, null);

                    TextView opcion1TextView = (TextView) convertView.findViewById(R.id.op1ReporteTextView);
                    PieChart pieChart1 = (PieChart) convertView.findViewById(R.id.op1PieChart);

                    opcion1TextView.setText("Ingresos: $" + String.valueOf(ingresoTotal));

                    colors.clear();
                    colors.add(getResources().getColor(R.color.rojo_pie));
                    colors.add(getResources().getColor(R.color.azul_pie));

                    entries.clear();
                    entries.add(new PieEntry(ingresoViejo.intValue(), "Rep"));
                    entries.add(new PieEntry(ingresoNuevo.intValue(), "New"));

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(colors);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.valueOf((int) Math.floor(value));
                        }
                    });

                    PieData data = new PieData(dataSet);
                    data.setValueTextColor(Color.WHITE);
                    data.setValueTextSize(18f);
                    pieChart1.setData(data);
                    pieChart1.getDescription().setEnabled(false);
                    pieChart1.animateX(1500);
                    pieChart1.animateY(1500);
                    pieChart1.animateXY(1500, 1500);
                    pieChart1.highlightValues(null);
                    pieChart1.invalidate();

                    return convertView;

                } else if (itemViewType ==6){

                    Integer pos = position - 10;

                    convertView = mInflater.inflate(R.layout.reporte_general_cell_2, null);

                    TextView op1TextView = (TextView) convertView.findViewById(R.id.op1RepCel2TextView);
                    TextView op2TextView = (TextView) convertView.findViewById(R.id.op2RepCel2TextView);
                    TextView op3TextView = (TextView) convertView.findViewById(R.id.op3RepCel2TextView);
                    TextView op4TextView = (TextView) convertView.findViewById(R.id.op4RepCel2TextView);
                    TextView op5TextView = (TextView) convertView.findViewById(R.id.op5RepCel2TextView);
                    TextView op6TextView = (TextView) convertView.findViewById(R.id.op6RepCel2TextView);

                    op1TextView.setText(NOMBREIngresoArray.get(pos));

                    op1TextView.setTextColor(Color.BLACK);
                    op2TextView.setTextColor((getResources().getColor(R.color.verde_Pando)));
                    op2TextView.setTypeface(null, Typeface.BOLD);
                    op4TextView.setTextColor(Color.BLACK);
                    op6TextView.setTextColor(Color.BLACK);

                    op1TextView.setText(NOMBREIngresoArray.get(pos));
                    op2TextView.setText("# " + String.valueOf(pos + 1));
                    op3TextView.setText("Consumo");
                    op4TextView.setText(String.valueOf(CANTIDADIngresoArray.get(pos)));
                    op5TextView.setText("Cliente nuevo");
                    op6TextView.setText("No");

                    if (!clientesRepDic.get(NOMBREIngresoArray.get(pos))){

                        op6TextView.setText("Si");
                        op6TextView.setTextColor(getResources().getColor(R.color.morado_Pando));

                    }

                    return convertView;

                }
            }

            return convertView;

        }
    }
}
