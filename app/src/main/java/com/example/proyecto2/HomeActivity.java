package com.example.proyecto2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private DBUser dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbUser = new DBUser(this);
        recyclerView = findViewById(R.id.recyclerViewUsers);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar los datos y configurar el adaptador
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        // Obtiene la lista de usuarios de la base de datos
        List<Usuario> listaUsuarios = dbUser.obtenerUsuarios();

        // Inicializa el adaptador y lo asigna al RecyclerView
        userAdapter = new UserAdapter(this, listaUsuarios);
        recyclerView.setAdapter(userAdapter);
    }
}