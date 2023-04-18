package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class AltaUsuario extends AppCompatActivity {
    private RadioGroup grupoRadios;
    private RadioButton rbEmpresa;
    private RadioButton rbparticular;
    private RadioButton radioButtonSeleccionado;
    private TextView tvNombre;
    private String nombreApellidos;
    private String nombreEmpresa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuario);

        grupoRadios = findViewById(R.id.grupo_radiobuttons);
        rbparticular = findViewById(R.id.rbParticula);
        rbEmpresa = findViewById(R.id.rbEmpresa);
        tvNombre = findViewById(R.id.etNombre);
        nombreApellidos = getString(R.string.nombre_y_apellidos);
        nombreEmpresa = getString(R.string.nombre_empresa);

        rbparticular.setChecked(true);

        //Cambio de valores del formulario según tipo de usuario
        grupoRadios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                radioButtonSeleccionado = findViewById(checkedId);
                // Hacer algo con el radio button seleccionado
                if(radioButtonSeleccionado==rbEmpresa){
                    tvNombre.setText(nombreEmpresa);

                }
                else{
                    tvNombre.setText(nombreApellidos);
                }
            }
        });
    }
}