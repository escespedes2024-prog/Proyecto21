package com.example.proyecto2;

import android.os.Bundle;
import android.view.View;
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

public class CursosActivity extends AppCompatActivity {

    private DBUser dbUser;
    private int idIglesia = 1;
    
    // Views
    private Button btnCursos, btnDocentes, btnEstudiantes, btnAsistencias;
    private View seccionCursos, seccionDocentes, seccionEstudiantes, seccionAsistencias;
    
    // Cursos
    private TextInputEditText cursoNombreInput, cursoDescripcionInput, cursoHorarioInput;
    private Spinner spinnerDocenteCurso;
    private Button btnGuardarCurso;
    private RecyclerView recyclerViewCursos;
    private List<Curso> listaCursos;
    private Curso cursoSeleccionado = null;
    
    // Docentes
    private TextInputEditText docenteNombreInput, docenteApellidoInput, docenteEmailInput;
    private TextInputEditText docenteTelefonoInput, docenteEspecialidadInput;
    private Button btnGuardarDocente;
    private RecyclerView recyclerViewDocentes;
    private List<Docente> listaDocentes;
    private Docente docenteSeleccionado = null;
    
    // Estudiantes
    private TextInputEditText estudianteNombreInput, estudianteApellidoInput, estudianteEmailInput;
    private TextInputEditText estudianteTelefonoInput, estudianteFechaNacimientoInput;
    private Spinner spinnerCursoEstudiante;
    private Button btnGuardarEstudiante;
    private RecyclerView recyclerViewEstudiantes;
    private List<Estudiante> listaEstudiantes;
    private Estudiante estudianteSeleccionado = null;
    
    // Asistencias
    private Spinner spinnerCursoAsistencia, spinnerDocenteAsistencia;
    private TextInputEditText asistenciaFechaInput;
    private RecyclerView recyclerViewEstudiantesAsistencia, recyclerViewAsistencias;
    private Button btnGuardarAsistencia;
    private List<Asistencia> listaAsistencias;
    private List<Estudiante> estudiantesCursoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        dbUser = new DBUser(this);
        
