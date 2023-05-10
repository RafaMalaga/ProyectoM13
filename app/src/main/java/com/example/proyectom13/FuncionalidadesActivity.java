package com.example.proyectom13;

import static com.example.proyectom13.R.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class FuncionalidadesActivity extends AppCompatActivity {
    Button btGuardarArticulo;
    Button btBuscar;
    Button btCompartir;
    Button btSalir;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_funcionalidades);
        btGuardarArticulo = findViewById(id.btGuardarArticulo);
        btBuscar=findViewById(id.btBuscar);
        btCompartir=findViewById(id.btCompartir);
        btSalir=findViewById(id.btSalir);

        btSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            } // cerramos la activity actual y todas la demas activitys asociadas a ella ( se cierra la app)
        });
         // en lo siguientes metodos Onclick de los distintos botones abrimos las distintas activity

        btGuardarArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FuncionalidadesActivity.this, AltaObjeto.class);
                startActivity(intent);
            }
        });
        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FuncionalidadesActivity.this, BuscarObjeto.class);
                startActivity(intent);
            }
        });

        btCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FuncionalidadesActivity.this, Compartir.class);
                startActivity(intent);
            }
        });


    }





}