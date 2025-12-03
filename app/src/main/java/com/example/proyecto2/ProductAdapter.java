package com.example.proyecto2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productos;

    public ProductAdapter(List<Product> productos) {
        this.productos = productos;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product producto = productos.get(position);
        holder.bind(producto);
    }

    @Override
    public int getItemCount() {
        return productos != null ? productos.size() : 0;
    }

    public void updateProducts(List<Product> nuevosProductos) {
        this.productos = nuevosProductos;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewProducto;
        private TextView textViewTitulo;
        private TextView textViewCategoria;
        private TextView textViewPrecio;
        private TextView textViewRating;
        private TextView textViewCount;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProducto = itemView.findViewById(R.id.imageViewProducto);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewCount = itemView.findViewById(R.id.textViewCount);
        }

        public void bind(Product producto) {
            textViewTitulo.setText(producto.getTitle());
            textViewCategoria.setText(producto.getCategory());
            textViewPrecio.setText(String.format("$%.2f", producto.getPrice()));
            
            if (producto.getRating() != null) {
                textViewRating.setText(String.format("★ %.1f", producto.getRating().getRate()));
                textViewCount.setText(String.format("(%d)", producto.getRating().getCount()));
            } else {
                textViewRating.setText("★ 0.0");
                textViewCount.setText("(0)");
            }

            // Cargar imagen con Picasso
            if (producto.getImage() != null && !producto.getImage().isEmpty()) {
                Picasso.get()
                        .load(producto.getImage())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(imageViewProducto);
            } else {
                imageViewProducto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }
}

