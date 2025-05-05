package com.example.taskmanager.views;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrearTareaActivity extends AppCompatActivity {

    Button btnAgregar;
    EditText titulo_tarea, descripcion_tarea, inicio_tarea;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;
    private boolean tareaCompletada = false; // <- se usará al editar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_tarea);

        this.setTitle("Añadir tarea");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        titulo_tarea = findViewById(R.id.titulo);
        descripcion_tarea = findViewById(R.id.descripcion);
        inicio_tarea = findViewById(R.id.fecha_inicio);
        btnAgregar = findViewById(R.id.button_añadir);

        // Establecer la fecha actual en el campo de fecha de inicio
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaActual = sdf.format(new Date());
        inicio_tarea.setText(fechaActual);

        String id = getIntent().getStringExtra("id_tarea");

        if (id == null || id.isEmpty()) {
            btnAgregar.setOnClickListener(v -> {
                String titulotarea = titulo_tarea.getText().toString().trim();
                String descripciontarea = descripcion_tarea.getText().toString().trim();
                String fechaInicioTexto = inicio_tarea.getText().toString().trim();

                if (titulotarea.isEmpty() || descripciontarea.isEmpty() || fechaInicioTexto.isEmpty()) {
                    Toast.makeText(this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                Date iniciotarea;
                try {
                    iniciotarea = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaInicioTexto);
                } catch (ParseException e) {
                    Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
                    return;
                }

                postTarea(titulotarea, descripciontarea, iniciotarea);
            });
        } else {
            this.setTitle("Editar tarea");
            btnAgregar.setText("Confirmar cambios");
            getTarea(id);

            btnAgregar.setOnClickListener(v -> {
                String titulotarea = titulo_tarea.getText().toString().trim();
                String descripciontarea = descripcion_tarea.getText().toString().trim();
                String fechaInicioTexto = inicio_tarea.getText().toString().trim();

                if (titulotarea.isEmpty() || descripciontarea.isEmpty() || fechaInicioTexto.isEmpty()) {
                    Toast.makeText(this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                Date iniciotarea;
                try {
                    iniciotarea = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaInicioTexto);
                } catch (ParseException e) {
                    Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateTarea(titulotarea, descripciontarea, iniciotarea, id, tareaCompletada);
            });
        }
    }

    private void updateTarea(String titulo, String descripcion, Date fecha, String id, boolean completada) {
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("descripcion", descripcion);
        map.put("fecha_inicio", fecha);
        map.put("completada", completada); // mantener el estado actual

        mfirestore.collection("tareas").document(id).update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al actualizar la tarea", Toast.LENGTH_SHORT).show());
    }

    private void postTarea(String titulo, String descripcion, Date fecha) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("descripcion", descripcion);
        map.put("fecha_inicio", fecha);
        map.put("userId", userId);
        map.put("completada", false); // nueva tarea = incompleta

        mfirestore.collection("tareas").add(map)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Tarea creada exitosamente", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(this::finish, 1000);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Error al ingresar datos", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void getTarea(String id) {
        mfirestore.collection("tareas").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String titulo = documentSnapshot.getString("titulo");
                    String descripcion = documentSnapshot.getString("descripcion");
                    Date fecha = documentSnapshot.getDate("fecha_inicio");
                    Boolean completada = documentSnapshot.getBoolean("completada");

                    titulo_tarea.setText(titulo);
                    descripcion_tarea.setText(descripcion);
                    if (fecha != null) {
                        String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha);
                        inicio_tarea.setText(fechaFormateada);
                    }

                    if (completada != null) {
                        tareaCompletada = completada;
                    }

                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
