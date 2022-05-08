package com.manager.salesapp.utils;

import com.manager.salesapp.model.OrderDetails;
import com.manager.salesapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String BASE_URL = "http://192.168.2.107/sales_app/";
    public static List<OrderDetails> productsInCart = new ArrayList<>();
    public static List<OrderDetails> productsToBuy = new ArrayList<>();
    public static User user = new User();
}
