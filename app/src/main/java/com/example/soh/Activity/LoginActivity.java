package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetAccountResponseAPIModel;
import com.example.soh.Models.Account;
import com.example.soh.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUser, edtPass;
    private Button btnLogin;
    APIInterface apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUser.getText().toString();
                String password = edtPass.getText().toString();

                // Gọi hàm processLogin với thông tin từ EditText
                processLogin(username, password);
            }
        });
    }

    private void getAccountList(String username, String password) {
        Call<GetAccountResponseAPIModel> call = apiInterface.getAccountList();
        call.enqueue(new Callback<GetAccountResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetAccountResponseAPIModel> call, Response<GetAccountResponseAPIModel> response) {
                List<Account> accountList = response.body().getData();
                processAccountListAndRedirect(username, password, accountList);
            }

            @Override
            public void onFailure(Call<GetAccountResponseAPIModel> call, Throwable t) {
                showToast(LoginActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }
    private Account findAccountInList(String username, String password, List<Account> accountList) {
        for (Account account : accountList) {
            if (account.getAccountName().equals(username) && account.getPassword().equals(password)) {
                return account; // Tài khoản được tìm thấy
            }
        }
        return null; // Tài khoản không tồn tại hoặc thông tin đăng nhập không chính xác
    }
    private void processLogin(String username, String password) {
        // Gọi hàm để lấy danh sách tài khoản từ API
        getAccountList(username,password);
    }

    private void processAccountListAndRedirect(String username, String password, List<Account> accountList) {
        if (accountList != null) {

            // Tìm kiếm tài khoản trong danh sách
            Account loggedInAccount = findAccountInList(username, password, accountList);

            if (loggedInAccount != null) {
                // Tài khoản tồn tại và thông tin đăng nhập chính xác
                int userPermission = loggedInAccount.getPermission();


                switch (userPermission) {
                    case 1:
                        // Chuyển hướng tới Activity cho vai trò admin
                        Intent iAdmin = new Intent(this, AdminActivity.class);
                        iAdmin.putExtra("userPermission", userPermission);
                        startActivity(iAdmin);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case 2:
                        // Chuyển hướng tới Activity cho vai trò order
                        Intent iOrder = new Intent(this, MainActivity.class);
                        iOrder.putExtra("userPermission", userPermission);
                        startActivity(iOrder);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case 3:
                        // Chuyển hướng tới Activity cho vai trò kitchen
                        Intent iKitchen = new Intent(this, KitchenActivity.class);
                        iKitchen.putExtra("userPermission", userPermission);
                        startActivity(iKitchen);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case 4:
                        // Chuyển hướng tới Activity cho vai trò cashier
                        Intent iCashier = new Intent(this, CashierActivity.class);
                        iCashier.putExtra("userPermission", userPermission);
                        startActivity(iCashier);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    default:
                        showToast(LoginActivity.this, "Không tìm thấy loại tài khoản!");
                        break;
                }
            }else {
                // Hiển thị thông báo khi tài khoản hoặc mật khẩu không chính xác
                showToast(LoginActivity.this, "Tài khoản hoặc mật khẩu không chính xác!");
            }
        }
    }



    //Intent

    public void Cancel(View view){
        finish();
    }
}
