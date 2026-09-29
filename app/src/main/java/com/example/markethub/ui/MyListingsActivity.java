package com.example.markethub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markethub.R;
import com.example.markethub.adapter.ProductAdapter;
import com.example.markethub.database.Product;
import com.example.markethub.repository.ProductRepository;
import com.example.markethub.utils.Constants;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class MyListingsActivity extends AppCompatActivity {

    private ProductRepository repository;
    private ProductAdapter productAdapter;

    private MaterialToolbar toolbar;
    private RecyclerView rvMyListings;
    private LinearLayout layoutEmpty;
    private Button btnCreateFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        repository = new ProductRepository(this);

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserListings();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvMyListings = findViewById(R.id.rv_my_listings);
        layoutEmpty = findViewById(R.id.layout_empty);
        btnCreateFirst = findViewById(R.id.btn_create_first_listing);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(product -> {
            Intent intent = new Intent(MyListingsActivity.this, ProductDetailActivity.class);
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });
        rvMyListings.setLayoutManager(new GridLayoutManager(this, 2));
        rvMyListings.setAdapter(productAdapter);
    }

    private void setupListeners() {
        btnCreateFirst.setOnClickListener(v -> {
            Intent intent = new Intent(MyListingsActivity.this, AddEditProductActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserListings() {
        repository.getUserListings(new ProductRepository.RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter.setProducts(products);
                if (products.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvMyListings.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvMyListings.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MyListingsActivity.this, "Error loading your listings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
