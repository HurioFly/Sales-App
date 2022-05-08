package com.manager.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegistrationActivity extends AppCompatActivity {
    Toolbar toolbarRegistration;
    EditText textViewUserName, textViewAddress, textViewPhoneNumber, textViewPassword, textViewRePassword;
    Button buttonRegistration;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
        actionBar();
        getEventClick();
    }

    private void getEventClick() {
        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
            }
        });
    }

    private void registration() {
        String userName = textViewUserName.getText().toString().trim();
        String address = textViewAddress.getText().toString().trim();
        String phoneNumber = textViewPhoneNumber.getText().toString().trim();
        String password = textViewPassword.getText().toString().trim();
        String rePassword = textViewRePassword.getText().toString().trim();

        if(TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Bạn chưa nhập tên người dùng!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Bạn chưa nhập địa chỉ!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Bạn chưa nhập số điện thoại!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Bạn chưa nhập mật khẩu!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(rePassword)) {
            Toast.makeText(this, "Bạn chưa nhập lại mật khẩu!", Toast.LENGTH_SHORT).show();
        }
        else {
            if(password.equals(rePassword)) {
                compositeDisposable.add(apiSalesApp.registration(phoneNumber, password, userName, address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(result.equals("success")) {
                                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                                Paper.book().write("phoneNumber", phoneNumber);
                                Paper.book().write("password", password);
                                finish();
                            }
                            else {
                                Toast.makeText(this, "Số điện thoại này đã đăng ký cho một tài khoản khác!", Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
            }
            else {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void actionBar() {
        setSupportActionBar(toolbarRegistration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarRegistration.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarRegistration = findViewById(R.id.toolbarRegistration);
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        textViewPassword = findViewById(R.id.textViewPassword);
        textViewRePassword = findViewById(R.id.textViewRePassword);
        buttonRegistration = findViewById(R.id.buttonRegistration);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);

        Paper.init(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}