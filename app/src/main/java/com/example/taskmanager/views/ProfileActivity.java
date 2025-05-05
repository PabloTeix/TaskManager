package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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

    private TextView tvNombreUsuario, tvEmailUsuario, tvContraseñaUsuario, tvTelefonoUsuario, tvDireccionUsuario;
    private Button btnCerrarSesion, btnCambiarContrasena;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Perfil");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario);
        tvContraseñaUsuario = findViewById(R.id.tvContraseñaUsuario);
        tvTelefonoUsuario = findViewById(R.id.tvTelefonoUsuario);
        tvDireccionUsuario = findViewById(R.id.tvDireccionUsuario);
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
        btnCambiarContrasena = findViewById(R.id.btn_cambiar);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            Log.d("ProfileActivity", "UID del usuario: " + uid);

            db.collection("user").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            String email = documentSnapshot.getString("email");
                            String contraseña = documentSnapshot.getString("contraseña");
                            String telefono = documentSnapshot.getString("telefono");
                            String direccion = documentSnapshot.getString("direccion");

                            tvNombreUsuario.setText("Nombre: " + nombre);
                            tvEmailUsuario.setText("Correo: " + email);
                            tvContraseñaUsuario.setText("Contraseña: "+ contraseña);
                            tvTelefonoUsuario.setText("Telefono: "+ telefono);
                            tvDireccionUsuario.setText("Direccion: "+ direccion);

                        } else {
                            Log.e("ProfileActivity", "No se encontraron datos para el usuario.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("ProfileActivity", "Error al obtener los datos: ", e));
        } else {
            Log.e("ProfileActivity", "No hay usuario autenticado");
        }

        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        btnCambiarContrasena.setOnClickListener(v -> mostrarDialogoCambioContrasena());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_profile) {
            return true;
        } else if (id == R.id.item_main_menu) {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoCambioContrasena() {
        final EditText input = new EditText(this);
        input.setHint("Nueva contraseña");

        new AlertDialog.Builder(this)
                .setTitle("Cambiar contraseña")
                .setMessage("Ingresa tu nueva contraseña")
                .setView(input)
                .setPositiveButton("Cambiar", (dialog, which) -> {
                    String nuevaContrasena = input.getText().toString().trim();
                    if (nuevaContrasena.isEmpty()) {
                        Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                    } else {
                        cambiarContrasena(nuevaContrasena);
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void cambiarContrasena(String nuevaContrasena) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (nuevaContrasena.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            final EditText inputPassword = new EditText(this);
            inputPassword.setHint("Contraseña actual");

            new AlertDialog.Builder(this)
                    .setTitle("Reautenticación")
                    .setMessage("Por favor, ingresa tu contraseña actual para cambiarla")
                    .setView(inputPassword)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        String contraseñaActual = inputPassword.getText().toString().trim();
                        if (!contraseñaActual.isEmpty()) {
                            reautenticarYCambiarContraseña(user, contraseñaActual, nuevaContrasena);
                        } else {
                            Toast.makeText(this, "La contraseña actual no puede estar vacía", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
    }

    private void reautenticarYCambiarContraseña(FirebaseUser user, String contraseñaActual, String nuevaContrasena) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), contraseñaActual);

        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(nuevaContrasena)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                actualizarContraseñaEnFirestore(user.getUid(), nuevaContrasena);

                                // Aquí actualizamos el TextView con la nueva contraseña
                                tvContraseñaUsuario.setText("Contraseña: " + nuevaContrasena); // Actualización en la vista

                                Toast.makeText(this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error al cambiar la contraseña: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                String errorMessage = "Error al reautenticar: " + task.getException().getMessage();
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    errorMessage = "Las credenciales son incorrectas.";
                } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                    errorMessage = "No se encontró el usuario.";
                } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                    errorMessage = "Se requiere reautenticación.";
                }

                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void actualizarContraseñaEnFirestore(String uid, String nuevaContrasena) {
        DocumentReference usuarioRef = db.collection("user").document(uid);
        Map<String, Object> updates = new HashMap<>();
        updates.put("contraseña", nuevaContrasena);

        usuarioRef.update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Contraseña actualizada en Firestore"))
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al actualizar la contraseña", e);
                    Toast.makeText(this, "Error al actualizar la contraseña en Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}
