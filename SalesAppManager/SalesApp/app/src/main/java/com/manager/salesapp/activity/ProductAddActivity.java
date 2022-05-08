package com.manager.salesapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.manager.salesapp.R;
import com.manager.salesapp.adapter.ProductTypeAdapter;
import com.manager.salesapp.model.Product;
import com.manager.salesapp.model.ProductType;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAddActivity extends AppCompatActivity {
    Toolbar toolbarProductAdd;
    EditText editTextProductName, editTextProductImage, editTextProductPrice, editTextProductDescription, editTextRemainingProducts;
    ImageView imageViewCamera;
    Spinner spinnerProductType;
    Button buttonProductAdd;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    List<ProductType> productTypeList;
    List<String> productTypeNameList;
    ArrayAdapter<String> spinnerAdapter;

    String mediaPath;
    int productType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        init();
        actionBar();
        getProductType();
        getEvent();
    }

    private void getEvent() {
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProductAddActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        spinnerProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                productType = productTypeList.get(i).getProductTypeID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(editTextProductName.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên sản phẩm!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(editTextProductImage.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập hình ảnh!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(editTextProductPrice.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập giá bán!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(editTextProductDescription.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mô tả!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(editTextRemainingProducts.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập số lượng!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String productName = editTextProductName.getText().toString().trim();
                    String productImage = Utils.BASE_URL + "images/" + editTextProductImage.getText().toString();
                    int productPrice = Integer.parseInt(editTextProductPrice.getText().toString().trim());
                    String productDescription = editTextProductDescription.getText().toString().trim();
                    int remainingProducts = Integer.parseInt(editTextRemainingProducts.getText().toString().trim());

                    compositeDisposable.add(apiSalesApp.insertProduct(productName, productImage, productPrice, productDescription, remainingProducts, productType)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    result -> {
                                        if(result.equals("success")) {
                                            Toast.makeText(getApplicationContext(), "Thêm sản phẩm thành công!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "Thêm sản phẩm không thành công!", Toast.LENGTH_LONG).show();
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                            )
                    );
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath = data.getDataString();
        uploadFile();
    }

    private String getPath(Uri uri) {
        String result;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor == null) {
            result = uri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index =cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }

        return result;
    }

    private void uploadFile() {
        if(mediaPath != null) {
            Uri uri = Uri.parse(mediaPath);
            File file = new File(getPath(uri));

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

            Call<String> call = apiSalesApp.uploadFile(fileToUpload, requestBody);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String serverResponse = response.body();
                    if (serverResponse != null) {
                        if (serverResponse != "unsuccessful") {
                            editTextProductImage.setText(serverResponse);
                        } else {
                            Toast.makeText(getApplicationContext(), "Upload không thành công!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("DDD", t.getMessage());
                }
            });
        }
    }

    private void getProductType() {
        compositeDisposable.add(apiSalesApp.getProductType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productTypeModel -> {
                            if(productTypeModel.getSuccess()) {
                                productTypeList = productTypeModel.getProductTypeList();
                                productTypeNameList = new ArrayList<>();
                                int n = productTypeList.size();
                                for(int i=0; i<n; i++) {
                                    productTypeNameList.add(productTypeList.get(i).getProductTypeName());
                                }
                                spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, productTypeNameList);
                                spinnerProductType.setAdapter(spinnerAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                )
        );
    }

    private void actionBar() {
        setSupportActionBar(toolbarProductAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarProductAdd.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        toolbarProductAdd = findViewById(R.id.toolbarProductAdd);
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductImage = findViewById(R.id.editTextProductImage);
        imageViewCamera = findViewById(R.id.imageViewCamera);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextRemainingProducts = findViewById(R.id.editTextRemainingProducts);
        spinnerProductType = findViewById(R.id.spinnerProductType);
        buttonProductAdd = findViewById(R.id.buttonProductAdd);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}