package com.example.taskmanager.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    // Se declaran las variables para los campos de entrada de texto
    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone, etAddress;
    // Botón para ejecutar el registro
    private Button btnRegister;
    // Barra de progreso que aparece mientras se realiza el registro
    private ProgressBar progressBar;
    // Instancias de Firebase para autenticación y base de datos
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Inicialización de Firebase y vistas
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Configurar el título de la actividad y habilitar la opción de retroceder en la barra de acción
        this.setTitle("Registro");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6B3E26")));  // Color marrón
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inicializar los campos de entrada de texto y el botón
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Configurar el evento para el botón de registro
        btnRegister.setOnClickListener(v -> {
            // Obtener los valores de los campos de texto
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Validar que los campos no estén vacíos
            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
                Toast.makeText(RegisterActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que el correo tenga el formato correcto
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que la contraseña tenga al menos 6 caracteres
            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que el teléfono solo contenga dígitos
            if (!phone.matches("\\d+")) {
                Toast.makeText(RegisterActivity.this, "El teléfono solo debe contener números", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que el teléfono tenga al menos 9 caracteres
            if (phone.length() < 9) {
                Toast.makeText(RegisterActivity.this, "El número de teléfono es muy corto", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si pasa todas las validaciones, llamar a la función para registrar al usuario
            registerUser(email, password, confirmPassword, fullName, phone, address);
        });
    }

    // Función encargada de registrar un nuevo usuario en Firebase Authentication
    private void registerUser(String email, String password, String confirmPassword, String fullName, String phone, String address) {
        // Crear el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Si el registro es exitoso
                        if (task.isSuccessful()) {
                            // Obtener el UID del usuario recién creado
                            String id = mAuth.getCurrentUser().getUid();

                            // Crear un mapa con los datos adicionales del usuario
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", id); // ID del usuario
                            map.put("nombre", fullName); // Nombre del usuario
                            map.put("email", email); // Correo electrónico
                            map.put("contraseña", password); // Contraseña del usuario
                            map.put("telefono", phone); // Teléfono del usuario
                            map.put("direccion", address); // Dirección del usuario

                            // Guardar los datos en la colección 'user' de Firestore
                            mFirestore.collection("user").document(id).set(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Si los datos se guardan correctamente, finalizar la actividad de registro
                                            finish();
                                            // Redirigir a la actividad principal (MainActivity)
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            // Mostrar un mensaje de éxito
                                            Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Mostrar un mensaje de error si no se puede guardar los datos
                                            Toast.makeText(RegisterActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                // Si el registro en Firebase Authentication falla
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Mostrar un mensaje de error
                        Toast.makeText(RegisterActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Implementa el método onSupportNavigateUp para manejar el retroceso en la barra de acción
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Llamar al método de retroceso estándar
        return true;
    }

    // Implementa el método onBackPressed para manejar el retroceso con el botón físico
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Llamar al comportamiento estándar de retroceso
    }
}
