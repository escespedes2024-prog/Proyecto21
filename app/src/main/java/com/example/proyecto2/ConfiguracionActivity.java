package com.example.proyecto2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class ConfiguracionActivity extends AppCompatActivity {

    private DBUser dbUser;
    private EditText nombreInput, direccionInput, telefonoInput, emailInput, descripcionInput;
    private EditText latitudInput, longitudInput;
    private Button guardarButton, obtenerUbicacionButton;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private int idIglesia = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        dbUser = new DBUser(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        nombreInput = findViewById(R.id.nombreInput);
        direccionInput = findViewById(R.id.direccionInput);
        telefonoInput = findViewById(R.id.telefonoInput);
        emailInput = findViewById(R.id.emailInput);
        descripcionInput = findViewById(R.id.descripcionInput);
        latitudInput = findViewById(R.id.latitudInput);
        longitudInput = findViewById(R.id.longitudInput);
        guardarButton = findViewById(R.id.guardarButton);
        obtenerUbicacionButton = findViewById(R.id.obtenerUbicacionButton);

        cargarInformacionIglesia();

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarInformacion();
            }
        });

        obtenerUbicacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerUbicacionActual();
            }
        });

        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void cargarInformacionIglesia() {
        Iglesia iglesia = dbUser.obtenerIglesia(idIglesia);
        if (iglesia != null) {
            nombreInput.setText(iglesia.getNombre());
            direccionInput.setText(iglesia.getDireccion());
            telefonoInput.setText(iglesia.getTelefono());
            emailInput.setText(iglesia.getEmail());
            descripcionInput.setText(iglesia.getDescripcion());
            latitudInput.setText(String.valueOf(iglesia.getLatitud()));
            longitudInput.setText(String.valueOf(iglesia.getLongitud()));
        }
    }

    private void guardarInformacion() {
        String nombre = nombreInput.getText().toString().trim();
        String direccion = direccionInput.getText().toString().trim();
        String telefono = telefonoInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String descripcion = descripcionInput.getText().toString().trim();
        
        double latitud = 0;
        double longitud = 0;
        
        try {
            latitud = Double.parseDouble(latitudInput.getText().toString().trim());
            longitud = Double.parseDouble(longitudInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Latitud y longitud deben ser números válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre de la iglesia es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Iglesia iglesia = dbUser.obtenerIglesia(idIglesia);
        if (iglesia == null) {
            iglesia = new Iglesia();
            iglesia.setId(idIglesia);
        }

        iglesia.setNombre(nombre);
        iglesia.setDireccion(direccion);
        iglesia.setTelefono(telefono);
        iglesia.setEmail(email);
        iglesia.setDescripcion(descripcion);
        iglesia.setLatitud(latitud);
        iglesia.setLongitud(longitud);

        if (iglesia.getId() == 0) {
            // Insertar nueva iglesia
            if (dbUser.insertarIglesia(iglesia)) {
                Toast.makeText(this, "Información guardada exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar información", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Actualizar iglesia existente
            if (dbUser.actualizarIglesia(iglesia)) {
                Toast.makeText(this, "Información actualizada exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar información", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void obtenerUbicacionActual() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitudInput.setText(String.valueOf(location.getLatitude()));
                                longitudInput.setText(String.valueOf(location.getLongitude()));
                                Toast.makeText(ConfiguracionActivity.this, "Ubicación obtenida", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ConfiguracionActivity.this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Se necesita permiso de ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

