package com.manager.salesapp.retrofit;

import com.manager.salesapp.model.OrderModel;
import com.manager.salesapp.model.ProductModel;
import com.manager.salesapp.model.ProductTypeModel;
import com.manager.salesapp.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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

    @POST("insertorder.php")
    @FormUrlEncoded
    Observable<String> insertOrder(
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

    @POST("insertproduct.php")
    @FormUrlEncoded
    Observable<String> insertProduct(
            @Field("productName") String productName,
            @Field("productImage") String productImage,
            @Field("productPrice") int productPrice,
            @Field("productDescription") String productDescription,
            @Field("remainingProducts") int remainingProducts,
            @Field("productTypeID") int productTypeID
    );

    @Multipart
    @POST("uploadimage.php")
    Call<String> uploadFile(@Part MultipartBody.Part file, @Part("file") RequestBody name);

    @POST("updateuser.php")
    @FormUrlEncoded
    Observable<String> updateUserName(
            @Field("phoneNumber") String phoneNumber,
            @Field("newUserName") String newUserName
    );

    @POST("updateuser.php")
    @FormUrlEncoded
    Observable<String> updateUserAddress(
            @Field("phoneNumber") String phoneNumber,
            @Field("newAddress") String newAddress
    );

    @POST("updateuser.php")
    @FormUrlEncoded
    Observable<String> updateUserPassword(
            @Field("phoneNumber") String phoneNumber,
            @Field("newPassword") String newPassword
    );

    @POST("getproduct.php")
    Observable<ProductModel> getAllProduct();

    @POST("deleteproduct.php")
    @FormUrlEncoded
    Observable<String> deleteProduct(
            @Field("productID") int productID
    );

    @POST("updateproduct.php")
    @FormUrlEncoded
    Observable<String> updateProduct(
            @Field("productID") int productID,
            @Field("productName") String productName,
            @Field("productImage") String productImage,
            @Field("productPrice") int productPrice,
            @Field("productDescription") String productDescription,
            @Field("remainingProducts") int remainingProducts,
            @Field("productTypeID") int productTypeID
    );
}
