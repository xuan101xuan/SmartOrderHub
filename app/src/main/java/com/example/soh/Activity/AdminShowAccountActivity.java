package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetAccountResponseAPIModel;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Adapters.Admin_AccountAdapter;
import com.example.soh.Models.Account;
import com.example.soh.MyInterface.OnAccountItemClickListener;
import com.example.soh.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminShowAccountActivity extends AppCompatActivity {
    private RecyclerView rcvAdmin;
    private List<Account> mListAccount;
    private GridLayoutManager gridLayoutManager;
    private Admin_AccountAdapter admin_accountAdapter;
    private APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //rcv product
        rcvAdmin = findViewById(R.id.rcvCashier);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rcvAdmin.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getAccountClient();
    }

    private void getAccountClient() {
        Call<GetAccountResponseAPIModel> call = apiInterface.getAccountList();
        call.enqueue(new Callback<GetAccountResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetAccountResponseAPIModel> call, Response<GetAccountResponseAPIModel> response) {
                mListAccount = response.body().getData();
                admin_accountAdapter = new Admin_AccountAdapter(mListAccount, new OnAccountItemClickListener() {
                    @Override
                    public void onAccountItemClick(Account account) {
                        final int position = mListAccount.indexOf(account);//1. Xác định vị trí của item
                        deleteAccountClient(account, position);

                    }

                    @Override
                    public void onAccountItemIntentClick(Account account) {
                        handleAccountClick(account);
                    }
                });
                rcvAdmin.setAdapter(admin_accountAdapter);
            }

            @Override
            public void onFailure(Call<GetAccountResponseAPIModel> call, Throwable t) {
                Toast.makeText(AdminShowAccountActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleAccountClick(Account account) {
        // Tạo Intent và đính kèm thông tin sản phẩm
        Intent intent = new Intent(AdminShowAccountActivity.this, UpdateAccountActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void deleteAccountClient(Account account, int position) {
        Call<ResponseAPIModel> call = apiInterface.deleteAccount(String.valueOf(account.getIdAcc()));
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                Log.d("Tag",""+position);
                if (response.isSuccessful()) {
                    // Xóa món khỏi danh sách khi xóa thành công
                    mListAccount.remove(position);
                    admin_accountAdapter.notifyItemRemoved(position);
                    getAccountClient();
                    showToast(AdminShowAccountActivity.this, "Xóa tài khoản thành công!");
                }
                else {
                    showToast(AdminShowAccountActivity.this, "Xóa tài khoản thất bại!");
                }
            }

            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                Toast.makeText(AdminShowAccountActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //-----Intent----------
    public void GoToAdmin(View view) {
        Intent t = new Intent(AdminShowAccountActivity.this, AdminShowAccountActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void BackToAdmin(View view) {
        Intent t = new Intent(AdminShowAccountActivity.this, AdminActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToAddAccount(View view) {
        Intent t = new Intent(AdminShowAccountActivity.this, InsertAccountActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
