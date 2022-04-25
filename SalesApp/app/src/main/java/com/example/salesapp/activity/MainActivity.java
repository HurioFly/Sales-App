package com.example.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.salesapp.R;
import com.example.salesapp.adapter.LatestProductAdapter;
import com.example.salesapp.adapter.ProductTypeAdapter;
import com.example.salesapp.model.Product;
import com.example.salesapp.model.ProductType;
import com.example.salesapp.retrofit.APISalesApp;
import com.example.salesapp.retrofit.RetrofitClient;
import com.example.salesapp.utils.Connection;
import com.example.salesapp.utils.Server;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayoutMainPage;
    Toolbar toolbarMainPage;
    ViewFlipper viewFlipperAd;
    RecyclerView recycleViewLatestProduct;
    NavigationView navigationViewMainPage;
    ListView listViewNavigationViewMainPage;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    APISalesApp apiSalesApp;

    List<ProductType> productTypeList;
    ProductTypeAdapter productTypeAdapter;

    List<Product> productList;
    LatestProductAdapter latestProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Connection.isConnected(this)) {
            init();
            actionBar();
            viewFlipperAd();
            apiSalesApp = RetrofitClient.getInstance(Server.BASE_URL).create(APISalesApp.class);
            getProductType();
            getNewProduct();
            getEventClick();
        }
        else {
            Toast.makeText(this, "Lỗi kết nối! Vui lòng kiểm tra và thử lại.", Toast.LENGTH_LONG).show();
        }
    }

    private void getEventClick() {
        listViewNavigationViewMainPage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent homepage = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(homepage);
                        drawerLayoutMainPage.closeDrawer(GravityCompat.START);
                        break;
                    case 1:
                        Intent phonepage = new Intent(getApplicationContext(), ProductActivity.class);
                        phonepage.putExtra("productTypeID", productTypeList.get(i).getProductTypeID());
                        startActivity(phonepage);
                        drawerLayoutMainPage.closeDrawer(GravityCompat.START);
                        break;
                    case 2:
                        Intent laptoppage = new Intent(getApplicationContext(), ProductActivity.class);
                        laptoppage.putExtra("productTypeID", productTypeList.get(i).getProductTypeID());
                        startActivity(laptoppage);
                        drawerLayoutMainPage.closeDrawer(GravityCompat.START);
                        break;
                }
            }
        });
    }

    private void getNewProduct() {
        compositeDisposable.add(apiSalesApp.getLatestProduct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        serverProductTypeList -> {
                            if(!serverProductTypeList.isEmpty()) {
                                productList = serverProductTypeList;
                                latestProductAdapter = new LatestProductAdapter(getApplicationContext(), productList);
                                recycleViewLatestProduct.setAdapter(latestProductAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "Lỗi kết nối server: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getProductType() {
        compositeDisposable.add(apiSalesApp.getProductType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        serverProductTypeList -> {
                            if(!serverProductTypeList.isEmpty()) {
                                productTypeList = serverProductTypeList;
                                productTypeAdapter = new ProductTypeAdapter(getApplicationContext(), productTypeList);
                                listViewNavigationViewMainPage.setAdapter(productTypeAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "Lỗi kết nối server: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void viewFlipperAd() {

        List<String> adList = new ArrayList<>();
        adList.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png");
        adList.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png");
        adList.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg");

        for(int i=0; i<adList.size(); i++) {
            ImageView imageView = new ImageView(this);
            Glide.with(this).load(adList.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipperAd.addView(imageView);
        }

        viewFlipperAd.setFlipInterval(3000);
        viewFlipperAd.setAutoStart(true);

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        viewFlipperAd.setInAnimation(slideIn);
        viewFlipperAd.setOutAnimation(slideOut);
    }

    private void actionBar() {

        setSupportActionBar(toolbarMainPage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarMainPage.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbarMainPage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayoutMainPage.openDrawer(GravityCompat.START);
            }
        });
    }

    private void init() {

        drawerLayoutMainPage = findViewById(R.id.drawerLayoutMainPage);
        toolbarMainPage = findViewById(R.id.toolbarMainPage);
        viewFlipperAd = findViewById(R.id.viewFlipperAd);

        recycleViewLatestProduct = findViewById(R.id.recycleViewLatestProduct);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recycleViewLatestProduct.setLayoutManager(layoutManager);
        recycleViewLatestProduct.setHasFixedSize(true);

        navigationViewMainPage = findViewById(R.id.navigationViewMainPage);
        listViewNavigationViewMainPage = findViewById(R.id.listViewNavigationViewMainPage);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}