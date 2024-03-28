package com.example.soh.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Models.Staff;
import com.example.soh.Models.TableNumber;
import com.example.soh.R;

import java.text.Normalizer;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertStaffActivity extends AppCompatActivity {
    EditText edtName, edtPhone, edtAddress, edtRole;
    Staff staff;

    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_staff);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        edtName = findViewById(R.id.edtStaffName);
        edtPhone = findViewById(R.id.edtPhoneNum);
        edtAddress = findViewById(R.id.edtAddress);
        edtRole = findViewById(R.id.edtRole);

        //so dien thoai luon nhap la so 0 dau tien
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // kiểm tra có số 0 không
                if (charSequence.length() > 0 && charSequence.charAt(0) != '0') {
                    edtPhone.setText("0" + charSequence.subSequence(1, charSequence.length()));
                    edtPhone.setSelection(1); // Set the cursor position to after the '0'
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed after text changes
            }
        });
    }


    public void InsertStaffClient(View view) {

        try {

            staff = new Staff();
            staff.setNameStaff(String.valueOf(edtName.getText()));
            staff.setPhoneNum(String.valueOf(edtPhone.getText()));
            staff.setAddress(String.valueOf(edtAddress.getText()));

            String staffRole = edtRole.getText().toString();
            String formattedRole = removeAccents(staffRole).trim().toLowerCase().replaceAll("\\s+", "");
            staff.setRole(formattedRole);



            //xử lý thêm sản phẩm qua api
            Call<ResponseAPIModel> call = apiInterface.insertStaff(staff);
            call.enqueue(new Callback<ResponseAPIModel>() {
                @Override
                public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                    ResponseAPIModel result = response.body();
                    if (result != null && result.getStatus()) {
                        Toast.makeText(InsertStaffActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMessage = result != null ? result.getMessage() : "Lỗi không xác định";
                        Toast.makeText(InsertStaffActivity.this, "Thêm thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                    Toast.makeText(InsertStaffActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
        }
    }

    public static String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }


    public void CancelClick(View view) {
        finish();
    }

}
