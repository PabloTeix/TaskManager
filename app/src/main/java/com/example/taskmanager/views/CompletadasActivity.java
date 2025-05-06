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

    private RecyclerView recyclerView;
    private AdapterTarea adapter;
    private FirebaseFirestore firestore;
    private TextView tvContadorCompletadas;
    private ListenerRegistration listenerRegistro; // Para detener el listener en onStop
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completadas);
        getSupportActionBar().setTitle("Tareas Completadas");

        recyclerView = findViewById(R.id.recyclerViewCompletadas);
        tvContadorCompletadas = findViewById(R.id.tareas_completada);
        firestore = FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Consulta para tareas completadas del usuario actual
        query = firestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", true);

        // Configurar el adaptador con esta consulta
        FirestoreRecyclerOptions<Tarea> options = new FirestoreRecyclerOptions.Builder<Tarea>()
                .setQuery(query, Tarea.class)
                .build();

        adapter = new AdapterTarea(options, this, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // Usar snapshot listener para actualización en tiempo real
    private void contarTareasCompletadasTiempoReal(Query query) {
        listenerRegistro = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, com.google.firebase.firestore.FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(CompletadasActivity.this, "Error al contar tareas", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshots != null) {
                    int total = snapshots.size();
                    tvContadorCompletadas.setText("Has completado un total de: " + total + " tareas");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_completadas_menu) {
            return true;
        } else if (id == R.id.item_main_menu) {
            startActivity(new Intent(CompletadasActivity.this, MainActivity.class));
            finish();
            return true;
        } else if (id == R.id.item_profile) {
            startActivity(new Intent(CompletadasActivity.this, ProfileActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

        contarTareasCompletadasTiempoReal(query); // Iniciar listener
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

        if (listenerRegistro != null) {
            listenerRegistro.remove(); // Detener el listener para evitar fugas de memoria
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar los datos al regresar a la actividad
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
