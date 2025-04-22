package com.example.taskmanager.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskmanager.repositories.UserRepository;
import com.google.firebase.auth.AuthResult;

public class RegisterViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<String> statusMessage;
    private MutableLiveData<Boolean> isRegistering;

    public RegisterViewModel() {
        userRepository = new UserRepository();
        statusMessage = new MutableLiveData<>();
        isRegistering = new MutableLiveData<>(false);
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public LiveData<Boolean> getIsRegistering() {
        return isRegistering;
    }

    // Método para registrar al usuario
    public void registerUser(String email, String password, String confirmPassword, String fullName, String phone, String address) {
        // Validar los datos
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            statusMessage.setValue("Por favor, complete todos los campos.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusMessage.setValue("Las contraseñas no coinciden.");
            return;
        }

        isRegistering.setValue(true);  // Empieza el registro

        // Llamar al repositorio para registrar al usuario
        userRepository.registerUser(email, password, fullName, phone, address, task -> {
            if (task.isSuccessful()) {
                statusMessage.setValue("Registro exitoso");
            } else {
                statusMessage.setValue("Error en el registro: " + task.getException().getMessage());
            }
            isRegistering.setValue(false);  // Terminar el proceso de registro
        });
    }
}
