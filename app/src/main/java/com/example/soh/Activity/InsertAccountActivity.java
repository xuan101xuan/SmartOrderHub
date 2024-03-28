package com.example.soh.Activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Models.Account;
import com.example.soh.Models.TableNumber;
import com.example.soh.R;
import com.example.soh.Utils.utilsInputFilter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertAccountActivity extends AppCompatActivity {
    EditText edtUser, edtPass, edtPermission;
    Account account;

    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_account);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        edtUser = findViewById(R.id.edtAccName);
        edtPass = findViewById(R.id.edtPassw);
        edtPermission = findViewById(R.id.edtPermiss);

        //Phạm vi từ 1-4
        int minValue = 1;
        int maxValue = 4;
        edtPermission.setFilters(new InputFilter[]{utilsInputFilter.createInputFilter(minValue, maxValue)});

    }


    public void InsertAccountClient(View view) {

        try {

            account = new Account();
            account.setAccountName(String.valueOf(edtUser.getText()));
            account.setPassword(String.valueOf(edtPass.getText()));
            account.setPermission(Integer.valueOf(edtPermission.getText().toString()));

            //xử lý thêm sản phẩm qua api
            Call<ResponseAPIModel> call = apiInterface.insertAccount(account);
            call.enqueue(new Callback<ResponseAPIModel>() {
                @Override
                public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                    ResponseAPIModel result = response.body();
                    if (result != null && result.getStatus()) {
                        Toast.makeText(InsertAccountActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMessage = result != null ? result.getMessage() : "Lỗi không xác định";
                        Toast.makeText(InsertAccountActivity.this, "Thêm thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                    Toast.makeText(InsertAccountActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("InsertAccountActivity", "Lỗi trong quá trình Insert: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
        }
    }



    public void CancelClick(View view) {
        finish();
    }

}
