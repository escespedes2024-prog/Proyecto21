package com.example.proyecto2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {


    private List<Usuario> userList;
    private final Context context;

    public UserAdapter(Context context, List<Usuario> userList) {
        this.context = context;
        this.userList = userList;
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

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvPhone = itemView.findViewById(R.id.tvUserPhone);
        }
    }
}