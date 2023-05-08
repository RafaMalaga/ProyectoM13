package com.example.proyectom13;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Compartir extends AppCompatActivity {


    private ListView lstResultados;
    private ListView lstResultadosObj;
    private MyResult result = new MyResult();
    private ArrayList<String> resultados= new ArrayList<>();
    private ArrayList<String> resultadosObj= new ArrayList<>();
    private ArrayList<Bitmap> resultadosFotos= new ArrayList<>();
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_OBJ = 2;
    Button btnCompartir;
    private ArrayList itemSeleccionados = new ArrayList<>();
    private ArrayList itemSeleccionadosObj = new ArrayList<>();
    private ArrayList <Integer> itemSeleccionadosObjPosicion = new ArrayList<>();
    private ArrayList<Bitmap> resultadosFotosSeleccionadas= new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir);
        EditText txtBuscar = findViewById(R.id.txtBuscarCompartir);
        EditText txtBuscarUsuario = findViewById(R.id.etBuscarUsuario);
        Button btnBuscarUsuarios = findViewById(R.id.btnBuscarUsuario);
        Button btnBuscar = findViewById(R.id.btnBuscarCompartir);
        Button btnAtras = findViewById(R.id.btnAtrasCompartir);
        btnCompartir = findViewById(R.id.btCompartirEnviar);

        lstResultados = findViewById(R.id.lstResultadosCompartir);
        lstResultadosObj = findViewById(R.id.lstResultadosCompartirObj);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(itemSeleccionados,itemSeleccionadosObj, resultadosFotosSeleccionadas);
            }
        });


        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar la actividad actual y regresar a la actividad anterior
                finish();
            }
        });


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, MyResult>() {
                    @Override
                    protected MyResult doInBackground(Void... params) {

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
                                    String lugarGuardado = jsonObject.getString("lugarGuardado");
                                    String fotobaseString64 = jsonObject.getString("foto");
                                    //Convertir string de foto a Bitmap
                                    byte[] fotoBytesBase64 = Base64.decode(fotobaseString64, Base64.URL_SAFE);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytesBase64,0,fotoBytesBase64.length);



                                    resultadosObj.add(nombre + ": \n" + descripcion + "\n" + lugarGuardado);
                                    resultadosFotos.add(bitmap);



                                }

                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        return new MyResult(resultadosObj, resultadosFotos);
                    }

                    @Override
                    protected void onPostExecute(MyResult result) {
                        super.onPostExecute(result);
                        resultadosObj= result.getList1();
                        resultadosFotos= result.getList2();


                        if (resultadosObj.isEmpty()) {  // si no encuentra ningun objeto se muestra el toast de abajo
                            Toast.makeText(Compartir.this, R.string.noEncontrar, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), SeleccionarDeLista.class);
                            intent.putExtra("arrayL", resultadosObj);
                            startActivityForResult(intent, REQUEST_CODE_OBJ);


                        }
                    }
                }.execute();
            }
        });
        btnBuscarUsuarios.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, ArrayList<String>>() {
                    @Override
                    protected ArrayList<String> doInBackground(Void... params) {
                        ArrayList<String> resultados = new ArrayList<>();
                        try {

                            String url = "http://" + MainActivity.HOST + "/api/buscar_usuarios.php?nombreUsuario=" + txtBuscarUsuario.getText().toString().trim();
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
                                    String nombre = jsonObject.getString("nombreUsuario");
                                    String email = jsonObject.getString("email");
                                    resultados.add(nombre + ": " + email);

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

                        if (resultados.isEmpty()) {  // si no encuentra ningun objeto se muestra el toast de abajo
                            Toast.makeText(Compartir.this, R.string.noEncontrar, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), SeleccionarUsuario.class);
                            intent.putExtra("arrayL", resultados);
                            startActivityForResult(intent, REQUEST_CODE);


                        }
                    }
                }.execute();
            }
        });


    }


    private void share(ArrayList itemSeleccionados, ArrayList itemSeleccionadosObj, ArrayList<Bitmap> resultadosFotosSeleccionadas) {
        ArrayList correos= new ArrayList();
        ArrayList nombresList= new ArrayList();
        ArrayList<Uri> uris = new ArrayList<>();
        Uri uri = null;


        for (int i=0; i<resultadosFotosSeleccionadas.size();i++) {
            Bitmap bitmap = resultadosFotosSeleccionadas.get(i);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);
            uri = Uri.parse(path);

            
            }
        for (Bitmap bitmap1 : resultadosFotosSeleccionadas) {
            uris.add(uri);

        }

        // Recorrer la lista original y extraer los correos electrónicos
        for (int i=0; i<itemSeleccionados.size(); i++) {
            String itemSeleccionadosString= (String) itemSeleccionados.get(i);
            String[] partes = itemSeleccionadosString.split(": "); // Separar nombre y correo
            String nombres = partes[0];
            String correo = partes[1]; // El segundo elemento es el correo

            correos.add(correo); // Agregar el correo a la nueva lista
            nombresList.add(nombres);
        }



        // Crear el texto a compartir
        String subject = getString(R.string.quiero_compartir);
        String encabezado = getString(R.string.hola) + TextUtils.join(", ", nombresList) + ":\n\n"+  getString(R.string.he_guardao) + "\n\n";
        String mensaje = encabezado + TextUtils.join("\n", itemSeleccionadosObj);
        String destinatarios = TextUtils.join(",", correos);

        // Crear el Intent con el tipo de datos "image/png"
        Intent intent = new Intent(Intent.ACTION_SEND);
 ;
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        intent.setType("image/*");

        // Agregar el texto como extra en el Intent
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {destinatarios});

        // Mostrar el ShareSheet
        startActivity(Intent.createChooser(intent, "Compartir imagen y texto"));



    }
    // Recuperamos el resultado de ActivityB
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE && resultCode == SeleccionarUsuario.RESULT_OK){
            // Recuperamos los datos enviados de vuelta desde ActivityB
            itemSeleccionados = data.getParcelableArrayListExtra("elemSeleccionados");



            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, itemSeleccionados);
            lstResultados.setAdapter(adapter);

        }

        if (requestCode == REQUEST_CODE_OBJ && resultCode == SeleccionarDeLista.RESULT_OK){
            // Recuperamos los datos enviados de vuelta desde ActivityB
            itemSeleccionadosObj = data.getParcelableArrayListExtra("elemSeleccionadosObj");
            itemSeleccionadosObjPosicion = data.getIntegerArrayListExtra("elemSeleccionadosPosicion");


            for (int i=0; i<itemSeleccionadosObjPosicion.size();i++) {

                int posicionOriginal =  itemSeleccionadosObjPosicion.get(i);


                resultadosFotosSeleccionadas.add(resultadosFotos.get(posicionOriginal));
            }


            ArrayAdapter<String> adapterObj = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, itemSeleccionadosObj);
            lstResultadosObj.setAdapter(adapterObj);
        }




    }

    private static class MyResult {
        private ArrayList<String> list1;
        private ArrayList<Bitmap> list2;
        public MyResult() {
        }

        public MyResult(ArrayList<String> list1, ArrayList<Bitmap> list2) {
            this.list1 = list1;
            this.list2 = list2;
        }

        public ArrayList<String> getList1() {
            return list1;
        }

        public ArrayList<Bitmap> getList2() {
            return list2;
        }
    }




}


