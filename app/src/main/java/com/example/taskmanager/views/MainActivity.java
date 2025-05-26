package com.example.taskmanager.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.AdapterTarea;
import com.example.taskmanager.models.Tarea;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    // Componentes de la interfaz de usuario
    private FloatingActionButton fabAgregarTarea;
    private RecyclerView mRecycler;
    private AdapterTarea mAdapter;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private SearchView search_view;
    private TextView tvContadorActivas;
    private ListenerRegistration listenerRegistroActivas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6B3E26")));  // Color marrón
        setContentView(R.layout.activity_main);

        // Inicialización de Firestore y FirebaseAuth
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Verificar si el usuario está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Si no está autenticado, redirigir a la pantalla de login
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Obtener el ID del usuario actual
        String userId = currentUser.getUid();

        // Configuración del RecyclerView para mostrar las tareas
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Crear una consulta a Firestore para obtener las tareas activas del usuario
        Query query = mFirestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", false);

        // Configuración del adaptador con las opciones de Firestore
        FirestoreRecyclerOptions<Tarea> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarea>()
                        .setQuery(query, Tarea.class)
                        .build();

        // Crear el adaptador y asignarlo al RecyclerView
        mAdapter = new AdapterTarea(firestoreRecyclerOptions, this, false);
        mRecycler.setAdapter(mAdapter);


        tvContadorActivas = findViewById(R.id.tareas_activas);
        search_view = findViewById(R.id.search);
        search_view.setQueryHint("Búsqueda por título");

        // Configurar el botón flotante para agregar nuevas tareas
        fabAgregarTarea = findViewById(R.id.fabAgregarTarea);
        fabAgregarTarea.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CrearTareaActivity.class)));


        configurarBuscador();
    }

    // Método para configurar el comportamiento del buscador
    private void configurarBuscador() {
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                realizarBusqueda(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                realizarBusqueda(newText);
                return false;
            }
        });
    }

    // Método para realizar la búsqueda de tareas por título
    private void realizarBusqueda(String texto) {
        String userId = mAuth.getCurrentUser().getUid();
        // Crear una nueva consulta con los filtros de búsqueda
        Query query = mFirestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", false)
                .orderBy("titulo")
                .startAt(texto)
                .endAt(texto + "\uf8ff");

        // Actualizar las opciones del adaptador con la nueva consulta
        FirestoreRecyclerOptions<Tarea> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarea>()
                        .setQuery(query, Tarea.class)
                        .build();

        // Actualizar el adaptador con los nuevos datos
        mAdapter.updateOptions(firestoreRecyclerOptions);
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
    }

    // Método para contar el número de tareas activas del usuario
    private void contarTareasActivas() {
        String userId = mAuth.getCurrentUser().getUid();
        Query queryActivas = mFirestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", false);

        // Registrar un listener para escuchar los cambios en las tareas activas
        listenerRegistroActivas = queryActivas.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                // Si ocurre un error al obtener las tareas activas
                Toast.makeText(MainActivity.this, "Error al contar tareas activas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                // Actualizar el contador con el número de tareas activas
                int totalActivas = snapshots.size();
                tvContadorActivas.setText("Tienes activas: " + totalActivas + " tareas");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        contarTareasActivas();

        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }

        if (listenerRegistroActivas != null) {
            listenerRegistroActivas.remove();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    // Método para crear el menú de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Método para manejar la selección de opciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_profile) {
            // Si se selecciona "Perfil", abrir la actividad de perfil
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            finish();
            return true;
        } else if (id == R.id.item_main_menu) {
            return true;
        } else if (id == R.id.item_completadas_menu) {
            // Si se selecciona "Completadas", abrir la actividad de tareas completadas
            startActivity(new Intent(MainActivity.this, CompletadasActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
