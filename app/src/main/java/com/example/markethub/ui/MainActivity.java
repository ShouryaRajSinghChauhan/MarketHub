package com.example.markethub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markethub.R;
import com.example.markethub.adapter.ProductAdapter;
import com.example.markethub.database.Product;
import com.example.markethub.repository.ProductRepository;
import com.example.markethub.utils.Constants;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProductRepository repository;
    private ProductAdapter productAdapter;

    private EditText etSearch;
    private ImageButton btnFilter;
    private RecyclerView rvProducts;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton fabAddProduct;
    private Button btnMyListings;

    // Filter states
    private double minPrice = 0;
    private double maxPrice = Double.MAX_VALUE;
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new ProductRepository(this);

        initViews();
        setupRecyclerViews();
        setupListeners();

        // Initial fetch from API and load from Room DB
        syncApiAndLoadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsFromDatabase();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        btnFilter = findViewById(R.id.btn_filter);
        rvProducts = findViewById(R.id.rv_products);
        layoutEmpty = findViewById(R.id.layout_empty);
        progressBar = findViewById(R.id.progress_bar);
        fabAddProduct = findViewById(R.id.fab_add_product);
        btnMyListings = findViewById(R.id.btn_my_listings);
    }

    private void setupRecyclerViews() {
        // Product Adapter
        productAdapter = new ProductAdapter(product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                loadProductsFromDatabase();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnFilter.setOnClickListener(v -> showFilterDialog());

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditProductActivity.class);
            startActivity(intent);
        });

        btnMyListings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyListingsActivity.class);
            startActivity(intent);
        });
    }

    private void syncApiAndLoadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        repository.syncWithApi(new ProductRepository.RepositoryCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressBar.setVisibility(View.GONE);
                loadProductsFromDatabase();
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                // Load local data regardless of API network errors
                loadProductsFromDatabase();
            }
        });
    }

    private void loadProductsFromDatabase() {
        repository.filterProducts(currentSearchQuery, minPrice, maxPrice, new ProductRepository.RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter.setProducts(products);
                if (products.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvProducts.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvProducts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MainActivity.this, "Error loading products: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFilterDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);
        TextInputEditText etMinPrice = dialogView.findViewById(R.id.et_min_price);
        TextInputEditText etMaxPrice = dialogView.findViewById(R.id.et_max_price);
        Button btnReset = dialogView.findViewById(R.id.btn_reset_filter);
        Button btnApply = dialogView.findViewById(R.id.btn_apply_filter);

        if (minPrice > 0) {
            etMinPrice.setText(String.valueOf(minPrice));
        }
        if (maxPrice < Double.MAX_VALUE) {
            etMaxPrice.setText(String.valueOf(maxPrice));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnReset.setOnClickListener(v -> {
            minPrice = 0;
            maxPrice = Double.MAX_VALUE;
            dialog.dismiss();
            loadProductsFromDatabase();
        });

        btnApply.setOnClickListener(v -> {
            String minStr = etMinPrice.getText() != null ? etMinPrice.getText().toString().trim() : "";
            String maxStr = etMaxPrice.getText() != null ? etMaxPrice.getText().toString().trim() : "";

            minPrice = minStr.isEmpty() ? 0 : Double.parseDouble(minStr);
            maxPrice = maxStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxStr);

            dialog.dismiss();
            loadProductsFromDatabase();
        });

        dialog.show();
    }
}
