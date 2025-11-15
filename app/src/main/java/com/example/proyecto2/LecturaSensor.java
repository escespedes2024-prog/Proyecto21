package com.example.proyecto2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LecturaSensor {
    private int id;
    private String tipo; // "temperatura", "humedad", "luz"
    private double valor;
    private String fechaHora;
    private String unidad; // "°C", "%", "lux"
    private int idIglesia;

    public LecturaSensor() {
    }

    public LecturaSensor(String tipo, double valor, String unidad) {
        this.tipo = tipo;
        this.valor = valor;
        this.unidad = unidad;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.fechaHora = formato.format(new Date());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public int getIdIglesia() {
        return idIglesia;
    }

    public void setIdIglesia(int idIglesia) {
        this.idIglesia = idIglesia;
    }
}

