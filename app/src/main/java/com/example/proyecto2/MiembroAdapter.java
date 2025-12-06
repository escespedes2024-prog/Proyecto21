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

public class MiembroAdapter extends RecyclerView.Adapter<MiembroAdapter.MiembroViewHolder> {

    private List<Miembro> miembroList;
    private final Context context;
    private OnMiembroClickListener listener;

    public interface OnMiembroClickListener {
        void onMiembroClick(Miembro miembro);
        void onMiembroDelete(Miembro miembro);
    }

    public MiembroAdapter(Context context, List<Miembro> miembroList) {
        this.context = context;
        this.miembroList = miembroList;
    }

    public MiembroAdapter(Context context, List<Miembro> miembroList, OnMiembroClickListener listener) {
        this.context = context;
        this.miembroList = miembroList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MiembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_miembro, parent, false);
        return new MiembroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiembroViewHolder holder, int position) {
        Miembro miembro = miembroList.get(position);
        String nombreCompleto = (miembro.getNombre() != null ? miembro.getNombre() : "") + 
            " " + (miembro.getApellido() != null ? miembro.getApellido() : "");
        holder.tvNombre.setText(nombreCompleto.trim());
        holder.tvEmail.setText(miembro.getEmail() != null && !miembro.getEmail().isEmpty() 
            ? miembro.getEmail() : "Sin email");
        holder.tvTelefono.setText("Tel: " + (miembro.getTelefono() != null && !miembro.getTelefono().isEmpty() 
            ? miembro.getTelefono() : "N/A"));
        holder.tvRol.setText("Rol: " + (miembro.getRol() != null ? miembro.getRol() : "Miembro"));
        
        if (listener != null) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
            
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMiembroClick(miembro);
                }
            });
            
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMiembroDelete(miembro);
                }
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return miembroList.size();
    }

    public void updateList(List<Miembro> newList) {
        this.miembroList = newList;
        notifyDataSetChanged();
    }

    public static class MiembroViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvTelefono, tvRol;
        Button btnEditar, btnEliminar;

        public MiembroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvMiembroNombre);
            tvEmail = itemView.findViewById(R.id.tvMiembroEmail);
            tvTelefono = itemView.findViewById(R.id.tvMiembroTelefono);
            tvRol = itemView.findViewById(R.id.tvMiembroRol);
            btnEditar = itemView.findViewById(R.id.btnEditarMiembro);
            btnEliminar = itemView.findViewById(R.id.btnEliminarMiembro);
        }
    }
}

