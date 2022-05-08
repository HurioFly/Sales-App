package com.manager.salesapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.model.User;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Paper.init(this);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1500);
                }
                catch (Exception ex) {

                }
                finally {
                    if(Paper.book().read("user") == null) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        User user = Paper.book().read("user");

                        compositeDisposable.add(apiSalesApp.login(user.getPhoneNumber(), user.getPassword())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        userModel -> {
                                            if(userModel.getSuccess()) {
                                                Utils.user = userModel.getUserList().get(0);

                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Tài khoản này đã bị xóa!", Toast.LENGTH_LONG).show();
                                                Paper.book().delete("user");
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        },
                                        throwable -> {
                                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                )
                        );
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}