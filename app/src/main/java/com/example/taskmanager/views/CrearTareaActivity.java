package com.example.taskmanager.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    // Declaración de vistas (componentes de la UI)
    Button btnAgregar;
    EditText titulo_tarea, descripcion_tarea, inicio_tarea;
    Spinner spinnerColor;

    // Instancias de Firebase
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;

    // Variable para determinar si la tarea está completada
    private boolean tareaCompletada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Habilita el diseño sin barras de borde.
        setContentView(R.layout.activity_crear_tarea);

        this.setTitle("Añadir tarea");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6B3E26")));  // Color marrón
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Agrega un botón de retroceso en la barra de acción.

        // Inicialización de las instancias de Firebase
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Enlazar las vistas con los elementos del layout
        titulo_tarea = findViewById(R.id.titulo);
        descripcion_tarea = findViewById(R.id.descripcion);
        inicio_tarea = findViewById(R.id.fecha_inicio);
        btnAgregar = findViewById(R.id.button_añadir);
        spinnerColor = findViewById(R.id.spinner_color);

        // Crear el adaptador para el Spinner con los colores predefinidos
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.colores_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapter);

        // Configurar la fecha actual como valor predeterminado en el campo "fecha_inicio"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        inicio_tarea.setText(sdf.format(new Date()));

        // Comprobar si se ha recibido un ID de tarea para editar
        String id = getIntent().getStringExtra("id_tarea");

        if (id == null || id.isEmpty()) {
            // Si no hay ID, es una tarea nueva, así que configuramos el botón para crearla
            btnAgregar.setOnClickListener(v -> crearTarea());
        } else {
            // Si existe un ID, es una tarea existente que se está editando
            this.setTitle("Editar tarea");
            btnAgregar.setText("Confirmar cambios");
            cargarDatosTarea(id);
            btnAgregar.setOnClickListener(v -> actualizarTarea(id));
        }
    }

    // Método para crear una nueva tarea
    private void crearTarea() {
        // Obtener los valores introducidos por el usuario
        String titulo = titulo_tarea.getText().toString().trim();
        String descripcion = descripcion_tarea.getText().toString().trim();
        String fechaTexto = inicio_tarea.getText().toString().trim();
        String colorSeleccionado = spinnerColor.getSelectedItem().toString();

        // Validar que los campos no estén vacíos
        if (titulo.isEmpty() || descripcion.isEmpty() || fechaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir la fecha a objeto Date
        Date fecha;
        try {
            fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaTexto);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el ID del usuario actual desde Firebase Authentication
        String userId = mAuth.getCurrentUser().getUid();

        // Crear un mapa con los datos de la tarea
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("descripcion", descripcion);
        map.put("fecha_inicio", fecha);
        map.put("userId", userId);
        map.put("completada", false);  // Por defecto, la tarea no está completada
        map.put("color", colorSeleccionado);

        // Guardar la tarea en Firestore
        mfirestore.collection("tareas").add(map)
                .addOnSuccessListener(documentReference -> {
                    // Mostrar un mensaje de éxito y cerrar la actividad después de un pequeño retraso
                    Toast.makeText(getApplicationContext(), "Tarea creada exitosamente", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(this::finish, 1000);
                })
                .addOnFailureListener(e -> {
                    // Mostrar un mensaje de error si ocurre algún fallo
                    Toast.makeText(getApplicationContext(), "Error al ingresar datos", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    // Método para actualizar una tarea existente
    private void actualizarTarea(String id) {
        // Obtener los valores introducidos por el usuario
        String titulo = titulo_tarea.getText().toString().trim();
        String descripcion = descripcion_tarea.getText().toString().trim();
        String fechaTexto = inicio_tarea.getText().toString().trim();
        String colorSeleccionado = spinnerColor.getSelectedItem().toString();

        // Validar que los campos no estén vacíos
        if (titulo.isEmpty() || descripcion.isEmpty() || fechaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir la fecha a objeto Date
        Date fecha;
        try {
            fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaTexto);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con los datos actualizados
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("descripcion", descripcion);
        map.put("fecha_inicio", fecha);
        map.put("completada", tareaCompletada);  // Mantener el estado de completado
        map.put("color", colorSeleccionado);

        // Actualizar la tarea en Firestore
        mfirestore.collection("tareas").document(id).update(map)
                .addOnSuccessListener(unused -> {
                    // Mostrar un mensaje de éxito y cerrar la actividad
                    Toast.makeText(getApplicationContext(), "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Mostrar un mensaje de error si ocurre algún fallo
                    Toast.makeText(getApplicationContext(), "Error al actualizar la tarea", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para cargar los datos de una tarea existente para editarla
    private void cargarDatosTarea(String id) {
        mfirestore.collection("tareas").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Obtener los datos de la tarea desde Firestore
                    String titulo = documentSnapshot.getString("titulo");
                    String descripcion = documentSnapshot.getString("descripcion");
                    Date fecha = documentSnapshot.getDate("fecha_inicio");
                    String color = documentSnapshot.getString("color");
                    Boolean completada = documentSnapshot.getBoolean("completada");

                    // Establecer los valores en los campos de la interfaz
                    titulo_tarea.setText(titulo);
                    descripcion_tarea.setText(descripcion);
                    if (fecha != null) {
                        String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha);
                        inicio_tarea.setText(fechaFormateada);
                    }

                    // Establecer el color seleccionado en el Spinner
                    if (color != null) {
                        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerColor.getAdapter();
                        int pos = adapter.getPosition(color);
                        spinnerColor.setSelection(pos);
                    }

                    // Establecer si la tarea está completada o no
                    if (completada != null) {
                        tareaCompletada = completada;
                    }
                })
                .addOnFailureListener(e -> {
                    // Mostrar un mensaje de error si no se pueden obtener los datos
                    Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para manejar la navegación hacia atrás
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Método para manejar el comportamiento de retroceso
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
