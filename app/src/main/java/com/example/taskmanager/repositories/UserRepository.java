package com.example.taskmanager.repositories;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.taskmanager.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRepository {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public UserRepository() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
    }

    // Método para registrar un nuevo usuario
    public void registerUser(String email, String password, String fullName, String phone, String address, final OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Crear un objeto User con los datos adicionales
                            User newUser = new User(fullName, email, phone, address);

                            // Guardar los datos en Firebase Realtime Database
                            mDatabase.child(user.getUid()).setValue(newUser)
                                    .addOnCompleteListener(dbTask -> {
                                        // Llamar al listener después de que la base de datos haya guardado los datos del usuario
                                        listener.onComplete(task); // Llamamos al listener del método original
                                    });
                        }
                    } else {
                        listener.onComplete(task); // Llamar al listener en caso de error
                    }
                });
    }

    // Método para iniciar sesión
    public void loginUser(String email, String password, final OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);  // Devolver el resultado de la autenticación
    }

    // Método para cerrar sesión
    public void logoutUser() {
        mAuth.signOut();
    }

    // Método para obtener el usuario actual
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
}
