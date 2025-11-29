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
    private static final int DATABASE_VERSION = 4;
    
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
        
        // Tabla de docentes
        db.execSQL("CREATE TABLE docentes(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido TEXT," +
                "email TEXT," +
                "telefono TEXT," +
                "especialidad TEXT," +
                "id_iglesia INTEGER," +
                "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
        
        // Tabla de cursos
        db.execSQL("CREATE TABLE cursos(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "descripcion TEXT," +
                "horario TEXT," +
                "id_docente INTEGER," +
                "id_iglesia INTEGER," +
                "FOREIGN KEY(id_docente) REFERENCES docentes(id)," +
                "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
        
        // Tabla de estudiantes
        db.execSQL("CREATE TABLE estudiantes(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido TEXT," +
                "email TEXT," +
                "telefono TEXT," +
                "fecha_nacimiento TEXT," +
                "id_curso INTEGER," +
                "id_iglesia INTEGER," +
                "FOREIGN KEY(id_curso) REFERENCES cursos(id)," +
                "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
        
        // Tabla de asistencias
        db.execSQL("CREATE TABLE asistencias(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_curso INTEGER," +
                "id_estudiante INTEGER," +
                "id_docente INTEGER," +
                "fecha TEXT NOT NULL," +
                "estado TEXT," +
                "observaciones TEXT," +
                "id_iglesia INTEGER," +
                "FOREIGN KEY(id_curso) REFERENCES cursos(id)," +
                "FOREIGN KEY(id_estudiante) REFERENCES estudiantes(id)," +
                "FOREIGN KEY(id_docente) REFERENCES docentes(id)," +
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
        
        if (oldVersion < 4) {
            // Crear tablas de cursos, docentes, estudiantes y asistencias
            db.execSQL("CREATE TABLE IF NOT EXISTS docentes(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "apellido TEXT," +
                    "email TEXT," +
                    "telefono TEXT," +
                    "especialidad TEXT," +
                    "id_iglesia INTEGER," +
                    "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
            
            db.execSQL("CREATE TABLE IF NOT EXISTS cursos(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "descripcion TEXT," +
                    "horario TEXT," +
                    "id_docente INTEGER," +
                    "id_iglesia INTEGER," +
                    "FOREIGN KEY(id_docente) REFERENCES docentes(id)," +
                    "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
            
            db.execSQL("CREATE TABLE IF NOT EXISTS estudiantes(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "apellido TEXT," +
                    "email TEXT," +
                    "telefono TEXT," +
                    "fecha_nacimiento TEXT," +
                    "id_curso INTEGER," +
                    "id_iglesia INTEGER," +
                    "FOREIGN KEY(id_curso) REFERENCES cursos(id)," +
                    "FOREIGN KEY(id_iglesia) REFERENCES iglesias(id))");
            
            db.execSQL("CREATE TABLE IF NOT EXISTS asistencias(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_curso INTEGER," +
                    "id_estudiante INTEGER," +
                    "id_docente INTEGER," +
                    "fecha TEXT NOT NULL," +
                    "estado TEXT," +
                    "observaciones TEXT," +
                    "id_iglesia INTEGER," +
                    "FOREIGN KEY(id_curso) REFERENCES cursos(id)," +
                    "FOREIGN KEY(id_estudiante) REFERENCES estudiantes(id)," +
                    "FOREIGN KEY(id_docente) REFERENCES docentes(id)," +
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

        String query = "SELECT id, nombre, apellido, correo, telefono, fecha_nac, contrasena FROM usuarios";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Usuario usuario = new Usuario();
                usuario.setId(cursor.getInt(0));
                usuario.setNombre(cursor.getString(1));
                usuario.setApellido(cursor.getString(2));
                usuario.setCorreo(cursor.getString(3));
                usuario.setTelefono(cursor.getString(4));
                usuario.setFecha_nac(cursor.getString(5));
                usuario.setContrasena(cursor.getString(6));

                listaUsuarios.add(usuario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaUsuarios;
    }
    
    // ========== MÉTODOS ADICIONALES PARA ADMINISTRACIÓN DE USUARIOS ==========
    
    public Usuario obtenerUsuarioPorCorreo(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("usuarios", null, "correo=?", 
                new String[]{correo}, null, null, null);
        
        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getInt(0));
            usuario.setNombre(cursor.getString(1));
            usuario.setApellido(cursor.getString(2));
            usuario.setCorreo(cursor.getString(3));
            usuario.setContrasena(cursor.getString(4));
            usuario.setTelefono(cursor.getString(5));
            usuario.setFecha_nac(cursor.getString(6));
        }
        cursor.close();
        db.close();
        return usuario;
    }
    
    public Usuario obtenerUsuarioPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("usuarios", null, "id=?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getInt(0));
            usuario.setNombre(cursor.getString(1));
            usuario.setApellido(cursor.getString(2));
            usuario.setCorreo(cursor.getString(3));
            usuario.setContrasena(cursor.getString(4));
            usuario.setTelefono(cursor.getString(5));
            usuario.setFecha_nac(cursor.getString(6));
        }
        cursor.close();
        db.close();
        return usuario;
    }
    
    public boolean actualizarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", usuario.getNombre());
        valores.put("apellido", usuario.getApellido());
        valores.put("correo", usuario.getCorreo());
        valores.put("contrasena", usuario.getContrasena());
        valores.put("telefono", usuario.getTelefono());
        valores.put("fecha_nac", usuario.getFecha_nac());
        
        int resultado = db.update("usuarios", valores, "id=?", 
                new String[]{String.valueOf(usuario.getId())});
        db.close();
        return resultado > 0;
    }
    
    public boolean eliminarUsuario(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("usuarios", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
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
    
    // ========== MÉTODOS PARA DOCENTES ==========
    
    public boolean insertarDocente(Docente docente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", docente.getNombre());
        valores.put("apellido", docente.getApellido());
        valores.put("email", docente.getEmail());
        valores.put("telefono", docente.getTelefono());
        valores.put("especialidad", docente.getEspecialidad());
        valores.put("id_iglesia", docente.getIdIglesia());
        
        long resultado = db.insert("docentes", null, valores);
        db.close();
        return resultado != -1;
    }
    
    public List<Docente> obtenerDocentes(int idIglesia) {
        List<Docente> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("docentes", null, "id_iglesia=?", 
                new String[]{String.valueOf(idIglesia)}, null, null, "nombre ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Docente docente = new Docente();
                docente.setId(cursor.getInt(0));
                docente.setNombre(cursor.getString(1));
                docente.setApellido(cursor.getString(2));
                docente.setEmail(cursor.getString(3));
                docente.setTelefono(cursor.getString(4));
                docente.setEspecialidad(cursor.getString(5));
                docente.setIdIglesia(cursor.getInt(6));
                lista.add(docente);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public Docente obtenerDocente(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("docentes", null, "id=?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        Docente docente = null;
        if (cursor.moveToFirst()) {
            docente = new Docente();
            docente.setId(cursor.getInt(0));
            docente.setNombre(cursor.getString(1));
            docente.setApellido(cursor.getString(2));
            docente.setEmail(cursor.getString(3));
            docente.setTelefono(cursor.getString(4));
            docente.setEspecialidad(cursor.getString(5));
            docente.setIdIglesia(cursor.getInt(6));
        }
        cursor.close();
        db.close();
        return docente;
    }
    
    public boolean actualizarDocente(Docente docente) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", docente.getNombre());
        valores.put("apellido", docente.getApellido());
        valores.put("email", docente.getEmail());
        valores.put("telefono", docente.getTelefono());
        valores.put("especialidad", docente.getEspecialidad());
        
        int resultado = db.update("docentes", valores, "id=?", 
                new String[]{String.valueOf(docente.getId())});
        db.close();
        return resultado > 0;
    }
    
    public boolean eliminarDocente(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("docentes", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }
    
    // ========== MÉTODOS PARA CURSOS ==========
    
    public boolean insertarCurso(Curso curso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", curso.getNombre());
        valores.put("descripcion", curso.getDescripcion());
        valores.put("horario", curso.getHorario());
        valores.put("id_docente", curso.getIdDocente());
        valores.put("id_iglesia", curso.getIdIglesia());
        
        long resultado = db.insert("cursos", null, valores);
        db.close();
        return resultado != -1;
    }
    
    public List<Curso> obtenerCursos(int idIglesia) {
        List<Curso> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cursos", null, "id_iglesia=?", 
                new String[]{String.valueOf(idIglesia)}, null, null, "nombre ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Curso curso = new Curso();
                curso.setId(cursor.getInt(0));
                curso.setNombre(cursor.getString(1));
                curso.setDescripcion(cursor.getString(2));
                curso.setHorario(cursor.getString(3));
                curso.setIdDocente(cursor.getInt(4));
                curso.setIdIglesia(cursor.getInt(5));
                lista.add(curso);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public Curso obtenerCurso(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cursos", null, "id=?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        Curso curso = null;
        if (cursor.moveToFirst()) {
            curso = new Curso();
            curso.setId(cursor.getInt(0));
            curso.setNombre(cursor.getString(1));
            curso.setDescripcion(cursor.getString(2));
            curso.setHorario(cursor.getString(3));
            curso.setIdDocente(cursor.getInt(4));
            curso.setIdIglesia(cursor.getInt(5));
        }
        cursor.close();
        db.close();
        return curso;
    }
    
    public boolean actualizarCurso(Curso curso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", curso.getNombre());
        valores.put("descripcion", curso.getDescripcion());
        valores.put("horario", curso.getHorario());
        valores.put("id_docente", curso.getIdDocente());
        
        int resultado = db.update("cursos", valores, "id=?", 
                new String[]{String.valueOf(curso.getId())});
        db.close();
        return resultado > 0;
    }
    
    public boolean eliminarCurso(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("cursos", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }
    
    // ========== MÉTODOS PARA ESTUDIANTES ==========
    
    public boolean insertarEstudiante(Estudiante estudiante) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", estudiante.getNombre());
        valores.put("apellido", estudiante.getApellido());
        valores.put("email", estudiante.getEmail());
        valores.put("telefono", estudiante.getTelefono());
        valores.put("fecha_nacimiento", estudiante.getFechaNacimiento());
        valores.put("id_curso", estudiante.getIdCurso());
        valores.put("id_iglesia", estudiante.getIdIglesia());
        
        long resultado = db.insert("estudiantes", null, valores);
        db.close();
        return resultado != -1;
    }
    
    public List<Estudiante> obtenerEstudiantes(int idIglesia) {
        List<Estudiante> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("estudiantes", null, "id_iglesia=?", 
                new String[]{String.valueOf(idIglesia)}, null, null, "nombre ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Estudiante estudiante = new Estudiante();
                estudiante.setId(cursor.getInt(0));
                estudiante.setNombre(cursor.getString(1));
                estudiante.setApellido(cursor.getString(2));
                estudiante.setEmail(cursor.getString(3));
                estudiante.setTelefono(cursor.getString(4));
                estudiante.setFechaNacimiento(cursor.getString(5));
                estudiante.setIdCurso(cursor.getInt(6));
                estudiante.setIdIglesia(cursor.getInt(7));
                lista.add(estudiante);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public List<Estudiante> obtenerEstudiantesPorCurso(int idCurso) {
        List<Estudiante> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("estudiantes", null, "id_curso=?", 
                new String[]{String.valueOf(idCurso)}, null, null, "nombre ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Estudiante estudiante = new Estudiante();
                estudiante.setId(cursor.getInt(0));
                estudiante.setNombre(cursor.getString(1));
                estudiante.setApellido(cursor.getString(2));
                estudiante.setEmail(cursor.getString(3));
                estudiante.setTelefono(cursor.getString(4));
                estudiante.setFechaNacimiento(cursor.getString(5));
                estudiante.setIdCurso(cursor.getInt(6));
                estudiante.setIdIglesia(cursor.getInt(7));
                lista.add(estudiante);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public Estudiante obtenerEstudiante(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("estudiantes", null, "id=?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        Estudiante estudiante = null;
        if (cursor.moveToFirst()) {
            estudiante = new Estudiante();
            estudiante.setId(cursor.getInt(0));
            estudiante.setNombre(cursor.getString(1));
            estudiante.setApellido(cursor.getString(2));
            estudiante.setEmail(cursor.getString(3));
            estudiante.setTelefono(cursor.getString(4));
            estudiante.setFechaNacimiento(cursor.getString(5));
            estudiante.setIdCurso(cursor.getInt(6));
            estudiante.setIdIglesia(cursor.getInt(7));
        }
        cursor.close();
        db.close();
        return estudiante;
    }
    
    public boolean actualizarEstudiante(Estudiante estudiante) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", estudiante.getNombre());
        valores.put("apellido", estudiante.getApellido());
        valores.put("email", estudiante.getEmail());
        valores.put("telefono", estudiante.getTelefono());
        valores.put("fecha_nacimiento", estudiante.getFechaNacimiento());
        valores.put("id_curso", estudiante.getIdCurso());
        
        int resultado = db.update("estudiantes", valores, "id=?", 
                new String[]{String.valueOf(estudiante.getId())});
        db.close();
        return resultado > 0;
    }
    
    public boolean eliminarEstudiante(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("estudiantes", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }
    
    // ========== MÉTODOS PARA ASISTENCIAS ==========
    
    public boolean insertarAsistencia(Asistencia asistencia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("id_curso", asistencia.getIdCurso());
        valores.put("id_estudiante", asistencia.getIdEstudiante());
        valores.put("id_docente", asistencia.getIdDocente());
        valores.put("fecha", asistencia.getFecha());
        valores.put("estado", asistencia.getEstado());
        valores.put("observaciones", asistencia.getObservaciones());
        valores.put("id_iglesia", asistencia.getIdIglesia());
        
        long resultado = db.insert("asistencias", null, valores);
        db.close();
        return resultado != -1;
    }
    
    public List<Asistencia> obtenerAsistencias(int idIglesia) {
        List<Asistencia> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("asistencias", null, "id_iglesia=?", 
                new String[]{String.valueOf(idIglesia)}, null, null, "fecha DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Asistencia asistencia = new Asistencia();
                asistencia.setId(cursor.getInt(0));
                asistencia.setIdCurso(cursor.getInt(1));
                asistencia.setIdEstudiante(cursor.getInt(2));
                asistencia.setIdDocente(cursor.getInt(3));
                asistencia.setFecha(cursor.getString(4));
                asistencia.setEstado(cursor.getString(5));
                asistencia.setObservaciones(cursor.getString(6));
                asistencia.setIdIglesia(cursor.getInt(7));
                lista.add(asistencia);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public List<Asistencia> obtenerAsistenciasPorCurso(int idCurso) {
        List<Asistencia> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("asistencias", null, "id_curso=?", 
                new String[]{String.valueOf(idCurso)}, null, null, "fecha DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Asistencia asistencia = new Asistencia();
                asistencia.setId(cursor.getInt(0));
                asistencia.setIdCurso(cursor.getInt(1));
                asistencia.setIdEstudiante(cursor.getInt(2));
                asistencia.setIdDocente(cursor.getInt(3));
                asistencia.setFecha(cursor.getString(4));
                asistencia.setEstado(cursor.getString(5));
                asistencia.setObservaciones(cursor.getString(6));
                asistencia.setIdIglesia(cursor.getInt(7));
                lista.add(asistencia);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    
    public boolean actualizarAsistencia(Asistencia asistencia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("id_curso", asistencia.getIdCurso());
        valores.put("id_estudiante", asistencia.getIdEstudiante());
        valores.put("id_docente", asistencia.getIdDocente());
        valores.put("fecha", asistencia.getFecha());
        valores.put("estado", asistencia.getEstado());
        valores.put("observaciones", asistencia.getObservaciones());
        
        int resultado = db.update("asistencias", valores, "id=?", 
                new String[]{String.valueOf(asistencia.getId())});
        db.close();
        return resultado > 0;
    }
    
    public boolean eliminarAsistencia(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("asistencias", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return resultado > 0;
    }
}