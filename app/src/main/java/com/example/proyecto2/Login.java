package com.example.proyecto2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_LAST_EMAIL = "last_email";

    private DBUser dbUser;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private androidx.cardview.widget.CardView fingerprintCard;
    private TextView registerText;
    private SharedPreferences sharedPreferences;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbUser = new DBUser(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        fingerprintCard = findViewById(R.id.fingerprintCard);
        registerText = findViewById(R.id.registerText);

        // Configurar executor para BiometricPrompt
        executor = ContextCompat.getMainExecutor(this);

        // Configurar BiometricPrompt
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Autenticación biométrica exitosa
                String lastEmail = sharedPreferences.getString(KEY_LAST_EMAIL, "");
                if (!lastEmail.isEmpty()) {
                    realizarLoginAutomatico(lastEmail);
                } else {
                    Toast.makeText(Login.this, "No hay usuario guardado para autenticación biométrica", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(Login.this, "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Login.this, "Huella dactilar no reconocida", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar información del diálogo de huella
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Coloca tu dedo en el sensor de huella")
                .setNegativeButtonText("Cancelar")
                .build();

        // Verificar si hay un email guardado para mostrar el botón de huella
        String lastEmail = sharedPreferences.getString(KEY_LAST_EMAIL, "");
        if (!lastEmail.isEmpty()) {
            fingerprintCard.setVisibility(View.VISIBLE);
        } else {
            fingerprintCard.setVisibility(View.GONE);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });

        fingerprintCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarAutenticacionBiometrica();
            }
        });

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registro.class);
                startActivity(intent);
            }
        });
    }

    private void validarLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar credenciales en la base de datos
        android.database.sqlite.SQLiteDatabase db = dbUser.getReadableDatabase();
        Cursor cursor = db.query("usuarios", null, "correo=? AND contrasena=?",
                new String[]{email, password}, null, null, null);

        if (cursor.moveToFirst()) {
            // Login exitoso - guardar email para futura autenticación biométrica
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LAST_EMAIL, email);
            editor.apply();

            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }

    private void iniciarAutenticacionBiometrica() {
        // Verificar si hay un email guardado
        String lastEmail = sharedPreferences.getString(KEY_LAST_EMAIL, "");
        if (lastEmail.isEmpty()) {
            Toast.makeText(this, "Primero debes iniciar sesión con usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar disponibilidad de autenticación biométrica
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS:
                // Mostrar diálogo de huella dactilar
                biometricPrompt.authenticate(promptInfo);
                break;
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Este dispositivo no tiene sensor de huella dactilar", Toast.LENGTH_SHORT).show();
                break;
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "El sensor de huella dactilar no está disponible", Toast.LENGTH_SHORT).show();
                break;
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No hay huellas dactilares registradas en el dispositivo", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Error desconocido con el sensor biométrico", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void realizarLoginAutomatico(String email) {
        // Buscar el usuario en la base de datos y hacer login automático
        android.database.sqlite.SQLiteDatabase db = dbUser.getReadableDatabase();
        Cursor cursor = db.query("usuarios", null, "correo=?",
                new String[]{email}, null, null, null);

        if (cursor.moveToFirst()) {
            // Login exitoso con huella dactilar
            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }
}
