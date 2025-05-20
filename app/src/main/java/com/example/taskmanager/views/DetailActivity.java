package com.example.taskmanager.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanager.R;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescripcion, tvFecha;

    @SuppressLint("MissingInflatedId")
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
        TextView tvFechaFinLabel = findViewById(R.id.tvfecha_fin); // <-- Esta es la vista que quieres ocultar
        TextView tvFechaFin = findViewById(R.id.tvFechaValorDetalle);  // Valor de la fecha fin

        // Obtener datos del intent
        String titulo = getIntent().getStringExtra("titulo");
        String descripcion = getIntent().getStringExtra("descripcion");
        String fecha = getIntent().getStringExtra("fecha");
        String fechaFin = getIntent().getStringExtra("fecha_fin");

        // Asignar datos a las vistas
        tvTitulo.setText(titulo != null ? titulo : "Sin título");
        tvDescripcion.setText(descripcion != null ? descripcion : "Sin descripción");
        tvFecha.setText(fecha != null ? fecha : "Sin fecha");

        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            tvFechaFin.setText(fechaFin);
        } else {
            tvFechaFinLabel.setVisibility(View.GONE);  // Oculta el "Fecha fin:" label
            tvFechaFin.setVisibility(View.GONE);       // Oculta el valor de fecha fin también
        }
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
