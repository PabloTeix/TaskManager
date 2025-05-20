package com.example.taskmanager.models;

import java.util.Date;

// Esta clase representa el modelo "Tarea", que se utilizará para almacenar y manejar los datos de una tarea.
public class Tarea {

    // Declaración de las propiedades de la clase Tarea.
    String titulo, descripcion; // El título y la descripción de la tarea.
    Date fecha_inicio; // La fecha en que se inicia la tarea.
    Date fecha_fin;
    boolean completada;
    String color;

    // Constructor vacío requerido para Firebase (para deserialización)
    public Tarea() {
    }

    // Constructor de la clase Tarea con parámetros para inicializar los valores de la tarea.
    // Este constructor permite crear una nueva tarea especificando su título, descripción y fecha de inicio.

    public Tarea(String titulo, String descripcion, Date fecha_inicio,Date fecha_fin, boolean completada, String color) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.completada = completada;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    // Getters y Setters para cada uno de los campos.
    // Estos métodos permiten acceder y modificar los valores de las propiedades de la clase Tarea.

    // Obtener el título de la tarea.
    public String getTitulo() {
        return titulo; // Devuelve el título de la tarea.
    }

    // Establecer un nuevo valor para el título de la tarea.
    public void setTitulo(String titulo) {
        this.titulo = titulo; // Asigna el valor del título a la propiedad de la tarea.
    }

    // Obtener la descripción de la tarea.
    public String getDescripcion() {
        return descripcion; // Devuelve la descripción de la tarea.
    }

    // Establecer una nueva descripción para la tarea.
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion; // Asigna el valor de la descripción a la propiedad de la tarea.
    }

    // Obtener la fecha de inicio de la tarea.
    public Date getFecha_inicio() {
        return fecha_inicio; // Devuelve la fecha de inicio de la tarea.
    }

    // Establecer una nueva fecha de inicio para la tarea.
    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio; // Asigna la nueva fecha de inicio a la tarea.
    }

    // Obtener la fecha de fin de la tarea.
    public Date getFecha_fin() {
        return fecha_fin; // Devuelve la fecha de fin de la tarea.
    }

    // Establecer una nueva fecha de fin para la tarea.
    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin; // Asigna la nueva fecha de fin a la tarea.
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }
}