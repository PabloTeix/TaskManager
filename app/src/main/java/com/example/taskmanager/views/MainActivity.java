package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.AdapterTarea;
import com.example.taskmanager.models.Tarea;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MainActivity extends AppCompatActivity {

    Button btnAgregar, btn_Cerrar;
    RecyclerView mRecycler;
    AdapterTarea mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SearchView search_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Usuario no logueado; redirigir al login o mostrar error
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Evita que siga ejecutando MainActivity
            return;
        }

        String userId = currentUser.getUid();  // ✅ Solo accedemos si no es null

        // Configurar RecyclerView
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Consultar solo las tareas del usuario actual y no completadas
        Query query = mFirestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", false);  // Solo tareas no completadas

        FirestoreRecyclerOptions<Tarea> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarea>()
                        .setQuery(query, Tarea.class)
                        .build();

        // Asignar adaptador
        mAdapter = new AdapterTarea(firestoreRecyclerOptions, this, false);
        mRecycler.setAdapter(mAdapter);

        // Botones y búsqueda
        search_view = findViewById(R.id.search);
        btn_Cerrar = findViewById(R.id.btn_cerrar);
        btnAgregar = findViewById(R.id.btnAgregar);

        btn_Cerrar.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        btnAgregar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CrearTareaActivity.class)));

        search_view();  // Configurar búsqueda
    }

    // Configuración de búsqueda
    private void search_view() {
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textSearch(newText);
                return false;
            }
        });
    }

    // Búsqueda filtrada por usuario y título
    public void textSearch(String queryText) {
        String userId = mAuth.getCurrentUser().getUid();
        Query query = mFirestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", false) // Solo tareas no completadas
                .orderBy("titulo")
                .startAt(queryText)
                .endAt(queryText + "~");

        FirestoreRecyclerOptions<Tarea> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarea>()
                        .setQuery(query, Tarea.class)
                        .build();

        mAdapter.updateOptions(firestoreRecyclerOptions);
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    // Inflar el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Inflar el archivo XML del menú
        return true;
    }

    // Manejar la selección del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // Guardar el ID para evitar errores de constantes no reconocidas

        if (id == R.id.item_profile) {
            // Redirigir a la actividad del perfil
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        } else if (id == R.id.item_main_menu) {
            // Si ya estás en el menú principal, no hacer nada
            return true;
        }else if (id == R.id.item_completadas_menu){
            startActivity(new Intent(MainActivity.this, CompletadasActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item); // Manejo predeterminado
    }
}
