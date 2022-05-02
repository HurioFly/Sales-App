package com.example.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesapp.R;
import com.example.salesapp.adapter.CartAdapter;
import com.example.salesapp.model.EventBus.CalculateTotalPaymentEvent;
import com.example.salesapp.model.EventBus.CartEmptyEvent;
import com.example.salesapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {

    Toolbar toolbarCart;
    RecyclerView recycleViewCart;
    TextView textViewCartEmpty, textViewTotalPayment, textViewPaymentMethods;
    Button buttonPayment;

    CartAdapter cartAdapter;

    int totalPayment;
    String paymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
        actionBar();
        getData();
        getEventClick();
    }

    private void getEventClick() {
        buttonPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra("paymentMethods", paymentMethods);
                intent.putExtra("totalPayment", totalPayment);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        if(Utils.productsInCart.size() > 0) {
            cartAdapter = new CartAdapter(getApplicationContext(), Utils.productsInCart);
            recycleViewCart.setAdapter(cartAdapter);
        }

        paymentMethods = textViewPaymentMethods.getText().toString();
        calculateTotalPayment();
    }

    private void calculateTotalPayment() {
        totalPayment = 0;
        for(int i = 0; i<Utils.productsToBuy.size(); i++) {
            totalPayment += (long) Utils.productsToBuy.get(i).getProductPrice() * Utils.productsToBuy.get(i).getQuantity();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        textViewTotalPayment.setText(decimalFormat.format(totalPayment) + "đ");

        if(totalPayment > 0) {
            buttonPayment.setEnabled(true);
        }
        else {
            buttonPayment.setEnabled(false);
        }
    }

    private void actionBar() {
        setSupportActionBar(toolbarCart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarCart.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarCart = findViewById(R.id.toolbarCart);

        recycleViewCart = findViewById(R.id.recycleViewCart);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycleViewCart.setLayoutManager(layoutManager);
        recycleViewCart.setHasFixedSize(true);

        textViewCartEmpty = findViewById(R.id.textViewCartEmpty);

        textViewPaymentMethods = findViewById(R.id.textViewPaymentMethods);

        textViewTotalPayment = findViewById(R.id.textViewTotalPayment);
        buttonPayment = findViewById(R.id.buttonPayment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.productsInCart.size() > 0) {
            recycleViewCart.setVisibility(View.VISIBLE);
            textViewCartEmpty.setVisibility(View.GONE);
            cartAdapter.notifyDataSetChanged();
        }
        else {
            recycleViewCart.setVisibility(View.GONE);
            textViewCartEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventCalculateTotalPayment(CalculateTotalPaymentEvent event) {
        if(event != null) {
            calculateTotalPayment();
            Toast.makeText(getApplicationContext(), totalPayment, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getEventCartEmpty(CartEmptyEvent event) {
        if(event != null) {
            recycleViewCart.setVisibility(View.GONE);
            textViewCartEmpty.setVisibility(View.VISIBLE);
            buttonPayment.setEnabled(false);
        }
        else {
            recycleViewCart.setVisibility(View.VISIBLE);
            textViewCartEmpty.setVisibility(View.GONE);
            buttonPayment.setEnabled(true);
        }
    }
}