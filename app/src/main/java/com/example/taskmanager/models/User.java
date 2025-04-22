package com.example.taskmanager.models;

//import java.util.List;

public class User {

    private String fullName;
    private String email;
    private String phone;
    private String address;
 //   private List<String> tareas;  // Lista de tareas del usuario

    // Constructor vacío requerido para Firebase
    public User() {
    }

    // Constructor para crear un usuario con datos
    public User(String fullName, String email, String phone, String address) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Métodos getter y setter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
