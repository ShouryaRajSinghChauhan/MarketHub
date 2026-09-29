package com.example.markethub.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM products ORDER BY dateListed DESC")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    Product getProductById(int id);

    @Query("SELECT * FROM products WHERE isUserCreated = 1 ORDER BY dateListed DESC")
    List<Product> getUserListings();

    @Query("SELECT * FROM products WHERE " +
            "(:searchQuery IS NULL OR :searchQuery = '' OR name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%') AND " +
            "price >= :minPrice AND price <= :maxPrice " +
            "ORDER BY dateListed DESC")
    List<Product> filterProducts(String searchQuery, double minPrice, double maxPrice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProduct(Product product);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProducts(List<Product> products);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("DELETE FROM products WHERE id = :id")
    void deleteProductById(int id);

    @Query("SELECT COUNT(*) FROM products")
    int getProductCount();

    @Query("SELECT COUNT(*) FROM products WHERE apiId = :apiId")
    int getApiProductCount(int apiId);
}
