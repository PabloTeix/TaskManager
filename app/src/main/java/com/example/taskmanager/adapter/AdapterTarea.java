package com.example.taskmanager.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.models.Tarea;
import com.example.taskmanager.views.CrearTareaActivity;
import com.example.taskmanager.views.DetailActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdapterTarea extends FirestoreRecyclerAdapter<Tarea, AdapterTarea.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance(); // Instancia de Firestore para hacer operaciones
    private Activity activity;  // Referencia a la actividad para iniciar otras actividades
    private boolean isCompletadasActivity; // Indica si estamos en la actividad de tareas completadas

    // Constructor del adaptador
    public AdapterTarea(@NonNull FirestoreRecyclerOptions<Tarea> options, Activity activity, boolean isCompletadasActivity) {
        super(options);  // Llamada al constructor de la clase base FirestoreRecyclerAdapter
        this.activity = activity;
        this.isCompletadasActivity = isCompletadasActivity;
    }

    // Este método se llama cada vez que un ítem necesita ser vinculado a la vista
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tarea tarea) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId(); // Obtiene el ID del documento en Firestore

        // Asignamos el título y la descripción de la tarea al ViewHolder
        holder.titulo.setText(tarea.getTitulo());
        holder.descripcion.setText(tarea.getDescripcion());

        // Formateamos la fecha de inicio
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (tarea.getFecha_inicio() != null) {
            String fechaFormateada = sdf.format(tarea.getFecha_inicio());
            holder.fecha.setText("Fecha inicio: " + fechaFormateada);
        } else {
            holder.fecha.setText("Fecha no disponible");
        }

        // Asignamos la fecha de fin, solo si está disponible
        if (tarea.getFecha_fin() != null) {
            String fechaFinFormateada = sdf.format(tarea.getFecha_fin());
            holder.fechaFin.setText("Fecha fin: " + fechaFinFormateada);
            holder.fechaFin.setVisibility(View.VISIBLE); // Hacemos visible el campo de fecha de fin
        } else {
            holder.fechaFin.setVisibility(View.GONE); // Ocultamos la fecha de fin si no está disponible
        }

        // Asignamos el color al CardView según el color especificado en la tarea
        String color = tarea.getColor();
        if (color != null) {
            switch (color) {
                case "Por defecto":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#F4F6F7"));
                    break;
                case "Rojo":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#EF5350"));
                    break;
                case "Verde":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
                    break;
                case "Azul":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#BBDEFB"));
                    break;
                case "Amarillo":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF9C4"));
                    break;
                case "Naranja":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#FFA726"));
                    break;
                case "Rosa":
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#F8BBD0"));
                    break;
                case "Blanco":
                    holder.cardView.setCardBackgroundColor(Color.WHITE);
                    break;
                default:
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#F4F6F7"));
                    break;
            }
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F4F6F7")); // Color por defecto
        }

        // Si estamos en la actividad de tareas completadas, ocultamos el botón de editar
        if (isCompletadasActivity) {
            holder.button_editar.setVisibility(View.GONE);
        } else {
            holder.button_editar.setVisibility(View.VISIBLE);
        }

        // Acción al presionar el botón de editar
        holder.button_editar.setOnClickListener(v -> {
            Intent i = new Intent(activity, CrearTareaActivity.class);
            i.putExtra("id_tarea", id); // Pasamos el ID de la tarea a la nueva actividad
            activity.startActivity(i);
        });

        // Acción al presionar el botón de eliminar
        holder.button_eliminar.setOnClickListener(v -> deleteTarea(id));

        // Acción al presionar el botón de completar
        holder.button_completar.setOnClickListener(v -> {
            // Obtener la fecha actual para marcar la tarea como completada
            Date fechaActual = new Date();

            // Actualizamos la tarea en Firestore para marcarla como completada y añadir la fecha de finalización
            mFirestore.collection("tareas").document(id)
                    .update("completada", true, "fecha_fin", fechaActual)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(activity, "Tarea marcada como completada", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(activity, "Error al completar tarea", Toast.LENGTH_SHORT).show());
        });

        // Si la tarea ya está completada, ocultamos el botón de completar
        if (tarea.isCompletada()) {
            holder.button_completar.setVisibility(View.GONE);
        } else {
            holder.button_completar.setVisibility(View.VISIBLE);
        }

        // Acción al hacer clic en cualquier parte del ítem para ver detalles de la tarea
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, DetailActivity.class);
            intent.putExtra("titulo", tarea.getTitulo());
            intent.putExtra("descripcion", tarea.getDescripcion());

            // Pasamos la fecha de inicio y fin si están disponibles
            if (tarea.getFecha_inicio() != null) {
                intent.putExtra("fecha", sdf.format(tarea.getFecha_inicio()));
            }
            if (tarea.getFecha_fin() != null) {
                intent.putExtra("fecha_fin", sdf.format(tarea.getFecha_fin()));
            }

            // Si la tarea está completada y tiene fecha de fin, la pasamos también
            if (tarea.isCompletada() && tarea.getFecha_fin() != null) {
                intent.putExtra("fecha_fin", sdf.format(tarea.getFecha_fin()));
            }

            activity.startActivity(intent);
        });
    }

    // Método para eliminar una tarea de Firestore
    private void deleteTarea(String id) {
        mFirestore.collection("tareas").document(id).delete()
                .addOnSuccessListener(unused -> Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show());
    }

    // Método para crear un ViewHolder, inflando el layout para cada ítem
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tarea_single, parent, false);
        return new ViewHolder(v);
    }

    // ViewHolder que contiene las vistas para cada ítem
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, fecha, fechaFin;  // Vistas para mostrar los datos de la tarea
        ImageView button_eliminar, button_editar, button_completar; // Botones de acción
        CardView cardView; // CardView que contiene cada tarea

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tarea_titulo);
            descripcion = itemView.findViewById(R.id.tarea_descripcion);
            fecha = itemView.findViewById(R.id.tarea_fecha);
            fechaFin = itemView.findViewById(R.id.tarea_fecha_fin);
            button_eliminar = itemView.findViewById(R.id.btn_eliminar);
            button_editar = itemView.findViewById(R.id.bt_editar);
            button_completar = itemView.findViewById(R.id.bt_completar);
            cardView = itemView.findViewById(R.id.card_tarea); // Referencia al CardView
        }
    }
}
