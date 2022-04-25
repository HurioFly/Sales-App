package com.example.salesapp.retrofit;

import com.example.salesapp.model.Product;
import com.example.salesapp.model.ProductType;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APISalesApp {
    @GET("getproducttype.php")
    Observable<List<ProductType>> getProductType();

    @GET("getlatestproduct.php")
    Observable<List<Product>> getLatestProduct();

    @POST("getproduct.php")
    @FormUrlEncoded
    Observable<List<Product>> getProduct(
            @Field("productTypeID") int productTypeID,
            @Field("page") int page
    );
}
