package com.example.markethub.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.markethub.R;
import com.example.markethub.adapter.ImageSliderAdapter;
import com.example.markethub.database.Product;
import com.example.markethub.repository.ProductRepository;
import com.example.markethub.utils.Constants;
import com.example.markethub.utils.ImageUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ProductRepository repository;
    private int productId = -1;
    private Product currentProduct;

    private MaterialToolbar toolbar;
    private ViewPager2 vpProductImages;
    private TextView tvTitle, tvPrice, tvDate, tvDescription;
    private TextView tvSellerName, tvSellerPhone, tvSellerLocation;
    private LinearLayout layoutOwnerActions;
    private Button btnEdit, btnDelete, btnCall, btnSms, btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        repository = new ProductRepository(this);

        productId = getIntent().getIntExtra(Constants.EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        vpProductImages = findViewById(R.id.vp_product_images);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvDate = findViewById(R.id.tv_detail_date);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvSellerName = findViewById(R.id.tv_seller_name);
        tvSellerPhone = findViewById(R.id.tv_seller_phone);
        tvSellerLocation = findViewById(R.id.tv_seller_location);
        layoutOwnerActions = findViewById(R.id.layout_owner_actions);
        btnEdit = findViewById(R.id.btn_edit_listing);
        btnDelete = findViewById(R.id.btn_delete_listing);
        btnCall = findViewById(R.id.btn_call_seller);
        btnSms = findViewById(R.id.btn_sms_seller);
        btnShare = findViewById(R.id.btn_share_listing);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnCall.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.getSellerPhone() != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + currentProduct.getSellerPhone()));
                startActivity(intent);
            }
        });

        btnSms.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.getSellerPhone() != null) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + currentProduct.getSellerPhone()));
                intent.putExtra("sms_body", "Hi, I'm interested in your product: " + currentProduct.getName());
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(v -> {
            if (currentProduct != null) {
                String shareText = "Check out this item on MarketHub!\n\n" +
                        currentProduct.getName() + "\n" +
                        "Price: ₹" + String.format(Locale.getDefault(), "%.2f", currentProduct.getPrice()) + "\n" +
                        "Location: " + currentProduct.getLocation() + "\n" +
                        "Contact Seller: " + currentProduct.getSellerPhone();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, currentProduct.getName());
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(intent, "Share Listing"));
            }
        });

        btnEdit.setOnClickListener(v -> {
            if (currentProduct != null) {
                Intent intent = new Intent(ProductDetailActivity.this, AddEditProductActivity.class);
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, currentProduct.getId());
                intent.putExtra(Constants.EXTRA_IS_EDIT_MODE, true);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(v -> confirmDeleteProduct());
    }

    private void loadProductDetails() {
        repository.getProductById(productId, new ProductRepository.RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product product) {
                currentProduct = product;
                bindProductData(product);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProductDetailActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bindProductData(Product product) {
        tvTitle.setText(product.getName());
        tvPrice.setText(String.format(Locale.getDefault(), "₹%.2f", product.getPrice()));
        tvDescription.setText(product.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        tvDate.setText("Listed on " + sdf.format(new Date(product.getDateListed())));

        tvSellerName.setText("Seller: " + product.getSellerName());
        tvSellerPhone.setText("Phone: " + product.getSellerPhone());
        tvSellerLocation.setText("Location: " + product.getLocation());

        List<String> imageUris = ImageUtils.parseImageUris(product.getImageUris());
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(imageUris, false, null);
        vpProductImages.setAdapter(sliderAdapter);

        // Show edit/delete buttons if it's user created listing
        if (product.isUserCreated()) {
            layoutOwnerActions.setVisibility(View.VISIBLE);
        } else {
            layoutOwnerActions.setVisibility(View.GONE);
        }
    }

    private void confirmDeleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Listing")
                .setMessage("Are you sure you want to delete this listing?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteProduct(productId, new ProductRepository.RepositoryCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Toast.makeText(ProductDetailActivity.this, "Listing deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(ProductDetailActivity.this, "Error deleting listing", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
