package com.example.proyecto2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MiembrosActivity extends AppCompatActivity implements MiembroAdapter.OnMiembroClickListener {

    private DBUser dbUser;
    private RecyclerView recyclerView;
    private TextInputEditText nombreInput, apellidoInput, emailInput, telefonoInput;
    private TextInputEditText direccionInput, fechaNacimientoInput, fechaIngresoInput, rolInput;
    private Button agregarButton;
    private List<Miembro> listaMiembros;
    private Miembro miembroEditando;
    private boolean modoEdicion = false;
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
        
        // Agregar formateadores de fecha
        fechaNacimientoInput.addTextChangedListener(new DateTextWatcher(fechaNacimientoInput, "yyyy-MM-dd"));
        fechaIngresoInput.addTextChangedListener(new DateTextWatcher(fechaIngresoInput, "yyyy-MM-dd"));
        
        listaMiembros = new ArrayList<>();
        cargarMiembros();

        agregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoEdicion) {
                    actualizarMiembro();
                } else {
                    agregarMiembro();
                }
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
        MiembroAdapter adapter = new MiembroAdapter(this, listaMiembros, this);
        recyclerView.setAdapter(adapter);
        if (listaMiembros.isEmpty()) {
            Toast.makeText(this, "No hay miembros registrados", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarMiembro() {
        if (miembroEditando == null) {
            Toast.makeText(this, "Error: No hay miembro seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }

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
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fechaIngreso = formato.format(new Date());
        }

        if (rol.isEmpty()) {
            rol = "Miembro";
        }

        miembroEditando.setNombre(nombre);
        miembroEditando.setApellido(apellido);
        miembroEditando.setEmail(email);
        miembroEditando.setTelefono(telefono);
        miembroEditando.setDireccion(direccion);
        miembroEditando.setFechaNacimiento(fechaNacimiento);
        miembroEditando.setFechaIngreso(fechaIngreso);
        miembroEditando.setRol(rol);

        if (dbUser.actualizarMiembro(miembroEditando)) {
            Toast.makeText(this, "Miembro actualizado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarMiembros();
        } else {
            Toast.makeText(this, "Error al actualizar miembro", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMiembroClick(Miembro miembro) {
        miembroEditando = miembro;
        modoEdicion = true;
        
        nombreInput.setText(miembro.getNombre());
        apellidoInput.setText(miembro.getApellido());
        emailInput.setText(miembro.getEmail());
        telefonoInput.setText(miembro.getTelefono());
        direccionInput.setText(miembro.getDireccion());
        fechaNacimientoInput.setText(miembro.getFechaNacimiento());
        fechaIngresoInput.setText(miembro.getFechaIngreso());
        rolInput.setText(miembro.getRol());
        
        agregarButton.setText("Actualizar Miembro");
    }

    @Override
    public void onMiembroDelete(Miembro miembro) {
        String nombreCompleto = (miembro.getNombre() != null ? miembro.getNombre() : "") + 
            " " + (miembro.getApellido() != null ? miembro.getApellido() : "");
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Miembro")
                .setMessage("¿Estás seguro de que deseas eliminar a " + nombreCompleto.trim() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (dbUser.eliminarMiembro(miembro.getId())) {
                        Toast.makeText(this, "Miembro eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        if (miembroEditando != null && miembroEditando.getId() == miembro.getId()) {
                            limpiarCampos();
                        }
                        cargarMiembros();
                    } else {
                        Toast.makeText(this, "Error al eliminar miembro", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
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
        miembroEditando = null;
        modoEdicion = false;
        agregarButton.setText("Agregar Miembro");
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMiembros();
    }
}

