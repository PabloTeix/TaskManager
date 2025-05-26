package com.example.taskmanager.models;

import java.util.Date;

// Esta clase representa el modelo "Tarea", que se utilizará para almacenar y manejar los datos de una tarea.
public class Tarea {

    // Declaración de las propiedades de la clase Tarea.
    String titulo, descripcion;
    Date fecha_inicio;
    Date fecha_fin;
    boolean completada;
    String color; //Para darle un color a la tarea

    // Constructor vacío requerido para Firebase
    public Tarea() {
    }

    // Constructor de la clase Tarea con parámetros para inicializar los valores de la tarea.
    // Este constructor permite crear una nueva tarea

    public Tarea(String titulo, String descripcion, Date fecha_inicio,Date fecha_fin, boolean completada, String color) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.completada = completada;
        this.color = color;
    }

    // Getters y Setters para cada uno de los campos.
    // Estos métodos permiten acceder y modificar los valores de las propiedades de la clase Tarea.

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getTitulo() {

        return titulo;
    }


    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public String getDescripcion() {
        return descripcion;
    }


    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }


    public Date getFecha_fin() {
        return fecha_fin;
    }


    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }
}