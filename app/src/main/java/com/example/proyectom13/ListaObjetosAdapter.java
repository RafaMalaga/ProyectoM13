package com.example.proyectom13;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.proyectom13.POJOS.Objeto;

import java.util.ArrayList;

public class ListaObjetosAdapter extends ArrayAdapter<Objeto> {

    public ListaObjetosAdapter(Context context, ArrayList<Objeto> objetos) {
        super(context, 0, objetos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Objeto objeto = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_objeto, parent, false);
        }
        LinearLayout fila = convertView.findViewById(R.id.fila);
        TextView tvNombre = convertView.findViewById(R.id.tvNombre);
        TextView tvLugarGuardado = convertView.findViewById(R.id.tvLugarGuardado);
        TextView tvFechaAlta = convertView.findViewById(R.id.tvFechaAlta);

        tvNombre.setText(objeto.getNombre());
        tvLugarGuardado.setText(objeto.getLugarGuardado());
        tvFechaAlta.setText(objeto.getFechaAlta());


        return convertView;
    }

}
