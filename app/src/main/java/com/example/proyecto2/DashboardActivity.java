package com.example.proyecto2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    private DBUser dbUser;
    private TextView nombreIglesia;
    private CardView cardEventos, cardMiembros, cardMapa, cardSensores, cardConfiguracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbUser = new DBUser(this);
        
        nombreIglesia = findViewById(R.id.nombreIglesia);
        cardEventos = findViewById(R.id.cardEventos);
        cardMiembros = findViewById(R.id.cardMiembros);
        cardMapa = findViewById(R.id.cardMapa);
        cardSensores = findViewById(R.id.cardSensores);
        cardConfiguracion = findViewById(R.id.cardConfiguracion);

        // Cargar información de la iglesia principal (ID 1 por defecto)
        cargarInformacionIglesia();

        // Configurar listeners
        cardEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, EventosActivity.class);
                startActivity(intent);
            }
        });

        cardMiembros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MiembrosActivity.class);
                startActivity(intent);
            }
        });

        cardMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MapaActivity.class);
                startActivity(intent);
            }
        });

        cardSensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, SensoresActivity.class);
                startActivity(intent);
            }
        });

        cardConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ConfiguracionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void cargarInformacionIglesia() {
        Iglesia iglesia = dbUser.obtenerIglesia(1);
        if (iglesia != null) {
            nombreIglesia.setText(iglesia.getNombre());
        } else {
            nombreIglesia.setText("Administración de Iglesia");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarInformacionIglesia();
    }
}

