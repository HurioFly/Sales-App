package com.manager.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.manager.salesapp.R;
import com.manager.salesapp.adapter.ManagerAdapter;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends AppCompatActivity {

    Toolbar toolbarManager;
    RecyclerView recycleViewManager;

    List<String> menuItemList = new ArrayList<>();
    ManagerAdapter managerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        init();
        actionBar();
        getData();
    }

    private void getData() {
        managerAdapter = new ManagerAdapter(getApplicationContext(), menuItemList);
        recycleViewManager.setAdapter(managerAdapter);
    }

    private void actionBar() {
        setSupportActionBar(toolbarManager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarManager.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarManager = findViewById(R.id.toolbarManager);
        recycleViewManager = findViewById(R.id.recycleViewManager);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycleViewManager.setHasFixedSize(true);
        recycleViewManager.setLayoutManager(layoutManager);

        menuItemList.add("quản lý sản phẩm");
        menuItemList.add("quản lý đơn hàng");
    }
}