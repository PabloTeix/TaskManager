package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanager.R;
import com.example.taskmanager.adapter.AdapterTarea;
import com.example.taskmanager.models.Tarea;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CompletadasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterTarea adapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completadas);
        getSupportActionBar().setTitle("Tareas Completadas");

        recyclerView = findViewById(R.id.recyclerViewCompletadas);
        firestore = FirebaseFirestore.getInstance();

        // Obtener las tareas completadas del usuario actual
        Query query = firestore.collection("tareas")
                .whereEqualTo("completada", true); // Filtrar solo las tareas completadas

        FirestoreRecyclerOptions<Tarea> options = new FirestoreRecyclerOptions.Builder<Tarea>()
                .setQuery(query, Tarea.class)
                .build();

        // Crear el adaptador y pasar true para indicar que es la pantalla de tareas completadas
        adapter = new AdapterTarea(options, this, true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
            return true;
        }else if(id == R.id.item_profile) {
            startActivity(new Intent(CompletadasActivity.this, ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
