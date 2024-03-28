package com.example.soh.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Models.Product;
import com.example.soh.R;
import com.example.soh.Utils.utilsInputFilter;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertProductActivity extends AppCompatActivity {
    EditText name, price, imageUrl , type;
    ImageView imgChooseImage;
    Button btnChooseImage;
    Product product;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private static final int PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE = 1;

    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        name = findViewById(R.id.edtProName);
        price = findViewById(R.id.edtProPrice);
        imageUrl = findViewById(R.id.editImageUrl);
        type = findViewById(R.id.edtProType);
        imgChooseImage = findViewById(R.id.imgChooseImage);
        btnChooseImage = findViewById(R.id.btnChooseImage);

        //Phạm vi từ 1-4
        int minValue = 1;
        int maxValue = 4;
        type.setFilters(new InputFilter[]{utilsInputFilter.createInputFilter(minValue, maxValue)});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Quyền đã được cấp
                // Bạn có thể thực hiện các thao tác liên quan đến việc chọn hình ảnh ở đây
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE);
            }
        }



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
//            imageUrl.setText(imageUri.toString());

            // Hiển thị hình ảnh đã chọn sử dụng Glide
            imgChooseImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUri).into(imgChooseImage);
        }
    }


    public void InsertProductClient(View view) {
        try {
            // Kiểm tra xem imageUri có giá trị không
            if (imageUri != null) {
                // Lấy đường dẫn thực của hình ảnh từ Uri
                String imagePathString = getRealPathFromUri(imageUri);
                File imageFile = new File(imagePathString);

                // Tạo request body từ file ảnh
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

                // Tạo các request body cho các trường dữ liệu khác
                RequestBody productName = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(name.getText()));
                RequestBody productPrice = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price.getText()));
                RequestBody productType = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(type.getText()));
                RequestBody productQuantity = RequestBody.create(MediaType.parse("text/plain"), "1");
                RequestBody productStatus = RequestBody.create(MediaType.parse("text/plain"), "1");

                // Gửi dữ liệu lên server
                Call<ResponseAPIModel> call = apiInterface.insertProductImage(imagePart, productName, productPrice, productType, productStatus, productQuantity);
                call.enqueue(new Callback<ResponseAPIModel>() {
                    @Override
                    public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                        // Xử lý kết quả từ server
                        ResponseAPIModel result = response.body();
                        if (result != null && result.getStatus()) {
                            Toast.makeText(InsertProductActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String errorMessage = result != null ? result.getMessage() : "Lỗi không xác định";
                            Toast.makeText(InsertProductActivity.this, "Thêm thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                        // Xử lý khi có lỗi xảy ra
                        Toast.makeText(InsertProductActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Xử lý khi không có ảnh được chọn
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("InsertProductActivity", "Lỗi trong quá trình InsertProductClient: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
        }
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


    public void CancelProductClick(View view) {
        finish();
    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


}
