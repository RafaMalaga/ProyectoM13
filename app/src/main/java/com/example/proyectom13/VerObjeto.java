package com.example.proyectom13;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Date;

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
        //String idobjetos = getIntent().getExtras().getString("idobjetos");
        //getFoto.execute("http://" + MainActivity.HOST + "/api/get_foto.php?idobjetos=" + idobjetos, "GET");
        //String url = "http://" + MainActivity.HOST + "/api/get_foto.php?idobjetos=" + BuscarObjeto.objeto.getIdObjeto();
        //System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + url);
        //getFoto.execute("http://" + MainActivity.HOST + "/api/get_foto.php?idobjetos=" + idobjetos, "GET");

        //http://192.168.1.131/api/get_foto.php?idobjetos=1
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
                btEditar.setBackgroundResource(R.drawable.edit);
                editable=false;
                dialogInterface.dismiss();
                Toast.makeText(getApplicationContext(), R.string.no_edit, Toast.LENGTH_SHORT).show(); // Mostrar un mensaje de confirmación
            }
        });
        AlertDialog dialog = builder.create();
        btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                if(editable==true) {
                    // Se crea un objeto Date con la fecha y hora actuales
                    Date fechaActual = new Date();
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaActualString = formatoFecha.format(fechaActual);

                    operacion = actualizar;
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
                    String url = "http://" + MainActivity.HOST + "/api/update.php";
                    actualizarFoto.execute(url, "POST", BuscarObjeto.objeto.toString());
                    etNombre.setEnabled(false);
                    etDescripcion.setEnabled(false);
                    etLugarGuardado.setEnabled(false);
                    editable = false;
                    dialog.dismiss();
                }
                else{

                    btEditar.setBackgroundResource(R.drawable.edit);
                    editable=!editable;
                }



            }
        });
        btBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask borrarFoto = new RequestTask();
                String url = "http://" + MainActivity.HOST + "/api/delete.php";
                JSONObject jsonObject = new JSONObject();
                operacion = borrar;
                try {
                    jsonObject.put("idobjetos", BuscarObjeto.objeto.getIdObjeto());
                    borrarFoto.execute(url, "POST", jsonObject.toString());

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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
                //JSONArray array = new JSONArray(s);
                JSONObject respuesta = new JSONObject(s);
                String mensaje = respuesta.getString("mensaje");
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" + mensaje);
                String show = "";
                if(mensaje.equals("OK")){
                    if(operacion==actualizar){
                        show = getString(R.string.actualizarOK);
                    }else{
                        show = getString(R.string.borrarOK);
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
