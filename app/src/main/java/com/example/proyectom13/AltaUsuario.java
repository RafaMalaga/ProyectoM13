package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AltaUsuario extends AppCompatActivity {
    private RadioGroup grupoRadios;
    private RadioButton rbEmpresa;
    private RadioButton rbparticular;
    private RadioButton radioButtonSeleccionado;
    private TextView tvNombre;
    private String nombreApellidos;
    private String nombreEmpresa;

    private Button btConfrimar;

    EditText etEmail;

    EditText etPonerPassword;

    EditText etApellidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuario);

        grupoRadios = findViewById(R.id.grupo_radiobuttons);
        rbparticular = findViewById(R.id.rbParticula);
        rbEmpresa = findViewById(R.id.rbEmpresa);
        tvNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etPonerPassword = findViewById(R.id.etPonerPassword);
        etEmail = findViewById(R.id.etEmail);
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

        btConfrimar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestTask registrar = new RequestTask() {
                    @Override
                    protected void onPostExecute(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String mensaje = jsonResponse.getString("mensaje");
                            if (mensaje.equals("OK")) {
                                Intent intent = new Intent(getApplicationContext(), FuncionalidadesActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Usuario usuario = new Usuario();
                usuario.setNombreUsuario(tvNombre.getText().toString());
                usuario.setEmail(etEmail.getText().toString());
                usuario.setPassword(etPonerPassword.getText().toString());

                registrar.execute("http://" + MainActivity.HOST + "/api/insert.php", "POST", usuario.toString());

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