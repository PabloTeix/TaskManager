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
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvNombreUsuario, tvEmailUsuario, tvContraseñaUsuario, tvTelefonoUsuario, tvDireccionUsuario;
    private TextView tvTareasActivas, tvTareasCompletadas;
    private Button btnCerrarSesion, btnCambiarContrasena;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Perfil del usuario");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario);
        tvContraseñaUsuario = findViewById(R.id.tvContraseñaUsuario);
        tvTelefonoUsuario = findViewById(R.id.tvTelefonoUsuario);
        tvDireccionUsuario = findViewById(R.id.tvDireccionUsuario);
        tvTareasActivas = findViewById(R.id.tvContadorActivas);
        tvTareasCompletadas = findViewById(R.id.tvContadorCompletadas);
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
        btnCambiarContrasena = findViewById(R.id.btn_cambiar);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            // Datos personales
            db.collection("user").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            tvNombreUsuario.setText("Nombre: " + documentSnapshot.getString("nombre"));
                            tvEmailUsuario.setText("Correo: " + documentSnapshot.getString("email"));
                            tvContraseñaUsuario.setText("Contraseña: " + documentSnapshot.getString("contraseña"));
                            tvTelefonoUsuario.setText("Teléfono: " + documentSnapshot.getString("telefono"));
                            tvDireccionUsuario.setText("Dirección: " + documentSnapshot.getString("direccion"));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("ProfileActivity", "Error al obtener los datos", e));

            // Contar tareas activas y completadas
            contarTareas(uid, true);  // Activas
            contarTareas(uid, false); // Completadas
        }

        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        btnCambiarContrasena.setOnClickListener(v -> mostrarDialogoCambioContrasena());
    }

    private void contarTareas(String userId, boolean activas) {
        Query query = db.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", !activas);  // NOT completada = activas

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int total = queryDocumentSnapshots.size();
            if (activas) {
                tvTareasActivas.setText("Tareas activas: " + total);
            } else {
                tvTareasCompletadas.setText("Tareas completadas: " + total);
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileActivity", "Error al contar tareas", e);
            if (activas) {
                tvTareasActivas.setText("Error al contar activas");
            } else {
                tvTareasCompletadas.setText("Error al contar completadas");
            }
        });
    }

    // Menú
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
            finish();
            return true;
        } else if (id == R.id.item_completadas_menu) {
            startActivity(new Intent(ProfileActivity.this, CompletadasActivity.class));
            finish();
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
                                tvContraseñaUsuario.setText("Contraseña: " + nuevaContrasena);
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
