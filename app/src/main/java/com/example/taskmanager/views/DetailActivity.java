package com.example.taskmanager.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanager.R;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescripcion, tvFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setTitle("Detalle de Tarea");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar vistas
        tvTitulo = findViewById(R.id.tvTituloDetalle);
        tvDescripcion = findViewById(R.id.tvDescripcionDetalle);
        tvFecha = findViewById(R.id.tvFechaDetalle);

        // Obtener datos del intent
        String titulo = getIntent().getStringExtra("titulo");
        String descripcion = getIntent().getStringExtra("descripcion");
        String fecha = getIntent().getStringExtra("fecha");

        // Asignar datos a las vistas
        tvTitulo.setText(titulo != null ? titulo : "Sin título");
        tvDescripcion.setText(descripcion != null ? descripcion : "Sin descripción");
        tvFecha.setText(fecha != null ? fecha : "Sin fecha");
    }

    // Habilitar navegación hacia atrás con el botón de la barra de acción
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Cierra la actividad actual
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
