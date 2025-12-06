package com.example.proyecto2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GestionDocentesActivity extends AppCompatActivity implements DocenteAdapter.OnDocenteClickListener {

    private DBUser dbUser;
    private int idIglesia = 1;
    
    private TextInputEditText docenteNombreInput, docenteApellidoInput, docenteEmailInput;
    private TextInputEditText docenteTelefonoInput, docenteEspecialidadInput;
    private Button btnGuardarDocente;
    private RecyclerView recyclerViewDocentes;
    private List<Docente> listaDocentes;
    private Docente docenteSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_docentes);

        dbUser = new DBUser(this);
        
        inicializarViews();
        configurarListeners();
        cargarDocentes();
    }

    private void inicializarViews() {
        docenteNombreInput = findViewById(R.id.docenteNombreInput);
        docenteApellidoInput = findViewById(R.id.docenteApellidoInput);
        docenteEmailInput = findViewById(R.id.docenteEmailInput);
        docenteTelefonoInput = findViewById(R.id.docenteTelefonoInput);
        docenteEspecialidadInput = findViewById(R.id.docenteEspecialidadInput);
        btnGuardarDocente = findViewById(R.id.btnGuardarDocente);
        recyclerViewDocentes = findViewById(R.id.recyclerViewDocentes);
        recyclerViewDocentes.setLayoutManager(new LinearLayoutManager(this));
        
        listaDocentes = new ArrayList<>();
    }

    private void configurarListeners() {
        btnGuardarDocente.setOnClickListener(v -> guardarDocente());
    }

    private void cargarDocentes() {
        listaDocentes = dbUser.obtenerDocentes(idIglesia);
        actualizarRecyclerViewDocentes();
    }

    private void guardarDocente() {
        String nombre = docenteNombreInput.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Docente docente = docenteSeleccionado != null ? docenteSeleccionado : new Docente();
        docente.setNombre(nombre);
        docente.setApellido(docenteApellidoInput.getText().toString().trim());
        docente.setEmail(docenteEmailInput.getText().toString().trim());
        docente.setTelefono(docenteTelefonoInput.getText().toString().trim());
        docente.setEspecialidad(docenteEspecialidadInput.getText().toString().trim());
        docente.setIdIglesia(idIglesia);

        boolean exito;
        if (docenteSeleccionado != null) {
            exito = dbUser.actualizarDocente(docente);
            Toast.makeText(this, exito ? "Docente actualizado" : "Error al actualizar", Toast.LENGTH_SHORT).show();
        } else {
            exito = dbUser.insertarDocente(docente);
            Toast.makeText(this, exito ? "Docente guardado" : "Error al guardar", Toast.LENGTH_SHORT).show();
        }

        if (exito) {
            limpiarFormularioDocente();
            cargarDocentes();
        }
    }

    private void limpiarFormularioDocente() {
        docenteNombreInput.setText("");
        docenteApellidoInput.setText("");
        docenteEmailInput.setText("");
        docenteTelefonoInput.setText("");
        docenteEspecialidadInput.setText("");
        docenteSeleccionado = null;
        btnGuardarDocente.setText("Guardar Docente");
    }

    private void actualizarRecyclerViewDocentes() {
        DocenteAdapter adapter = new DocenteAdapter(this, listaDocentes, this);
        recyclerViewDocentes.setAdapter(adapter);
    }

    @Override
    public void onDocenteClick(Docente docente) {
        docenteSeleccionado = docente;
        
        docenteNombreInput.setText(docente.getNombre());
        docenteApellidoInput.setText(docente.getApellido());
        docenteEmailInput.setText(docente.getEmail());
        docenteTelefonoInput.setText(docente.getTelefono());
        docenteEspecialidadInput.setText(docente.getEspecialidad());
        
        btnGuardarDocente.setText("Actualizar Docente");
    }

    @Override
    public void onDocenteDelete(Docente docente) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Docente")
                .setMessage("¿Estás seguro de que deseas eliminar a " + docente.getNombreCompleto() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (dbUser.eliminarDocente(docente.getId())) {
                        Toast.makeText(this, "Docente eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        if (docenteSeleccionado != null && docenteSeleccionado.getId() == docente.getId()) {
                            limpiarFormularioDocente();
                        }
                        cargarDocentes();
                    } else {
                        Toast.makeText(this, "Error al eliminar docente", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDocentes();
    }
}

