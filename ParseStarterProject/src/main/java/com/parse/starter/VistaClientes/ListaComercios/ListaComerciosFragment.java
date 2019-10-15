package com.parse.starter.VistaClientes.ListaComercios;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ListaComerciosFragment extends Fragment {

    String usuarioId;

    TextView goToQRCodeTextView;

    ListView listaComercioListView;

    ArrayList<String> COMERCIOS = new ArrayList();

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

    private void cargarComercios() {

        final CustomAdapter customAdapter = new CustomAdapter();

        ParseQuery.getQuery("Comercios").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null){

                    COMERCIOS.clear();

                    for (ParseObject object : objects){

                        COMERCIOS.add(object.getString("nombreComercio"));

                    }

                    listaComercioListView.setAdapter(customAdapter);

                    terminarSppiner();

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

                view = mInflater.inflate(R.layout.una_opcion_con_flecha, null);

                ((TextView) view.findViewById(R.id.opcion1FlechaTextView)).setText(COMERCIOS.get(i));

                return view;

            }

            ((TextView) view.findViewById(R.id.opcion1FlechaTextView)).setText(COMERCIOS.get(i));

            return view;
        }
    }
}
