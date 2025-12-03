package com.example.proyecto2;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProductos;
    private ProgressBar progressBar;
    private TextView textViewError;
    private ProductAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        recyclerViewProductos = findViewById(R.id.recyclerViewProductos);
        progressBar = findViewById(R.id.progressBar);
        textViewError = findViewById(R.id.textViewError);

        // Configurar RecyclerView
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(null);
        recyclerViewProductos.setAdapter(adapter);

        // Inicializar API service
        apiService = ApiClient.getApiService();

        // Cargar productos
        cargarProductos();
    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);
        textViewError.setVisibility(View.GONE);
        recyclerViewProductos.setVisibility(View.GONE);

        Call<List<Product>> call = apiService.getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productos = response.body();
                    adapter.updateProducts(productos);
                    recyclerViewProductos.setVisibility(View.VISIBLE);
                    Toast.makeText(ProductosActivity.this, 
                            "Productos cargados: " + productos.size(), 
                            Toast.LENGTH_SHORT).show();
                } else {
                    mostrarError("Error al obtener productos");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                mostrarError("Error de conexión: " + t.getMessage());
                Toast.makeText(ProductosActivity.this, 
                        "Error: " + t.getMessage(), 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarError(String mensaje) {
        textViewError.setText(mensaje);
        textViewError.setVisibility(View.VISIBLE);
        recyclerViewProductos.setVisibility(View.GONE);
    }
}

