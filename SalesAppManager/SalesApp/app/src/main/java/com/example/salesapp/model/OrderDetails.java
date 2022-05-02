package com.example.salesapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetails {

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
    @SerializedName("remainingProducts")
    @Expose
    private int remainingProducts;
    @SerializedName("quantity")
    @Expose
    private int quantity;

    public OrderDetails(int productID, String productName, String productImage, int productPrice, int remainingProducts, int quantity) {
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.remainingProducts = remainingProducts;
        this.quantity = quantity;
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

    public int getRemainingProducts() {
        return remainingProducts;
    }

    public void setRemainingProducts(int remainingProducts) {
        this.remainingProducts = remainingProducts;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}