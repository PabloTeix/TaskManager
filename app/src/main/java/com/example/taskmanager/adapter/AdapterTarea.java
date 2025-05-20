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

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;
    private boolean isCompletadasActivity;

    public AdapterTarea(@NonNull FirestoreRecyclerOptions<Tarea> options, Activity activity, boolean isCompletadasActivity) {
        super(options);
        this.activity = activity;
        this.isCompletadasActivity = isCompletadasActivity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tarea tarea) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.titulo.setText(tarea.getTitulo());
        holder.descripcion.setText(tarea.getDescripcion());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Verificar si la fecha de inicio no es null antes de formatearla
        if (tarea.getFecha_inicio() != null) {
            String fechaFormateada = sdf.format(tarea.getFecha_inicio());
            holder.fecha.setText("Fecha inicio: " + fechaFormateada);
        } else {
            holder.fecha.setText("Fecha no disponible");
        }

        // Asignar la fecha de fin solo si existe
        if (tarea.getFecha_fin() != null) {
            String fechaFinFormateada = sdf.format(tarea.getFecha_fin());
            holder.fechaFin.setText("Fecha fin: " + fechaFinFormateada);
            holder.fechaFin.setVisibility(View.VISIBLE); // Hacer visible la fecha de fin
        } else {
            holder.fechaFin.setVisibility(View.GONE); // Ocultar el TextView si no hay fecha de fin
        }

        // Asignar color al CardView
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
                    holder.cardView.setCardBackgroundColor(Color.WHITE); // blanco
                    break;
                default:
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#F4F6F7"));
                    break;
            }
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F4F6F7"));
        }

        if (isCompletadasActivity) {
            holder.button_editar.setVisibility(View.GONE);
        } else {
            holder.button_editar.setVisibility(View.VISIBLE);
        }

        holder.button_editar.setOnClickListener(v -> {
            Intent i = new Intent(activity, CrearTareaActivity.class);
            i.putExtra("id_tarea", id);
            activity.startActivity(i);
        });

        holder.button_eliminar.setOnClickListener(v -> deleteTarea(id));

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

        if (tarea.isCompletada()) {
            holder.button_completar.setVisibility(View.GONE);
        } else {
            holder.button_completar.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, DetailActivity.class);
            intent.putExtra("titulo", tarea.getTitulo());
            intent.putExtra("descripcion", tarea.getDescripcion());

            // Verificar si la fecha de inicio es válida
            if (tarea.getFecha_inicio() != null) {
                intent.putExtra("fecha", sdf.format(tarea.getFecha_inicio()));
            }

            // Verificar si la fecha de fin es válida
            if (tarea.getFecha_fin() != null) {
                intent.putExtra("fecha_fin", sdf.format(tarea.getFecha_fin()));
            }

            if (tarea.isCompletada() && tarea.getFecha_fin() != null) {
                intent.putExtra("fecha_fin", sdf.format(tarea.getFecha_fin()));  // Enviar fecha fin si está disponible
            }

            activity.startActivity(intent);
        });
    }

    private void deleteTarea(String id) {
        mFirestore.collection("tareas").document(id).delete()
                .addOnSuccessListener(unused -> Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tarea_single, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, fecha,fechaFin;
        ImageView button_eliminar, button_editar, button_completar;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tarea_titulo);
            descripcion = itemView.findViewById(R.id.tarea_descripcion);
            fecha = itemView.findViewById(R.id.tarea_fecha);
            fechaFin = itemView.findViewById(R.id.tarea_fecha_fin);
            button_eliminar = itemView.findViewById(R.id.btn_eliminar);
            button_editar = itemView.findViewById(R.id.bt_editar);
            button_completar = itemView.findViewById(R.id.bt_completar);
            cardView = itemView.findViewById(R.id.card_tarea); // <- asegúrate de tener este ID en tu CardView
        }
    }
}