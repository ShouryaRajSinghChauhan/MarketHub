package com.example.markethub.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markethub.R;
import com.example.markethub.adapter.ImageSliderAdapter;
import com.example.markethub.database.Product;
import com.example.markethub.repository.ProductRepository;
import com.example.markethub.utils.Constants;
import com.example.markethub.utils.ImageUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddEditProductActivity extends AppCompatActivity {

    private ProductRepository repository;
    private boolean isEditMode = false;
    private int productId = -1;
    private Product existingProduct;

    private MaterialToolbar toolbar;
    private Button btnPickImages, btnSubmit;
    private TextView tvImageCount;
    private RecyclerView rvSelectedImages;
    private TextInputEditText etName, etPrice, etSellerName, etSellerPhone, etLocation, etDescription;

    private final List<String> selectedImageUris = new ArrayList<>();
    private ImageSliderAdapter imageAdapter;

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(),
            uris -> {
                if (uris != null && !uris.isEmpty()) {
                    for (Uri uri : uris) {
                        if (selectedImageUris.size() < 3) {
                            try {
                                getContentResolver().takePersistableUriPermission(
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                            } catch (Exception ignored) {}
                            selectedImageUris.add(uri.toString());
                        }
                    }
                    updateImagesView();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        repository = new ProductRepository(this);

        isEditMode = getIntent().getBooleanExtra(Constants.EXTRA_IS_EDIT_MODE, false);
        productId = getIntent().getIntExtra(Constants.EXTRA_PRODUCT_ID, -1);

        initViews();
        setupListeners();

        if (isEditMode && productId != -1) {
            toolbar.setTitle("Edit Listing");
            btnSubmit.setText("Update Listing");
            loadExistingProduct();
        } else {
            toolbar.setTitle("Create Listing");
            btnSubmit.setText("Submit Listing");
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        btnPickImages = findViewById(R.id.btn_pick_images);
        btnSubmit = findViewById(R.id.btn_submit);
        tvImageCount = findViewById(R.id.tv_image_count);
        rvSelectedImages = findViewById(R.id.rv_selected_images);
        etName = findViewById(R.id.et_product_name);
        etPrice = findViewById(R.id.et_product_price);
        etSellerName = findViewById(R.id.et_seller_name);
        etSellerPhone = findViewById(R.id.et_seller_phone);
        etLocation = findViewById(R.id.et_location);
        etDescription = findViewById(R.id.et_description);

        toolbar.setNavigationOnClickListener(v -> finish());

        rvSelectedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageSliderAdapter(selectedImageUris, true, position -> {
            if (position >= 0 && position < selectedImageUris.size()) {
                selectedImageUris.remove(position);
                updateImagesView();
            }
        });
        rvSelectedImages.setAdapter(imageAdapter);
        updateImagesView();
    }

    private void setupListeners() {
        btnPickImages.setOnClickListener(v -> {
            if (selectedImageUris.size() >= 3) {
                Toast.makeText(this, "Maximum 3 images allowed", Toast.LENGTH_SHORT).show();
            } else {
                galleryLauncher.launch("image/*");
            }
        });

        btnSubmit.setOnClickListener(v -> saveProduct());
    }

    private void updateImagesView() {
        tvImageCount.setText(selectedImageUris.size() + " / 3 images selected");
        imageAdapter.notifyDataSetChanged();
    }

    private void loadExistingProduct() {
        repository.getProductById(productId, new ProductRepository.RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product product) {
                existingProduct = product;
                etName.setText(product.getName());
                etPrice.setText(String.valueOf(product.getPrice()));
                etSellerName.setText(product.getSellerName());
                etSellerPhone.setText(product.getSellerPhone());
                etLocation.setText(product.getLocation());
                etDescription.setText(product.getDescription());

                selectedImageUris.clear();
                selectedImageUris.addAll(ImageUtils.parseImageUris(product.getImageUris()));
                updateImagesView();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AddEditProductActivity.this, "Failed to load product", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveProduct() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String sellerName = etSellerName.getText() != null ? etSellerName.getText().toString().trim() : "";
        String phone = etSellerPhone.getText() != null ? etSellerPhone.getText().toString().trim() : "";
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        // Client-side Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Product name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etPrice.setError("Price must be greater than 0");
                etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Enter a valid price");
            etPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(sellerName)) {
            etSellerName.setError("Seller name is required");
            etSellerName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.replaceAll("[^0-9]", "").length() < 7) {
            etSellerPhone.setError("Enter a valid phone number (min 7 digits)");
            etSellerPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Please select at least 1 image for the product", Toast.LENGTH_LONG).show();
            return;
        }

        String joinedUris = ImageUtils.joinImageUris(selectedImageUris);
        long now = System.currentTimeMillis();

        if (isEditMode && existingProduct != null) {
            existingProduct.setName(name);
            existingProduct.setPrice(price);
            existingProduct.setSellerName(sellerName);
            existingProduct.setSellerPhone(phone);
            existingProduct.setLocation(location);
            existingProduct.setDescription(description);
            existingProduct.setImageUris(joinedUris);

            repository.updateProduct(existingProduct, new ProductRepository.RepositoryCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    Toast.makeText(AddEditProductActivity.this, "Listing updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(AddEditProductActivity.this, "Error updating listing", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Product newProduct = new Product(
                    name, price, description,
                    sellerName, phone, location, joinedUris, now,
                    true, 0
            );

            repository.insertProduct(newProduct, new ProductRepository.RepositoryCallback<Long>() {
                @Override
                public void onSuccess(Long newId) {
                    Toast.makeText(AddEditProductActivity.this, "Listing published successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(AddEditProductActivity.this, "Error publishing listing", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
