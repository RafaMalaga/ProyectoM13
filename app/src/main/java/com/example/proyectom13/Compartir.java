package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    ArrayList<String> itemSeleccionados;

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice, resultados);
        lstResultados.setAdapter(adapter);

        
        int selectedItem = 0;
        lstResultados.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> itemSeleccionados = new ArrayList<>();

                // Agregar los elementos seleccionados al ArrayList
                SparseBooleanArray checkedItems = lstResultados.getCheckedItemPositions();
                for (int i = 0; i < checkedItems.size(); i++) {
                    if (checkedItems.valueAt(i)) {
                        itemSeleccionados.add(resultados.get(checkedItems.keyAt(i)));

                    }
                    btnCompartir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            share(itemSeleccionados);
                        }
                    });
                }


            }



        });


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

    private void share(ArrayList itemSeleccionados) {
        String subject = "Asunto del correo";
        String encabezado = "Este es el encabezado del mensaje que se comparte:\n\n";
        String mensaje = encabezado + TextUtils.join("\n", itemSeleccionados);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);
        // Para compartir mediante email, puedes agregar el destinatario en el Intent
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"destinatario@example.com"});
        startActivity(Intent.createChooser(intent, "Compartir con"));
    }
}

