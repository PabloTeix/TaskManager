package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone, etAddress;
    private Button btnRegister;
    private ProgressBar progressBar;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inicializar elementos de UI
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Evento del botón registrar
        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Validar campos vacíos
            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
                Toast.makeText(RegisterActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar formato de email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar longitud mínima de contraseña
            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que el teléfono solo tenga dígitos
            if (!phone.matches("\\d+")) {
                Toast.makeText(RegisterActivity.this, "El teléfono solo debe contener números", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar longitud del número de teléfono
            if (phone.length() < 9) {
                Toast.makeText(RegisterActivity.this, "El número de teléfono es muy corto", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si pasa todas las validaciones, registrar usuario
            registerUser(email, password, confirmPassword, fullName, phone, address);
        });
    }

    private void registerUser(String email, String password, String confirmPassword, String fullName, String phone, String address) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String id = mAuth.getCurrentUser().getUid();
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", id);
                        map.put("nombre", fullName);
                        map.put("email", email);
                        map.put("contraseña", password);
                        map.put("telefono", phone);
                        map.put("direccion", address);

                        mFirestore.collection("user").document(id).set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        finish();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
