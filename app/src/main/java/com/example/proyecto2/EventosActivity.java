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

public class EventosActivity extends AppCompatActivity implements EventoAdapter.OnEventoClickListener {

    private DBUser dbUser;
    private RecyclerView recyclerView;
    private TextInputEditText tituloInput, descripcionInput, fechaInput, horaInput, tipoInput;
    private Button agregarButton;
    private List<Evento> listaEventos;
    private Evento eventoEditando;
    private boolean modoEdicion = false;
    private int idIglesia = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        dbUser = new DBUser(this);
        
        tituloInput = findViewById(R.id.tituloInput);
        descripcionInput = findViewById(R.id.descripcionInput);
        fechaInput = findViewById(R.id.fechaInput);
        horaInput = findViewById(R.id.horaInput);
        tipoInput = findViewById(R.id.tipoInput);
        agregarButton = findViewById(R.id.agregarButton);
        recyclerView = findViewById(R.id.recyclerViewEventos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Agregar formateador de fecha
        fechaInput.addTextChangedListener(new DateTextWatcher(fechaInput, "yyyy-MM-dd"));
        
        listaEventos = new ArrayList<>();
        cargarEventos();

        agregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoEdicion) {
                    actualizarEvento();
                } else {
                    agregarEvento();
                }
            }
        });
    }

    private void agregarEvento() {
        String titulo = tituloInput.getText().toString().trim();
        String descripcion = descripcionInput.getText().toString().trim();
        String fecha = fechaInput.getText().toString().trim();
        String hora = horaInput.getText().toString().trim();
        String tipo = tipoInput.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.isEmpty()) {
            // Usar fecha actual si no se especifica
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fecha = formato.format(new Date());
        }

        if (hora.isEmpty()) {
            hora = "00:00";
        }

        if (tipo.isEmpty()) {
            tipo = "General";
        }

        Evento evento = new Evento(titulo, descripcion, fecha, hora, tipo);
        evento.setIdIglesia(idIglesia);

        if (dbUser.insertarEvento(evento)) {
            Toast.makeText(this, "Evento agregado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarEventos();
        } else {
            Toast.makeText(this, "Error al agregar evento", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEventos() {
        listaEventos = dbUser.obtenerEventos(idIglesia);
        EventoAdapter adapter = new EventoAdapter(this, listaEventos, this);
        recyclerView.setAdapter(adapter);
        if (listaEventos.isEmpty()) {
            Toast.makeText(this, "No hay eventos registrados", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarEvento() {
        if (eventoEditando == null) {
            Toast.makeText(this, "Error: No hay evento seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = tituloInput.getText().toString().trim();
        String descripcion = descripcionInput.getText().toString().trim();
        String fecha = fechaInput.getText().toString().trim();
        String hora = horaInput.getText().toString().trim();
        String tipo = tipoInput.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.isEmpty()) {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            fecha = formato.format(new Date());
        }

        if (hora.isEmpty()) {
            hora = "00:00";
        }

        if (tipo.isEmpty()) {
            tipo = "General";
        }

        eventoEditando.setTitulo(titulo);
        eventoEditando.setDescripcion(descripcion);
        eventoEditando.setFecha(fecha);
        eventoEditando.setHora(hora);
        eventoEditando.setTipo(tipo);

        if (dbUser.actualizarEvento(eventoEditando)) {
            Toast.makeText(this, "Evento actualizado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarEventos();
        } else {
            Toast.makeText(this, "Error al actualizar evento", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEventoClick(Evento evento) {
        eventoEditando = evento;
        modoEdicion = true;
        
        tituloInput.setText(evento.getTitulo());
        descripcionInput.setText(evento.getDescripcion());
        fechaInput.setText(evento.getFecha());
        horaInput.setText(evento.getHora());
        tipoInput.setText(evento.getTipo());
        
        agregarButton.setText("Actualizar Evento");
    }

    @Override
    public void onEventoDelete(Evento evento) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Evento")
                .setMessage("¿Estás seguro de que deseas eliminar el evento \"" + evento.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (dbUser.eliminarEvento(evento.getId())) {
                        Toast.makeText(this, "Evento eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        if (eventoEditando != null && eventoEditando.getId() == evento.getId()) {
                            limpiarCampos();
                        }
                        cargarEventos();
                    } else {
                        Toast.makeText(this, "Error al eliminar evento", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void limpiarCampos() {
        tituloInput.setText("");
        descripcionInput.setText("");
        fechaInput.setText("");
        horaInput.setText("");
        tipoInput.setText("");
        eventoEditando = null;
        modoEdicion = false;
        agregarButton.setText("Agregar Evento");
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEventos();
    }
}

