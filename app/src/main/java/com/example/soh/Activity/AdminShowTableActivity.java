package com.example.soh.Activity;

import static com.example.soh.Utils.utilsSetButton.setButtonStyle;
import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetTableResponseAPIModel;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Adapters.Admin_TableAdapter;
import com.example.soh.Models.TableNumber;
import com.example.soh.MyInterface.OnTableNumberItemClickListener;
import com.example.soh.R;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminShowTableActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rcvAdmin;
    private List<TableNumber> mListTableNumber;
    private GridLayoutManager gridLayoutManager;
    Admin_TableAdapter admin_tableAdapter;
    private int selectedValue = 1;
    private Button btnFloor1, btnFloor2;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_table);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //rcv product
        rcvAdmin = findViewById(R.id.rcvCashier);
        gridLayoutManager = new GridLayoutManager(this, 8);
        rcvAdmin.setLayoutManager(gridLayoutManager);

        //ánh xạ
        btnFloor1 = findViewById(R.id.btnFloor1);
        btnFloor2 = findViewById(R.id.btnFloor2);

        btnFloor1.setOnClickListener(this);
        btnFloor2.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GetTableClient();
    }

    private void GetTableClient() {
        Call<GetTableResponseAPIModel> call = apiInterface.getTableList(selectedValue);
        call.enqueue(new Callback<GetTableResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetTableResponseAPIModel> call, Response<GetTableResponseAPIModel> response) {
                mListTableNumber = response.body().getData();
                // Sắp xếp danh sách theo TableNum
                Collections.sort(mListTableNumber);
                admin_tableAdapter = new Admin_TableAdapter(mListTableNumber, new OnTableNumberItemClickListener() {
                    @Override
                    public void onTableNumberItemClick(TableNumber tableNumber) {
                        final int position = mListTableNumber.indexOf(tableNumber);//1. Xác định vị trí của item
                        Log.d("Test",position + "");
//                        mListTableNumber.remove(position);// 2. Xóa món ăn khỏi danh sách
                        deleteTableClient(tableNumber, position);
                        //refreshAdminRecyclerView();// 3. Cập nhật giao diện
                    }
                    @Override
                    public void onTableNumberItemIntentClick(TableNumber tableNumber) {
                        handleTableClick(tableNumber);
                    }
                });
                rcvAdmin.setAdapter(admin_tableAdapter);
            }

            @Override
            public void onFailure(Call<GetTableResponseAPIModel> call, Throwable t) {
                showToast(AdminShowTableActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }
    private void handleTableClick(TableNumber tableNumber) {
        // Tạo Intent và đính kèm thông tin sản phẩm
        Intent intent = new Intent(AdminShowTableActivity.this, UpdateTableActivity.class);
        intent.putExtra("table", tableNumber);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void deleteTableClient(TableNumber tableNumber, int position) {
        Call<ResponseAPIModel> call = apiInterface.deleteTable(String.valueOf(tableNumber.getIdTable()));
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                if (response.isSuccessful()) {
                    // Xóa món khỏi danh sách khi xóa thành công
                    mListTableNumber.remove(position);
                    admin_tableAdapter.notifyItemRemoved(position);
                    GetTableClient();
                    showToast(AdminShowTableActivity.this, "Xóa số bàn thành công!");
                }
                else {
                    showToast(AdminShowTableActivity.this, "Xóa số bàn thất bại!");
                }
            }

            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(AdminShowTableActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }

    //-------sk onClick------------
    @Override
    public void onClick(View view) {
        String buttonId = getResources().getResourceEntryName(view.getId()); // Lấy tên của ID

        switch (buttonId){
            case "btnFloor1":
                setButtonStyle(btnFloor1, "#E8C5A3", 1.0f);
                setButtonStyle(btnFloor2, "#6750A3", 0.1f);
                selectedValue = 1;
                GetTableClient();
                break;

            case "btnFloor2":
                setButtonStyle(btnFloor2, "#E8C5A3", 1.0f);
                setButtonStyle(btnFloor1, "#6750A3", 0.1f);
                selectedValue = 2;
                GetTableClient();
                break;
        }
    }

    //-----Intent----------
    public void GoToAdmin(View view) {
        Intent t = new Intent(AdminShowTableActivity.this, AdminShowTableActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void BackToAdmin(View view) {
        Intent t = new Intent(AdminShowTableActivity.this, AdminActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToAddTable(View view) {
        Intent t = new Intent(AdminShowTableActivity.this, InsertTableActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
