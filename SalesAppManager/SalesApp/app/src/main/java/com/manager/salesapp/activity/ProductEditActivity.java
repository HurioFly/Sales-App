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
import com.manager.salesapp.model.Product;
import com.manager.salesapp.model.ProductType;
import com.manager.salesapp.retrofit.APISalesApp;
import com.manager.salesapp.retrofit.RetrofitClient;
import com.manager.salesapp.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductEditActivity extends AppCompatActivity {

    Toolbar toolbarProductAdd;
    EditText editTextProductID, editTextProductName, editTextProductImage, editTextProductPrice, editTextProductDescription, editTextRemainingProducts;
    ImageView imageViewCamera;
    Spinner spinnerProductType;
    Button buttonProductSave;

    APISalesApp apiSalesApp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    List<ProductType> productTypeList;
    List<String> productTypeNameList;
    ArrayAdapter<String> spinnerAdapter;

    String mediaPath;
    int productType;

    Product product;

    boolean isEditProductImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        init();
        actionBar();
        getProductType();
        getData();
        getEvent();
    }

    private void getEvent() {
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProductEditActivity.this)
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

        buttonProductSave.setOnClickListener(new View.OnClickListener() {
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
                    int productID = Integer.parseInt(editTextProductID.getText().toString().trim());
                    String productName = editTextProductName.getText().toString().trim();
                    String productImage;
                    if(isEditProductImage) {
                        productImage = Utils.BASE_URL + "images/" + editTextProductImage.getText().toString();
                    }
                    else {
                        productImage = product.getProductImage();
                    }
                    int productPrice = Integer.parseInt(editTextProductPrice.getText().toString().trim());
                    String productDescription = editTextProductDescription.getText().toString().trim();
                    int remainingProducts = Integer.parseInt(editTextRemainingProducts.getText().toString().trim());

                    compositeDisposable.add(apiSalesApp.updateProduct(productID, productName, productImage, productPrice, productDescription, remainingProducts, productType)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    result -> {
                                        if(result.equals("success")) {
                                            Toast.makeText(getApplicationContext(), "Sửa thông tin sản phẩm thành công!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "Sửa thông tin sản phẩm không thành công!", Toast.LENGTH_LONG).show();
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

            isEditProductImage = true;
        }
    }

    private void getData() {
        editTextProductID.setText(product.getProductID() + "");
        editTextProductName.setText(product.getProductName());
        Uri uri = Uri.parse(product.getProductImage());
        File file = new File(getPath(uri));
        editTextProductImage.setText(file.getName());
        editTextProductPrice.setText(product.getProductPrice() + "");
        editTextProductDescription.setText(product.getProductDescription());
        editTextRemainingProducts.setText(product.getRemainingProducts() + "");

        switch (product.getProductTypeID()) {
            case 1:
                spinnerProductType.post(new Runnable() {
                    @Override
                    public void run() {
                        spinnerProductType.setSelection(0);
                    }
                });
                break;
            case 2:
                spinnerProductType.post(new Runnable() {
                    @Override
                    public void run() {
                        spinnerProductType.setSelection(1);
                    }
                });
                break;
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
        editTextProductID = findViewById(R.id.editTextProductID);
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductImage = findViewById(R.id.editTextProductImage);
        imageViewCamera = findViewById(R.id.imageViewCamera);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextRemainingProducts = findViewById(R.id.editTextRemainingProducts);
        spinnerProductType = findViewById(R.id.spinnerProductType);
        buttonProductSave = findViewById(R.id.buttonProductSave);

        apiSalesApp = RetrofitClient.getInstance(Utils.BASE_URL).create(APISalesApp.class);

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");

        productTypeList = new ArrayList<>();
        productTypeNameList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}