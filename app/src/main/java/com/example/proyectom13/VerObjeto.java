package com.example.proyectom13;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectom13.POJOS.Objeto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerObjeto extends AppCompatActivity {

    ImageView ivObjeto;
    EditText etNombre;
    EditText etFechaAlta;
    EditText etDescripcion;
    EditText etLugarGuardado;
    ImageButton btEditar;
    ImageButton btBorrar;
    Button btAtras;
    private boolean editable = false;
    int operacion;
    final int borrar = 0;

    final int actualizar = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_objeto);
        RequestTask getFoto = new RequestTask();

        ivObjeto = findViewById(R.id.ivObjeto);
        etNombre = findViewById(R.id.etNombre);
        etFechaAlta = findViewById(R.id.etFechaAlta);
        etFechaAlta.setEnabled(false);
        etDescripcion = findViewById(R.id.etDescripcion);
        etLugarGuardado = findViewById(R.id.etLugarGuardado);
        btEditar = findViewById(R.id.btEditarObjeto);
        btBorrar = findViewById(R.id.btBorrarObjeto);
        btAtras = findViewById(R.id.botonAtras);

        etNombre.setText(BuscarObjeto.objeto.getNombre());
        etFechaAlta.setText(BuscarObjeto.objeto.getFechaAlta());
        etDescripcion.setText(BuscarObjeto.objeto.getDescripcion());
        etLugarGuardado.setText(BuscarObjeto.objeto.getLugarGuardado());

        byte[] fotoBytesBase64 = Base64.decode(BuscarObjeto.objeto.getFoto(), Base64.URL_SAFE);
        Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytesBase64,0,fotoBytesBase64.length);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        ivObjeto.setBackground(d);

        //Dialog para preguntar si quieres editar o no
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.desea_editar);
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Código para hacer si el usuario hace clic en "Sí"
                etNombre.setEnabled(true);
                etDescripcion.setEnabled(true);
                etLugarGuardado.setEnabled(true);
                etNombre.setTextColor(getColor(R.color.white));
                etLugarGuardado.setTextColor(getColor(R.color.white));
                etDescripcion.setTextColor(getColor(R.color.white));
                btEditar.setBackgroundResource(R.drawable.guardar);
                editable=true;
                Toast.makeText(getApplicationContext(), R.string.edit, Toast.LENGTH_SHORT).show(); // Mostrar un mensaje de confirmación
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Código para hacer si el usuario hace clic en "No"
                etNombre.setEnabled(false);
                etDescripcion.setEnabled(false);
                etLugarGuardado.setEnabled(false);
                etNombre.setTextColor(getColor(R.color.gris_oscuro_texto));
                etLugarGuardado.setTextColor(getColor(R.color.gris_oscuro_texto));
                etDescripcion.setTextColor(getColor(R.color.gris_oscuro_texto));
                btEditar.setBackgroundResource(R.drawable.edit);
                editable=false;
                dialogInterface.dismiss();
                Toast.makeText(getApplicationContext(), R.string.no_edit, Toast.LENGTH_SHORT).show(); // Mostrar un mensaje de confirmación
            }
        });
        AlertDialog dialog = builder.create();
        btEditar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                dialog.show();

                if (editable == true) {

                    btAtras.setEnabled(true);
                    btBorrar.setEnabled(true);
                    // Se crea un objeto Date con la fecha y hora actuales
                    Date fechaActual = new Date();
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaActualString = formatoFecha.format(fechaActual);

                    operacion = actualizar; // codigo para actualizar el objeto, si los campos no estan vacios se recogen los valores de dichos campos y se añaden al objeto
                    if (!etNombre.getText().toString().isEmpty()) {
                        BuscarObjeto.objeto.setNombre(etNombre.getText().toString());
                    }

                    if (!etDescripcion.getText().toString().isEmpty()) {
                        BuscarObjeto.objeto.setDescripcion(etDescripcion.getText().toString());
                    }


                    if (!etLugarGuardado.getText().toString().isEmpty()) {
                        BuscarObjeto.objeto.setLugarGuardado(etLugarGuardado.getText().toString());
                    }
                    RequestTask actualizarFoto = new RequestTask();
                    String url = "https://" + MainActivity.HOST + "/api/update.php"; //llamada a la api de actualizar
                    actualizarFoto.execute(url, "POST", BuscarObjeto.objeto.toString());
                    etNombre.setEnabled(false);
                    etDescripcion.setEnabled(false); // deshabilitar la escritura en los campos de texto
                    etLugarGuardado.setEnabled(false);
                    etNombre.setTextColor(getColor(R.color.gris_oscuro_texto));
                    etLugarGuardado.setTextColor(getColor(R.color.gris_oscuro_texto));// mostrar texto en gris oscuro para visualizar mejor el texto
                    etDescripcion.setTextColor(getColor(R.color.gris_oscuro_texto));
                    btEditar.setBackgroundResource(R.drawable.edit);
                    editable = false;
                    dialog.dismiss();
                } else {
                btAtras.setEnabled(false);
                btBorrar.setEnabled(false);
                btEditar.setBackgroundResource(R.drawable.edit);
                editable = !editable;
            }


        }

        });
        btBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerObjeto.this);
                builder.setMessage("¿Estás seguro de borrar este objeto?")   // al pulsr el icono borrar se abre cuadro de dialogo alertando al usuario
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acciones a realizar cuando se selecciona "SI"
                                RequestTask borrarFoto = new RequestTask();
                                String url = "https://" + MainActivity.HOST + "/api/delete.php"; // si el user pulsa en si se llama a la api para borrar el objeto
                                JSONObject jsonObject = new JSONObject();
                                operacion = borrar;
                                try {
                                    jsonObject.put("idobjetos", BuscarObjeto.objeto.getIdObjeto());
                                    borrarFoto.execute(url, "POST", jsonObject.toString());


                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                finish(); // una vez borrado el objeto salimos a la actividad anterior

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acciones a realizar cuando se selecciona "NO"
                                dialog.dismiss(); // si pulsa en no, se cierra el cuadro de dialogo sin hacer ninguna accion
                            }
                        })
                        .setCancelable(false); // Evita que se pueda cancelar el diálogo al tocar fuera de él

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Personalizar tamaño de texto de los botones
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                positiveButton.setTextSize(20);
                negativeButton.setTextSize(20);
            }
        });


        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }


    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... info) {
            String url = info[0];
            String metodo = info[1];
            String resultado = "";

            if (metodo.equals("GET")) {
                resultado = sendGet(url);
            } else {
                String jsonData = info[2];
                resultado = sendPost(url, jsonData);
            }

            Log.d("MainActivity", resultado);
            return resultado;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //Metodo para actualizar objeto
                JSONObject respuesta = new JSONObject(s);
                String mensaje = respuesta.getString("mensaje");
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" + mensaje);
                String show = "";
                if(mensaje.equals("OK")){
                    if(operacion==actualizar){
                        show = getString(R.string.actualizarOK);
                    }else{
                        show = getString(R.string.borrarOK);
                        Intent intent=new Intent(getApplicationContext(), FuncionalidadesActivity.class);
                        startActivity(intent);

                    }
                }else{
                    if(operacion==actualizar){
                        show = getString(R.string.actualizarKO);

                    }else{
                        show = getString(R.string.borrarKO);

                    }
                }
                Toast.makeText(getApplicationContext(), show, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String sendGet(String surl) {
            try {
                URL url = new URL(surl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Log.d("MainActivity", "Session: " + MainActivity.session);
                con.setRequestMethod("GET");
                con.addRequestProperty("Cookie", MainActivity.session);

                int responseCode = con.getResponseCode();
                Log.d("MainActivity", "GET Response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    //Leemos la respuesta del servidor
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    Log.e("MainActivity", "El metodo get no ha funcionado: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error al hacer el get: " + e.toString());
            }
            return null;
        }

        public String sendPost(String surl, String jsonData) {
            try {
                URL url = new URL(surl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(10000);
                //Ponemos cabeceras http para indicar que mandamos un json
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Length", String.valueOf(jsonData.getBytes().length));
                con.addRequestProperty("Cookie", MainActivity.session);

                //Especificamos el metodo http (POST/GET/PUT/DELETE/HEAD/OPTIONS)
                con.setRequestMethod("POST");
                //Especificamos que mandamos datos
                con.setDoOutput(true);
                //Enviamos la peticion, escribiendo el json
                OutputStream os = con.getOutputStream();
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.close();

                //Si el servidor devuelve 200 es que ha ido bien
                if (con.getResponseCode() == 200) {
                    //Leemos la respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;

                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    Log.d("MainActivity", "Respuesta: " + response.toString());
                    return response.toString();
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error: " + e.toString());
            }

            return null;
        }

    }

}
