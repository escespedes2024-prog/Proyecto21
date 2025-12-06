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

public class GestionCursosActivity extends AppCompatActivity implements CursoAdapter.OnCursoClickListener {

    private DBUser dbUser;
    private int idIglesia = 1;
    
    private TextInputEditText cursoNombreInput, cursoDescripcionInput, cursoHorarioInput;
    private Spinner spinnerDocenteCurso;
    private Button btnGuardarCurso;
    private RecyclerView recyclerViewCursos;
    private List<Curso> listaCursos;
    private List<Docente> listaDocentes;
    private Curso cursoSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_cursos);

        dbUser = new DBUser(this);
        
        inicializarViews();
        configurarListeners();
        cargarDatos();
    }

    private void inicializarViews() {
        cursoNombreInput = findViewById(R.id.cursoNombreInput);
        cursoDescripcionInput = findViewById(R.id.cursoDescripcionInput);
        cursoHorarioInput = findViewById(R.id.cursoHorarioInput);
        spinnerDocenteCurso = findViewById(R.id.spinnerDocenteCurso);
        btnGuardarCurso = findViewById(R.id.btnGuardarCurso);
        recyclerViewCursos = findViewById(R.id.recyclerViewCursos);
        recyclerViewCursos.setLayoutManager(new LinearLayoutManager(this));
        
        listaCursos = new ArrayList<>();
        listaDocentes = new ArrayList<>();
    }

    private void configurarListeners() {
        btnGuardarCurso.setOnClickListener(v -> guardarCurso());
    }

    private void cargarDatos() {
        cargarDocentes();
        cargarCursos();
    }

    private void cargarDocentes() {
        listaDocentes = dbUser.obtenerDocentes(idIglesia);
        actualizarSpinnerDocentes();
    }

    private void cargarCursos() {
        listaCursos = dbUser.obtenerCursos(idIglesia);
        actualizarRecyclerViewCursos();
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

    private void limpiarFormularioCurso() {
        cursoNombreInput.setText("");
        cursoDescripcionInput.setText("");
        cursoHorarioInput.setText("");
        spinnerDocenteCurso.setSelection(0);
        cursoSeleccionado = null;
        btnGuardarCurso.setText("Guardar Curso");
    }

    private void actualizarRecyclerViewCursos() {
        CursoAdapter adapter = new CursoAdapter(this, listaCursos, this);
        recyclerViewCursos.setAdapter(adapter);
    }

    @Override
    public void onCursoClick(Curso curso) {
        cursoSeleccionado = curso;
        
        cursoNombreInput.setText(curso.getNombre());
        cursoDescripcionInput.setText(curso.getDescripcion());
        cursoHorarioInput.setText(curso.getHorario());
        
        // Seleccionar docente en spinner
        for (int i = 0; i < listaDocentes.size(); i++) {
            if (listaDocentes.get(i).getId() == curso.getIdDocente()) {
                spinnerDocenteCurso.setSelection(i + 1);
                break;
            }
        }
        
        btnGuardarCurso.setText("Actualizar Curso");
    }

    @Override
    public void onCursoDelete(Curso curso) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Curso")
                .setMessage("¿Estás seguro de que deseas eliminar el curso \"" + curso.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (dbUser.eliminarCurso(curso.getId())) {
                        Toast.makeText(this, "Curso eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        if (cursoSeleccionado != null && cursoSeleccionado.getId() == curso.getId()) {
                            limpiarFormularioCurso();
                        }
                        cargarCursos();
                    } else {
                        Toast.makeText(this, "Error al eliminar curso", Toast.LENGTH_SHORT).show();
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

