package com.example.proyecto2;

public class Usuario {
    int id;
    String nombre;
    String apellido;
    String correo;
    String contrasena;
    String telefono;
    String fecha_nac;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFecha_nac() {
        return fecha_nac;
    }

    public void setFecha_nac(String fecha_nac) {
        this.fecha_nac = fecha_nac;
    }


    public Usuario(String nombre, String apellido, String correo, String contrasena, String telefono, String fecha_nac) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.fecha_nac = fecha_nac;
    }


    public Usuario(String nombre, String apellido, String correo, String contrasena) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = null;
        this.fecha_nac = null;
    }


    public Usuario() {
        this.nombre = "Erwin";
        this.apellido = "Cespedes";
        this.correo = "erwim53@gmail.com";
        this.contrasena = "1234";
        this.telefono = "62663432";
        this.fecha_nac = "15/06/2006";
    }

    public Usuario(Usuario U) {
        this.nombre = U.getNombre();
        this.apellido = U.getApellido();
        this.correo = U.getCorreo();
        this.contrasena = U.getContrasena();
        this.telefono = U.getTelefono();
        this.fecha_nac = U.getFecha_nac();
    }
}