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

public class DocenteAdapter extends RecyclerView.Adapter<DocenteAdapter.DocenteViewHolder> {

    private List<Docente> docenteList;
    private final Context context;
    private OnDocenteClickListener listener;

    public interface OnDocenteClickListener {
        void onDocenteClick(Docente docente);
        void onDocenteDelete(Docente docente);
    }

    public DocenteAdapter(Context context, List<Docente> docenteList) {
        this.context = context;
        this.docenteList = docenteList;
    }

    public DocenteAdapter(Context context, List<Docente> docenteList, OnDocenteClickListener listener) {
        this.context = context;
        this.docenteList = docenteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_docente, parent, false);
        return new DocenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocenteViewHolder holder, int position) {
        Docente docente = docenteList.get(position);
        holder.tvNombre.setText(docente.getNombreCompleto());
        holder.tvEmail.setText(docente.getEmail() != null && !docente.getEmail().isEmpty() 
            ? docente.getEmail() : "Sin email");
        holder.tvTelefono.setText("Tel: " + (docente.getTelefono() != null && !docente.getTelefono().isEmpty() 
            ? docente.getTelefono() : "N/A"));
        holder.tvEspecialidad.setText("Especialidad: " + (docente.getEspecialidad() != null 
            ? docente.getEspecialidad() : "Sin especialidad"));
        
        if (listener != null) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
            
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDocenteClick(docente);
                }
            });
            
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDocenteDelete(docente);
                }
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return docenteList.size();
    }

    public void updateList(List<Docente> newList) {
        this.docenteList = newList;
        notifyDataSetChanged();
    }

    public static class DocenteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvTelefono, tvEspecialidad;
        Button btnEditar, btnEliminar;

        public DocenteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvDocenteNombre);
            tvEmail = itemView.findViewById(R.id.tvDocenteEmail);
            tvTelefono = itemView.findViewById(R.id.tvDocenteTelefono);
            tvEspecialidad = itemView.findViewById(R.id.tvDocenteEspecialidad);
            btnEditar = itemView.findViewById(R.id.btnEditarDocente);
            btnEliminar = itemView.findViewById(R.id.btnEliminarDocente);
        }
    }
}

