package com.example.taskmanager.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.AdapterTarea;
import com.example.taskmanager.models.Tarea;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CompletadasActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterTarea adapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completadas); // Asegúrate de tener este layout

        recyclerView = findViewById(R.id.recyclerViewCompletadas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser().getUid();

        Query query = firestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", true); // Solo tareas completadas

        FirestoreRecyclerOptions<Tarea> options = new FirestoreRecyclerOptions.Builder<Tarea>()
                .setQuery(query, Tarea.class)
                .build();

        adapter = new AdapterTarea(options, this);
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

