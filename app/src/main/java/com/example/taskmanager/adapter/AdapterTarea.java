package com.example.taskmanager.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.models.Tarea;
import com.example.taskmanager.views.CrearTareaActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AdapterTarea extends FirestoreRecyclerAdapter<Tarea, AdapterTarea.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;

    public AdapterTarea(@NonNull FirestoreRecyclerOptions<Tarea> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tarea tarea) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.titulo.setText(tarea.getTitulo());
        holder.descripcion.setText(tarea.getDescripcion());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaFormateada = sdf.format(tarea.getFecha_inicio());
        holder.fecha.setText(fechaFormateada);

        holder.button_editar.setOnClickListener(v -> {
            Intent i = new Intent(activity, CrearTareaActivity.class);
            i.putExtra("id_tarea", id);
            activity.startActivity(i);
        });

        holder.button_eliminar.setOnClickListener(v -> deleteTarea(id));

        holder.button_completar.setOnClickListener(v -> {
            mFirestore.collection("tareas").document(id)
                    .update("completada", true)
                    .addOnSuccessListener(unused -> Toast.makeText(activity, "Tarea marcada como completada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(activity, "Error al completar tarea", Toast.LENGTH_SHORT).show());
        });

        // Si ya está completada, ocultar el botón (opcional)
        if (tarea.isCompletada()) {
            holder.button_completar.setVisibility(View.GONE);
        } else {
            holder.button_completar.setVisibility(View.VISIBLE);
        }
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
        TextView titulo, descripcion, fecha;
        ImageView button_eliminar, button_editar, button_completar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tarea_titulo);
            descripcion = itemView.findViewById(R.id.tarea_descripcion);
            fecha = itemView.findViewById(R.id.tarea_fecha);
            button_eliminar = itemView.findViewById(R.id.btn_eliminar);
            button_editar = itemView.findViewById(R.id.bt_editar);
            button_completar = itemView.findViewById(R.id.bt_completar);
        }
    }
}
