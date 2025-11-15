package com.example.proyecto2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SensoresActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_STORAGE_PERMISSION = 102;

    private DBUser dbUser;
    private ImageView imagePreview;
    private Button btnTomarFoto, btnGuardarDocumento, btnEliminarFoto;
    private EditText nombreDocumento, descripcionDocumento;
    private RecyclerView recyclerViewDocumentos;
    
    private String currentPhotoPath;
    private int idIglesia = 1;
    private List<Documento> listaDocumentos;
    private Bitmap currentBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        dbUser = new DBUser(this);
        
        imagePreview = findViewById(R.id.imagePreview);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardarDocumento = findViewById(R.id.btnGuardarDocumento);
        btnEliminarFoto = findViewById(R.id.btnEliminarFoto);
        nombreDocumento = findViewById(R.id.nombreDocumento);
        descripcionDocumento = findViewById(R.id.descripcionDocumento);
        recyclerViewDocumentos = findViewById(R.id.recyclerViewDocumentos);

        recyclerViewDocumentos.setLayoutManager(new LinearLayoutManager(this));
        listaDocumentos = new ArrayList<>();
        
        cargarDocumentos();

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPermisosYTomarFoto();
            }
        });

        btnGuardarDocumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDocumento();
            }
        });

        btnEliminarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarFotoActual();
            }
        });

        // Deshabilitar botón guardar inicialmente
        btnGuardarDocumento.setEnabled(false);
        btnEliminarFoto.setEnabled(false);
    }

    private void verificarPermisosYTomarFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
            return;
        }

        tomarFoto();
    }

    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
            }
            
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.proyecto2.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "DOCUMENTO_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Cargar la imagen desde el archivo
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                currentBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imagePreview.setImageBitmap(currentBitmap);
                imagePreview.setVisibility(View.VISIBLE);
                
                // Habilitar botones
                btnGuardarDocumento.setEnabled(true);
                btnEliminarFoto.setEnabled(true);
                
                // Sugerir nombre si está vacío
                if (nombreDocumento.getText().toString().trim().isEmpty()) {
                    String nombreSugerido = "Documento_" + 
                            new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());
                    nombreDocumento.setText(nombreSugerido);
                }
            }
        }
    }

    private void guardarDocumento() {
        String nombre = nombreDocumento.getText().toString().trim();
        
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre del documento es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPhotoPath == null || currentBitmap == null) {
            Toast.makeText(this, "Primero debe tomar una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcion = descripcionDocumento.getText().toString().trim();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaCreacion = formato.format(new Date());

        Documento documento = new Documento();
        documento.setNombre(nombre);
        documento.setRutaImagen(currentPhotoPath);
        documento.setFechaCreacion(fechaCreacion);
        documento.setDescripcion(descripcion);
        documento.setIdIglesia(idIglesia);

        if (dbUser.insertarDocumento(documento)) {
            Toast.makeText(this, "Documento guardado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarDocumentos();
        } else {
            Toast.makeText(this, "Error al guardar el documento", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarFotoActual() {
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            if (file.exists()) {
                file.delete();
            }
        }
        
        imagePreview.setVisibility(View.GONE);
        currentPhotoPath = null;
        currentBitmap = null;
        btnGuardarDocumento.setEnabled(false);
        btnEliminarFoto.setEnabled(false);
        nombreDocumento.setText("");
        descripcionDocumento.setText("");
    }

    private void cargarDocumentos() {
        listaDocumentos = dbUser.obtenerDocumentos(idIglesia);
        // Aquí podrías usar un adaptador personalizado para mostrar los documentos
        // Por ahora solo cargamos la lista
        if (listaDocumentos.isEmpty()) {
            Toast.makeText(this, "No hay documentos guardados", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        nombreDocumento.setText("");
        descripcionDocumento.setText("");
        eliminarFotoActual();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                verificarPermisosYTomarFoto();
            } else {
                Toast.makeText(this, "Se necesita permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                verificarPermisosYTomarFoto();
            } else {
                Toast.makeText(this, "Se necesita permiso de almacenamiento para guardar fotos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDocumentos();
    }
}
