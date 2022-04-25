package com.example.salesapp.model;

public class ProductType {

    private int productTypeID;
    private String productTypeName;
    private String productTypeImage;

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