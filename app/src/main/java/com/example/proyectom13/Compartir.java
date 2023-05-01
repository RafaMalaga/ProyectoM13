package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Compartir extends AppCompatActivity {



    private ListView lstResultados;
    private ArrayList<String> resultados;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir);
        EditText txtBuscar = findViewById(R.id.txtBuscarCompartir);
        Button btnBuscar = findViewById(R.id.btnBuscarCompartir);
        Button btnAtras = findViewById(R.id.btnAtrasCompartir);
        Button btnCompartir = findViewById(R.id.btEnviarCompartir);
        lstResultados = findViewById(R.id.lstResultadosCompartir);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar la actividad actual y regresar a la actividad anterior
                finish();
            }
        });

        resultados = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resultados);
        lstResultados.setAdapter(adapter);

        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                share();
            }
        });

        // Obtener los últimos 5 objetos alamcenados en  la base de datos siempre que se abre la acitvity
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String url = "http://" + MainActivity.HOST + "/api/ultimos_objetos.php?cantidad=5";
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        Log.d("RESPONSE", stringBuilder.toString());
                        JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre"); // en la vista "previa" solo obtenemos nombre y descripcion
                            String descripcion = jsonObject.getString("descripcion");
                            resultados.add(nombre + ": " + descripcion);
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }
        }.execute();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, ArrayList<String>>() {
                    @Override
                    protected ArrayList<String> doInBackground(Void... params) {
                        ArrayList<String> resultados = new ArrayList<>();
                        try {

                            String url = "http://" + MainActivity.HOST + "/api/buscar_objetos.php?nombre=" + txtBuscar.getText().toString().trim();
                            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                            connection.setRequestMethod("GET");
                            connection.connect();
                            int responseCode = connection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                InputStream inputStream =
                                        connection.getInputStream();
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                StringBuilder stringBuilder = new StringBuilder();
                                String line;
                                while ((line = bufferedReader.readLine()) != null) {
                                    stringBuilder.append(line);
                                }
                                Log.d("RESPONSE", stringBuilder.toString());
                                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String nombre = jsonObject.getString("nombre");
                                    String descripcion = jsonObject.getString("descripcion");
                                    resultados.add(nombre + ": " + descripcion);
                                }
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        return resultados;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<String> resultados) {
                        super.onPostExecute(resultados);
                        adapter.clear();
                        if (resultados.isEmpty()) {  // si no encuentra ningun objeto se muestra el toast de abajo
                            Toast.makeText(Compartir.this, R.string.noEncontrar, Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.addAll(resultados);
                        }
                    }
                }.execute();
            }
        });
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Este es mi texto para compartir.");
        startActivity(Intent.createChooser(sendIntent, "Compartir"));
    }
}

