package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity {

    public static final String HOST = "192.168.1.134";
    public static String session = "";
    EditText etPassword;
    EditText etUsuario;
    ImageView ibEntrar;

    Button btRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etUsuario = (EditText) findViewById(R.id.etUsuario);
        ibEntrar = (ImageView) findViewById(R.id.ibEntrar);
        btRegistro = (Button) findViewById(R.id.btRegistro);

        ibEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etUsuario.getText().length()  > 0 && etPassword.getText().length() > 0) {
                    RequestTask login = new RequestTask();
                    JSONObject loginData = crearJSONLogin();
                    login.execute("http://" + HOST + "/api/login.php", "POST", loginData.toString());
                    RequestTask task = new RequestTask();
                    task.execute("http://" + HOST + "/api/index.php", "GET");
                }else{
                    Toast.makeText(getApplicationContext(), getText(R.string.faltanDatosLogin), Toast.LENGTH_SHORT).show();
                }


            }
        });

        btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registro = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(registro);
            }
        });


    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... info) {
            String url = info[0];
            String metodo = info[1];
            String resultado = "";

            if(metodo.equals("GET")){
                resultado = sendGet(url);
            }else{
                String jsonData = info[2];
                resultado = sendPost(url, jsonData);
            }

            Log.d("MainActivity",  resultado);
            return resultado;
        }
    }
    public static String sendGet(String surl){
        try{
            URL url = new URL(surl);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            HttpURLConnection con =  (HttpURLConnection) url.openConnection();
            Log.d("MainActivity", "Session: " + session);

            con.addRequestProperty("Cookie", session);
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
                    if(cookie.getName().equals("PHPSESSID")) {
                        session = cookie.toString();
                        break;
                    }

                }

                return response.toString();
            } else {
                Log.e("MainActivity", "El metodo get no ha funcionado: " + responseCode);
            }

        }catch (Exception e){
            Log.e("MainActivity", "Error al hacer el get: " + e.toString());
        }
        return null;
    }

    public JSONObject crearJSONLogin(){
        JSONObject json = new JSONObject();
        try {
            json.put("usuario", etUsuario.getText());
            json.put("password", etPassword.getText());
            return json;
        } catch (Exception e) {
            Log.e("MainActivity","Error al crear el json: " + e.toString());
        }
        return null;
    }

    public static String sendPost(String surl, String jsonData)  {
        try{
            URL url = new URL(surl);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + jsonData);
            //Creamos la connexión
            HttpURLConnection con =  (HttpURLConnection) url.openConnection();
            //Ponemos cabeceras http para indicar que mandamos un json
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Length", String.valueOf(jsonData.getBytes().length));
            //Especificamos el metodo http (POST/GET/PUT/DELETE/HEAD/OPTIONS)
            con.addRequestProperty("Cookie", session);
            con.setRequestMethod("POST");
            //Especificamos que mandamos datos
            con.setDoOutput(true);
            //Enviamos la peticion, escribiendo el json
            OutputStream os = con.getOutputStream();
            byte[] input = jsonData.getBytes("utf-8");
            os.write(input, 0, input.length);
            os.close();

            //Si el servidor devuelve 200 es que ha ido bien
            if(con.getResponseCode() == 200) {
                //Leemos la respuesta del servidor
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine = null;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                for (HttpCookie cookie : cookies) {
                    if(cookie.getName().equals("PHPSESSID")) {
                        session = cookie.toString();
                        break;
                    }

                }

                Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx MainActivity", "Respuesta: " + response.toString());
                return response.toString();

            }

        }catch (Exception e){
            Log.e("MainActivity", "Error: " + e.toString());
        }

        return null;
    }




}