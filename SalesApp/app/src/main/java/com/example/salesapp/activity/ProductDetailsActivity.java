package com.example.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.salesapp.R;
import com.example.salesapp.model.Product;

import java.text.DecimalFormat;

public class ProductDetailsActivity extends AppCompatActivity {

    Toolbar toolbarProductDetails;
    ImageView imageViewProductImage;
    TextView textViewProductName, textViewProductPrice, textViewProductDescription;
    Spinner spinnerQuantityOfProductToOrder;
    Button bottonAddToCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        init();
        actionBar();
        getData();
    }

    private void getData() {
        Product product = (Product) getIntent().getSerializableExtra("product");
        Glide.with(this).load(product.getProductImage()).into(imageViewProductImage);
        textViewProductName.setText(product.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        textViewProductPrice.setText("Giá: " + decimalFormat.format(product.getProductPrice()) + "VNĐ");
        textViewProductDescription.setText(product.getProductDescription());

        Integer[] number = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(this, androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, number);
        spinnerQuantityOfProductToOrder.setAdapter(arrayAdapter);
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
        textViewProductName = findViewById(R.id.textViewProductName);
        textViewProductPrice = findViewById(R.id.textViewProductPrice);
        textViewProductDescription = findViewById(R.id.textViewProductDescription);
        spinnerQuantityOfProductToOrder = findViewById(R.id.spinnerQuantityOfProductToOrder);
        bottonAddToCard = findViewById(R.id.bottonAddToCard);
    }
}