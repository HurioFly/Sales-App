package com.manager.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.adapter.OrderAdapter;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrderHistoryActivity extends AppCompatActivity {

    Toolbar toolbarOrderHistory;
    TextView textViewOrderHistoryEmpty;
    RecyclerView recycleViewOrderHistory;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        init();
        actionBar();
        getData();
    }

    private void getData() {
        compositeDisposable.add(apiSalesApp.getOrder(Utils.user.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        orderModel -> {
                            if(orderModel.getSuccess()) {
                                recycleViewOrderHistory.setVisibility(View.VISIBLE);
                                textViewOrderHistoryEmpty.setVisibility(View.GONE);
                                OrderAdapter orderAdapter = new OrderAdapter(getApplicationContext(), orderModel.getOrderList());
                                recycleViewOrderHistory.setAdapter(orderAdapter);
                            }
                            else {
                                textViewOrderHistoryEmpty.setVisibility(View.VISIBLE);
                                recycleViewOrderHistory.setVisibility(View.GONE);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void actionBar() {
        setSupportActionBar(toolbarOrderHistory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarOrderHistory.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarOrderHistory = findViewById(R.id.toolbarOrderHistory);
        textViewOrderHistoryEmpty = findViewById(R.id.textViewOrderHistoryEmpty);
        recycleViewOrderHistory = findViewById(R.id.recycleViewOrderHistory);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleViewOrderHistory.setLayoutManager(linearLayoutManager);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}