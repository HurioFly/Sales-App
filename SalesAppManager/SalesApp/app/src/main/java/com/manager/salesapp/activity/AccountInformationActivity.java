package com.manager.salesapp.activity;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.manager.salesapp.R;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AccountInformationActivity extends AppCompatActivity {
    Toolbar toolbarAccountInformation;
    EditText editTextUserName, editTextAddress, editTextPhoneNumber, editTextCurrentPassword, editTextNewPassword, editTextReNewPassword;
    Button buttonUserNameChange, buttonAddressChange, buttonChangePassword, buttonSaveNewPassword;
    LinearLayout linearLayoutChangePassword;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);

        init();
        actionBar();
        getData();
        getEvent();
    }

    private void getEvent() {
        buttonUserNameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextUserName.isEnabled() == false) {
                    buttonUserNameChange.setText("Lưu");
                    editTextUserName.setEnabled(true);
                    editTextUserName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextUserName, InputMethodManager.SHOW_IMPLICIT);
                }
                else {
                    String newUserName = editTextUserName.getText().toString().trim();
                    if(TextUtils.isEmpty(newUserName)) {
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập họ và tên mới!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        compositeDisposable.add(apiSalesApp.updateUserName(Utils.user.getPhoneNumber(), newUserName)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        result -> {
                                            if(result.equals("success")) {
                                                Toast.makeText(getApplicationContext(), "Đổi họ và tên thành công!", Toast.LENGTH_LONG).show();
                                                Utils.user.setUserName(newUserName);
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Đổi họ và tên không thành công!", Toast.LENGTH_LONG).show();
                                            }
                                        },
                                        throwable -> {
                                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                )
                        );
                        buttonUserNameChange.setText("Sửa");
                        editTextUserName.setEnabled(false);
                    }
                }
            }
        });

        buttonAddressChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextAddress.isEnabled() == false) {
                    buttonAddressChange.setText("Lưu");
                    editTextAddress.setEnabled(true);
                    editTextAddress.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextAddress, InputMethodManager.SHOW_IMPLICIT);
                }
                else {
                    String newUserAddress = editTextUserName.getText().toString().trim();
                    if(TextUtils.isEmpty(newUserAddress)) {
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ mới!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        compositeDisposable.add(apiSalesApp.updateUserName(Utils.user.getPhoneNumber(), newUserAddress)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        result -> {
                                            if(result.equals("success")) {
                                                Toast.makeText(getApplicationContext(), "Đổi địa chỉ thành công!", Toast.LENGTH_LONG).show();
                                                Utils.user.setAddress(newUserAddress);
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Đổi địa chỉ không thành công!", Toast.LENGTH_LONG).show();
                                            }
                                        },
                                        throwable -> {
                                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                )
                        );
                        buttonAddressChange.setText("Sửa");
                        editTextAddress.setEnabled(false);
                    }
                }
            }
        });

        buttonSaveNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = editTextCurrentPassword.getText().toString().trim();
                String newPassword = editTextNewPassword.getText().toString().trim();
                String reNewPassword = editTextReNewPassword.getText().toString().trim();

                if(TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu hiện tại!", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa mật khẩu mới!", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(reNewPassword)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập lại mật khẩu mới!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(newPassword.equals(reNewPassword)) {
                        if(currentPassword.equals(Utils.user.getPassword())) {
                            compositeDisposable.add(apiSalesApp.updateUserPassword(Utils.user.getPhoneNumber(), newPassword)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            result -> {
                                                if(result.equals("success")) {
                                                    Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                                                    Utils.user.setPassword(newPassword);
                                                    Paper.book().write("user", Utils.user);
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(), "Đổi mật khẩu không thành công!", Toast.LENGTH_LONG).show();
                                                }
                                            },
                                            throwable -> {
                                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                    )
                            );
                            buttonChangePassword.setText("Đổi mật khẩu");
                            linearLayoutChangePassword.setVisibility(View.GONE);
                            editTextCurrentPassword.setText("");
                            editTextNewPassword.setText("");
                            editTextReNewPassword.setText("");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Mật khẩu hiện tại không chính xác!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayoutChangePassword.getVisibility() == View.GONE) {
                    buttonChangePassword.setText("Hủy đổi mật khẩu");
                    linearLayoutChangePassword.setVisibility(View.VISIBLE);
                    editTextCurrentPassword.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextCurrentPassword, InputMethodManager.SHOW_IMPLICIT);
                }
                else {
                    buttonChangePassword.setText("Đổi mật khẩu");
                    linearLayoutChangePassword.setVisibility(View.GONE);
                    editTextCurrentPassword.setText("");
                    editTextNewPassword.setText("");
                    editTextReNewPassword.setText("");
                }
            }
        });
    }

    private void getData() {
        editTextUserName.setText(Utils.user.getUserName());
        editTextAddress.setText(Utils.user.getAddress());
        editTextPhoneNumber.setText(Utils.user.getPhoneNumber());
    }

    private void actionBar() {
        setSupportActionBar(toolbarAccountInformation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarAccountInformation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarAccountInformation = findViewById(R.id.toolbarAccountInformation);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextReNewPassword = findViewById(R.id.editTextReNewPassword);
        buttonUserNameChange = findViewById(R.id.buttonUserNameChange);
        buttonAddressChange = findViewById(R.id.buttonAddressChange);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonSaveNewPassword = findViewById(R.id.buttonSaveNewPassword);
        linearLayoutChangePassword = findViewById(R.id.linearLayoutChangePassword);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);
    }
}