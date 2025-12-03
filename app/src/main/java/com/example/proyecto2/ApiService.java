package com.example.proyecto2;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    String BASE_URL = "https://fakestoreapi.com/";

    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") int id);

    @GET("products/category/{category}")
    Call<List<Product>> getProductsByCategory(@Path("category") String category);
}

