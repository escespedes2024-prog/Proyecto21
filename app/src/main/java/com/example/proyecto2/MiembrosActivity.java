package com.example.proyecto2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MiembrosActivity extends AppCompatActivity {

    private DBUser dbUser;
    private RecyclerView recyclerView;
    private EditText nombreInput, apellidoInput, emailInput, telefonoInput;
    private EditText direccionInput, fechaNacimientoInput, fechaIngresoInput, rolInput;
    private Button agregarButton;
    private List<Miembro> listaMiembros;
    private int idIglesia = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miembros);

        dbUser = new DBUser(this);
        
        nombreInput = findViewById(R.id.nombreInput);
        apellidoInput = findViewById(R.id.apellidoInput);
        emailInput = findViewById(R.id.emailInput);
        telefonoInput = findViewById(R.id.telefonoInput);
        direccionInput = findViewById(R.id.direccionInput);
        fechaNacimientoInput = findViewById(R.id.fechaNacimientoInput);
        fechaIngresoInput = findViewById(R.id.fechaIngresoInput);
        rolInput = findViewById(R.id.rolInput);
        agregarButton = findViewById(R.id.agregarButton);
        recyclerView = findViewById(R.id.recyclerViewMiembros);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        listaMiembros = new ArrayList<>();
        cargarMiembros();

        agregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarMiembro();
            }
        });
    }

    private void agregarMiembro() {
        String nombre = nombreInput.getText().toString().trim();
        String apellido = apellidoInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String telefono = telefonoInput.getText().toString().trim();
        String direccion = direccionInput.getText().toString().trim();
        String fechaNacimiento = fechaNacimientoInput.getText().toString().trim();
        String fechaIngreso = fechaIngresoInput.getText().toString().trim();
        String rol = rolInput.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaIngreso.isEmpty()) {
            // Usar fecha actual si no se especifica
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaIngreso = formato.format(new Date());
        }

        if (rol.isEmpty()) {
            rol = "Miembro";
        }

        Miembro miembro = new Miembro();
        miembro.setNombre(nombre);
        miembro.setApellido(apellido);
        miembro.setEmail(email);
        miembro.setTelefono(telefono);
        miembro.setDireccion(direccion);
        miembro.setFechaNacimiento(fechaNacimiento);
        miembro.setFechaIngreso(fechaIngreso);
        miembro.setRol(rol);
        miembro.setIdIglesia(idIglesia);

        if (dbUser.insertarMiembro(miembro)) {
            Toast.makeText(this, "Miembro agregado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarMiembros();
        } else {
            Toast.makeText(this, "Error al agregar miembro", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        nombreInput.setText("");
        apellidoInput.setText("");
        emailInput.setText("");
        telefonoInput.setText("");
        direccionInput.setText("");
        fechaNacimientoInput.setText("");
        fechaIngresoInput.setText("");
        rolInput.setText("");
    }

    private void cargarMiembros() {
        listaMiembros = dbUser.obtenerMiembros(idIglesia);
        if (listaMiembros.isEmpty()) {
            Toast.makeText(this, "No hay miembros registrados", Toast.LENGTH_SHORT).show();
        }
        // Aquí podrías usar un adaptador personalizado para mostrar los miembros
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMiembros();
    }
}

