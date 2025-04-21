package com.example.taskmanager.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//import com.example.taskmanager.views.RegisterActivity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.auth.User;

public class RegisterViewModel extends ViewModel {

    private FirebaseAuth mAuth;
    private MutableLiveData<String> statusMessage;
    private MutableLiveData<Boolean> isRegistering;

    public RegisterViewModel() {
        mAuth = FirebaseAuth.getInstance();
        statusMessage = new MutableLiveData<>();
        isRegistering = new MutableLiveData<>(false);
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public LiveData<Boolean> getIsRegistering() {
        return isRegistering;
    }

    public void registerUser(String email, String password, String confirmPassword,
                             String fullName, String phone, String address) {
        // Validación de los campos
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            statusMessage.setValue("Por favor, complete todos los campos");
            return;
        }

        // Verificar si las contraseñas coinciden
        if (!password.equals(confirmPassword)) {
            statusMessage.setValue("Las contraseñas no coinciden");
            return;
        }

        // Iniciar el proceso de registro
        isRegistering.setValue(true);

        // Registrar usuario con Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    isRegistering.setValue(false);

                    if (task.isSuccessful()) {
                        // Obtener el ID del usuario y almacenar los datos adicionales en Firebase Realtime Database
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(fullName, phone, address);

                        FirebaseDatabase.getInstance().getReference("Users").child(userId).setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        statusMessage.setValue("Registro exitoso");
                                    } else {
                                        statusMessage.setValue("Error al guardar los datos");
                                    }
                                });
                    } else {
                        // Si el registro falla, mostrar el mensaje de error
                        statusMessage.setValue("Error en el registro: " + task.getException().getMessage());
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

