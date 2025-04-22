package com.example.taskmanager;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText edtTitulo, edtDescripcion;
    private Button btnAgregar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar la base de datos de Firebase
        db = FirebaseFirestore.getInstance();

        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        btnAgregar = findViewById(R.id.btnAgregar);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = edtTitulo.getText().toString().trim();
                String descripcion = edtDescripcion.getText().toString().trim();

                if (!titulo.isEmpty() && !descripcion.isEmpty()) {
                    // Crear un objeto de tarea con título y descripción
                    Tarea nuevaTarea = new Tarea(titulo, descripcion);

                    // Subir la tarea a Firebase
                    db.collection("tareas")
                            .add(nuevaTarea)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(MainActivity.this, "Tarea agregada", Toast.LENGTH_SHORT).show();
                                edtTitulo.setText(""); // Limpiar el campo de título
                                edtDescripcion.setText(""); // Limpiar el campo de descripción
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this, "Error al agregar tarea", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Por favor ingresa un título y una descripción", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Clase para representar una tarea con título y descripción
    public static class Tarea {
        private String titulo;
        private String descripcion;

        // Constructor
        public Tarea(String titulo, String descripcion) {
            this.titulo = titulo;
            this.descripcion = descripcion;
        }

        // Getters y Setters
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
    }
}


