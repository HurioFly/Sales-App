package com.manager.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PaymentActivity extends AppCompatActivity {
    Toolbar toolbarPayment;
    TextView textViewTotalPayment, textViewPaymentMethods;
    EditText editTextUserName, editTextAddress, editTextPhoneNumber;
    Button buttonPayment;

    int totalPayment;
    String paymentMethods;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        init();
        actionBar();
        getData();
        getEventClick();
    }

    private void getEventClick() {
        buttonPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = editTextUserName.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();

                if(TextUtils.isEmpty(userName)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên người nhận!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ!", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Date currentTime = Calendar.getInstance().getTime();
                    compositeDisposable.add(apiSalesApp.insertOrder(Utils.user.getPhoneNumber(), userName, address, phoneNumber, currentTime.toString(), totalPayment, paymentMethods, new Gson().toJson(Utils.productsToBuy))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    result -> {
                                        if(result.equals("success")) {
                                            Toast.makeText(getApplicationContext(), "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                                            finish();
                                            Utils.productsInCart.removeAll(Utils.productsToBuy);
                                            Utils.productsToBuy.clear();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "Thanh toán không thành công!", Toast.LENGTH_LONG).show();
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                            ));
                }
            }
        });
    }

    private void getData() {
        paymentMethods = getIntent().getStringExtra("paymentMethods");
        totalPayment = getIntent().getIntExtra("totalPayment", 0);
        textViewTotalPayment.setText(paymentMethods);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        textViewTotalPayment.setText(decimalFormat.format(totalPayment) + "đ");
        editTextUserName.setText(Utils.user.getUserName());
        editTextAddress.setText(Utils.user.getAddress());
        editTextPhoneNumber.setText(Utils.user.getPhoneNumber());
    }

    private void actionBar() {
        setSupportActionBar(toolbarPayment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPayment.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarPayment = findViewById(R.id.toolbarPayment);
        textViewTotalPayment = findViewById(R.id.textViewTotalPayment);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        textViewPaymentMethods = findViewById(R.id.textViewPaymentMethods);
        buttonPayment = findViewById(R.id.buttonPayment);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}