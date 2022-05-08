package com.manager.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    TextView textViewPhoneNumber, textViewPassword, textViewRegistration, textViewForgotPassword;
    Button buttonLogin;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        getEventClick();
    }

    private void getEventClick() {
        textViewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });

//        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
//                startActivity(intent);
//            }
//        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = textViewPhoneNumber.getText().toString().trim();
                String password = textViewPassword.getText().toString().trim();

                if(TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại!", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                }
                else {
                    login(phoneNumber, password);
                }
            }
        });
    }

    private void login(String phoneNumber, String password) {
        compositeDisposable.add(apiSalesApp.login(phoneNumber, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.getSuccess()) {
                                Utils.user = userModel.getUserList().get(0);
                                Paper.book().write("user", Utils.user);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Tên tài khoản hoặc mật khẩu không chính xác!", Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void init() {
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        textViewPassword = findViewById(R.id.textViewPassword);
        textViewRegistration = findViewById(R.id.textViewRegistration);
//        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        Paper.init(this);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(Paper.book().read("user") != null) {
//            textViewPhoneNumber.setText(Paper.book().read("user"));
//            textViewPassword.setText(Paper.book().read("user"));
//        }
//        else {
//            textViewPhoneNumber.setText("");
//        }
//        if(Paper.book().read("password") != null) {
//        }
//        else {
//            textViewPassword.setText("");
//        }
//
//        if(Paper.book().read("phoneNumber") != null && Paper.book().read("password") != null) {
//            login(Paper.book().read("phoneNumber"), Paper.book().read("password"));
//        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}