package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.AdapterTarea;
import com.example.taskmanager.models.Tarea;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    Button btnAgregar;
    RecyclerView mRecycler;
    AdapterTarea mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SearchView search_view;
    TextView tvContadorActivas;
    ListenerRegistration listenerRegistroActivas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();

        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        Query query = mFirestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", false);

        FirestoreRecyclerOptions<Tarea> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarea>()
                        .setQuery(query, Tarea.class)
                        .build();

        mAdapter = new AdapterTarea(firestoreRecyclerOptions, this, false);
        mRecycler.setAdapter(mAdapter);

        tvContadorActivas = findViewById(R.id.tareas_activas);
        search_view = findViewById(R.id.search);
        search_view.setQueryHint("Búsqueda por título");

        btnAgregar = findViewById(R.id.btnAgregar);

        btnAgregar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CrearTareaActivity.class)));
        search_view();
    }

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

    public void textSearch(String queryText) {
        String userId = mAuth.getCurrentUser().getUid();
        Query query = mFirestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", false)
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

    private void contarTareasActivas() {
        String userId = mAuth.getCurrentUser().getUid();
        Query queryActivas = mFirestore.collection("tareas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completada", false);

        listenerRegistroActivas = queryActivas.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(MainActivity.this, "Error al contar tareas activas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            finish();
            return true;
        } else if (id == R.id.item_main_menu) {
            return true;
        } else if (id == R.id.item_completadas_menu) {
            startActivity(new Intent(MainActivity.this, CompletadasActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
