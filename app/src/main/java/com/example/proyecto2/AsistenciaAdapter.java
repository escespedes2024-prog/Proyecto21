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

public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder> {

    private List<Asistencia> asistenciaList;
    private final Context context;
    private DBUser dbUser;
    private OnAsistenciaClickListener listener;

    public interface OnAsistenciaClickListener {
        void onAsistenciaClick(Asistencia asistencia);
        void onAsistenciaDelete(Asistencia asistencia);
    }

    public AsistenciaAdapter(Context context, List<Asistencia> asistenciaList) {
        this.context = context;
        this.asistenciaList = asistenciaList;
        this.dbUser = new DBUser(context);
    }

    public AsistenciaAdapter(Context context, List<Asistencia> asistenciaList, OnAsistenciaClickListener listener) {
        this.context = context;
        this.asistenciaList = asistenciaList;
        this.dbUser = new DBUser(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AsistenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_asistencia, parent, false);
        return new AsistenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsistenciaViewHolder holder, int position) {
        Asistencia asistencia = asistenciaList.get(position);
        
        // Obtener nombre del estudiante
        if (asistencia.getIdEstudiante() > 0) {
            Estudiante estudiante = dbUser.obtenerEstudiante(asistencia.getIdEstudiante());
            if (estudiante != null) {
                holder.tvEstudiante.setText(estudiante.getNombreCompleto());
            } else {
                holder.tvEstudiante.setText("Estudiante: N/A");
            }
        } else {
            holder.tvEstudiante.setText("Estudiante: N/A");
        }
        
        // Obtener nombre del curso
        if (asistencia.getIdCurso() > 0) {
            Curso curso = dbUser.obtenerCurso(asistencia.getIdCurso());
            if (curso != null) {
                holder.tvCurso.setText("Curso: " + curso.getNombre());
            } else {
                holder.tvCurso.setText("Curso: N/A");
            }
        } else {
            holder.tvCurso.setText("Curso: N/A");
        }
        
        holder.tvFecha.setText("Fecha: " + (asistencia.getFecha() != null ? asistencia.getFecha() : "N/A"));
        holder.tvEstado.setText("Estado: " + (asistencia.getEstado() != null ? asistencia.getEstado() : "N/A"));
        
        if (listener != null) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
            
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAsistenciaClick(asistencia);
                }
            });
            
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAsistenciaDelete(asistencia);
                }
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return asistenciaList.size();
    }

    public void updateList(List<Asistencia> newList) {
        this.asistenciaList = newList;
        notifyDataSetChanged();
    }

    public static class AsistenciaViewHolder extends RecyclerView.ViewHolder {
        TextView tvEstudiante, tvCurso, tvFecha, tvEstado;
        Button btnEditar, btnEliminar;

        public AsistenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEstudiante = itemView.findViewById(R.id.tvAsistenciaEstudiante);
            tvCurso = itemView.findViewById(R.id.tvAsistenciaCurso);
            tvFecha = itemView.findViewById(R.id.tvAsistenciaFecha);
            tvEstado = itemView.findViewById(R.id.tvAsistenciaEstado);
            btnEditar = itemView.findViewById(R.id.btnEditarAsistencia);
            btnEliminar = itemView.findViewById(R.id.btnEliminarAsistencia);
        }
    }
}

