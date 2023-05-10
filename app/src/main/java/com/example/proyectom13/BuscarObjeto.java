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

    public static Objeto objeto;
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

                objeto = resultados.get(position);

                startActivity(verObjeto);
            }
        });

        // Obtener los últimos 5 objetos alamcenados en  la base de datos siempre que se abre la acitvity
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
                            String fechaAlta = jsonObject.getString("fechaAlta");
                            String lugarGuardado = jsonObject.getString("lugarGuardado");

                            String foto = jsonObject.getString("foto");

                            Objeto objeto = new Objeto();
                            objeto.setIdObjeto(Integer.parseInt(idobjetos));
                            objeto.setNombre(nombre);
                            objeto.setLugarGuardado(lugarGuardado);
                            objeto.setDescripcion(descripcion);
                            objeto.setFechaAlta(fechaAlta);
                            objeto.setFoto(foto);

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
                                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxx" + jsonObject.toString());
                                    String nombre = jsonObject.getString("nombre");
                                    String descripcion = jsonObject.getString("descripcion");
                                    String idobjetos = jsonObject.getString("idobjetos");
                                    String lugarGuardado = jsonObject.getString("lugarGuardado");
                                    String fechaAlta = jsonObject.getString("fechaAlta");
                                    String foto64 = jsonObject.getString("foto");
                                    Objeto objeto = new Objeto();
                                    objeto.setIdObjeto(Integer.parseInt(idobjetos));
                                    objeto.setNombre(nombre);
                                    objeto.setLugarGuardado(lugarGuardado);
                                    objeto.setDescripcion(descripcion);
                                    objeto.setFechaAlta(fechaAlta);
                                    objeto.setFoto(foto64);
                                    resultados.add(objeto);
                                }
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        return resultados;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Objeto> resultados) {
                        super.onPostExecute(resultados);
                        adapter.clear();
                        if (resultados.isEmpty()) {  // si no encuentra ningun objeto se muestra el toast de abajo
                            Toast.makeText(BuscarObjeto.this, R.string.noEncontrar, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BuscarObjeto.this, R.string.encontrado, Toast.LENGTH_SHORT).show();
                            adapter.clear(); // vaciar los datos anteriores del adaptador
                            adapter.addAll(resultados); // añadir los nuevos datos al adaptador
                            adapter.notifyDataSetChanged(); // notificar al adaptador que los datos han cambiado

                           adapter = new ListaObjetosAdapter(getApplicationContext(), resultados);
                        }
                    }
                }.execute();
            }
        });
    }
}