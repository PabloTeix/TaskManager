//clase encargada de iniciar sesion
package com.example.taskmanager.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLogin, etPasswordLogin;
    private Button btnLogin, btnRegisterRedirect, btnInvitado;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6B3E26")));  // Color marrón
        setContentView(R.layout.login_activity);

        // Inicializa la instancia de FirebaseAuth para gestionar la autenticación
        mAuth = FirebaseAuth.getInstance();

        // Verifica si el usuario ya está logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si ya está logueado, redirige directamente a la actividad principal
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Termina la actividad de login
            return; // Sale de la función para evitar que se cargue la UI de login
        }

        // Inicializa los elementos de la interfaz de usuario (UI)
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterRedirect = findViewById(R.id.btnRegisterRedirect);
        btnInvitado = findViewById(R.id.btn_invitado);
        progressBar = findViewById(R.id.progressBar);

        // Configura el clic del botón para login anónimo
        btnInvitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama al método para login anónimo
                loginAnonimo();
            }
        });

        // Configura el clic del botón para redirigir a la pantalla de registro
        btnRegisterRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia la actividad de registro cuando se hace clic
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Configura el clic del botón para iniciar sesión con el correo y la contraseña
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailLogin.getText().toString().trim();
                String password = etPasswordLogin.getText().toString().trim();

                // Verifica si los campos están vacíos
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    // Muestra un mensaje de error si algún campo está vacío
                    Toast.makeText(LoginActivity.this, "Por favor, ingresa todos los campos", Toast.LENGTH_SHORT).show();
                    return; // Sale del método si los campos están vacíos
                } else {
                    // Si los campos están completos, intenta hacer login con las credenciales proporcionadas
                    loginUser(email, password);
                }
            }
        });
    }

    // Método para realizar el login anónimo
    private void loginAnonimo() {
        // Inicia sesión de forma anónima usando FirebaseAuth
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Si el login anónimo es exitoso, obtiene el usuario actual
                    FirebaseUser user = mAuth.getCurrentUser();
                    // Inicia la actividad principal si el login es exitoso
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Si el login anónimo falla, muestra un mensaje de error
                Toast.makeText(LoginActivity.this, "Error al acceder", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Método para realizar el login con correo y contraseña
    private void loginUser(String email, String password) {
        // Muestra la barra de progreso mientras se realiza el login
        progressBar.setVisibility(View.VISIBLE);

        // Inicia sesión con Firebase usando el correo y la contraseña
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Oculta la barra de progreso una vez completada la tarea
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Si el login es exitoso, termina la actividad de login y redirige a MainActivity
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            // Muestra un mensaje de bienvenida
                            Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();
                        } else {
                            // Si ocurre un error en el login, muestra un mensaje de error
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Si el login falla, oculta la barra de progreso y muestra un mensaje de error
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Error de iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verifica si el usuario ya está logueado al iniciar la actividad
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Si el usuario ya está logueado, redirige a la actividad principal
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Termina la actividad de login
        }
    }
}
