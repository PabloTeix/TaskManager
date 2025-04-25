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

import com.example.taskmanager.views.CrearTareaActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.models.Tarea;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AdapterTarea extends FirestoreRecyclerAdapter<Tarea, AdapterTarea.ViewHolder> {

    // Instancia de Firestore para realizar operaciones con la base de datos
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    // Actividad que llama a este adaptador (se usa para iniciar nuevas actividades)
    Activity activity;

    // Constructor del adaptador que recibe las opciones de Firestore y la actividad
    public AdapterTarea(@NonNull FirestoreRecyclerOptions<Tarea> options, Activity activity) {
        super(options);
        this.activity = activity; // Asignar la actividad
    }

    // Método que vincula los datos con las vistas del ViewHolder
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tarea tarea) {

        // Obtener el documento de la tarea actual a través de su posición
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        // Obtener el ID del documento para futuras acciones como editar o eliminar
        final String id = documentSnapshot.getId();

        // Asignar los valores de la tarea a las vistas (TextViews)
        holder.titulo.setText(tarea.getTitulo());
        holder.descripcion.setText(tarea.getDescripcion());

        // Formatear la fecha de inicio de la tarea para mostrarla en formato dd/MM/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaFormateada = sdf.format(tarea.getFecha_inicio());
        holder.fecha.setText(fechaFormateada);

        // Configurar el botón de editar para navegar a la actividad de edición
        holder.button_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para abrir CrearTareaActivity y pasarle el ID de la tarea
                Intent i = new Intent(activity, CrearTareaActivity.class);
                i.putExtra("id_tarea", id);
                activity.startActivity(i); // Iniciar la actividad de edición
            }
        });

        // Configurar el botón de eliminar para borrar la tarea de Firestore
        holder.button_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTarea(id); // Llamar al método para eliminar la tarea
            }
        });
    }

    // Método que elimina la tarea de Firestore dado su ID
    private void deleteTarea(String id) {
        mFirestore.collection("tareas").document(id).delete() // Eliminar documento en la colección "tareas"
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Mostrar un mensaje si la tarea fue eliminada correctamente
                        Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Mostrar un mensaje si ocurrió un error al eliminar la tarea
                        Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método que infla el layout para cada item de la lista
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada tarea
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tarea_single, parent, false);
        return new ViewHolder(v); // Retornar el ViewHolder con el layout inflado
    }

    // ViewHolder para manejar las vistas de cada item en el RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Vistas que componen cada item del RecyclerView
        TextView titulo, descripcion, fecha;
        ImageView button_eliminar, button_editar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar las vistas del item
            titulo = itemView.findViewById(R.id.tarea_titulo);
            descripcion = itemView.findViewById(R.id.tarea_descripcion);
            fecha = itemView.findViewById(R.id.tarea_fecha);
            button_eliminar = itemView.findViewById(R.id.btn_eliminar);
            button_editar = itemView.findViewById(R.id.bt_editar);
        }
    }
}


