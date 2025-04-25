package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLogin, etPasswordLogin;
    private Button btnLogin, btnRegisterRedirect,btnInvitado;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Verificar si ya hay un usuario logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Inicializar elementos de la UI
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterRedirect = findViewById(R.id.btnRegisterRedirect);
        btnInvitado = findViewById(R.id.btn_invitado);
        progressBar = findViewById(R.id.progressBar);


        btnInvitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAnonimo();
            }
        });


        // Manejar el clic del botón para ir al registro
        btnRegisterRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Manejar el clic del botón de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailLogin.getText().toString().trim();
                String password = etPasswordLogin.getText().toString().trim();

                // Validar los campos
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Por favor, ingresa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginAnonimo() {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error al acceder", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginUser(String email, String password) {
        // Mostrar el progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Iniciar sesión con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Ocultar el progress bar
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Error de iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}
