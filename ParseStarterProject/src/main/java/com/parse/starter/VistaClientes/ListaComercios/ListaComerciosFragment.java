package com.parse.starter.VistaClientes.ListaComercios;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;
import com.parse.starter.VistaComercio.Administrador.AdministradorActivity;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaComerciosFragment extends Fragment {

    String usuarioId;

    int contador;
    int contador2;

    Map<String,Boolean> dicTieneCupon =  new HashMap<String,Boolean>();
    Map<String,Boolean> dicOfreceCupon =  new HashMap<String,Boolean>();
    Map<String,String> dicDescripcion =  new HashMap<String,String>();

    TextView goToQRCodeTextView;

    ListView listaComercioListView;

    ArrayList<String> COMERCIOS = new ArrayList();
    ArrayList<String> sloganArray = new ArrayList();
    ArrayList<String> comercioIdArray = new ArrayList();

    ArrayList<Bitmap> logoArray = new ArrayList();

    ArrayList<ParseFile> parseImageArray = new ArrayList();

    Bitmap bmp1;

    ProgressDialog progressDialog;

    public void iniciarSppiner() {
        this.progressDialog = new ProgressDialog(getContext());
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("Cargando...");
        this.progressDialog.show();
        getActivity().getWindow().setFlags(16, 16);
    }

    public void terminarSppiner() {
        getActivity().getWindow().clearFlags(16);
        this.progressDialog.dismiss();
    }

    private void convertFileArray(ArrayList<ParseFile> arrayList) {
        for(ParseFile file: arrayList) {
            if(file != null) {
                byte[] bytes = new byte[0];
                try {
                    bytes = file.getData();

                    bmp1 = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    logoArray.add(bmp1);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cargarPromos(){

        dicOfreceCupon.clear();
        dicDescripcion.clear();

        contador2 = 0;

        for (int i = 0; i < comercioIdArray.size(); i++){

            final String comercioId = comercioIdArray.get(i);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("CuponInicial");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("activo", true);
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        String descripcionPromo;

                        if (objects.size() > 0){

                            dicOfreceCupon.put(comercioId, true);

                            for (ParseObject object : objects){

                                descripcionPromo = object.getString("descripcionCupon");

                                dicDescripcion.put(comercioId, descripcionPromo);

                                contador2 = contador2 + 1;

                            }

                        } else {

                            dicOfreceCupon.put(comercioId, false);

                            descripcionPromo = "";

                            dicDescripcion.put(comercioId, descripcionPromo);

                            contador2 = contador2 + 1;

                        }

                        if (contador2 >= comercioIdArray.size()){

                            final CustomAdapter customAdapter = new CustomAdapter();

                            convertFileArray(parseImageArray);

                            listaComercioListView.setAdapter(customAdapter);

                            terminarSppiner();


                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(getContext(), "Tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void cargarVisitasIniciales(){

        contador = 0;
        dicTieneCupon.clear();

        for (int i = 0; i < comercioIdArray.size(); i++){

            final String comercioId = comercioIdArray.get(i);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("PuntosCliente");
            query.whereEqualTo("comercioId", comercioId);
            query.whereEqualTo("usuarioId", ParseUser.getCurrentUser().getObjectId());
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){

                        if (objects.size() <= 0){

                            Log.i("Prueba", "Primera vez - Se busca promo");

                            dicTieneCupon.put(comercioId, true);

                            contador = contador + 1;

                        } else {

                            Log.i("Prueba", "Ya es cliente");

                            dicTieneCupon.put(comercioId, false);

                            contador = contador + 1;

                        }

                        if (contador >= comercioIdArray.size()){

                            cargarPromos();

                        }

                    } else {

                        terminarSppiner();

                        Toast.makeText(getContext(), "Tuvimos un problema - Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void cargarComercios() {

        ParseQuery.getQuery("Comercios").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    COMERCIOS.clear();
                    sloganArray.clear();
                    comercioIdArray.clear();
                    logoArray.clear();
                    parseImageArray.clear();

                    for (ParseObject object : objects){

                        COMERCIOS.add(object.getString("nombreComercio"));
                        sloganArray.add(object.getString("slogan"));
                        comercioIdArray.add(object.getObjectId());

                        ParseFile parseFile = (ParseFile) object.get("logo");

                        parseImageArray.add(parseFile);

                    }

                    cargarVisitasIniciales();

                } else {

                    terminarSppiner();

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        usuarioId = ParseUser.getCurrentUser().getObjectId();

        Bitmap myBitmap = QRCode.from(usuarioId).withSize(600, 600).bitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();
        final ParseFile image = new ParseFile("QRCliente.png", bitmapBytes);

        iniciarSppiner();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("CodigoQRCliente");
        query.whereEqualTo("usuarioId", usuarioId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    if (objects.size() > 0){

                        cargarComercios();

                    } else {

                        ParseObject object = new ParseObject("CodigoQRCliente");
                        object.put("usuarioId", usuarioId);
                        object.put("correoCliente", ParseUser.getCurrentUser().getEmail());
                        object.put("codigoQRCliente", image);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null){

                                    cargarComercios();

                                } else {

                                    terminarSppiner();

                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }

                } else {

                    terminarSppiner();

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_lista_comercios, container, false);

        listaComercioListView = (ListView) inflate.findViewById(R.id.listaComListView);
        goToQRCodeTextView = (TextView) inflate.findViewById(R.id.verCodigoTextView);

        listaComercioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getContext(), DescripcionComercioActivity.class);
                intent.putExtra("nombreComercio", COMERCIOS.get(i));
                intent.putExtra("usarQR", false);
                startActivity(intent);

            }
        });

        goToQRCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), DescripcionComercioActivity.class);
                intent.putExtra("usarQR", true);
                startActivity(intent);

            }
        });

        return inflate;

    }

    class CustomAdapter extends BaseAdapter implements Adapter{

        LayoutInflater mInflater = LayoutInflater.from(getContext());

        @Override
        public int getCount() {
            return COMERCIOS.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {

                if (dicTieneCupon.get(comercioIdArray.get(i))){

                    if (dicOfreceCupon.get(comercioIdArray.get(i))){

                        view = mInflater.inflate(R.layout.lista_comercios_cell_1, null);

                        TextView op1TextView = (TextView) view.findViewById(R.id.op1ListComTextView);
                        TextView op2TextView = (TextView) view.findViewById(R.id.op2ListComTextView);
                        TextView op3TextView = (TextView) view.findViewById(R.id.op3ListComTextView);
                        final ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1ListComImageView);
                        ImageView op2imageView = (ImageView) view.findViewById(R.id.op2listComImageView);

                        op1TextView.setText(COMERCIOS.get(i));
                        op2TextView.setText(sloganArray.get(i));
                        op1ImageView.setImageBitmap(logoArray.get(i));

                        op3TextView.setText(dicDescripcion.get(comercioIdArray.get(i)));
                        op2imageView.setImageResource(R.drawable.sale);

                    } else {

                        view = mInflater.inflate(R.layout.lista_comercios_cell_1_op2, null);

                        TextView op1TextView = (TextView) view.findViewById(R.id.op1ListCom2TextView);
                        TextView op2TextView = (TextView) view.findViewById(R.id.op2ListCom2TextView);
                        final ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1ListCom2ImageView);

                        op1TextView.setText(COMERCIOS.get(i));
                        op2TextView.setText(sloganArray.get(i));
                        op1ImageView.setImageBitmap(logoArray.get(i));

                    }

                } else {

                    view = mInflater.inflate(R.layout.lista_comercios_cell_1_op2, null);

                    TextView op1TextView = (TextView) view.findViewById(R.id.op1ListCom2TextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op2ListCom2TextView);
                    final ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1ListCom2ImageView);

                    op1TextView.setText(COMERCIOS.get(i));
                    op2TextView.setText(sloganArray.get(i));
                    op1ImageView.setImageBitmap(logoArray.get(i));

                }

                return view;

            } else {

                if (dicTieneCupon.get(comercioIdArray.get(i))){

                    if (dicOfreceCupon.get(comercioIdArray.get(i))){

                        view = mInflater.inflate(R.layout.lista_comercios_cell_1, null);

                        TextView op1TextView = (TextView) view.findViewById(R.id.op1ListComTextView);
                        TextView op2TextView = (TextView) view.findViewById(R.id.op2ListComTextView);
                        TextView op3TextView = (TextView) view.findViewById(R.id.op3ListComTextView);
                        final ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1ListComImageView);
                        ImageView op2imageView = (ImageView) view.findViewById(R.id.op2listComImageView);

                        op1TextView.setText(COMERCIOS.get(i));
                        op2TextView.setText(sloganArray.get(i));
                        op1ImageView.setImageBitmap(logoArray.get(i));

                        op3TextView.setText(dicDescripcion.get(comercioIdArray.get(i)));
                        op2imageView.setImageResource(R.drawable.sale);

                    } else {

                        view = mInflater.inflate(R.layout.lista_comercios_cell_1_op2, null);

                        TextView op1TextView = (TextView) view.findViewById(R.id.op1ListCom2TextView);
                        TextView op2TextView = (TextView) view.findViewById(R.id.op2ListCom2TextView);
                        final ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1ListCom2ImageView);

                        op1TextView.setText(COMERCIOS.get(i));
                        op2TextView.setText(sloganArray.get(i));
                        op1ImageView.setImageBitmap(logoArray.get(i));

                    }

                } else {

                    view = mInflater.inflate(R.layout.lista_comercios_cell_1_op2, null);

                    TextView op1TextView = (TextView) view.findViewById(R.id.op1ListCom2TextView);
                    TextView op2TextView = (TextView) view.findViewById(R.id.op2ListCom2TextView);
                    final ImageView op1ImageView = (ImageView) view.findViewById(R.id.op1ListCom2ImageView);

                    op1TextView.setText(COMERCIOS.get(i));
                    op2TextView.setText(sloganArray.get(i));
                    op1ImageView.setImageBitmap(logoArray.get(i));

                }

                return view;

            }
        }
    }
}
