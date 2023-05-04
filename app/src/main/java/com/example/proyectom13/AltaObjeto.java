package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;

import com.example.proyectom13.POJOS.Objeto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AltaObjeto extends AppCompatActivity {

    EditText etNombre;
    EditText etLugarGuardado;

    EditText etDescripcion;
    EditText etFechaAlta;
    ImageView ivObjeto;
    Button btGuardarObjeto;
    Uri photoURI;
    String fotoPath;
    static Bitmap bitmap;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_objeto);
        ivObjeto = (ImageView) findViewById(R.id.ivObjeto);
        etFechaAlta = (EditText) findViewById(R.id.etFechaAlta);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        etNombre = findViewById(R.id.etNombre);
        etLugarGuardado = findViewById(R.id.etLugarGuardado);
                etDescripcion = findViewById(R.id.etDescripcion);
        etFechaAlta.setText(timeStamp);
        btGuardarObjeto = findViewById(R.id.btGuardarObjeto);

        ActivityCompat.requestPermissions( this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE

                }, 1
        );
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        ivObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File fotoFile = File.createTempFile(
                            "test",  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */
                    );
                    photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.proyectom13", fotoFile);
                    fotoPath = fotoFile.getAbsolutePath();
                    imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(imageCaptureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        });

        btGuardarObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fotoBase64 = "";
                if (bitmap != null){
                    fotoBase64 = Base64.encodeToString(compressBitmap(bitmap, (int)Math.pow(2, 20)), Base64.URL_SAFE);
                }
                Objeto objeto = new Objeto();
                //TODO cambiar IdUsuario
                objeto.setIdUsuario(1);
                objeto.setNombre(etNombre.getText().toString());
                objeto.setFechaAlta("2008-7-04");
                objeto.setLugarGuardado(etLugarGuardado.getText().toString());
                objeto.setDescripcion(etDescripcion.getText().toString());
                objeto.setFoto(fotoBase64);
                RequestTask subirFoto = new RequestTask();
                try {
                    JSONObject json = new JSONObject();
                    json.put("nombre", "foto");
                    json.put("foto", fotoBase64);
                    subirFoto.execute("http://" + MainActivity.HOST + "/api/foto.php", "POST", objeto.toString());

                } catch (Exception e) {
                    Log.e("MainActivity", "Error al crear el json: " + e.toString());

                }


            }
        });

    }
    public static byte[] compressBitmap(Bitmap bitmap, int maxSizeBytes){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int currSize;
        int currQuality = 50;

        do {
            bitmap.compress(Bitmap.CompressFormat.JPEG, currQuality, stream);
            currSize = stream.toByteArray().length;
            // limit quality by 5 percent every time
            currQuality -= 5;

        } while (currSize >= maxSizeBytes && currQuality>=5);

        return stream.toByteArray();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");

            bitmap = BitmapFactory.decodeFile(fotoPath);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            ivObjeto.setBackground(d);
        }
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
                JSONObject respuesta = new JSONObject(s);
                String mensaje = respuesta.getString("mensaje");
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx:: " + mensaje);
                if(mensaje.equals("OK")) {
                    Toast.makeText(getApplicationContext(), getText(R.string.okObjeto) , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), getText(R.string.koObjeto) , Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public String sendGet(String surl) {
            try {
                URL url = new URL(surl);
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Log.d("MainActivity", "Session: " + MainActivity.session);

                con.addRequestProperty("Cookie", MainActivity.session);
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
                    List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                    for (HttpCookie cookie : cookies) {
                        if (cookie.getName().equals("PHPSESSID")) {
                            MainActivity.session = cookie.toString();

                        }

                    }

                    return response.toString();
                } else {
                    Log.e("MainActivity", "El metodo get no ha funcionado: " + responseCode);
                }

            } catch (Exception e) {
                Log.e("MainActivity", "Error al hacer el get: " + e.toString());
            }
            return null;
        }
    }
    public String sendPost(String surl, String jsonData) {
        try {
            URL url = new URL(surl);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + jsonData);
            //Creamos la connexión
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(10000);
            //Ponemos cabeceras http para indicar que mandamos un json
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Length", String.valueOf(jsonData.getBytes().length));

            //Especificamos el metodo http (POST/GET/PUT/DELETE/HEAD/OPTIONS)
            con.addRequestProperty("Cookie", MainActivity.session);
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

                List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                for (HttpCookie cookie : cookies) {
                    if (cookie.getName().equals("PHPSESSID")) {
                        MainActivity.session = cookie.toString();

                    }

                }


                Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx MainActivity", "Respuesta: " + response.toString());
                return response.toString();


            }

        } catch (Exception e) {
            Log.e("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx MainActivity", "Error: " + e.toString());
        }

        return null;

    }

}