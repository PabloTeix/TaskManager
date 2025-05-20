package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.AdapterTarea;
import com.example.taskmanager.models.Tarea;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class CompletadasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  // RecyclerView para mostrar las tareas completadas
    private AdapterTarea adapter;  // Adaptador para el RecyclerView
    private FirebaseFirestore firestore;  // Instancia de FirebaseFirestore para interactuar con la base de datos
    private TextView tvContadorCompletadas;  // TextView para mostrar el contador de tareas completadas
    private ListenerRegistration listenerRegistro;  // Listener para actualización en tiempo real de las tareas completadas
    private Query query;  // Consulta para obtener las tareas completadas del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completadas);  // Establecemos el layout de la actividad
        getSupportActionBar().setTitle("Tareas Completadas");  // Establecemos el título de la barra de acción

        // Inicialización de las vistas
        recyclerView = findViewById(R.id.recyclerViewCompletadas);
        tvContadorCompletadas = findViewById(R.id.tareas_completada);
        firestore = FirebaseFirestore.getInstance();

        // Obtención del ID del usuario actual para filtrar las tareas por el usuario logueado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Consulta para obtener las tareas del usuario que están marcadas como completadas
        query = firestore.collection("tareas")
                .whereEqualTo("userId", userId)  // Filtramos por el ID del usuario
                .whereEqualTo("completada", true);  // Filtramos por tareas que están marcadas como completadas

        // Configuración del adaptador con la consulta de Firestore
        FirestoreRecyclerOptions<Tarea> options = new FirestoreRecyclerOptions.Builder<Tarea>()
                .setQuery(query, Tarea.class)  // Configuramos la consulta y el tipo de objeto que esperamos
                .build();

        // Inicialización del adaptador y configuración del RecyclerView
        adapter = new AdapterTarea(options, this, true);  // 'true' indica que estamos en la actividad de tareas completadas
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Establecemos el layout manager para el RecyclerView
        recyclerView.setAdapter(adapter);  // Asignamos el adaptador al RecyclerView
    }

    // Método que escucha los cambios en tiempo real para contar las tareas completadas
    private void contarTareasCompletadasTiempoReal(Query query) {
        listenerRegistro = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, com.google.firebase.firestore.FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(CompletadasActivity.this, "Error al contar tareas", Toast.LENGTH_SHORT).show();
                    return;  // Si ocurre un error, mostramos un mensaje y terminamos la ejecución
                }

                if (snapshots != null) {
                    // Obtenemos el total de tareas completadas y actualizamos el TextView
                    int total = snapshots.size();
                    tvContadorCompletadas.setText("Has completado un total de: " + total + " tareas");
                }
            }
        });
    }

    // Inflamos el menú de opciones (como la barra superior)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);  // Inflamos el menú desde el archivo XML
        return true;
    }

    // Método que maneja las opciones seleccionadas en el menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();  // Obtenemos el ID del item seleccionado

        if (id == R.id.item_completadas_menu) {
            return true;  // Si se selecciona "Tareas completadas", no hacemos nada
        } else if (id == R.id.item_main_menu) {
            startActivity(new Intent(CompletadasActivity.this, MainActivity.class));  // Abrimos la actividad principal
            finish();  // Terminamos esta actividad para evitar que el usuario regrese a ella
            return true;
        } else if (id == R.id.item_profile) {
            startActivity(new Intent(CompletadasActivity.this, ProfileActivity.class));  // Abrimos la actividad de perfil
            finish();  // Terminamos esta actividad
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Método llamado cuando la actividad se inicia o se reanuda
    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();  // Iniciamos el listener para escuchar cambios en la base de datos
        }

        contarTareasCompletadasTiempoReal(query);  // Iniciamos el listener para contar las tareas completadas en tiempo real
    }

    // Método llamado cuando la actividad se detiene
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();  // Detenemos el listener del adaptador
        }

        if (listenerRegistro != null) {
            listenerRegistro.remove();  // Detenemos el listener de tareas completadas
        }
    }

    // Método llamado cuando la actividad se reanuda (por ejemplo, al regresar de otra actividad)
    @Override
    protected void onResume() {
        super.onResume();
        // Recargamos los datos al regresar a la actividad
        if (adapter != null) {
            adapter.notifyDataSetChanged();  // Notificamos al adaptador que debe actualizar los datos
        }
    }
}
