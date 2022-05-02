package com.example.salesapp.retrofit;

import com.example.salesapp.model.Order;
import com.example.salesapp.model.OrderModel;
import com.example.salesapp.model.Product;
import com.example.salesapp.model.ProductModel;
import com.example.salesapp.model.ProductType;
import com.example.salesapp.model.ProductTypeModel;
import com.example.salesapp.model.User;
import com.example.salesapp.model.UserModel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APISalesApp {
    @GET("getproducttype.php")
    Observable<ProductTypeModel> getProductType();

    @GET("getlatestproduct.php")
    Observable<ProductModel> getLatestProduct();

    @POST("getproduct.php")
    @FormUrlEncoded
    Observable<ProductModel> getProductByProductType(
            @Field("productTypeID") int productTypeID,
            @Field("productName") String productName
    );

    @POST("registration.php")
    @FormUrlEncoded
    Observable<String> registration(
            @Field("phoneNumber") String phoneNumber,
            @Field("password") String password,
            @Field("userName") String userName,
            @Field("address") String address
    );

    @POST("login.php")
    @FormUrlEncoded
    Observable<UserModel> login(
            @Field("phoneNumber") String phoneNumber,
            @Field("password") String password
    );

    @POST("createorder.php")
    @FormUrlEncoded
    Observable<String> createOrder(
            @Field("phoneNumber") String phoneNumber,
            @Field("consigneeName") String consigneeName,
            @Field("consigneeAddress") String consigneeAddress,
            @Field("consigneePhoneNumber") String consigneePhoneNumber,
            @Field("dateCreated") String dateCreated,
            @Field("totalMoney") int totalMoney,
            @Field("paymentMethods") String paymentMethods,
            @Field("orderDetails") String orderDetails
    );

    @POST("getorder.php")
    @FormUrlEncoded
    Observable<OrderModel> getOrder(
            @Field("phoneNumber") String phoneNumber
    );

    @POST("getproduct.php")
    @FormUrlEncoded
    Observable<ProductModel> getProductByProductName(
            @Field("productName") String productName
    );

    @POST("getproduct.php")
    @FormUrlEncoded
    Observable<ProductModel> getProductByProductID(
            @Field("productID") int productID
    );
}
