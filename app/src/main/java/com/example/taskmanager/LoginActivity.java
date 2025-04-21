package com.example.taskmanager;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLogin, etPasswordLogin;
    private Button btnLogin, btnRegisterRedirect;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Inicializar elementos de la interfaz
        mAuth = FirebaseAuth.getInstance();
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterRedirect = findViewById(R.id.btnRegisterRedirect);

        // Verificar si ya hay un usuario autenticado
        if (mAuth.getCurrentUser() != null) {
            // Si ya está autenticado, redirigir al DashboardActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finalizar LoginActivity para evitar que el usuario regrese a ella
        }

        // Set listeners para los botones
        btnLogin.setOnClickListener(v -> loginUser());
        btnRegisterRedirect.setOnClickListener(v -> redirectToRegisterActivity());
    }

    // Función para hacer login
    private void loginUser() {
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        // Validación de campos vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor ingresa tu email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intento de inicio de sesión con Firebase Authentication
        btnLogin.setEnabled(false); // Deshabilitar el botón de login mientras se autentica
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true); // Habilitar el botón de login nuevamente

                    if (task.isSuccessful()) {
                        // Si el login es exitoso, redirigir al DashboardActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Cerrar la actividad de login para que no se pueda volver con el botón "Atrás"
                    } else {
                        // Si el login falla, mostrar un mensaje más específico
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Autenticación fallida";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Redirigir a la actividad de registro
    private void redirectToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