        inicializarViews();
        configurarNavegacion();
        configurarListeners();
        cargarDatos();
    }

    private void inicializarViews() {
        // Botones de navegación
        btnCursos = findViewById(R.id.btnCursos);
        btnDocentes = findViewById(R.id.btnDocentes);
        btnEstudiantes = findViewById(R.id.btnEstudiantes);
        btnAsistencias = findViewById(R.id.btnAsistencias);
        
        // Secciones
        seccionCursos = findViewById(R.id.seccionCursos);
        seccionDocentes = findViewById(R.id.seccionDocentes);
        seccionEstudiantes = findViewById(R.id.seccionEstudiantes);
        seccionAsistencias = findViewById(R.id.seccionAsistencias);
        
        // Cursos
        cursoNombreInput = findViewById(R.id.cursoNombreInput);
        cursoDescripcionInput = findViewById(R.id.cursoDescripcionInput);
        cursoHorarioInput = findViewById(R.id.cursoHorarioInput);
        spinnerDocenteCurso = findViewById(R.id.spinnerDocenteCurso);
        btnGuardarCurso = findViewById(R.id.btnGuardarCurso);
        recyclerViewCursos = findViewById(R.id.recyclerViewCursos);
        recyclerViewCursos.setLayoutManager(new LinearLayoutManager(this));
        
        // Docentes
        docenteNombreInput = findViewById(R.id.docenteNombreInput);
        docenteApellidoInput = findViewById(R.id.docenteApellidoInput);
        docenteEmailInput = findViewById(R.id.docenteEmailInput);
        docenteTelefonoInput = findViewById(R.id.docenteTelefonoInput);
        docenteEspecialidadInput = findViewById(R.id.docenteEspecialidadInput);
        btnGuardarDocente = findViewById(R.id.btnGuardarDocente);
        recyclerViewDocentes = findViewById(R.id.recyclerViewDocentes);
        recyclerViewDocentes.setLayoutManager(new LinearLayoutManager(this));
        
        // Estudiantes
        estudianteNombreInput = findViewById(R.id.estudianteNombreInput);
        estudianteApellidoInput = findViewById(R.id.estudianteApellidoInput);
        estudianteEmailInput = findViewById(R.id.estudianteEmailInput);
        estudianteTelefonoInput = findViewById(R.id.estudianteTelefonoInput);
        estudianteFechaNacimientoInput = findViewById(R.id.estudianteFechaNacimientoInput);
        spinnerCursoEstudiante = findViewById(R.id.spinnerCursoEstudiante);
        btnGuardarEstudiante = findViewById(R.id.btnGuardarEstudiante);
        recyclerViewEstudiantes = findViewById(R.id.recyclerViewEstudiantes);
        recyclerViewEstudiantes.setLayoutManager(new LinearLayoutManager(this));
        
        // Asistencias
        spinnerCursoAsistencia = findViewById(R.id.spinnerCursoAsistencia);
        spinnerDocenteAsistencia = findViewById(R.id.spinnerDocenteAsistencia);
        asistenciaFechaInput = findViewById(R.id.asistenciaFechaInput);
        recyclerViewEstudiantesAsistencia = findViewById(R.id.recyclerViewEstudiantesAsistencia);
        recyclerViewEstudiantesAsistencia.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAsistencias = findViewById(R.id.recyclerViewAsistencias);
        recyclerViewAsistencias.setLayoutManager(new LinearLayoutManager(this));
        btnGuardarAsistencia = findViewById(R.id.btnGuardarAsistencia);
        
        listaCursos = new ArrayList<>();
        listaDocentes = new ArrayList<>();
        listaEstudiantes = new ArrayList<>();
        listaAsistencias = new ArrayList<>();
        estudiantesCursoSeleccionado = new ArrayList<>();
    }

    private void configurarNavegacion() {
        btnCursos.setOnClickListener(v -> mostrarSeccion(1));
        btnDocentes.setOnClickListener(v -> mostrarSeccion(2));
        btnEstudiantes.setOnClickListener(v -> mostrarSeccion(3));
        btnAsistencias.setOnClickListener(v -> mostrarSeccion(4));
    }

    private void mostrarSeccion(int seccion) {
        seccionCursos.setVisibility(seccion == 1 ? View.VISIBLE : View.GONE);
        seccionDocentes.setVisibility(seccion == 2 ? View.VISIBLE : View.GONE);
        seccionEstudiantes.setVisibility(seccion == 3 ? View.VISIBLE : View.GONE);
        seccionAsistencias.setVisibility(seccion == 4 ? View.VISIBLE : View.GONE);
        
        if (seccion == 4) {
            cargarSpinnersAsistencia();
        }
    }

    private void configurarListeners() {
        btnGuardarCurso.setOnClickListener(v -> guardarCurso());
        btnGuardarDocente.setOnClickListener(v -> guardarDocente());
        btnGuardarEstudiante.setOnClickListener(v -> guardarEstudiante());
        btnGuardarAsistencia.setOnClickListener(v -> guardarAsistencia());
        
        spinnerCursoAsistencia.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
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
        cargarDocentes();
        cargarCursos();
        cargarEstudiantes();
        cargarAsistencias();
    }

    private void cargarDocentes() {
        listaDocentes = dbUser.obtenerDocentes(idIglesia);
        actualizarSpinnerDocentes();
        actualizarRecyclerViewDocentes();
    }

    private void cargarCursos() {
        listaCursos = dbUser.obtenerCursos(idIglesia);
        actualizarSpinnerCursos();
        actualizarRecyclerViewCursos();
    }

    private void cargarEstudiantes() {
        listaEstudiantes = dbUser.obtenerEstudiantes(idIglesia);
        actualizarRecyclerViewEstudiantes();
    }

    private void cargarEstudiantesPorCurso(int idCurso) {
        estudiantesCursoSeleccionado = dbUser.obtenerEstudiantesPorCurso(idCurso);
        actualizarRecyclerViewEstudiantesAsistencia();
    }

    private void cargarAsistencias() {
        listaAsistencias = dbUser.obtenerAsistencias(idIglesia);
        actualizarRecyclerViewAsistencias();
    }

    private void actualizarSpinnerDocentes() {
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccionar Docente");
        for (Docente d : listaDocentes) {
            nombres.add(d.getNombreCompleto());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocenteCurso.setAdapter(adapter);
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

    private void cargarSpinnersAsistencia() {
        // Spinner de cursos
        List<String> nombresCursos = new ArrayList<>();
        nombresCursos.add("Seleccionar Curso");
        for (Curso c : listaCursos) {
            nombresCursos.add(c.getNombre());
        }
        ArrayAdapter<String> adapterCursos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCursos);
        adapterCursos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCursoAsistencia.setAdapter(adapterCursos);
        
        // Spinner de docentes
        List<String> nombresDocentes = new ArrayList<>();
        nombresDocentes.add("Seleccionar Docente");
        for (Docente d : listaDocentes) {
            nombresDocentes.add(d.getNombreCompleto());
        }
        ArrayAdapter<String> adapterDocentes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresDocentes);
        adapterDocentes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocenteAsistencia.setAdapter(adapterDocentes);
    }

    private void guardarCurso() {
        String nombre = cursoNombreInput.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre del curso es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicionDocente = spinnerDocenteCurso.getSelectedItemPosition();
        if (posicionDocente == 0) {
            Toast.makeText(this, "Debe seleccionar un docente", Toast.LENGTH_SHORT).show();
            return;
        }

        Curso curso = cursoSeleccionado != null ? cursoSeleccionado : new Curso();
        curso.setNombre(nombre);
        curso.setDescripcion(cursoDescripcionInput.getText().toString().trim());
        curso.setHorario(cursoHorarioInput.getText().toString().trim());
        curso.setIdDocente(listaDocentes.get(posicionDocente - 1).getId());
        curso.setIdIglesia(idIglesia);

        boolean exito;
        if (cursoSeleccionado != null) {
            exito = dbUser.actualizarCurso(curso);
            Toast.makeText(this, exito ? "Curso actualizado" : "Error al actualizar", Toast.LENGTH_SHORT).show();
        } else {
            exito = dbUser.insertarCurso(curso);
            Toast.makeText(this, exito ? "Curso guardado" : "Error al guardar", Toast.LENGTH_SHORT).show();
        }

        if (exito) {
            limpiarFormularioCurso();
            cargarCursos();
        }
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

    private void limpiarFormularioCurso() {
        cursoNombreInput.setText("");
        cursoDescripcionInput.setText("");
        cursoHorarioInput.setText("");
        spinnerDocenteCurso.setSelection(0);
        cursoSeleccionado = null;
        btnGuardarCurso.setText("Guardar Curso");
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

    private void actualizarRecyclerViewCursos() {
        // Implementar adaptador personalizado si es necesario
        // Por ahora solo mostramos un mensaje
    }

    private void actualizarRecyclerViewDocentes() {
        // Implementar adaptador personalizado si es necesario
    }

    private void actualizarRecyclerViewEstudiantes() {
        // Implementar adaptador personalizado si es necesario
    }

    private void actualizarRecyclerViewEstudiantesAsistencia() {
        // Implementar adaptador personalizado si es necesario
    }

    private void actualizarRecyclerViewAsistencias() {
        // Implementar adaptador personalizado si es necesario
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }
}

