package com.example.taskmanager.views;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone, etAddress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Inicializar los elementos de la interfaz
        mAuth = FirebaseAuth.getInstance();
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        Button btnRegister = findViewById(R.id.btnRegister);

        // Manejar el clic del botón de registro
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Validación de campos
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si las contraseñas coinciden
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registrar usuario con Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Obtener el ID del usuario y almacenar los datos adicionales en Firebase Realtime Database
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(fullName, phone, address);
                        FirebaseDatabase.getInstance().getReference("Users").child(userId).setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        // Redirigir a LoginActivity
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();  // Finalizar la actividad de registro
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Mostrar mensaje de error si el registro falla
                        Toast.makeText(RegisterActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Clase interna para representar al usuario
    public static class User {
        public String fullName, phone, address;

        public User(String fullName, String phone, String address) {
            this.fullName = fullName;
            this.phone = phone;
            this.address = address;
        }
    }
}