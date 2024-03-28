package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class UpdateAccountActivity extends AppCompatActivity {
    EditText edtUser, edtPass, edtPermiss;
    private Account selectedAccount;
    private APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        edtUser = findViewById(R.id.edtAccName);
        edtPass = findViewById(R.id.edtPassw);
        edtPermiss = findViewById(R.id.edtPermiss);

        //Phạm vi từ 1-4
        int minValue = 1;
        int maxValue = 4;
        edtPermiss.setFilters(new InputFilter[]{utilsInputFilter.createInputFilter(minValue, maxValue)});

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("account")) {
            selectedAccount = (Account) intent.getSerializableExtra("account");
            if (selectedAccount != null) {
                // Hiển thị thông tin sản phẩm trong EditText
                showAccountDetails();
            }
        }
    }



    private void showAccountDetails() {
        if (selectedAccount != null) {
            edtUser.setText(String.valueOf(selectedAccount.getAccountName()));
            edtPass.setText(String.valueOf(selectedAccount.getPassword()));
            edtPermiss.setText(String.valueOf(selectedAccount.getPermission()));
        }
    }
    private void updateAccountClient(Account updatedAccount){
        Log.d("Update", "updateAcc:  "+ updatedAccount);
        Call<ResponseAPIModel> call = apiInterface.updateAccount(updatedAccount.getIdAcc(), updatedAccount);
        Log.d("Update2", "Id update:  "+ updatedAccount.getIdAcc());
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                ResponseAPIModel result = response.body();
                if (result != null && result.getStatus()) {
                    showToast(UpdateAccountActivity.this,"Cập nhật thông tin sản phẩm thành công");
                } else {
                    showToast(UpdateAccountActivity.this,"Cập nhật thông tin sản phẩm thất bại");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(UpdateAccountActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }
    private Account getUpdatedAccount() {
        String accName = edtUser.getText().toString();
        String accPass = edtPass.getText().toString();
        String accPermiss = edtPermiss.getText().toString();

        // Tạo một đối tượng  mới với thông tin đã được cập nhật
        Account updatedAccount = new Account();
        updatedAccount.setIdAcc(selectedAccount.getIdAcc()); // Giữ nguyên ID của sản phẩm
        updatedAccount.setAccountName(accName);
        updatedAccount.setPassword(accPass);
        updatedAccount.setPermission(Integer.parseInt(accPermiss));

        return updatedAccount;
    }


    //Intent
    public void CancelClick(View view) {
        finish();
    }

    public void UpdateAccountClick(View view) {
        // Lấy thông tin sản phẩm đã được cập nhật từ EditText
        Account updatedAccount = getUpdatedAccount();
        // Gọi phương thức cập nhật sản phẩm thông qua API
        updateAccountClient(updatedAccount);
        Intent t = new Intent(UpdateAccountActivity.this, AdminShowAccountActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

}
