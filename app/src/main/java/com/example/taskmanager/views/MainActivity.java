package com.example.taskmanager.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.taskmanager.R;
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

    Button btnAgregar, btn_Cerrar;
    RecyclerView mRecycler;
    AdapterTarea mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SearchView search_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Habilitar la funcionalidad de bordes a bordes para la actividad
        setContentView(R.layout.activity_main); // Establecer el layout de la actividad principal

        // Inicialización de Firebase
        mFirestore = FirebaseFirestore.getInstance(); // Obtener la instancia de Firestore
        mAuth = FirebaseAuth.getInstance(); // Obtener la instancia de FirebaseAuth (autenticación)

        // Configurar el RecyclerView para mostrar las tareas
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this)); // Usar un LayoutManager para mostrar las tareas de forma vertical

        // Crear una consulta de Firestore para obtener las tareas de la colección "tareas"
        Query query = mFirestore.collection("tareas");

        // Crear un objeto FirestoreRecyclerOptions para asociar la consulta con el RecyclerView
        FirestoreRecyclerOptions<Tarea> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarea>().setQuery(query, Tarea.class).build();

        // Inicializar el adaptador del RecyclerView con los datos obtenidos de Firestore
        mAdapter = new AdapterTarea(firestoreRecyclerOptions, this);
        mRecycler.setAdapter(mAdapter); // Establecer el adaptador al RecyclerView

        // Configurar el SearchView para buscar tareas
        search_view = findViewById(R.id.search);
        setUpRecyclerView(); // Método que prepara el RecyclerView (aunque en este caso no hace nada adicional)
        search_view(); // Configurar el SearchView para realizar búsquedas

        // Configuración del botón "Cerrar sesión"
        btn_Cerrar = findViewById(R.id.btn_cerrar);
        btn_Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut(); // Cerrar sesión en Firebase
                finish(); // Terminar la actividad actual
                startActivity(new Intent(MainActivity.this, LoginActivity.class)); // Navegar a la actividad de login
            }
        });

        // Configuración del botón "Agregar tarea"
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la actividad de crear una nueva tarea
                startActivity(new Intent(MainActivity.this, CrearTareaActivity.class));
            }
        });
    }

    // Este método está marcado con @SuppressLint porque el código no realiza una acción en este método
    // (el método solo se invoca en onCreate(), pero no tiene lógica adicional).
    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {
        // La configuración del RecyclerView ya se maneja en el método onCreate(), por lo que no es necesario hacer nada adicional aquí
    }

    // Configurar el SearchView para realizar búsquedas en las tareas
    private void search_view() {
        // Establecer el listener para capturar las acciones de texto en el SearchView
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearch(query); // Llamar al método de búsqueda con el texto ingresado
                return false; // No hacer nada adicional al enviar la consulta
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textSearch(newText); // Llamar al método de búsqueda cuando el texto cambia
                return false; // No hacer nada adicional al cambiar el texto
            }
        });
    }

    // Método que realiza la búsqueda en la base de datos de Firestore según el texto ingresado
    public void textSearch(String queryText) {
        // Definir la consulta basada en el texto ingresado por el usuario en el SearchView
        Query query = mFirestore.collection("tareas")
                .orderBy("titulo") // Ordenar los resultados por el título de la tarea
                .startAt(queryText) // Iniciar la búsqueda desde el texto ingresado
                .endAt(queryText + "~"); // Finalizar la búsqueda hasta el texto ingresado con el carácter "~" al final

        // Crear nuevas opciones para el adaptador con la nueva consulta
        FirestoreRecyclerOptions<Tarea> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarea>()
                        .setQuery(query, Tarea.class)
                        .build();

        // Actualizar las opciones del adaptador y comenzar a escuchar los resultados de Firestore
        mAdapter.updateOptions(firestoreRecyclerOptions); // Actualizar las opciones del adaptador con la nueva consulta
        mAdapter.startListening(); // Iniciar la escucha de cambios en la base de datos
        mRecycler.setAdapter(mAdapter); // Establecer el adaptador actualizado al RecyclerView
    }

    // Ciclo de vida de la actividad: iniciar el adaptador para escuchar actualizaciones de Firestore
    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening(); // Comenzar a escuchar las actualizaciones de Firestore
        }
    }

    // Ciclo de vida de la actividad: detener la escucha de Firestore cuando la actividad deja de estar activa
    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening(); // Detener la escucha de cambios en Firestore cuando la actividad no está visible
        }
    }

    // Ciclo de vida de la actividad: forzar la actualización del RecyclerView cuando la actividad vuelve a estar visible
    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged(); // Forzar que el RecyclerView se actualice cuando la actividad regresa a primer plano
        }
    }
}
