package com.example.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.taskmanager.views.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.adapter.AdapterTarea;
import com.example.taskmanager.models.Tarea;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button btnAgregar,btn_Cerrar;
    RecyclerView mRecycler;
    AdapterTarea mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Firebase Firestore instance
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Set query to fetch tasks from Firestore
        Query query = mFirestore.collection("tareas");

        // FirestoreRecyclerOptions to bind data to RecyclerView
        FirestoreRecyclerOptions<Tarea> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarea>().setQuery(query, Tarea.class).build();

        // Adapter for RecyclerView
        mAdapter = new AdapterTarea(firestoreRecyclerOptions,this);
        mRecycler.setAdapter(mAdapter);

        btn_Cerrar = findViewById(R.id.btn_cerrar);
        btn_Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });



        // Set up "Add Task" button
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to "Create Task" activity
                startActivity(new Intent(MainActivity.this, CrearTareaActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening(); // Start listening for Firestore updates
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening(); // Stop listening for updates when activity is not visible
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged(); // Force RecyclerView to refresh when returning to MainActivity
        }
    }
}
