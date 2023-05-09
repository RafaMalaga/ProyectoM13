package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proyectom13.POJOS.Objeto;

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

public class BuscarObjeto extends AppCompatActivity {

    private ListView lstResultados;
    private ArrayList<Objeto> resultados;
    int idUsuario = MainActivity.idUsuario;
    ListaObjetosAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_objeto);
        EditText txtBuscar = findViewById(R.id.txtBuscar);
        Button btnBuscar = findViewById(R.id.btnBuscar);
        lstResultados = findViewById(R.id.lstResultados);

        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar la actividad actual y regresar a la actividad anterior
                finish();
            }
        });

        resultados = new ArrayList<Objeto>();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resultados);
         adapter = new ListaObjetosAdapter(this, resultados);
        lstResultados.setAdapter(adapter);
        lstResultados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent verObjeto = new Intent(getApplicationContext(), VerObjeto.class);
                //TODO: cambiar 1 por el id del objeto
                Objeto objeto = resultados.get(position);
                verObjeto.putExtra("idobjetos", Integer.toString(objeto.getIdObjeto()));
                startActivity(verObjeto);
            }
        });

        // Obtener los últimos 5 objetos almacenados en la BBDD ordenador siempre que se abre la acitvity
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String url = "http://" + MainActivity.HOST + "/api/ultimos_objetos.php?cantidad=5&idusuario=" + idUsuario;
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
                            String idobjetos = jsonObject.getString("idobjetos");
                            String fechaAlta = jsonObject.getString("lugarGuardado");

                            Objeto objeto = new Objeto();
                            objeto.setIdObjeto(Integer.parseInt(idobjetos));
                            objeto.setNombre(nombre);
                            objeto.setLugarGuardado(descripcion);
                            objeto.setFechaAlta(fechaAlta);

                            resultados.add(objeto);
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
                if (txtBuscar.getText().toString().isEmpty()) {
                    // Si el campo de búsqueda está vacío, llamar al AsyncTask que obtiene los últimos 5 objetos almacenados en la BBDD
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                String url = "http://" + MainActivity.HOST + "/api/ultimos_objetos.php?cantidad=5&idusuario=" + idUsuario;
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
                                        String idobjetos = jsonObject.getString("idobjetos");
                                        String fechaAlta = jsonObject.getString("lugarGuardado");

                                        Objeto objeto = new Objeto();
                                        objeto.setIdObjeto(Integer.parseInt(idobjetos));
                                        objeto.setNombre(nombre);
                                        objeto.setLugarGuardado(descripcion);
                                        objeto.setFechaAlta(fechaAlta);

                                        resultados.add(objeto);

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
                } else {
                    // Si se ha ingresado un término de búsqueda, llamar al AsyncTask correspondiente
                    new AsyncTask<Void, Void, ArrayList<Objeto>>() {
                        @Override
                        protected ArrayList<Objeto> doInBackground(Void... params) {
                            ArrayList<Objeto> resultados = new ArrayList<>();
                            try {
                                String url = "http://" + MainActivity.HOST + "/api/buscar_objetos.php?nombre=" + txtBuscar.getText().toString().trim() + "&idusuarios=" + MainActivity.idUsuario;
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
                                        String nombre = jsonObject.getString("nombre");
                                        String descripcion = jsonObject.getString("descripcion");
                                        String idobjetos = jsonObject.getString("idobjetos");
                                        String fechaAlta = jsonObject.getString("lugarGuardado");

                                        Objeto objeto = new Objeto();
                                        objeto.setIdObjeto(Integer.parseInt(idobjetos));
                                        objeto.setIdObjeto(Integer.parseInt(idobjetos));
                                        objeto.setNombre(nombre);
                                        objeto.setLugarGuardado(descripcion);
                                        objeto.setFechaAlta(fechaAlta);

                                        resultados.add(objeto);


                                    }
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            return resultados;



                        }

                        @Override
                        protected void onPostExecute(ArrayList<Objeto> objetos) {
                            super.onPostExecute(objetos);
                            resultados.clear();
                            resultados.addAll(objetos);
                            adapter.notifyDataSetChanged();
                            if (resultados.isEmpty()) {
                                Toast.makeText(BuscarObjeto.this, "No se encontraron resultados", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                }
            }
        });


    }
}