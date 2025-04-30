package com.example.taskmanager.views;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvNombreUsuario, tvEmailUsuario;
    private Button btnCerrarSesion, btnCambiarContrasena;
    private Switch switchModoOscuro;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Perfil");

        // Inicializar FirebaseAuth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario);
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
        btnCambiarContrasena = findViewById(R.id.btn_cambiar);
        switchModoOscuro = findViewById(R.id.switchModoOscuro);

        // Obtener el usuario autenticado
        FirebaseUser user = mAuth.getCurrentUser();

        // Verificar si el usuario está autenticado
        if (user != null) {
            // Obtener y registrar el UID del usuario para depuración
            String uid = user.getUid();
            Log.d("ProfileActivity", "UID del usuario: " + uid);

            // Cargar los datos del usuario desde Firestore usando el UID
            db.collection("user").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Obtener los campos "nombre" y "email"
                            String nombre = documentSnapshot.getString("nombre");
                            String email = documentSnapshot.getString("email");

                            Log.d("ProfileActivity", "Nombre: " + nombre + ", Correo: " + email);

                            // Asignar los valores a los TextViews
                            tvNombreUsuario.setText("Nombre: " + nombre);
                            tvEmailUsuario.setText("Correo: " + email);
                        } else {
                            Log.e("ProfileActivity", "No se encontraron datos para el usuario.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejar cualquier error de la consulta aquí
                        Log.e("ProfileActivity", "Error al obtener los datos: ", e);
                    });
        } else {
            Log.e("ProfileActivity", "No hay usuario autenticado");
        }

        // Acción para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            // Cerrar sesión y regresar al inicio de sesión
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class)); // Redirigir al login
            finish();  // Finalizar la actividad actual
        });

        // Acción para cambiar la contraseña
        btnCambiarContrasena.setOnClickListener(v -> mostrarDialogoCambioContrasena());
    }

    // Inflar el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Manejar la selección de opciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_profile) {
            // Ya estás en la actividad de perfil, no hacer nada
            return true;
        } else if (id == R.id.item_main_menu) {
            // Redirigir a la pantalla principal
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Método para mostrar el diálogo para cambiar la contraseña
    private void mostrarDialogoCambioContrasena() {
        // Crear un EditText para que el usuario ingrese la nueva contraseña
        final EditText input = new EditText(this);
        input.setHint("Nueva contraseña");

        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar contraseña")
                .setMessage("Ingresa tu nueva contraseña")
                .setView(input)
                .setPositiveButton("Cambiar", (dialog, which) -> {
                    String nuevaContrasena = input.getText().toString().trim();

                    if (nuevaContrasena.isEmpty()) {
                        Toast.makeText(ProfileActivity.this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                    } else {
                        cambiarContrasena(nuevaContrasena);
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Método para cambiar la contraseña
    private void cambiarContrasena(String nuevaContrasena) {
        FirebaseUser user = mAuth.getCurrentUser(); // Obtén el usuario actual

        if (user != null) {
            // Asegúrate de que la nueva contraseña sea válida (mínimo 6 caracteres)
            if (nuevaContrasena.length() < 6) {
                Toast.makeText(ProfileActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí pedimos al usuario que ingrese su contraseña actual para reautenticarse
            final EditText inputPassword = new EditText(this);
            inputPassword.setHint("Contraseña actual");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reautenticación")
                    .setMessage("Por favor, ingresa tu contraseña actual para cambiarla")
                    .setView(inputPassword)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        String contraseñaActual = inputPassword.getText().toString().trim();
                        if (!contraseñaActual.isEmpty()) {
                            reautenticarYCambiarContraseña(user, contraseñaActual, nuevaContrasena);
                        } else {
                            Toast.makeText(ProfileActivity.this, "La contraseña actual no puede estar vacía", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para reautenticar al usuario con su contraseña actual y luego cambiar la contraseña
    private void reautenticarYCambiarContraseña(FirebaseUser user, String contraseñaActual, String nuevaContrasena) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), contraseñaActual);

        // Reautenticación
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Si la reautenticación fue exitosa, cambiamos la contraseña
                user.updatePassword(nuevaContrasena)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                // Actualizar la contraseña en Firestore también
                                actualizarContraseñaEnFirestore(user.getUid(), nuevaContrasena);
                                Toast.makeText(ProfileActivity.this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error al cambiar la contraseña: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Si la reautenticación falla, mostramos un error detallado
                String errorMessage = "Error al reautenticar: " + task.getException().getMessage();
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    errorMessage = "Las credenciales son incorrectas.";
                } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                    errorMessage = "No se encontró el usuario con las credenciales proporcionadas.";
                } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                    errorMessage = "Se requiere reautenticación para cambiar la contraseña.";
                }

                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Método para actualizar la contraseña en Firestore
    private void actualizarContraseñaEnFirestore(String uid, String nuevaContrasena) {
        DocumentReference usuarioRef = db.collection("user").document(uid);

        // Crea un mapa con el campo que deseas actualizar
        Map<String, Object> updates = new HashMap<>();
        updates.put("contraseña", nuevaContrasena);  // Cambia 'contraseña' por el nombre del campo adecuado

        // Actualiza el campo de la contraseña
        usuarioRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Si la actualización fue exitosa
                    Log.d("Firestore", "Contraseña actualizada en Firestore");
                })
                .addOnFailureListener(e -> {
                    // Si ocurrió un error al actualizar en Firestore
                    Log.w("Firestore", "Error al actualizar la contraseña", e);
                    Toast.makeText(ProfileActivity.this, "Error al actualizar la contraseña en Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}
