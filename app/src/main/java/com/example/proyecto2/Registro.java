package com.example.proyecto2;

import static android.os.Build.VERSION_CODES.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Registro extends AppCompatActivity {

    EditText edtnombre, edtapellido, edtcorreo, edtcontra, edtconfircontra;
    Button btregistrar;
    DBUser dbUser;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        edtnombre = findViewById(R.id.nombres);
        edtapellido = findViewById(R.id.apellidos);
        edtcorreo = findViewById(R.id.correo);
        edtcontra = findViewById(R.id.contra);
        edtconfircontra = findViewById(R.id.confirmar);
        btregistrar = findViewById(R.id.nextButton);

        recyclerView = findViewById(R.id.recyclerViewUsersOnRegister);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbUser = new DBUser(this);

        cargarUsuarios();

        btregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = edtnombre.getText().toString();
                String apellido = edtapellido.getText().toString();
                String correo = edtcorreo.getText().toString();
                String contrasena = edtcontra.getText().toString();
                String confirmar = edtconfircontra.getText().toString();

                if (!contrasena.equals(confirmar)) {
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                Usuario usuario = new Usuario(nombre, apellido, correo, contrasena);


                boolean exito = dbUser.insertarUsuario(usuario);

                if (exito) {
                    Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(Registro.this, HomeActivity.class);
                    intent.putExtra("correo", usuario.getCorreo());
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Error al registrar. El correo ya existe o faltan datos.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cargarUsuarios() {
        List<Usuario> listaUsuarios = dbUser.obtenerUsuarios();

        if (userAdapter == null) {
            userAdapter = new UserAdapter(this, listaUsuarios);
            recyclerView.setAdapter(userAdapter);
        } else {
            userAdapter.updateList(listaUsuarios);
        }
    }
}