package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

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

public class UpdateStaffActivity extends AppCompatActivity {
    EditText edtName, edtPhone, edtAddress, edtRole;
    private Staff selectedStaff;
    private APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_staff);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        edtName = findViewById(R.id.edtStaffName);
        edtPhone = findViewById(R.id.edtPhoneNum);
        edtAddress = findViewById(R.id.edtAddress);
        edtRole = findViewById(R.id.edtRole);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("staff")) {
            selectedStaff = (Staff) intent.getSerializableExtra("staff");
            if (selectedStaff != null) {
                // Hiển thị thông tin sản phẩm trong EditText
                showStaffDetails();
            }
        }

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showStaffDetails() {
        if (selectedStaff != null) {
            edtName.setText(selectedStaff.getNameStaff());
            edtPhone.setText(selectedStaff.getPhoneNum());
            edtAddress.setText(selectedStaff.getAddress());
            edtRole.setText(selectedStaff.getRole());
        }
    }
    private void updateStaffClient(Staff updatedStaff){
        Call<ResponseAPIModel> call = apiInterface.updateStaff(updatedStaff.getIdStaff(), updatedStaff);
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                ResponseAPIModel result = response.body();
                if (result != null && result.getStatus()) {
                    showToast(UpdateStaffActivity.this,"Cập nhật thông tin sản phẩm thành công");
                } else {
                    showToast(UpdateStaffActivity.this,"Cập nhật thông tin sản phẩm thất bại");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(UpdateStaffActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }
    private Staff getUpdatedStaff() {
        String staffName = edtName.getText().toString();
        String staffPhone = edtPhone.getText().toString();
        String staffAddress = edtAddress.getText().toString();
        String staffRole = edtRole.getText().toString();
        String formattedRole = removeAccents(staffRole).trim().toLowerCase().replaceAll("\\s+", "");

        // Tạo một đối tượng  mới với thông tin đã được cập nhật
        Staff updatedStaff = new Staff();
        updatedStaff.setIdStaff(selectedStaff.getIdStaff()); // Giữ nguyên ID của sản phẩm
        updatedStaff.setNameStaff(staffName);
        updatedStaff.setPhoneNum(staffPhone);
        updatedStaff.setAddress(staffAddress);
        updatedStaff.setRole(formattedRole);

        return updatedStaff;
    }

    // Hàm loại bỏ dấu tiếng Việt
    public static String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }


    //Intent
    public void CancelClick(View view) {
        finish();
    }

    public void UpdateStaffClick(View view) {
        // Lấy thông tin sản phẩm đã được cập nhật từ EditText
        Staff updatedStaff = getUpdatedStaff();

        // Gọi phương thức cập nhật sản phẩm thông qua API
        updateStaffClient(updatedStaff);
        Intent t = new Intent(UpdateStaffActivity.this, AdminShowStaffActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

}
