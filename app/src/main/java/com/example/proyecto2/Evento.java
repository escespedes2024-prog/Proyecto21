package com.example.proyecto2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Evento {
    private int id;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String hora;
    private String tipo; // "Servicio", "Reunión", "Actividad", etc.
    private int idIglesia;

    public Evento() {
    }

    public Evento(String titulo, String descripcion, String fecha, String hora, String tipo) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdIglesia() {
        return idIglesia;
    }

    public void setIdIglesia(int idIglesia) {
        this.idIglesia = idIglesia;
    }
}

