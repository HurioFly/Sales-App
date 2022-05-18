package com.manager.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.adapter.OrderAdapter;
import com.manager.salesapp.adapter.OrderManagerAdapter;
import com.manager.salesapp.model.EventBus.ProductManagerEvent;
import com.manager.salesapp.model.EventBus.SetOrderStatusEvent;
import com.manager.salesapp.model.Order;
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

public class OrderManagerActivity extends AppCompatActivity {

    Toolbar toolbarOrderManager;
    RecyclerView recycleViewOrderManager;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);
        init();
        actionBar();
        getData();
    }

    private void getData() {
        List<String> orderStatus = new ArrayList<>();
        orderStatus.add("Đang xử lý");
        orderStatus.add("Đang giao hàng");
        orderStatus.add("Giao hàng thành công");
        compositeDisposable.add(apiSalesApp.getAllOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        orderModel -> {
                            if(orderModel.getSuccess()) {
                                OrderManagerAdapter orderAdapter = new OrderManagerAdapter(getApplicationContext(), orderModel.getOrderList(), orderStatus);
                                recycleViewOrderManager.setAdapter(orderAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void actionBar() {
        setSupportActionBar(toolbarOrderManager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarOrderManager.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarOrderManager = findViewById(R.id.toolbarOrderManager);
        recycleViewOrderManager = findViewById(R.id.recycleViewOrderManager);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycleViewOrderManager.setHasFixedSize(true);
        recycleViewOrderManager.setLayoutManager(layoutManager);
        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventSetOrderStatus(SetOrderStatusEvent setOrderStatusEvent) {
        if(setOrderStatusEvent != null) {
            Order order = setOrderStatusEvent.getOrder();
            compositeDisposable.add(apiSalesApp.updateOrder(order.getOrderID(), order.getOrderStatus())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                if(result.equals("success")) {
//                                    Toast.makeText(this, "Đã cập nhập trạng thái đơn hàng!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(this, "Cập nhập trạng thái đơn hàng không thành công!", Toast.LENGTH_LONG).show();
                                    getData();
                                }
                            },
                            throwable -> {
                                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    )
            );
            EventBus.getDefault().removeStickyEvent(setOrderStatusEvent);
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}