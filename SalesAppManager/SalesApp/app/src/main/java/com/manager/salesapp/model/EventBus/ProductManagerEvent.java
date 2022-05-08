package com.manager.salesapp.model.EventBus;

import com.manager.salesapp.model.Product;

public class ProductManagerEvent {
    Product product;

    public ProductManagerEvent() {
    }

    public ProductManagerEvent(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
