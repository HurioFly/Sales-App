package com.example.salesapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductType implements Serializable{

    @SerializedName("productTypeID")
    @Expose
    private int productTypeID;
    @SerializedName("productTypeName")
    @Expose
    private String productTypeName;
    @SerializedName("productTypeImage")
    @Expose
    private String productTypeImage;

    public ProductType(String productTypeName, String productTypeImage) {
        this.productTypeName = productTypeName;
        this.productTypeImage = productTypeImage;
    }

    public int getProductTypeID() {
        return productTypeID;
    }

    public void setProductTypeID(int productTypeID) {
        this.productTypeID = productTypeID;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getProductTypeImage() {
        return productTypeImage;
    }

    public void setProductTypeImage(String productTypeImage) {
        this.productTypeImage = productTypeImage;
    }

}