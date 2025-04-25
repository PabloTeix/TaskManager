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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_tarea);

        this.setTitle("Añadir tarea");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra("id_tarea");
        mfirestore = FirebaseFirestore.getInstance();

        titulo_tarea = findViewById(R.id.titulo);
        descripcion_tarea = findViewById(R.id.descripcion);
        inicio_tarea = findViewById(R.id.fecha_inicio);
        btnAgregar = findViewById(R.id.button_añadir);

        if(id == null || id == ""){
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtener los valores ingresados
                    String titulotarea = titulo_tarea.getText().toString().trim();
                    String descripciontarea = descripcion_tarea.getText().toString().trim();
                    String fechaInicioTexto = inicio_tarea.getText().toString().trim();

                    // Verificar que los campos no estén vacíos
                    if (titulotarea.isEmpty() || descripciontarea.isEmpty() || fechaInicioTexto.isEmpty()) {
                        Toast.makeText(CrearTareaActivity.this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
                        return; // Evitar continuar si algún campo está vacío
                    }

                    // Verificar el formato de la fecha
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date iniciotarea = null;
                    try {
                        iniciotarea = formatoFecha.parse(fechaInicioTexto);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(CrearTareaActivity.this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
                        return; // No continuar si la fecha tiene un formato incorrecto
                    }

                    // Llamar al método para guardar la tarea si todo es válido
                    postTarea(titulotarea, descripciontarea, iniciotarea);
                }
            });

        }else{
            this.setTitle("Editar tarea");
            btnAgregar.setText("Confirmar cambios");
            getTarea(id);
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String titulotarea = titulo_tarea.getText().toString().trim();
                    String descripciontarea = descripcion_tarea.getText().toString().trim();
                    String fechaInicioTexto = inicio_tarea.getText().toString().trim();

                    if (titulotarea.isEmpty() || descripciontarea.isEmpty() || fechaInicioTexto.isEmpty()) {
                        Toast.makeText(CrearTareaActivity.this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
                        return; // Evitar continuar si algún campo está vacío
                    }

                    // Verificar el formato de la fecha
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date iniciotarea = null;
                    try {
                        iniciotarea = formatoFecha.parse(fechaInicioTexto);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(CrearTareaActivity.this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
                        return; // No continuar si la fecha tiene un formato incorrecto
                    }

                    // Llamar al método para guardar la tarea si todo es válido
                    updateTarea(titulotarea, descripciontarea, iniciotarea,id);



                }
            });

        }






        // Definir el evento de clic para agregar tarea
   /*     btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados
                String titulotarea = titulo_tarea.getText().toString().trim();
                String descripciontarea = descripcion_tarea.getText().toString().trim();
                String fechaInicioTexto = inicio_tarea.getText().toString().trim();

                // Verificar que los campos no estén vacíos
                if (titulotarea.isEmpty() || descripciontarea.isEmpty() || fechaInicioTexto.isEmpty()) {
                    Toast.makeText(CrearTareaActivity.this, "Por favor ingresa todos los datos", Toast.LENGTH_SHORT).show();
                    return; // Evitar continuar si algún campo está vacío
                }

                // Verificar el formato de la fecha
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date iniciotarea = null;
                try {
                    iniciotarea = formatoFecha.parse(fechaInicioTexto);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(CrearTareaActivity.this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
                    return; // No continuar si la fecha tiene un formato incorrecto
                }

                // Llamar al método para guardar la tarea si todo es válido
                postTarea(titulotarea, descripciontarea, iniciotarea);
            }
        }); */
    }

    private void updateTarea(String titulotarea, String descripcionTarea, Date fechaInicio, String id) {
        // Crear el mapa de datos para Firestore
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulotarea);
        map.put("descripcion", descripcionTarea);
        map.put("fecha_inicio", fechaInicio);

        mfirestore.collection("tareas").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al actualizar la tarea", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void postTarea(String titulotarea, String descripcionTarea, Date fechaInicio) {
        // Crear el mapa de datos para Firestore
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulotarea);
        map.put("descripcion", descripcionTarea);
        map.put("fecha_inicio", fechaInicio);

        // Agregar tarea a Firestore
        mfirestore.collection("tareas").add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Tarea creada exitosamente", Toast.LENGTH_SHORT).show();
                        // Usar un Handler para asegurar que la tarea se haya guardado correctamente antes de cerrar
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish(); // Cerrar la actividad después de un pequeño retraso
                            }
                        }, 1000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al ingresar datos", Toast.LENGTH_SHORT).show();
                        e.printStackTrace(); // Esto ayudará a depurar si el error persiste
                    }
                });
    }
    private void getTarea(String id){
        mfirestore.collection("tareas").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String tituloTarea = documentSnapshot.getString("titulo");
                String descripcionTarea = documentSnapshot.getString("descripcion");
                Date inicioTarea = documentSnapshot.getDate("fecha_inicio");

                titulo_tarea.setText(tituloTarea);
                descripcion_tarea.setText(descripcionTarea);

                if (inicioTarea != null) {
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String fechaFormateada = formatoFecha.format(inicioTarea);
                    inicio_tarea.setText(fechaFormateada);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Manejar la acción de retroceder en el ActionBar
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Llamar al comportamiento estándar para el retroceso
    }
}
