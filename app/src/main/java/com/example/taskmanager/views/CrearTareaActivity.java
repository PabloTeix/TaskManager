package com.example.taskmanager.views;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.R;
import com.google.firebase.auth.FirebaseAuth;
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
    Spinner spinnerColor;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;
    private boolean tareaCompletada = false;

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
        spinnerColor = findViewById(R.id.spinner_color);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.colores_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapter);

        // Fecha por defecto actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        inicio_tarea.setText(sdf.format(new Date()));

        String id = getIntent().getStringExtra("id_tarea");

        if (id == null || id.isEmpty()) {
            btnAgregar.setOnClickListener(v -> crearTarea());
        } else {
            this.setTitle("Editar tarea");
            btnAgregar.setText("Confirmar cambios");
            cargarDatosTarea(id);
            btnAgregar.setOnClickListener(v -> actualizarTarea(id));
        }
    }

    private void crearTarea() {
        String titulo = titulo_tarea.getText().toString().trim();
        String descripcion = descripcion_tarea.getText().toString().trim();
        String fechaTexto = inicio_tarea.getText().toString().trim();
        String colorSeleccionado = spinnerColor.getSelectedItem().toString();

        if (titulo.isEmpty() || descripcion.isEmpty() || fechaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        Date fecha;
        try {
            fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaTexto);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("descripcion", descripcion);
        map.put("fecha_inicio", fecha);
        map.put("userId", userId);
        map.put("completada", false);
        map.put("color", colorSeleccionado);

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

    private void actualizarTarea(String id) {
        String titulo = titulo_tarea.getText().toString().trim();
        String descripcion = descripcion_tarea.getText().toString().trim();
        String fechaTexto = inicio_tarea.getText().toString().trim();
        String colorSeleccionado = spinnerColor.getSelectedItem().toString();

        if (titulo.isEmpty() || descripcion.isEmpty() || fechaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        Date fecha;
        try {
            fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaTexto);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("descripcion", descripcion);
        map.put("fecha_inicio", fecha);
        map.put("completada", tareaCompletada);
        map.put("color", colorSeleccionado);

        mfirestore.collection("tareas").document(id).update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al actualizar la tarea", Toast.LENGTH_SHORT).show());
    }

    private void cargarDatosTarea(String id) {
        mfirestore.collection("tareas").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String titulo = documentSnapshot.getString("titulo");
                    String descripcion = documentSnapshot.getString("descripcion");
                    Date fecha = documentSnapshot.getDate("fecha_inicio");
                    String color = documentSnapshot.getString("color");
                    Boolean completada = documentSnapshot.getBoolean("completada");

                    titulo_tarea.setText(titulo);
                    descripcion_tarea.setText(descripcion);
                    if (fecha != null) {
                        String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha);
                        inicio_tarea.setText(fechaFormateada);
                    }

                    if (color != null) {
                        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerColor.getAdapter();
                        int pos = adapter.getPosition(color);
                        spinnerColor.setSelection(pos);
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