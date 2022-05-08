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
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbarSearch;
    SearchView searchView;
    RecyclerView recycleViewProduct;

    FrameLayout frameLayoutCart;
    NotificationBadge notificationBadgeOrderQuantity;

    String productName;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    List<Product> productList = new ArrayList<>();
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
        actionBar();
        getData(productName);
        getEventClick();
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
                getData(productName);
                return true;
            }
        });
    }

    private void getData(String productName) {
        productList.clear();
        compositeDisposable.add(apiSalesApp.getProductByProductName(productName.trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            productList = productModel.getProductList();
                            productAdapter = new ProductAdapter(getApplicationContext(), productList);
                            recycleViewProduct.setAdapter(productAdapter);
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void actionBar() {
        searchView.setQuery(productName, false);

        setSupportActionBar(toolbarSearch);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarSearch.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarSearch = findViewById(R.id.toolbarSearch);
        searchView = findViewById(R.id.searchView);

        recycleViewProduct = findViewById(R.id.recycleViewProduct);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycleViewProduct.setLayoutManager(layoutManager);
        recycleViewProduct.setHasFixedSize(true);

        productName = getIntent().getStringExtra("productName");

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);

        frameLayoutCart = findViewById(R.id.frameLayoutCart);
        notificationBadgeOrderQuantity = findViewById(R.id.notificationBadgeOrderQuantity);
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
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}