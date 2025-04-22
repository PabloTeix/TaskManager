package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmanager.R;
import com.example.taskmanager.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone, etAddress;
    private Button btnRegister;
    private ProgressBar progressBar;

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Inicializar los elementos de la interfaz
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Inicializar el ViewModel
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Observar el estado de los mensajes y del progreso
        registerViewModel.getStatusMessage().observe(this, message -> {
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            if (message.equals("Registro exitoso")) {
                navigateToLogin();  // Navegar al Login después de un registro exitoso
            }
        });

        registerViewModel.getIsRegistering().observe(this, isRegistering -> {
            if (isRegistering) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                btnRegister.setEnabled(false);
            } else {
                progressBar.setVisibility(ProgressBar.GONE);
                btnRegister.setEnabled(true);
            }
        });

        // Manejar el evento de clic del botón de registro
        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Usar el ViewModel para registrar al usuario
            registerViewModel.registerUser(email, password, confirmPassword, fullName, phone, address);
        });
    }

    // Método para redirigir a LoginActivity
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();  // Finalizamos esta actividad para que no pueda volver atrás al registrarse
    }
}
