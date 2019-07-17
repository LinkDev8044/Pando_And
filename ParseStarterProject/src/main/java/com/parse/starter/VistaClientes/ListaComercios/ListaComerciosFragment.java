package com.parse.starter.VistaClientes.ListaComercios;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;
import com.parse.starter.VistaClientes.DescripcionComercio.DescripcionComercioActivity;

import java.util.ArrayList;
import java.util.List;

public class ListaComerciosFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_lista_comercios, container, false);

        iniciarSppiner();

        listaComercioListView = (ListView) inflate.findViewById(R.id.listaComListView);

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

        listaComercioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getContext(), DescripcionComercioActivity.class);
                intent.putExtra("nombreComercio", COMERCIOS.get(i));
                startActivity(intent);

                /*FragmentManager fragmentManager = getFragmentManager();
                Fragment descripcionFragment = new DescripcionComercioFragment();
                Bundle bundle = new Bundle();
                bundle.putString("nombreComercio", COMERCIOS.get(i));
                descripcionFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, descripcionFragment).addToBackStack(null).commit();*/

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
