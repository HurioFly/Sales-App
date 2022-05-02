package com.example.salesapp.utils;

import com.example.salesapp.model.OrderDetails;
import com.example.salesapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String BASE_URL = "http://192.168.2.107/sales_app/";
    public static List<OrderDetails> productsInCart = new ArrayList<>();
    public static List<OrderDetails> productsToBuy = new ArrayList<>();
    public static User user = new User();
}
