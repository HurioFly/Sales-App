package com.manager.salesapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.adapter.ProductManagerAdapter;
import com.manager.salesapp.model.EventBus.ProductManagerEvent;
import com.manager.salesapp.model.Product;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductManagerActivity extends AppCompatActivity {
    Toolbar toolbarProductManager;
    ImageView imageViewProductAdd;
    RecyclerView recycleViewProductManager;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    List<Product> productList = new ArrayList<>();
    ProductManagerAdapter productManagerAdapter;

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);

        init();
        actionBar();
        getData();
        getEvent();
    }

    private void getEvent() {
        imageViewProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProductAddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        compositeDisposable.add(apiSalesApp.getAllProduct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            if(productModel.getSuccess()) {
                                productList = productModel.getProductList();
                                productManagerAdapter = new ProductManagerAdapter(getApplicationContext(), productList);
                                recycleViewProductManager.setAdapter(productManagerAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void actionBar() {
        setSupportActionBar(toolbarProductManager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarProductManager.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarProductManager = findViewById(R.id.toolbarProductManager);
        imageViewProductAdd = findViewById(R.id.imageViewProductAdd);
        recycleViewProductManager = findViewById(R.id.recycleViewProductManager);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycleViewProductManager.setHasFixedSize(true);
        recycleViewProductManager.setLayoutManager(layoutManager);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventProductEdit(ProductManagerEvent productManagerEvent) {
        if(productManagerEvent != null) {
            product = productManagerEvent.getProduct();
            compositeDisposable.add(apiSalesApp.deleteProduct(product.getProductID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                if(result.equals("success")) {
                                    Toast.makeText(this, "Xóa sản phẩm thành công!", Toast.LENGTH_LONG).show();
                                    getData();
                                }
                                else {
                                    Toast.makeText(this, "Xóa sản phẩm không thành công!", Toast.LENGTH_LONG).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    )
            );
            EventBus.getDefault().removeStickyEvent(productManagerEvent);
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}