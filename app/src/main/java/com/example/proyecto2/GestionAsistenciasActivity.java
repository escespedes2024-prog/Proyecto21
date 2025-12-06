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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GestionAsistenciasActivity extends AppCompatActivity implements AsistenciaAdapter.OnAsistenciaClickListener {

    private DBUser dbUser;
    private int idIglesia = 1;
    
    private Spinner spinnerCursoAsistencia, spinnerDocenteAsistencia;
    private TextInputEditText asistenciaFechaInput;
    private RecyclerView recyclerViewEstudiantesAsistencia, recyclerViewAsistencias;
    private Button btnGuardarAsistencia;
    private List<Asistencia> listaAsistencias;
    private List<Curso> listaCursos;
    private List<Docente> listaDocentes;
    private List<Estudiante> estudiantesCursoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_asistencias);

        dbUser = new DBUser(this);
        
        inicializarViews();
        configurarListeners();
        cargarDatos();
    }

    private void inicializarViews() {
        spinnerCursoAsistencia = findViewById(R.id.spinnerCursoAsistencia);
        spinnerDocenteAsistencia = findViewById(R.id.spinnerDocenteAsistencia);
        asistenciaFechaInput = findViewById(R.id.asistenciaFechaInput);
        recyclerViewEstudiantesAsistencia = findViewById(R.id.recyclerViewEstudiantesAsistencia);
        recyclerViewEstudiantesAsistencia.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAsistencias = findViewById(R.id.recyclerViewAsistencias);
        recyclerViewAsistencias.setLayoutManager(new LinearLayoutManager(this));
        btnGuardarAsistencia = findViewById(R.id.btnGuardarAsistencia);
        
        listaAsistencias = new ArrayList<>();
        listaCursos = new ArrayList<>();
        listaDocentes = new ArrayList<>();
        estudiantesCursoSeleccionado = new ArrayList<>();
    }

    private void configurarListeners() {
        btnGuardarAsistencia.setOnClickListener(v -> guardarAsistencia());
        
        // Agregar formateador de fecha
        asistenciaFechaInput.addTextChangedListener(new DateTextWatcher(asistenciaFechaInput, "yyyy-MM-dd"));
        
        spinnerCursoAsistencia.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position > 0) {
                    Curso curso = listaCursos.get(position - 1);
                    cargarEstudiantesPorCurso(curso.getId());
                } else {
                    estudiantesCursoSeleccionado.clear();
                    actualizarRecyclerViewEstudiantesAsistencia();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void cargarDatos() {
        cargarCursos();
        cargarDocentes();
        cargarAsistencias();
    }

    private void cargarCursos() {
        listaCursos = dbUser.obtenerCursos(idIglesia);
        actualizarSpinnerCursos();
    }

    private void cargarDocentes() {
        listaDocentes = dbUser.obtenerDocentes(idIglesia);
        actualizarSpinnerDocentes();
    }

    private void cargarEstudiantesPorCurso(int idCurso) {
        estudiantesCursoSeleccionado = dbUser.obtenerEstudiantesPorCurso(idCurso);
        actualizarRecyclerViewEstudiantesAsistencia();
    }

    private void cargarAsistencias() {
        listaAsistencias = dbUser.obtenerAsistencias(idIglesia);
        actualizarRecyclerViewAsistencias();
    }

    private void actualizarSpinnerCursos() {
        List<String> nombresCursos = new ArrayList<>();
        nombresCursos.add("Seleccionar Curso");
        for (Curso c : listaCursos) {
            nombresCursos.add(c.getNombre());
        }
        ArrayAdapter<String> adapterCursos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCursos);
        adapterCursos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCursoAsistencia.setAdapter(adapterCursos);
    }

    private void actualizarSpinnerDocentes() {
        List<String> nombresDocentes = new ArrayList<>();
        nombresDocentes.add("Seleccionar Docente");
        for (Docente d : listaDocentes) {
            nombresDocentes.add(d.getNombreCompleto());
        }
        ArrayAdapter<String> adapterDocentes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresDocentes);
        adapterDocentes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocenteAsistencia.setAdapter(adapterDocentes);
    }

    private void guardarAsistencia() {
        int posicionCurso = spinnerCursoAsistencia.getSelectedItemPosition();
        int posicionDocente = spinnerDocenteAsistencia.getSelectedItemPosition();
        String fecha = asistenciaFechaInput.getText().toString().trim();

        if (posicionCurso == 0) {
            Toast.makeText(this, "Debe seleccionar un curso", Toast.LENGTH_SHORT).show();
            return;
        }
        if (posicionDocente == 0) {
            Toast.makeText(this, "Debe seleccionar un docente", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fecha.isEmpty()) {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fecha = formato.format(new Date());
        }
        if (estudiantesCursoSeleccionado.isEmpty()) {
            Toast.makeText(this, "El curso no tiene estudiantes", Toast.LENGTH_SHORT).show();
            return;
        }

        Curso curso = listaCursos.get(posicionCurso - 1);
        Docente docente = listaDocentes.get(posicionDocente - 1);

        // Guardar asistencia para cada estudiante
        int guardados = 0;
        for (Estudiante estudiante : estudiantesCursoSeleccionado) {
            Asistencia asistencia = new Asistencia();
            asistencia.setIdCurso(curso.getId());
            asistencia.setIdEstudiante(estudiante.getId());
            asistencia.setIdDocente(docente.getId());
            asistencia.setFecha(fecha);
            asistencia.setEstado("presente"); // Por defecto presente
            asistencia.setIdIglesia(idIglesia);
            
            if (dbUser.insertarAsistencia(asistencia)) {
                guardados++;
            }
        }

        Toast.makeText(this, "Asistencias guardadas: " + guardados + "/" + estudiantesCursoSeleccionado.size(), Toast.LENGTH_SHORT).show();
        asistenciaFechaInput.setText("");
        cargarAsistencias();
    }

    private void actualizarRecyclerViewEstudiantesAsistencia() {
        EstudianteAdapter adapter = new EstudianteAdapter(this, estudiantesCursoSeleccionado);
        recyclerViewEstudiantesAsistencia.setAdapter(adapter);
    }

    private void actualizarRecyclerViewAsistencias() {
        AsistenciaAdapter adapter = new AsistenciaAdapter(this, listaAsistencias, this);
        recyclerViewAsistencias.setAdapter(adapter);
    }

    @Override
    public void onAsistenciaClick(Asistencia asistencia) {
        // Para editar asistencia, podrías abrir un diálogo o navegar a otra actividad
        Toast.makeText(this, "Funcionalidad de edición de asistencia en desarrollo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAsistenciaDelete(Asistencia asistencia) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Asistencia")
                .setMessage("¿Estás seguro de que deseas eliminar esta asistencia?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (dbUser.eliminarAsistencia(asistencia.getId())) {
                        Toast.makeText(this, "Asistencia eliminada exitosamente", Toast.LENGTH_SHORT).show();
                        cargarAsistencias();
                    } else {
                        Toast.makeText(this, "Error al eliminar asistencia", Toast.LENGTH_SHORT).show();
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

