package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class SeleccionarDeLista extends AppCompatActivity {
    ListView lstCompartir;
    Button btSeleccionarDeLista;
    Button btAtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_de_lista);

        lstCompartir = findViewById(R.id.lvListaObjetos);
        btSeleccionarDeLista = findViewById(R.id.btCompartirDeLista);
        btAtras = findViewById(R.id.btCompartirAtras);
        Intent intent = getIntent();

        ArrayList resultadosObj  = intent.getParcelableArrayListExtra("arrayL");



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice , resultadosObj);
        lstCompartir.setAdapter(adapter);

        btSeleccionarDeLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Obtener el SparseBooleanArray de elementos seleccionados
                SparseBooleanArray checkedPositions = lstCompartir.getCheckedItemPositions();

                // Crear un ArrayList para los elementos seleccionados
                ArrayList elemSeleccionados = new ArrayList<>();
                ArrayList <Integer> elemSeleccionadosPosicion = new ArrayList<>();

                // Iterar a través del SparseBooleanArray y agregar los elementos seleccionados al ArrayList
                for (int i = 0; i < checkedPositions.size(); i++) {
                    int position = checkedPositions.keyAt(i);
                    boolean isChecked = checkedPositions.valueAt(i);
                    if (isChecked) {
                        String selectedItem = (String) lstCompartir.getItemAtPosition(position);
                        elemSeleccionados.add(selectedItem);
                        elemSeleccionadosPosicion.add(position);
                    }

                }


                // Creamos un Intent con los datos que queremos enviar de vuelta a ActivityA
                Intent intent = new Intent();
                intent.putExtra("elemSeleccionadosObj", elemSeleccionados);
                intent.putExtra("elemSeleccionadosPosicion", elemSeleccionadosPosicion);
                setResult(Activity.RESULT_OK, intent);


                // Finalizamos ActivityB
                finish();



            }
        });
        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}