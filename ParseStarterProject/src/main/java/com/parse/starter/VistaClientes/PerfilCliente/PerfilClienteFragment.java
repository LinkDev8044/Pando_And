package com.parse.starter.VistaClientes.PerfilCliente;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.AyudaYSugerencias.AyudaSugerenciaActivity;
import com.parse.starter.Inicio.inicio_Pando_Activity;
import com.parse.starter.R;

import java.util.List;

public class PerfilClienteFragment extends Fragment {

    String nombreCliente;
    String correoCliente;
    String apellidoCliente;

    String[] TITULOS = {"Ayuda o sugerencias", "Cerrar sesión"};

    Boolean isLogOut;

    int[] IMAGES = {R.drawable.support, R.drawable.cerrar_sesion};

    TextView nombreTextView;

    ListView perfilListView;

    ProgressDialog progressDialog;

    public void iniciarSppiner() {
        this.progressDialog = new ProgressDialog(getContext());
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("Cargando...");
        this.progressDialog.show();
        getActivity().getWindow().setFlags(16,16);

    }

    public void terminarSppiner() {
        getActivity().getWindow().clearFlags(16);
        this.progressDialog.dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        isLogOut = false;

        nombreTextView = (TextView) inflate.findViewById(R.id.nombrePerfilTextView);
        perfilListView = (ListView) inflate.findViewById(R.id.perfilListView);

        final CustomAdapter customAdapter = new CustomAdapter();

        iniciarSppiner();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null){

                    for (ParseObject object : objects){

                       nombreCliente = object.getString("nombre");
                       apellidoCliente = object.getString("apellido");
                       correoCliente = object.getString("email");

                    }

                    nombreTextView.setText("Hola " + nombreCliente);

                    perfilListView.setAdapter(customAdapter);

                    terminarSppiner();

                    return;

                }
            }
        });

        perfilListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {

                    Intent intent = new Intent(getContext(), AyudaSugerenciaActivity.class);
                    intent.putExtra("correoCliente", correoCliente);
                    intent.putExtra("nombreCliente", nombreCliente);
                    intent.putExtra("apellidoCliente", apellidoCliente);
                    startActivity(intent);

                    return;

                }

                if (i == 1){

                    if (isLogOut){

                        ParseUser.logOut();
                        startActivity(new Intent(getContext(), inicio_Pando_Activity.class));

                    } else {

                        isLogOut = true;

                        Toast.makeText(getActivity(), "Presiona de nuevo para CONFIRMAR cerrar sesión", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                isLogOut = false;

                            }
                        }, 5000);
                    }

                }
            }
        });

        terminarSppiner();

        return inflate;

    }

    class CustomAdapter extends BaseAdapter implements Adapter {

        LayoutInflater mInflater = LayoutInflater.from(getContext());

        @Override
        public int getCount() {
            return TITULOS.length;
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

            if (view == null){

                view = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

                ((TextView) view.findViewById(R.id.opc1ImaTextView)).setText(TITULOS[i]);
                ((ImageView) view.findViewById(R.id.opc1ImaImageView)).setImageResource(IMAGES[i]);

                return view;
            }

            view = mInflater.inflate(R.layout.una_opcion_con_imagen, null);

            ((TextView) view.findViewById(R.id.opc1ImaTextView)).setText(TITULOS[i]);
            ((ImageView) view.findViewById(R.id.opc1ImaImageView)).setImageResource(IMAGES[i]);

            return view;
        }
    }
}
