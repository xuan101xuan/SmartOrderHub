package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetStaffResponseAPIModel;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Adapters.Admin_StaffAdapter;
import com.example.soh.Models.Staff;
import com.example.soh.MyInterface.OnStaffItemClickListener;
import com.example.soh.R;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminShowStaffActivity extends AppCompatActivity {
    private RecyclerView rcvAdmin;
    private List<Staff> mListStaff;
    private GridLayoutManager gridLayoutManager;
    private Admin_StaffAdapter admin_staffAdapter;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_staff);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //rcv product
        rcvAdmin = findViewById(R.id.rcvAdmin);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rcvAdmin.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getStaffClient();
    }

    private void getStaffClient() {
        Call<GetStaffResponseAPIModel> call = apiInterface.getStaffList();
        call.enqueue(new Callback<GetStaffResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetStaffResponseAPIModel> call, Response<GetStaffResponseAPIModel> response) {
                mListStaff = response.body().getData();
                Collections.sort(mListStaff);
                admin_staffAdapter = new Admin_StaffAdapter(mListStaff, new OnStaffItemClickListener() {
                    @Override
                    public void onStaffItemClick(Staff staff) {
                        final int position = mListStaff.indexOf(staff);//1. Xác định vị trí của item
                        deleteStaffClient(staff, position);
                    }

                    @Override
                    public void onStaffItemIntentClick(Staff staff) {
                        handleStaffClick(staff);
                    }
                });
                rcvAdmin.setAdapter(admin_staffAdapter);
            }

            @Override
            public void onFailure(Call<GetStaffResponseAPIModel> call, Throwable t) {
                Toast.makeText(AdminShowStaffActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleStaffClick(Staff staff) {
        // Tạo Intent và đính kèm thông tin sản phẩm
        Intent intent = new Intent(AdminShowStaffActivity.this, UpdateStaffActivity.class);
        intent.putExtra("staff", staff);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void deleteStaffClient(Staff staff, int position) {
        Call<ResponseAPIModel> call = apiInterface.deleteStaff(String.valueOf(staff.getIdStaff()));
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                if (response.isSuccessful()) {
                    // Xóa món khỏi danh sách khi xóa thành công
                    mListStaff.remove(position);
                    admin_staffAdapter.notifyItemRemoved(position);
                    getStaffClient();
                    showToast(AdminShowStaffActivity.this, "Xóa nhân viên thành công!");
                }
                else {
                    showToast(AdminShowStaffActivity.this, "Xóa nhân viên thất bại!");
                }
            }

            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                Toast.makeText(AdminShowStaffActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //-----Intent----------
    public void GoToAdmin(View view) {
        Intent t = new Intent(AdminShowStaffActivity.this, AdminShowStaffActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void BackToAdmin(View view) {
        Intent t = new Intent(AdminShowStaffActivity.this, AdminActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToAddStaff(View view) {
        Intent t = new Intent(AdminShowStaffActivity.this, InsertStaffActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
