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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<Usuario> userList;
    private final Context context;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(Usuario usuario);
        void onUserDelete(Usuario usuario);
    }

    public UserAdapter(Context context, List<Usuario> userList) {
        this.context = context;
        this.userList = userList;
    }

    public UserAdapter(Context context, List<Usuario> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Usuario usuario = userList.get(position);
        holder.tvName.setText("Nombre: " + usuario.getNombre() + " " + usuario.getApellido());
        holder.tvEmail.setText("Correo: " + usuario.getCorreo());
        holder.tvPhone.setText("Teléfono: " + (usuario.getTelefono() != null && !usuario.getTelefono().isEmpty() ? usuario.getTelefono() : "N/A"));
        
        if (listener != null) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
            
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(usuario);
                }
            });
            
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserDelete(usuario);
                }
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<Usuario> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone;
        Button btnEditar, btnEliminar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvPhone = itemView.findViewById(R.id.tvUserPhone);
            btnEditar = itemView.findViewById(R.id.btnEditarUsuario);
            btnEliminar = itemView.findViewById(R.id.btnEliminarUsuario);
        }
    }
}
