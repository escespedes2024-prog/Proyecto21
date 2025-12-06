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

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private List<Evento> eventoList;
    private final Context context;
    private OnEventoClickListener listener;

    public interface OnEventoClickListener {
        void onEventoClick(Evento evento);
        void onEventoDelete(Evento evento);
    }

    public EventoAdapter(Context context, List<Evento> eventoList) {
        this.context = context;
        this.eventoList = eventoList;
    }

    public EventoAdapter(Context context, List<Evento> eventoList, OnEventoClickListener listener) {
        this.context = context;
        this.eventoList = eventoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventoList.get(position);
        holder.tvTitulo.setText(evento.getTitulo());
        holder.tvDescripcion.setText(evento.getDescripcion() != null && !evento.getDescripcion().isEmpty() 
            ? evento.getDescripcion() : "Sin descripción");
        holder.tvFecha.setText("Fecha: " + (evento.getFecha() != null ? evento.getFecha() : "N/A"));
        holder.tvHora.setText("Hora: " + (evento.getHora() != null ? evento.getHora() : "N/A"));
        holder.tvTipo.setText("Tipo: " + (evento.getTipo() != null ? evento.getTipo() : "General"));
        
        if (listener != null) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
            
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventoClick(evento);
                }
            });
            
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventoDelete(evento);
                }
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventoList.size();
    }

    public void updateList(List<Evento> newList) {
        this.eventoList = newList;
        notifyDataSetChanged();
    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvFecha, tvHora, tvTipo;
        Button btnEditar, btnEliminar;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvEventoTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvEventoDescripcion);
            tvFecha = itemView.findViewById(R.id.tvEventoFecha);
            tvHora = itemView.findViewById(R.id.tvEventoHora);
            tvTipo = itemView.findViewById(R.id.tvEventoTipo);
            btnEditar = itemView.findViewById(R.id.btnEditarEvento);
            btnEliminar = itemView.findViewById(R.id.btnEliminarEvento);
        }
    }
}

