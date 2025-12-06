package com.example.proyecto2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EstudianteAdapter extends RecyclerView.Adapter<EstudianteAdapter.EstudianteViewHolder> {

    private List<Estudiante> estudianteList;
    private final Context context;
    private DBUser dbUser;
    private OnEstudianteClickListener listener;

    public interface OnEstudianteClickListener {
        void onEstudianteClick(Estudiante estudiante);
        void onEstudianteDelete(Estudiante estudiante);
    }

    public EstudianteAdapter(Context context, List<Estudiante> estudianteList) {
        this.context = context;
        this.estudianteList = estudianteList;
        this.dbUser = new DBUser(context);
    }

    public EstudianteAdapter(Context context, List<Estudiante> estudianteList, OnEstudianteClickListener listener) {
        this.context = context;
        this.estudianteList = estudianteList;
        this.dbUser = new DBUser(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public EstudianteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_estudiante, parent, false);
        return new EstudianteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstudianteViewHolder holder, int position) {
        Estudiante estudiante = estudianteList.get(position);
        holder.tvNombre.setText(estudiante.getNombreCompleto());
        holder.tvEmail.setText(estudiante.getEmail() != null && !estudiante.getEmail().isEmpty() 
            ? estudiante.getEmail() : "Sin email");
        holder.tvTelefono.setText("Tel: " + (estudiante.getTelefono() != null && !estudiante.getTelefono().isEmpty() 
            ? estudiante.getTelefono() : "N/A"));
        
        // Obtener nombre del curso
        if (estudiante.getIdCurso() > 0) {
            Curso curso = dbUser.obtenerCurso(estudiante.getIdCurso());
            if (curso != null) {
                holder.tvCurso.setText("Curso: " + curso.getNombre());
            } else {
                holder.tvCurso.setText("Curso: N/A");
            }
        } else {
            holder.tvCurso.setText("Curso: Sin asignar");
        }
        
        if (listener != null) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
            
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEstudianteClick(estudiante);
                }
            });
            
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEstudianteDelete(estudiante);
                }
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return estudianteList.size();
    }

    public void updateList(List<Estudiante> newList) {
        this.estudianteList = newList;
        notifyDataSetChanged();
    }

    public static class EstudianteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvTelefono, tvCurso;
        Button btnEditar, btnEliminar;

        public EstudianteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvEstudianteNombre);
            tvEmail = itemView.findViewById(R.id.tvEstudianteEmail);
            tvTelefono = itemView.findViewById(R.id.tvEstudianteTelefono);
            tvCurso = itemView.findViewById(R.id.tvEstudianteCurso);
            btnEditar = itemView.findViewById(R.id.btnEditarEstudiante);
            btnEliminar = itemView.findViewById(R.id.btnEliminarEstudiante);
        }
    }
}

