package com.manager.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.adapter.ProductAdapter;
import com.manager.salesapp.model.Product;
import com.manager.salesapp.model.ProductType;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Connection;
import com.manager.salesapp.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductActivity extends AppCompatActivity {

    Toolbar toolbarProduct;
    RecyclerView recycleViewProduct;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    ProductType productType;
    String productName = "";

    List<Product> productList = new ArrayList<>();
    ProductAdapter productAdapter;

    FrameLayout frameLayoutCart;
    NotificationBadge notificationBadgeOrderQuantity;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        if(Connection.isConnected(this)) {
            init();
            actionBar();
            getData(productType.getProductTypeID(), productName);
            getEventClick();
        }
        else {
            Toast.makeText(this, "Lỗi kết nối! Vui lòng kiểm tra và thử lại.", Toast.LENGTH_LONG).show();
        }
    }

    private void getEventClick() {
        frameLayoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productName = searchView.getQuery().toString();
                getData(productType.getProductTypeID(), productName);
                return true;
            }
        });
    }

    private void getData(int productTypeID, String productName) {
        compositeDisposable.add(apiSalesApp.getProductByProductType(productTypeID, productName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            if(productModel.getSuccess()) {
                                productList = productModel.getProductList();
                                productAdapter = new ProductAdapter(getApplicationContext(), productList);
                                recycleViewProduct.setAdapter(productAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void actionBar() {
        toolbarProduct.setTitle(productType.getProductTypeName());

        setSupportActionBar(toolbarProduct);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarProduct.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarProduct = findViewById(R.id.toolbarProduct);
        recycleViewProduct = findViewById(R.id.recycleViewProduct);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleViewProduct.setLayoutManager(linearLayoutManager);
        recycleViewProduct.setHasFixedSize(true);

        productType = (ProductType) getIntent().getSerializableExtra("productType");

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);

        frameLayoutCart = findViewById(R.id.frameLayoutCart);
        notificationBadgeOrderQuantity = findViewById(R.id.notificationBadgeOrderQuantity);

        searchView = findViewById(R.id.searchView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.productsInCart.size() > 0) {
            notificationBadgeOrderQuantity.setText(String.valueOf(Utils.productsInCart.size()));
        }
        else {
            notificationBadgeOrderQuantity.clear();
        }

        searchView.setIconified(true);
        searchView.clearFocus();

        getData(productType.getProductTypeID(), productName);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}