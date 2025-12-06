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

import java.util.List;

public class AdminUsuariosActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private DBUser dbUser;
    private TextInputEditText nombreInput, apellidoInput, correoInput, contraInput, telefonoInput, fechaNacInput;
    private Button btnGuardar, btnLimpiar;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Usuario> listaUsuarios;
    private Usuario usuarioEditando;
    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_usuarios);

        dbUser = new DBUser(this);
        
        // Inicializar campos de texto
        nombreInput = findViewById(R.id.nombreInput);
        apellidoInput = findViewById(R.id.apellidoInput);
        correoInput = findViewById(R.id.correoInput);
        contraInput = findViewById(R.id.contraInput);
        telefonoInput = findViewById(R.id.telefonoInput);
        fechaNacInput = findViewById(R.id.fechaNacInput);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        recyclerView = findViewById(R.id.recyclerViewUsuarios);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Agregar formateador de fecha (formato dd/MM/yyyy según el hint)
        fechaNacInput.addTextChangedListener(new DateTextWatcher(fechaNacInput, "dd/MM/yyyy"));
        
        cargarUsuarios();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoEdicion) {
                    actualizarUsuario();
                } else {
                    crearUsuario();
                }
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarFormulario();
            }
        });
    }

    private void cargarUsuarios() {
        listaUsuarios = dbUser.obtenerUsuarios();
        userAdapter = new UserAdapter(this, listaUsuarios, this);
        recyclerView.setAdapter(userAdapter);
    }

    private void crearUsuario() {
        String nombre = nombreInput.getText().toString().trim();
        String apellido = apellidoInput.getText().toString().trim();
        String correo = correoInput.getText().toString().trim();
        String contrasena = contraInput.getText().toString().trim();
        String telefono = telefonoInput.getText().toString().trim();
        String fechaNac = fechaNacInput.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Nombre, correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario(nombre, apellido, correo, contrasena, telefono, fechaNac);
        
        if (dbUser.insertarUsuario(usuario)) {
            Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarFormulario();
            cargarUsuarios();
        } else {
            Toast.makeText(this, "Error al crear usuario. El correo ya existe.", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarUsuario() {
        if (usuarioEditando == null) {
            Toast.makeText(this, "Error: No hay usuario seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = nombreInput.getText().toString().trim();
        String apellido = apellidoInput.getText().toString().trim();
        String correo = correoInput.getText().toString().trim();
        String contrasena = contraInput.getText().toString().trim();
        String telefono = telefonoInput.getText().toString().trim();
        String fechaNac = fechaNacInput.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Nombre, correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        usuarioEditando.setNombre(nombre);
        usuarioEditando.setApellido(apellido);
        usuarioEditando.setCorreo(correo);
        usuarioEditando.setContrasena(contrasena);
        usuarioEditando.setTelefono(telefono);
        usuarioEditando.setFecha_nac(fechaNac);

        if (dbUser.actualizarUsuario(usuarioEditando)) {
            Toast.makeText(this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
            limpiarFormulario();
            cargarUsuarios();
        } else {
            Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarFormulario() {
        nombreInput.setText("");
        apellidoInput.setText("");
        correoInput.setText("");
        contraInput.setText("");
        telefonoInput.setText("");
        fechaNacInput.setText("");
        usuarioEditando = null;
        modoEdicion = false;
        btnGuardar.setText("Crear Usuario");
    }

    @Override
    public void onUserClick(Usuario usuario) {
        // Editar usuario
        usuarioEditando = usuario;
        modoEdicion = true;
        
        nombreInput.setText(usuario.getNombre());
        apellidoInput.setText(usuario.getApellido());
        correoInput.setText(usuario.getCorreo());
        contraInput.setText(usuario.getContrasena());
        telefonoInput.setText(usuario.getTelefono());
        fechaNacInput.setText(usuario.getFecha_nac());
        
        btnGuardar.setText("Actualizar Usuario");
    }

    @Override
    public void onUserDelete(Usuario usuario) {
        // Confirmar eliminación
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que deseas eliminar a " + usuario.getNombre() + " " + usuario.getApellido() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (dbUser.eliminarUsuario(usuario.getId())) {
                        Toast.makeText(this, "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        if (usuarioEditando != null && usuarioEditando.getId() == usuario.getId()) {
                            limpiarFormulario();
                        }
                        cargarUsuarios();
                    } else {
                        Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUsuarios();
    }
}

