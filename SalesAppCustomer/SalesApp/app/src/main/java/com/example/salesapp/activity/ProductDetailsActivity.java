package com.example.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.salesapp.R;
import com.example.salesapp.adapter.ProductAdapter;
import com.example.salesapp.model.OrderDetails;
import com.example.salesapp.model.Product;
import com.example.salesapp.retrofit.APISalesApp;
import com.example.salesapp.retrofit.RetrofitClient;
import com.example.salesapp.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductDetailsActivity extends AppCompatActivity {

    Toolbar toolbarProductDetails;
    ImageView imageViewProductImage, imageViewReduceNumberOfProducts, imageViewIncreaseNumberOfProducts;
    TextView textViewProductName, textViewProductPrice, textViewProductDescription, textViewRemainingProducts, textViewNumberOfProducts;
    Button buttonAddToCart;
    FrameLayout frameLayoutCart;
    NotificationBadge notificationBadgeOrderQuantity;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    int productID;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        init();
        actionBar();
        getData();
        getEventClick();
    }

    private void getEventClick() {
        imageViewReduceNumberOfProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(textViewNumberOfProducts.getText().toString());
                if(quantity > 1) {
                    quantity--;
                    textViewNumberOfProducts.setText(quantity + "");
                }
            }
        });

        imageViewIncreaseNumberOfProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(textViewNumberOfProducts.getText().toString());
                if(quantity < product.getRemainingProducts()) {
                    quantity++;
                    textViewNumberOfProducts.setText(quantity + "");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Kho chỉ còn " + quantity + " sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        frameLayoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addToCart() {
        int quantity = Integer.parseInt(textViewNumberOfProducts.getText().toString());

        boolean flag = false;

        if(Utils.productsInCart.size() > 0) {
            for(int i = 0; i<Utils.productsInCart.size(); i++) {
                if(Utils.productsInCart.get(i).getProductID() == product.getProductID()) {
                    Utils.productsInCart.get(i).setQuantity(Utils.productsInCart.get(i).getQuantity()+quantity);
                    flag = true;
                }
            }

            if(!flag) {
                OrderDetails orderDetails = new OrderDetails(product.getProductID(), product.getProductName(), product.getProductImage(), product.getProductPrice(), product.getRemainingProducts(), quantity);
                Utils.productsInCart.add(orderDetails);
            }
        }
        else {
            OrderDetails orderDetails = new OrderDetails(product.getProductID(), product.getProductName(), product.getProductImage(), product.getProductPrice(), product.getRemainingProducts(), quantity);
            Utils.productsInCart.add(orderDetails);
        }
        notificationBadgeOrderQuantity.setText(String.valueOf(Utils.productsInCart.size()));
    }

    private void getData() {
        compositeDisposable.add(apiSalesApp.getProductByProductID(productID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            if(productModel.getSuccess()) {
                                product = productModel.getProductList().get(0);
                                Glide.with(this).load(product.getProductImage()).into(imageViewProductImage);
                                textViewProductName.setText(product.getProductName());
                                DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                                textViewProductPrice.setText("Giá: " + decimalFormat.format(product.getProductPrice()) + "đ");
                                textViewProductDescription.setText(product.getProductDescription());
                                if(product.getRemainingProducts() > 0) {
                                    textViewRemainingProducts.setText("Kho còn: " + product.getRemainingProducts());
                                }
                                else {
                                    textViewRemainingProducts.setText("Sản phẩm này đã hết hàng");
                                    buttonAddToCart.setEnabled(false);
                                }
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                )
        );
    }

    private void actionBar() {
        setSupportActionBar(toolbarProductDetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarProductDetails.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarProductDetails = findViewById(R.id.toolbarProductDetails);
        imageViewProductImage = findViewById(R.id.imageViewProductImage);
        imageViewReduceNumberOfProducts = findViewById(R.id.imageViewReduceNumberOfProducts);
        imageViewIncreaseNumberOfProducts = findViewById(R.id.imageViewIncreaseNumberOfProducts);
        textViewProductName = findViewById(R.id.textViewProductName);
        textViewProductPrice = findViewById(R.id.textViewProductPrice);
        textViewProductDescription = findViewById(R.id.textViewProductDescription);
        textViewRemainingProducts = findViewById(R.id.textViewRemainingProducts);
        textViewNumberOfProducts = findViewById(R.id.textViewNumberOfProducts);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        frameLayoutCart = findViewById(R.id.frameLayoutCart);
        notificationBadgeOrderQuantity = findViewById(R.id.notificationBadgeOrderQuantity);

        productID = getIntent().getIntExtra("productID", -1);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);
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

        getData();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}