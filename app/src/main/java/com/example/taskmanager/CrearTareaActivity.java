package com.example.taskmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
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
        mfirestore = FirebaseFirestore.getInstance();

        titulo_tarea = findViewById(R.id.titulo);
        descripcion_tarea = findViewById(R.id.descripcion);
        inicio_tarea = findViewById(R.id.fecha_inicio);
        btnAgregar = findViewById(R.id.button_añadir);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulotarea = titulo_tarea.getText().toString().trim();
                String descripciontarea = descripcion_tarea.getText().toString().trim();
                String fechaInicioTexto = inicio_tarea.getText().toString().trim();

                // Verificamos que la fecha esté en el formato correcto
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date iniciotarea = null;

                try {
                    iniciotarea = formatoFecha.parse(fechaInicioTexto);
                } catch (ParseException e) {
                    e.printStackTrace(); // Podrías mostrar un Toast también
                    Toast.makeText(CrearTareaActivity.this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show();
                }

                // Validamos los campos
                if (titulotarea.isEmpty() || descripciontarea.isEmpty() || fechaInicioTexto.isEmpty() || iniciotarea == null) {
                    Toast.makeText(CrearTareaActivity.this, "Ingresar todos los datos correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    postTarea(titulotarea, descripciontarea, iniciotarea); // Pasamos la fecha como un Date
                }
            }
        });
    }

    private void postTarea(String titulotarea, String descripcionTarea, Date fechaInicio) {
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulotarea);
        map.put("descripcion", descripcionTarea);
        map.put("fecha_inicio", fechaInicio);  // Usamos el objeto Date directamente

        mfirestore.collection("tareas").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Tarea creada exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al ingresar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Deprecated, pero funciona
        return true;
    }
}
