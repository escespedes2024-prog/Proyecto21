package com.example.proyecto2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CursosActivity extends AppCompatActivity {

    private CardView cardGestionCursos, cardGestionDocentes, cardGestionEstudiantes, cardGestionAsistencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        inicializarViews();
        configurarListeners();
    }

    private void inicializarViews() {
        cardGestionCursos = findViewById(R.id.cardGestionCursos);
        cardGestionDocentes = findViewById(R.id.cardGestionDocentes);
        cardGestionEstudiantes = findViewById(R.id.cardGestionEstudiantes);
        cardGestionAsistencias = findViewById(R.id.cardGestionAsistencias);
    }

    private void configurarListeners() {
        cardGestionCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CursosActivity.this, GestionCursosActivity.class);
                startActivity(intent);
            }
        });

        cardGestionDocentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CursosActivity.this, GestionDocentesActivity.class);
                startActivity(intent);
            }
        });

        cardGestionEstudiantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CursosActivity.this, GestionEstudiantesActivity.class);
                startActivity(intent);
            }
        });

        cardGestionAsistencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CursosActivity.this, GestionAsistenciasActivity.class);
                startActivity(intent);
            }
        });
    }
}

