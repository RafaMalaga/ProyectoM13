package com.example.proyectom13.POJOS;

import org.json.JSONException;
import org.json.JSONObject;

public class Usuario {


    int idusuarios;
    String nombre_empresa;
    String nombreUsuario;
    String password;
    String email;

    public Usuario(int idusuarios, String nombre_empresa, String nombreUsuario, String password, String email) {
        this.idusuarios = idusuarios;
        this.nombre_empresa = nombre_empresa;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.email = email;
    }

    public Usuario() {

    }


    public int getIdusuarios() {
        return idusuarios;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setIdusuarios(int idusuarios) {
        this.idusuarios = idusuarios;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idusuarios", idusuarios);
            jsonObject.put("nombre_empresa", nombre_empresa);
            jsonObject.put("nombreUsuario", nombreUsuario);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
            return jsonObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
