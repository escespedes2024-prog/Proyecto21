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

public class EventosActivity extends AppCompatActivity {

    private DBUser dbUser;
    private RecyclerView recyclerView;
    private EditText tituloInput, descripcionInput, fechaInput, horaInput, tipoInput;
    private Button agregarButton;
    private List<Evento> listaEventos;
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
        
        listaEventos = new ArrayList<>();
        cargarEventos();

        agregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarEvento();
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

    private void limpiarCampos() {
        tituloInput.setText("");
        descripcionInput.setText("");
        fechaInput.setText("");
        horaInput.setText("");
        tipoInput.setText("");
    }

    private void cargarEventos() {
        listaEventos = dbUser.obtenerEventos(idIglesia);
        if (listaEventos.isEmpty()) {
            Toast.makeText(this, "No hay eventos registrados", Toast.LENGTH_SHORT).show();
        }
        // Aquí podrías usar un adaptador personalizado para mostrar los eventos
        // Por ahora solo cargamos la lista
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEventos();
    }
}

