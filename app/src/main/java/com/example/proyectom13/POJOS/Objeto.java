package com.example.proyectom13.POJOS;

import org.json.JSONException;
import org.json.JSONObject;

public class Objeto {

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLugarGuardado() {
        return lugarGuardado;
    }

    public void setLugarGuardado(String lugarGuardado) {
        this.lugarGuardado = lugarGuardado;
    }

    String nombre;
    String descripcion;
    String fechaAlta;
    String foto;
    int idUsuario;
    String lugarGuardado;

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nombre", nombre);
            jsonObject.put("descripcion", descripcion);
            jsonObject.put("fechaAlta", fechaAlta);
            jsonObject.put("foto", foto);
            jsonObject.put("idUsuario", idUsuario);
            jsonObject.put("lugarGuardado", lugarGuardado);

            return jsonObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
