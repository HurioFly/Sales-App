package com.manager.salesapp.model.EventBus;

import com.manager.salesapp.model.Order;

public class SetOrderStatusEvent {
    Order order;

    public SetOrderStatusEvent(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
