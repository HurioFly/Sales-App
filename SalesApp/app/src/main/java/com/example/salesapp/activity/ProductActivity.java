package com.example.salesapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.adapter.ProductAdapter;
import com.example.salesapp.model.Product;
import com.example.salesapp.retrofit.APISalesApp;
import com.example.salesapp.retrofit.RetrofitClient;
import com.example.salesapp.utils.Connection;
import com.example.salesapp.utils.Server;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductActivity extends AppCompatActivity {

    Toolbar toolbarPhonePage;
    RecyclerView recycleViewPhone;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    int productTypeID;
    int page = 1;

    List<Product> productList;
    ProductAdapter productAdapter;

    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        if(Connection.isConnected(this)) {
            init();
            productTypeID = getIntent().getIntExtra("productTypeID", -1);
            actionBar();
            apiSalesApp = RetrofitClient.getInstance(Server.BASE_URL).create(APISalesApp.class);
            getData(page);
            addEventLoad();
        }
        else {
            Toast.makeText(this, "Lỗi kết nối! Vui lòng kiểm tra và thử lại.", Toast.LENGTH_LONG).show();
        }
    }

    private void addEventLoad() {
        recycleViewPhone.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading == false) {
                    if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == productList.size()-1) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                productList.add(null);
                productAdapter.notifyItemInserted(productList.size()-1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                productList.remove(productList.size()-1);
                productAdapter.notifyItemRemoved(productList.size());
                page++;
                getData(page);
                productAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    private void getData(int page) {
        compositeDisposable.add(apiSalesApp.getProduct(productTypeID, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        serverProductTypeList -> {
                            if(!serverProductTypeList.isEmpty()) {
                                if(productAdapter == null) {
                                    productList = serverProductTypeList;
                                    productAdapter = new ProductAdapter(getApplicationContext(), productList);
                                    recycleViewPhone.setAdapter(productAdapter);
                                }
                                else {
                                    int positionAdded = productList.size()-1;
                                    int quantityAdded = serverProductTypeList.size();
                                    for(int i=0; i<quantityAdded; i++) {
                                        productList.add(serverProductTypeList.get(i));
                                    }
                                    productAdapter.notifyItemRangeInserted(positionAdded, quantityAdded);
                                }
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "Lỗi kết nối server: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void actionBar() {
        if(productTypeID == 2) {
            toolbarPhonePage.setTitle("Điện thoại");
        }
        else {
            toolbarPhonePage.setTitle("Laptop");
        }

        setSupportActionBar(toolbarPhonePage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPhonePage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarPhonePage = findViewById(R.id.toolbarProduct);
        recycleViewPhone = findViewById(R.id.recycleViewProduct);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycleViewPhone.setLayoutManager(linearLayoutManager);
        recycleViewPhone.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}