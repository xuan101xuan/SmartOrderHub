package com.example.soh.Activity;

import static com.example.soh.Utils.utilsSetButton.setButtonStyle;
import static com.example.soh.Utils.utilsShow.showToast;
import static com.example.soh.Utils.utilsShowNoti.showNotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetKitchenResponseAPIModel;
import com.example.soh.APIModels.GetTableResponseAPIModel;
import com.example.soh.Adapters.TableAdapter;
import com.example.soh.Models.Kitchen;
import com.example.soh.Models.KitchenViewModel;
import com.example.soh.Models.TableNumber;
import com.example.soh.Models.TableViewModel;
import com.example.soh.R;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rcvTable;
    private TableAdapter tableAdapter;
    private List<TableNumber> mListTableNumber;
    private GridLayoutManager gridLayoutManager;
    private int selectedValue = 1;
    private Button btnFloor1, btnFloor2;
    private TableViewModel tableViewModel;
    private KitchenViewModel kitchenViewModel;
    private List<Kitchen> mListKitchen;
    private int initialListSize = 0;
    private ExecutorService executorService;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        rcvTable = findViewById(R.id.rcvTable);
        gridLayoutManager = new GridLayoutManager(this, 7);
        rcvTable.setLayoutManager(gridLayoutManager);

        //ánh xạ
        btnFloor1 = findViewById(R.id.btnFloor1);
        btnFloor2 = findViewById(R.id.btnFloor2);

        btnFloor1.setOnClickListener(this);
        btnFloor2.setOnClickListener(this);

        Intent intent = getIntent();
        int userPermission = intent.getIntExtra("userPermission", -1);
        LinearLayout linearLayoutToHide = findViewById(R.id.llMain);
        LinearLayout linearLayoutToShow = findViewById(R.id.llLogout);
        if (userPermission == 2) {
            linearLayoutToHide.setVisibility(View.GONE);
            linearLayoutToShow.setVisibility(View.VISIBLE);
        }

        tableViewModel = new ViewModelProvider(this).get(TableViewModel.class);

//         Thêm LiveData để theo dõi sự thay đổi trong danh sách bàn
        tableViewModel.getUpdateTableNumberListLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean update) {
                if (update != null && update) {
                    // Gọi GetTableClient để lấy dữ liệu mới từ API khi có sự thay đổi
                    GetTableClient();
                }
            }
        });

        kitchenViewModel = new ViewModelProvider(this).get(KitchenViewModel.class);

//         Thêm LiveData để theo dõi sự thay đổi trong danh sách món ăn
        kitchenViewModel.getUpdateKitchenListLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean update) {
                if (update != null && update) {
                    // Gọi GetFoodClient để lấy dữ liệu mới từ API khi có sự thay đổi
                    GetKichenClient();
                    if (mListKitchen != null && mListKitchen.size() < initialListSize) {
                        // Điều kiện đạt được, hiển thị thông báo
                        showNotification(MainActivity.this, "Thông báo từ bếp", "Cần phục vụ lên món");
                    }
                    kitchenViewModel.getUpdateKitchenListLiveData().setValue(false);
                }
            }
        });

        //xu ly tren 5 luong
        executorService = Executors.newFixedThreadPool(5);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Thực hiện công việc nặng nề ở đây
                GetTableClient();
                GetKichenClient();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo ExecutorService được đóng khi không cần nữa để tránh rò rỉ bộ nhớ
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        kitchenViewModel.getUpdateKitchenListLiveData().removeObservers(this);
    }

    private void GetKichenClient() {

        Call<GetKitchenResponseAPIModel> call = apiInterface.getAllKitchenList();
        call.enqueue(new Callback<GetKitchenResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetKitchenResponseAPIModel> call, Response<GetKitchenResponseAPIModel> response) {
                mListKitchen = response.body().getData();
                if (kitchenViewModel.getUpdateKitchenListLiveData() != null) {
                    kitchenViewModel.getUpdateKitchenListLiveData().setValue(true);
                }
                initialListSize = mListKitchen.size();
            }

            @Override
            public void onFailure(Call<GetKitchenResponseAPIModel> call, Throwable t) {
                showToast(MainActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }

    private void GetTableClient() {

        //show món
        Call<GetTableResponseAPIModel> call = apiInterface.getTableList(selectedValue);
        call.enqueue(new Callback<GetTableResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetTableResponseAPIModel> call, Response<GetTableResponseAPIModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                List<TableNumber> newTableList = response.body().getData();
                Collections.sort(newTableList);

                runOnUiThread(() -> {
                    if (tableAdapter == null) {
                        mListTableNumber = newTableList;
                        tableAdapter = new TableAdapter(mListTableNumber, tableNumber -> {
                            // Xử lý sự kiện khi bấm vào một bàn
                            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("TableId", tableNumber.getTableNum());

                            // Nhận permission
                            Intent intentGet = getIntent();
                            int userPermission = intentGet.getIntExtra("userPermission", -1);
                            intent.putExtra("userPermission", userPermission);

                            startActivity(intent);
                            Log.d("MainActivity", "Starting OrderActivity");
                        });
                        rcvTable.setAdapter(tableAdapter);
                        if (tableViewModel.getUpdateTableNumberListLiveData() != null) {
                            tableViewModel.getUpdateTableNumberListLiveData().setValue(true);
                        }
                    } else {
                        mListTableNumber.clear();
                        mListTableNumber.addAll(newTableList);
//                        Collections.sort(mListTableNumber);
                        tableAdapter.notifyDataSetChanged();
                        if (tableViewModel.getUpdateTableNumberListLiveData() != null) {
                            tableViewModel.getUpdateTableNumberListLiveData().setValue(true);
                        }
                    }
                });
                } else {
                    showToast(MainActivity.this, "Không thể lấy thông tin của bàn");
                }
            }

            @Override
            public void onFailure(Call<GetTableResponseAPIModel> call, Throwable t) {
                showToast(MainActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }
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


    //-----------Intent-------------

    public void GoToKitchen(View view) {
        Intent t = new Intent(MainActivity.this, KitchenActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void GoToCashier(View view) {
        Intent t = new Intent(MainActivity.this, CashierActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void GoToAdmin(View view) {
        Intent t = new Intent(MainActivity.this, AdminActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToLogin(View view) {
        Intent t = new Intent(MainActivity.this, LoginActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}