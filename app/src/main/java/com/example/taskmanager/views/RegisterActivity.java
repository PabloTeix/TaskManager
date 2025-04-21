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

    // Usamos ViewModelProvider para obtener el RegisterViewModel
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

        // Obtener el ViewModel a través de ViewModelProvider
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Observar el mensaje de estado del ViewModel
        registerViewModel.getStatusMessage().observe(this, message -> {
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

            // Si el registro es exitoso, navegar a la pantalla de Login
            if (message.equals("Registro exitoso")) {
                navigateToLogin();
            }
        });

        // Observar si el registro está en progreso
        registerViewModel.getIsRegistering().observe(this, isRegistering -> {
            if (isRegistering) {
                progressBar.setVisibility(ProgressBar.VISIBLE); // Mostrar el progreso
                btnRegister.setEnabled(false); // Deshabilitar el botón de registro
            } else {
                progressBar.setVisibility(ProgressBar.GONE); // Ocultar el progreso
                btnRegister.setEnabled(true); // Habilitar el botón de registro
            }
        });

        // Manejar el clic del botón de registro
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

    // Método para redirigir a la actividad de Login
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
