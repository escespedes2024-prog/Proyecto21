package com.example.proyecto2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GestionEstudiantesActivity extends AppCompatActivity implements EstudianteAdapter.OnEstudianteClickListener {

    private DBUser dbUser;
    private int idIglesia = 1;
    
    private TextInputEditText estudianteNombreInput, estudianteApellidoInput, estudianteEmailInput;
    private TextInputEditText estudianteTelefonoInput, estudianteFechaNacimientoInput;
    private Spinner spinnerCursoEstudiante;
    private Button btnGuardarEstudiante;
    private RecyclerView recyclerViewEstudiantes;
    private List<Estudiante> listaEstudiantes;
    private List<Curso> listaCursos;
    private Estudiante estudianteSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_estudiantes);

        dbUser = new DBUser(this);
        
        inicializarViews();
        configurarListeners();
        cargarDatos();
    }

    private void inicializarViews() {
        estudianteNombreInput = findViewById(R.id.estudianteNombreInput);
        estudianteApellidoInput = findViewById(R.id.estudianteApellidoInput);
        estudianteEmailInput = findViewById(R.id.estudianteEmailInput);
        estudianteTelefonoInput = findViewById(R.id.estudianteTelefonoInput);
        estudianteFechaNacimientoInput = findViewById(R.id.estudianteFechaNacimientoInput);
        spinnerCursoEstudiante = findViewById(R.id.spinnerCursoEstudiante);
        btnGuardarEstudiante = findViewById(R.id.btnGuardarEstudiante);
        recyclerViewEstudiantes = findViewById(R.id.recyclerViewEstudiantes);
        recyclerViewEstudiantes.setLayoutManager(new LinearLayoutManager(this));
        
        listaEstudiantes = new ArrayList<>();
        listaCursos = new ArrayList<>();
    }

    private void configurarListeners() {
        btnGuardarEstudiante.setOnClickListener(v -> guardarEstudiante());
        
        // Agregar formateador de fecha
        estudianteFechaNacimientoInput.addTextChangedListener(new DateTextWatcher(estudianteFechaNacimientoInput, "yyyy-MM-dd"));
    }

    private void cargarDatos() {
        cargarCursos();
        cargarEstudiantes();
    }

    private void cargarCursos() {
        listaCursos = dbUser.obtenerCursos(idIglesia);
        actualizarSpinnerCursos();
    }

    private void cargarEstudiantes() {
        listaEstudiantes = dbUser.obtenerEstudiantes(idIglesia);
        actualizarRecyclerViewEstudiantes();
    }

    private void actualizarSpinnerCursos() {
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccionar Curso");
        for (Curso c : listaCursos) {
            nombres.add(c.getNombre());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCursoEstudiante.setAdapter(adapter);
    }

    private void guardarEstudiante() {
        String nombre = estudianteNombreInput.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicionCurso = spinnerCursoEstudiante.getSelectedItemPosition();
        if (posicionCurso == 0) {
            Toast.makeText(this, "Debe seleccionar un curso", Toast.LENGTH_SHORT).show();
            return;
        }

        Estudiante estudiante = estudianteSeleccionado != null ? estudianteSeleccionado : new Estudiante();
        estudiante.setNombre(nombre);
        estudiante.setApellido(estudianteApellidoInput.getText().toString().trim());
        estudiante.setEmail(estudianteEmailInput.getText().toString().trim());
        estudiante.setTelefono(estudianteTelefonoInput.getText().toString().trim());
        estudiante.setFechaNacimiento(estudianteFechaNacimientoInput.getText().toString().trim());
        estudiante.setIdCurso(listaCursos.get(posicionCurso - 1).getId());
        estudiante.setIdIglesia(idIglesia);

        boolean exito;
        if (estudianteSeleccionado != null) {
            exito = dbUser.actualizarEstudiante(estudiante);
            Toast.makeText(this, exito ? "Estudiante actualizado" : "Error al actualizar", Toast.LENGTH_SHORT).show();
        } else {
            exito = dbUser.insertarEstudiante(estudiante);
            Toast.makeText(this, exito ? "Estudiante guardado" : "Error al guardar", Toast.LENGTH_SHORT).show();
        }

        if (exito) {
            limpiarFormularioEstudiante();
            cargarEstudiantes();
        }
    }

    private void limpiarFormularioEstudiante() {
        estudianteNombreInput.setText("");
        estudianteApellidoInput.setText("");
        estudianteEmailInput.setText("");
        estudianteTelefonoInput.setText("");
        estudianteFechaNacimientoInput.setText("");
        spinnerCursoEstudiante.setSelection(0);
        estudianteSeleccionado = null;
        btnGuardarEstudiante.setText("Guardar Estudiante");
    }

    private void actualizarRecyclerViewEstudiantes() {
        EstudianteAdapter adapter = new EstudianteAdapter(this, listaEstudiantes, this);
        recyclerViewEstudiantes.setAdapter(adapter);
    }

    @Override
    public void onEstudianteClick(Estudiante estudiante) {
        estudianteSeleccionado = estudiante;
        
        estudianteNombreInput.setText(estudiante.getNombre());
        estudianteApellidoInput.setText(estudiante.getApellido());
        estudianteEmailInput.setText(estudiante.getEmail());
        estudianteTelefonoInput.setText(estudiante.getTelefono());
        estudianteFechaNacimientoInput.setText(estudiante.getFechaNacimiento());
        
        // Seleccionar curso en spinner
        for (int i = 0; i < listaCursos.size(); i++) {
            if (listaCursos.get(i).getId() == estudiante.getIdCurso()) {
                spinnerCursoEstudiante.setSelection(i + 1);
                break;
            }
        }
        
        btnGuardarEstudiante.setText("Actualizar Estudiante");
    }

    @Override
    public void onEstudianteDelete(Estudiante estudiante) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Estudiante")
                .setMessage("¿Estás seguro de que deseas eliminar a " + estudiante.getNombreCompleto() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (dbUser.eliminarEstudiante(estudiante.getId())) {
                        Toast.makeText(this, "Estudiante eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        if (estudianteSeleccionado != null && estudianteSeleccionado.getId() == estudiante.getId()) {
                            limpiarFormularioEstudiante();
                        }
                        cargarEstudiantes();
                    } else {
                        Toast.makeText(this, "Error al eliminar estudiante", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }
}

