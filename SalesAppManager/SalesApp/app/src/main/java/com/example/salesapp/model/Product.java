package com.example.salesapp.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product implements Serializable {

    @SerializedName("productID")
    @Expose
    private int productID;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("productImage")
    @Expose
    private String productImage;
    @SerializedName("productPrice")
    @Expose
    private int productPrice;
    @SerializedName("productDescription")
    @Expose
    private String productDescription;
    @SerializedName("remainingProducts")
    @Expose
    private int remainingProducts;
    @SerializedName("productTypeID")
    @Expose
    private int productTypeID;

    public Product(int productID, String productName, String productImage, int productPrice, String productDescription, int remainingProducts, int productTypeID) {
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.remainingProducts = remainingProducts;
        this.productTypeID = productTypeID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getRemainingProducts() {
        return remainingProducts;
    }

    public void setRemainingProducts(int remainingProducts) {
        this.remainingProducts = remainingProducts;
    }

    public int getProductTypeID() {
        return productTypeID;
    }

    public void setProductTypeID(int productTypeID) {
        this.productTypeID = productTypeID;
    }
}