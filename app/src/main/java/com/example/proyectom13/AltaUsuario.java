package com.example.proyectom13;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectom13.POJOS.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class AltaUsuario extends AppCompatActivity {
    private RadioGroup grupoRadios;
    private RadioButton rbEmpresa;
    private RadioButton rbparticular;
    private RadioButton radioButtonSeleccionado;
    private String nombreApellidos;
    private String nombreEmpresa;
    private String nombreUsuario=null;
    private Button btConfrimar;
    private Usuario usuario = new Usuario();
    private boolean nombreUsuarioExiste;
    private boolean emailExiste;

    EditText etNombre;
    EditText etNombreUsuario;
    EditText etPonerPassword;
    EditText etPonerPassword2;
    EditText etEmail;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuario);

        grupoRadios = findViewById(R.id.grupo_radiobuttons);
        rbparticular = findViewById(R.id.rbParticula);
        rbEmpresa = findViewById(R.id.rbEmpresa);
        etNombre = findViewById(R.id.etNombre);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etPonerPassword = findViewById(R.id.etPonerPassword);
        etPonerPassword2 = findViewById(R.id.etPonerPassword2);
        etEmail = findViewById(R.id.etEmail);

        btConfrimar = findViewById(R.id.btConfrimar);

        nombreApellidos = getString(R.string.nombre_y_apellidos);
        nombreEmpresa = getString(R.string.nombre_empresa);


        rbparticular.setChecked(true);

        //Cambio de valores del formulario según tipo de usuario
        grupoRadios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                radioButtonSeleccionado = findViewById(checkedId);
                if (radioButtonSeleccionado == rbEmpresa) {
                    etNombre.setHint(nombreEmpresa);

                } else {
                    etNombre.setHint(nombreApellidos);
                }
            }
        });



        btConfrimar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos EditText
                String nombre = etNombre.getText().toString().trim();
                String nombreUsuario = etNombreUsuario.getText().toString().trim();
                String password = etPonerPassword.getText().toString().trim();
                String password2 = etPonerPassword2.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                //contraseña tenga al menos una letra mayúscula, una letra minúscula, un número y una longitud mínima de 8 caracteres
                String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
                //Patrón para el email
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                // Compilar las expresiones regulares en un patrón
                Pattern pattern = Pattern.compile(passwordPattern);
                Pattern patternEmail = Pattern.compile(emailPattern);
                nombreUsuario=etNombreUsuario.getText().toString();
                email=etEmail.getText().toString();
                nombreUsuarioExiste=false;
                emailExiste=false;
                if (nombreUsuarioExiste==true) {
                    String mensaje = getString(R.string.exiteUsuario);
                    etNombreUsuario.setError(mensaje);
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                    etNombreUsuario.requestFocus();
                    return;
                }
                if (emailExiste==true) {
                    String mensaje = getString(R.string.existe_email);
                    etEmail.setError(mensaje);
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                    return;
                }

                // Verificar si alguno de los campos está vacío
                if (nombre.isEmpty() || nombreUsuario.isEmpty() || password.isEmpty() || password2.isEmpty() || email.isEmpty()) {
                    // Mostrar mensaje de error utilizando Toast
                    String mensaje= getString(R.string.completeCampos);
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                //Verificar patrón correcto de password
                }else if(!password.equals(password2)){
                    String mensaje= getString(R.string.comprobarContra);
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                    etPonerPassword.requestFocus();
                //Verificar si las dos contraseñas son iguales
                }else if(!pattern.matcher(password).matches()){
                    etPonerPassword.setError(getString(R.string.formatoContraseña));
                    etPonerPassword.requestFocus();
                //Verificar patrón correcto de email
                }else if(!patternEmail.matcher(email).matches()){
                    etEmail.setError(getString(R.string.formatoEmail));
                    etEmail.requestFocus();


                }else{
                    @SuppressLint("StaticFieldLeak") RequestTask registrar = new RequestTask() {
                        @Override
                        protected void onPostExecute(String response) {


                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String mensaje = jsonResponse.getString("mensaje");
                                if (mensaje.equals("OK")) {
                                    nombreUsuarioExiste=false;
                                    emailExiste=false;
                                    // he cambiado esta linea para que el user tenga que acceder con sus datos para poder obtener el valor de la variable idusuarios
                                    Toast.makeText(getApplicationContext(), R.string.altaUsuarioOK, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    // Limpiar los campos de texto después de agregar usuario
                                    etNombre.setText("");
                                    etNombreUsuario.setText("");
                                    etPonerPassword.setText("");
                                    etPonerPassword2.setText("");
                                    etEmail.setText("");
                                } else if (mensaje.equals("KO")){
                                    nombreUsuarioExiste=true;
                                    mensaje= getString(R.string.exiteUsuario);
                                    etNombreUsuario.setError(mensaje);
                                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();


                                } else if (mensaje.equals("KOemail")){
                                    emailExiste=true;
                                    mensaje=getString(R.string.existe_email);
                                    etEmail.setError(mensaje);
                                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    usuario.setNombre_empresa(etNombre.getText().toString());
                    usuario.setNombreUsuario(nombreUsuario);
                    usuario.setPassword(etPonerPassword.getText().toString());
                    usuario.setEmail(etEmail.getText().toString());


                    registrar.execute("https://" + MainActivity.HOST + "/api/insert.php", "POST", usuario.toString());

                }

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

            Log.d("AltaUsuario", resultado);
            return resultado;
        }

        public String sendGet(String surl) {
            try {
                URL url = new URL(surl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                Log.d("AltaUsuario", "GET Response code: " + responseCode);
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
                    Log.e("AltaUsuario", "El metodo get no ha funcionado: " + responseCode);
                }

            } catch (Exception e) {
                Log.e("AltaUsuario", "Error al hacer el get: " + e.toString());
            }
            return null;
        }
    }

    public String sendPost(String surl, String jsonData) {
        try {
            URL url = new URL(surl);
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + surl);

            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + jsonData);
            //Creamos la connexión
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //Ponemos cabeceras http para indicar que mandamos un json
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Length", String.valueOf(jsonData.getBytes().length));
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

                Log.d("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx AltaUsuario", "Respuesta: " + response.toString());
                return response.toString();

            }

        } catch (Exception e) {
            Log.e("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx AltaUsuario", "Error: " + e.toString());
        }

        return null;

    }
}