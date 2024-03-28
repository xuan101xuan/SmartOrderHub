package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Models.Product;
import com.example.soh.R;
import com.example.soh.Utils.utilsInputFilter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProductActivity extends AppCompatActivity {
    EditText name, price, imageUrl , type, status;
    ImageView imgChooseImage;
    Button btnChooseImage;
    private Product selectedProduct;
    private APIInterface apiInterface;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        name = findViewById(R.id.edtProName);
        price = findViewById(R.id.edtProPrice);
        imageUrl = findViewById(R.id.editImageUrl);
        type = findViewById(R.id.edtProType);
        status = findViewById(R.id.editProStatus);
        imgChooseImage = findViewById(R.id.imgChooseImage2);
        btnChooseImage = findViewById(R.id.btnChooseImage);

        //Phạm vi từ 1-4
        int minValue = 1;
        int maxValue = 4;
        type.setFilters(new InputFilter[]{utilsInputFilter.createInputFilter(minValue, maxValue)});

        //Phạm vi từ 0-1
        int minValue2 = 0;
        int maxValue2 = 1;
        status.setFilters(new InputFilter[]{utilsInputFilter.createInputFilter(minValue2, maxValue2)});

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product")) {
            selectedProduct = (Product) intent.getSerializableExtra("product");
            if (selectedProduct != null) {
                // Hiển thị thông tin sản phẩm trong EditText
                showProductDetails();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
//        getProductClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String imagePath = getRealPathFromUri(imageUri);

            if (imagePath != null) {
                // Upload hình ảnh lên server
//                uploadImage(imagePath);
            } else {
                showToast(UpdateProductActivity.this,"Không thể lấy đường dẫn tệp từ URI");
            }
//            // Hiển thị hình ảnh đã chọn sử dụng Glide
            imageUrl.setText(imageUri.toString());
            Glide.with(this).load(imageUri).into(imgChooseImage);
        }
    }
    private void showProductDetails() {
        if (selectedProduct != null) {
            name.setText(selectedProduct.getProductName());
            price.setText(String.format("%.0f", selectedProduct.getProductPrice()));
            imageUrl.setText(selectedProduct.getImageUrl());
//            Glide.with(this)
//                    .load(selectedProduct.getImageUrl())
//                    .centerCrop()
//                    .fitCenter()
//                    .into(imgChooseImage);

            type.setText(String.valueOf(selectedProduct.getProductType()));
            status.setText(String.valueOf(selectedProduct.getProductStatus()));
        }
    }
    private void updateProductClient(Product updatedProduct){
        Call<ResponseAPIModel> call = apiInterface.updateProduct(updatedProduct.getIdProduct(), updatedProduct);
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                ResponseAPIModel result = response.body();
                if (result != null && result.getStatus()) {
                    showToast(UpdateProductActivity.this,"Cập nhật thông tin sản phẩm thành công");
                } else {
                    showToast(UpdateProductActivity.this,"Cập nhật thông tin sản phẩm thất bại");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(UpdateProductActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }
    private Product getUpdatedProduct() {
        String productName = name.getText().toString();
        double productPrice = Double.parseDouble(price.getText().toString());
        String productImageUrl = imageUrl.getText().toString();
        String productType = type.getText().toString();
        String productStatus = status.getText().toString();

        // Tạo một đối tượng Product mới với thông tin đã được cập nhật
        Product updatedProduct = new Product();
        updatedProduct.setIdProduct(selectedProduct.getIdProduct()); // Giữ nguyên ID của sản phẩm
        updatedProduct.setProductName(productName);
        updatedProduct.setProductPrice(productPrice);
        updatedProduct.setImageUrl(productImageUrl);
        updatedProduct.setProductType(Integer.parseInt(productType));
        updatedProduct.setProductStatus(Integer.parseInt(productStatus));

        return updatedProduct;
    }


    //Intent
    public void CancelProductClick(View view) {
        finish();
    }

    public void updateProductClick(View view) {
        // Lấy thông tin sản phẩm đã được cập nhật từ EditText
        Product updatedProduct = getUpdatedProduct();

        // Gọi phương thức cập nhật sản phẩm thông qua API
        updateProductClient(updatedProduct);
        Intent t = new Intent(UpdateProductActivity.this, AdminShowProductActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}

