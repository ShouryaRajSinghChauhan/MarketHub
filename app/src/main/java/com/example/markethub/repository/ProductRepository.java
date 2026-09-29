package com.example.markethub.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.markethub.api.ApiProduct;
import com.example.markethub.api.RetrofitClient;
import com.example.markethub.database.AppDatabase;
import com.example.markethub.database.Product;
import com.example.markethub.database.ProductDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class ProductRepository {

    private final ProductDao productDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    public ProductRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.productDao = database.productDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void getAllProducts(RepositoryCallback<List<Product>> callback) {
        executorService.execute(() -> {
            List<Product> products = productDao.getAllProducts();
            mainHandler.post(() -> callback.onSuccess(products));
        });
    }

    public void getProductById(int id, RepositoryCallback<Product> callback) {
        executorService.execute(() -> {
            Product product = productDao.getProductById(id);
            mainHandler.post(() -> {
                if (product != null) {
                    callback.onSuccess(product);
                } else {
                    callback.onError("Product not found");
                }
            });
        });
    }

    public void getUserListings(RepositoryCallback<List<Product>> callback) {
        executorService.execute(() -> {
            List<Product> products = productDao.getUserListings();
            mainHandler.post(() -> callback.onSuccess(products));
        });
    }

    public void filterProducts(String query, double minPrice, double maxPrice, RepositoryCallback<List<Product>> callback) {
        executorService.execute(() -> {
            List<Product> products = productDao.filterProducts(query, minPrice, maxPrice);
            mainHandler.post(() -> callback.onSuccess(products));
        });
    }

    public void insertProduct(Product product, RepositoryCallback<Long> callback) {
        executorService.execute(() -> {
            long newId = productDao.insertProduct(product);
            mainHandler.post(() -> callback.onSuccess(newId));
        });
    }

    public void updateProduct(Product product, RepositoryCallback<Boolean> callback) {
        executorService.execute(() -> {
            productDao.updateProduct(product);
            mainHandler.post(() -> callback.onSuccess(true));
        });
    }

    public void deleteProduct(int productId, RepositoryCallback<Boolean> callback) {
        executorService.execute(() -> {
            productDao.deleteProductById(productId);
            mainHandler.post(() -> callback.onSuccess(true));
        });
    }

    /**
     * Fetch initial sample product data from FakeStoreAPI using Retrofit
     * and persist items into Room DB if not already present.
     */
    public void syncWithApi(RepositoryCallback<String> callback) {
        RetrofitClient.getApiService().getProducts().enqueue(new retrofit2.Callback<List<ApiProduct>>() {
            @Override
            public void onResponse(Call<List<ApiProduct>> call, Response<List<ApiProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ApiProduct> apiProducts = response.body();
                    executorService.execute(() -> {
                        List<Product> roomProducts = new ArrayList<>();
                        long now = System.currentTimeMillis();

                        for (ApiProduct apiProduct : apiProducts) {
                            if (productDao.getApiProductCount(apiProduct.getId()) == 0) {
                                // Convert USD price to realistic INR price (~85x conversion)
                                double inrPrice = Math.round(apiProduct.getPrice() * 85 * 100.0) / 100.0;
                                Product product = new Product(
                                        apiProduct.getTitle(),
                                        inrPrice,
                                        apiProduct.getDescription(),
                                        "Verified Seller",
                                        "9876543210",
                                        "Central Marketplace",
                                        apiProduct.getImage(),
                                        now,
                                        false,
                                        apiProduct.getId()
                                );
                                roomProducts.add(product);
                            }
                        }

                        if (!roomProducts.isEmpty()) {
                            productDao.insertProducts(roomProducts);
                        }

                        mainHandler.post(() -> callback.onSuccess("Synced " + roomProducts.size() + " new items from API"));
                    });
                } else {
                    mainHandler.post(() -> callback.onError("Failed to fetch API data"));
                }
            }

            @Override
            public void onFailure(Call<List<ApiProduct>> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getLocalizedMessage()));
            }
        });
    }
}
