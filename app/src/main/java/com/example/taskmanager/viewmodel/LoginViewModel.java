package com.example.taskmanager.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private FirebaseAuth mAuth;
    private MutableLiveData<String> statusMessage;
    private MutableLiveData<Boolean> isLoggingIn;
    private MutableLiveData<FirebaseUser> loggedInUser;

    public LoginViewModel() {
        mAuth = FirebaseAuth.getInstance();
        statusMessage = new MutableLiveData<>();
        isLoggingIn = new MutableLiveData<>(false);
        loggedInUser = new MutableLiveData<>();
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public LiveData<Boolean> getIsLoggingIn() {
        return isLoggingIn;
    }

    public LiveData<FirebaseUser> getLoggedInUser() {
        return loggedInUser;
    }

    public void loginUser(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            statusMessage.setValue("Por favor, ingresa todos los campos");
            return;
        }

        isLoggingIn.setValue(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    isLoggingIn.setValue(false);
                    if (task.isSuccessful()) {
                        // Si el login es exitoso, obtener el usuario actual
                        FirebaseUser user = mAuth.getCurrentUser();
                        loggedInUser.setValue(user);
                    } else {
                        // Si el login falla, mostrar el error
                        statusMessage.setValue("Error en el login: " + task.getException().getMessage());
                    }
                });
    }
}

