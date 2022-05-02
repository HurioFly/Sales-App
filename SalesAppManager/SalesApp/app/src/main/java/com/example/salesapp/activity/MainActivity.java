package com.example.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
import com.example.salesapp.model.User;
import com.example.salesapp.retrofit.APISalesApp;
import com.example.salesapp.retrofit.RetrofitClient;
import com.example.salesapp.utils.Connection;
import com.example.salesapp.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
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

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    List<ProductType> productTypeList = new ArrayList<>();
    ProductTypeAdapter productTypeAdapter;

    List<Product> productList = new ArrayList<>();
    LatestProductAdapter latestProductAdapter;

    FrameLayout frameLayoutCart;
    NotificationBadge notificationBadgeOrderQuantity;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Connection.isConnected(this)) {
            init();
            actionBar();
            viewFlipperAd();
            getProductType();
            getLatestProduct();
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
                        Intent homePage = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(homePage);
                        finish();
                        break;
                    case 1:
                        Intent phonePage = new Intent(getApplicationContext(), ProductActivity.class);
                        phonePage.putExtra("productTypeID", productTypeList.get(i).getProductTypeID());
                        startActivity(phonePage);
                        drawerLayoutMainPage.closeDrawer(GravityCompat.START);
                        break;
                    case 2:
                        Intent laptopPage = new Intent(getApplicationContext(), ProductActivity.class);
                        laptopPage.putExtra("productTypeID", productTypeList.get(i).getProductTypeID());
                        startActivity(laptopPage);
                        drawerLayoutMainPage.closeDrawer(GravityCompat.START);
                        break;
                    case 3:
                        Intent orderHistoryPage = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                        orderHistoryPage.putExtra("productTypeID", productTypeList.get(i).getProductTypeID());
                        startActivity(orderHistoryPage);
                        drawerLayoutMainPage.closeDrawer(GravityCompat.START);
                        break;
                    case 6:
                        Utils.user = new User();
                        Paper.book().delete("user");
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                        finish();
                        break;

                }
            }
        });

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
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("productName", searchView.getQuery().toString());
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getLatestProduct() {
        compositeDisposable.add(apiSalesApp.getLatestProduct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            if(productModel.getSuccess()) {
                                productList = productModel.getProductList();
                                latestProductAdapter = new LatestProductAdapter(getApplicationContext(), productList);
                                recycleViewLatestProduct.setAdapter(latestProductAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getProductType() {
        compositeDisposable.add(apiSalesApp.getProductType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productTypeModel -> {
                            if(productTypeModel.getSuccess()) {
                                productTypeList = productTypeModel.getProductTypeList();
                                productTypeList.add(0,new ProductType("Trang chủ", "https://ngochieu.name.vn/img/home.png"));
                                productTypeList.add(new ProductType("Lịch sử mua hàng", "https://cdn-icons-png.flaticon.com/512/2132/2132336.png"));
                                productTypeList.add(new ProductType("Thông tin", "https://ngochieu.name.vn/img/info.png"));
                                productTypeList.add(new ProductType("Liên hệ", "https://ngochieu.name.vn/img/contact.png"));
                                productTypeList.add(new ProductType("Đăng xuất", "https://cdn0.iconfinder.com/data/icons/interface-icons-rounded/110/Logout-512.png"));
                                productTypeAdapter = new ProductTypeAdapter(getApplicationContext(), productTypeList);
                                listViewNavigationViewMainPage.setAdapter(productTypeAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void viewFlipperAd() {

        List<String> adList = new ArrayList<>();
        adList.add("https://cdn.tgdd.vn/2022/04/banner/30-4-aseri-720-220-720x220-1.png");
        adList.add("https://cdn.tgdd.vn/2022/04/banner/720-220-720x220-322.png");
        adList.add("https://cdn.tgdd.vn/2022/04/banner/30-4-ip-720-220-720x220-1.png");

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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycleViewLatestProduct.setLayoutManager(layoutManager);
        recycleViewLatestProduct.setHasFixedSize(true);

        navigationViewMainPage = findViewById(R.id.navigationViewMainPage);
        listViewNavigationViewMainPage = findViewById(R.id.listViewNavigationViewMainPage);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);

        frameLayoutCart = findViewById(R.id.frameLayoutCart);
        notificationBadgeOrderQuantity = findViewById(R.id.notificationBadgeOrderQuantity);

        Paper.init(this);

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

        getProductType();
        getLatestProduct();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}