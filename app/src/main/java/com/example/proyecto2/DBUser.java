package com.example.proyecto2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor; // Importación necesaria
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList; // Importación necesaria
import java.util.List; // Importación necesaria
import java.util.Locale;

public class DBUser extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    
    public DBUser(@Nullable Context context){
        super(context, "Iglesia.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla de usuarios (login)
        db.execSQL("CREATE TABLE usuarios(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT, " +
                "apellido TEXT," +
                "correo TEXT UNIQUE, " +
                "contrasena TEXT, " +
                "telefono TEXT, " +
                "fecha_nac TEXT," +
                "verificado INTEGER DEFAULT 0)");
        
        // Tabla de iglesias
        db.execSQL("CREATE TABLE iglesias(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "direccion TEXT," +
                "latitud REAL," +
                "longitud REAL," +
                "telefono TEXT," +
                "email TEXT," +
                "descripcion TEXT)");
        
        // Tabla de miembros
        db.execSQL("CREATE TABLE miembros(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido TEXT," +
                "email TEXT," +
                "telefono TEXT," +
                "direccion TEXT," +
                "fecha_nacimiento TEXT," +
                "fecha_ingreso TEXT," +
                "rol TEXT," +
                "id_iglesia INTEGER," +
                "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
        
        // Tabla de eventos
        db.execSQL("CREATE TABLE eventos(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "titulo TEXT NOT NULL," +
                "descripcion TEXT," +
                "fecha TEXT," +
                "hora TEXT," +
                "tipo TEXT," +
                "id_iglesia INTEGER," +
                "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
        
        // Tabla de documentos
        db.execSQL("CREATE TABLE documentos(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "ruta_imagen TEXT NOT NULL," +
                "fecha_creacion TEXT," +
                "descripcion TEXT," +
                "id_iglesia INTEGER," +
                "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
        
        // Insertar iglesia por defecto
        ContentValues valores = new ContentValues();
        valores.put("nombre", "Iglesia Principal");
        valores.put("direccion", "Dirección de la iglesia");
        valores.put("latitud", -17.75596); // Coordenadas por defecto
        valores.put("longitud", -63.17915);
        db.insert("iglesias", null, valores);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Crear nuevas tablas si no existen
            db.execSQL("CREATE TABLE IF NOT EXISTS iglesias(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "direccion TEXT," +
                    "latitud REAL," +
                    "longitud REAL," +
                    "telefono TEXT," +
                    "email TEXT," +
                    "descripcion TEXT)");
            
            db.execSQL("CREATE TABLE IF NOT EXISTS miembros(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "apellido TEXT," +
                    "email TEXT," +
                    "telefono TEXT," +
                    "direccion TEXT," +
                    "fecha_nacimiento TEXT," +
                    "fecha_ingreso TEXT," +
                    "rol TEXT," +
                    "id_iglesia INTEGER," +
                    "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
            
            db.execSQL("CREATE TABLE IF NOT EXISTS eventos(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "titulo TEXT NOT NULL," +
                    "descripcion TEXT," +
                    "fecha TEXT," +
                    "hora TEXT," +
                    "tipo TEXT," +
                    "id_iglesia INTEGER," +
                    "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
            
            db.execSQL("CREATE TABLE IF NOT EXISTS documentos(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "ruta_imagen TEXT NOT NULL," +
                    "fecha_creacion TEXT," +
                    "descripcion TEXT," +
                    "id_iglesia INTEGER," +
                    "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
            
            // Insertar iglesia por defecto si no existe
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM iglesias", null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getInt(0) == 0) {
                    ContentValues valores = new ContentValues();
                    valores.put("nombre", "Iglesia Principal");
                    valores.put("direccion", "Dirección de la iglesia");
                    valores.put("latitud", -17.75596);
                    valores.put("longitud", -63.17915);
                    db.insert("iglesias", null, valores);
                }
                cursor.close();
            }
        }
        
        if (oldVersion < 3) {
            // Crear tabla de documentos
            db.execSQL("CREATE TABLE IF NOT EXISTS documentos(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "ruta_imagen TEXT NOT NULL," +
                    "fecha_creacion TEXT," +
                    "descripcion TEXT," +
                    "id_iglesia INTEGER," +
                    "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
        }
    }


    public boolean insertarUsuario(String nombre, String apellido, String correo,String contrasena,String telefono,String fecha_nac,String Verificado){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("apellido", apellido);
        valores.put("correo", correo);
        valores.put("contrasena", contrasena);
        valores.put("telefono", telefono);
        valores.put("fecha_nac", fecha_nac);

        long resultado = db.insert("usuarios", null, valores);
        db.close();
        return resultado != -1;
    }

    public boolean insertarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", usuario.getNombre());
        valores.put("apellido", usuario.getApellido());
        valores.put("correo", usuario.getCorreo());
        valores.put("contrasena", usuario.getContrasena());
        if (usuario.getTelefono() != null) {
            valores.put("telefono", usuario.getTelefono());
        } else {
            valores.put("telefono", "");
        }

        if (usuario.getFecha_nac() != null) {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaFormateada = formato.format(usuario.getFecha_nac());
            valores.put("fecha_nac", fechaFormateada);
        }else{
            valores.put("fecha_nac", "");
        }
        long resultado = db.insert("usuarios", null, valores);
        db.close();
        return resultado != -1;
    }

    public List<Usuario> obtenerUsuarios() {
        List<Usuario> listaUsuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT nombre, apellido, correo, telefono, fecha_nac, contrasena FROM usuarios";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Usuario usuario = new Usuario();
                usuario.setNombre(cursor.getString(0));
                usuario.setApellido(cursor.getString(1));
                usuario.setCorreo(cursor.getString(2));
                usuario.setTelefono(cursor.getString(3));
                usuario.setFecha_nac(cursor.getString(4));
                usuario.setContrasena(cursor.getString(5));

                listaUsuarios.add(usuario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaUsuarios;
    }
    
    // ========== MÉTODOS PARA IGLESIAS ==========
    
    public boolean insertarIglesia(Iglesia iglesia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", iglesia.getNombre());
        valores.put("direccion", iglesia.getDireccion());
        valores.put("latitud", iglesia.getLatitud());
        valores.put("longitud", iglesia.getLongitud());
        valores.put("telefono", iglesia.getTelefono());
        valores.put("email", iglesia.getEmail());
        valores.put("descripcion", iglesia.getDescripcion());
        
        long resultado = db.insert("iglesias", null, valores);
        db.close();
        return resultado != -1;
    }
    
    public Iglesia obtenerIglesia(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("iglesias", null, "id=?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        Iglesia iglesia = null;
        if (cursor.moveToFirst()) {
            iglesia = new Iglesia();
            iglesia.setId(cursor.getInt(0));
            iglesia.setNombre(cursor.getString(1));
            iglesia.setDireccion(cursor.getString(2));
            iglesia.setLatitud(cursor.getDouble(3));
            iglesia.setLongitud(cursor.getDouble(4));
            iglesia.setTelefono(cursor.getString(5));
            iglesia.setEmail(cursor.getString(6));
            iglesia.setDescripcion(cursor.getString(7));
        }
        cursor.close();
        db.close();
        return iglesia;
    }
    
    public List<Iglesia> obtenerTodasLasIglesias() {
        List<Iglesia> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM iglesias", null);
        
        if (cursor.moveToFirst()) {
            do {
                Iglesia iglesia = new Iglesia();
                iglesia.setId(cursor.getInt(0));
                iglesia.setNombre(cursor.getString(1));
                iglesia.setDireccion(cursor.getString(2));
                iglesia.setLatitud(cursor.getDouble(3));
                iglesia.setLongitud(cursor.getDouble(4));
                iglesia.setTelefono(cursor.getString(5));
                iglesia.setEmail(cursor.getString(6));
                iglesia.setDescripcion(cursor.getString(7));
                lista.add(iglesia);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public boolean actualizarIglesia(Iglesia iglesia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", iglesia.getNombre());
        valores.put("direccion", iglesia.getDireccion());
        valores.put("latitud", iglesia.getLatitud());
        valores.put("longitud", iglesia.getLongitud());
        valores.put("telefono", iglesia.getTelefono());
        valores.put("email", iglesia.getEmail());
        valores.put("descripcion", iglesia.getDescripcion());
        
        int resultado = db.update("iglesias", valores, "id=?", 
                new String[]{String.valueOf(iglesia.getId())});
        db.close();
        return resultado > 0;
    }
    
    // ========== MÉTODOS PARA EVENTOS ==========
    
    public boolean insertarEvento(Evento evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("titulo", evento.getTitulo());
        valores.put("descripcion", evento.getDescripcion());
        valores.put("fecha", evento.getFecha());
        valores.put("hora", evento.getHora());
        valores.put("tipo", evento.getTipo());
        valores.put("id_iglesia", evento.getIdIglesia());
        
        long resultado = db.insert("eventos", null, valores);
        db.close();
        return resultado != -1;
    }
    
    public List<Evento> obtenerEventos(int idIglesia) {
        List<Evento> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("eventos", null, "id_iglesia=?", 
                new String[]{String.valueOf(idIglesia)}, null, null, "fecha ASC, hora ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Evento evento = new Evento();
                evento.setId(cursor.getInt(0));
                evento.setTitulo(cursor.getString(1));
                evento.setDescripcion(cursor.getString(2));
                evento.setFecha(cursor.getString(3));
                evento.setHora(cursor.getString(4));
                evento.setTipo(cursor.getString(5));
                evento.setIdIglesia(cursor.getInt(6));
                lista.add(evento);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public boolean eliminarEvento(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("eventos", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }
    
    // ========== MÉTODOS PARA MIEMBROS ==========
    
    public boolean insertarMiembro(Miembro miembro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", miembro.getNombre());
        valores.put("apellido", miembro.getApellido());
        valores.put("email", miembro.getEmail());
        valores.put("telefono", miembro.getTelefono());
        valores.put("direccion", miembro.getDireccion());
        valores.put("fecha_nacimiento", miembro.getFechaNacimiento());
        valores.put("fecha_ingreso", miembro.getFechaIngreso());
        valores.put("rol", miembro.getRol());
        valores.put("id_iglesia", miembro.getIdIglesia());
        
        long resultado = db.insert("miembros", null, valores);
        db.close();
        return resultado != -1;
    }
    
    public List<Miembro> obtenerMiembros(int idIglesia) {
        List<Miembro> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("miembros", null, "id_iglesia=?", 
                new String[]{String.valueOf(idIglesia)}, null, null, "nombre ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Miembro miembro = new Miembro();
                miembro.setId(cursor.getInt(0));
                miembro.setNombre(cursor.getString(1));
                miembro.setApellido(cursor.getString(2));
                miembro.setEmail(cursor.getString(3));
                miembro.setTelefono(cursor.getString(4));
                miembro.setDireccion(cursor.getString(5));
                miembro.setFechaNacimiento(cursor.getString(6));
                miembro.setFechaIngreso(cursor.getString(7));
                miembro.setRol(cursor.getString(8));
                miembro.setIdIglesia(cursor.getInt(9));
                lista.add(miembro);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public boolean eliminarMiembro(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("miembros", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }
    
    // ========== MÉTODOS PARA DOCUMENTOS ==========
    
    public boolean insertarDocumento(Documento documento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", documento.getNombre());
        valores.put("ruta_imagen", documento.getRutaImagen());
        valores.put("fecha_creacion", documento.getFechaCreacion());
        valores.put("descripcion", documento.getDescripcion());
        valores.put("id_iglesia", documento.getIdIglesia());
        
        long resultado = db.insert("documentos", null, valores);
        db.close();
        return resultado != -1;
    }
    
    public List<Documento> obtenerDocumentos(int idIglesia) {
        List<Documento> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("documentos", null, "id_iglesia=?", 
                new String[]{String.valueOf(idIglesia)}, null, null, "fecha_creacion DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Documento documento = new Documento();
                documento.setId(cursor.getInt(0));
                documento.setNombre(cursor.getString(1));
                documento.setRutaImagen(cursor.getString(2));
                documento.setFechaCreacion(cursor.getString(3));
                documento.setDescripcion(cursor.getString(4));
                documento.setIdIglesia(cursor.getInt(5));
                lista.add(documento);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public boolean eliminarDocumento(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("documentos", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }
    
    public Documento obtenerDocumento(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("documentos", null, "id=?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        Documento documento = null;
        if (cursor.moveToFirst()) {
            documento = new Documento();
            documento.setId(cursor.getInt(0));
            documento.setNombre(cursor.getString(1));
            documento.setRutaImagen(cursor.getString(2));
            documento.setFechaCreacion(cursor.getString(3));
            documento.setDescripcion(cursor.getString(4));
            documento.setIdIglesia(cursor.getInt(5));
        }
        cursor.close();
        db.close();
        return documento;
    }
}