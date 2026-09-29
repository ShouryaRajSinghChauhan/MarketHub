package com.example.markethub.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private double price;
    private String description;
    private String sellerName;
    private String sellerPhone;
    private String location;
    private String imageUris;
    private long dateListed;
    private boolean isUserCreated;
    private int apiId;

    public Product() {
    }

    @Ignore
    public Product(String name, double price, String description, String sellerName, String sellerPhone,
                   String location, String imageUris, long dateListed,
                   boolean isUserCreated, int apiId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.location = location;
        this.imageUris = imageUris;
        this.dateListed = dateListed;
        this.isUserCreated = isUserCreated;
        this.apiId = apiId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUris() {
        return imageUris;
    }

    public void setImageUris(String imageUris) {
        this.imageUris = imageUris;
    }

    public long getDateListed() {
        return dateListed;
    }

    public void setDateListed(long dateListed) {
        this.dateListed = dateListed;
    }

    public boolean isUserCreated() {
        return isUserCreated;
    }

    public void setUserCreated(boolean userCreated) {
        isUserCreated = userCreated;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }
}
