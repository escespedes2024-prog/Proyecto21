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

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.CursoViewHolder> {

    private List<Curso> cursoList;
    private final Context context;
    private DBUser dbUser;
    private OnCursoClickListener listener;

    public interface OnCursoClickListener {
        void onCursoClick(Curso curso);
        void onCursoDelete(Curso curso);
    }

    public CursoAdapter(Context context, List<Curso> cursoList) {
        this.context = context;
        this.cursoList = cursoList;
        this.dbUser = new DBUser(context);
    }

    public CursoAdapter(Context context, List<Curso> cursoList, OnCursoClickListener listener) {
        this.context = context;
        this.cursoList = cursoList;
        this.dbUser = new DBUser(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_curso, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = cursoList.get(position);
        holder.tvNombre.setText(curso.getNombre());
        holder.tvDescripcion.setText(curso.getDescripcion() != null && !curso.getDescripcion().isEmpty() 
            ? curso.getDescripcion() : "Sin descripción");
        holder.tvHorario.setText("Horario: " + (curso.getHorario() != null ? curso.getHorario() : "N/A"));
        
        // Obtener nombre del docente
        if (curso.getIdDocente() > 0) {
            Docente docente = dbUser.obtenerDocente(curso.getIdDocente());
            if (docente != null) {
                holder.tvDocente.setText("Docente: " + docente.getNombreCompleto());
            } else {
                holder.tvDocente.setText("Docente: N/A");
            }
        } else {
            holder.tvDocente.setText("Docente: Sin asignar");
        }
        
        if (listener != null) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
            
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCursoClick(curso);
                }
            });
            
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCursoDelete(curso);
                }
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return cursoList.size();
    }

    public void updateList(List<Curso> newList) {
        this.cursoList = newList;
        notifyDataSetChanged();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvHorario, tvDocente;
        Button btnEditar, btnEliminar;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvCursoNombre);
            tvDescripcion = itemView.findViewById(R.id.tvCursoDescripcion);
            tvHorario = itemView.findViewById(R.id.tvCursoHorario);
            tvDocente = itemView.findViewById(R.id.tvCursoDocente);
            btnEditar = itemView.findViewById(R.id.btnEditarCurso);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCurso);
        }
    }
}

