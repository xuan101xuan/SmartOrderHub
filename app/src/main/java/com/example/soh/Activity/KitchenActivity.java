package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;
import static com.example.soh.Utils.utilsShowNoti.showNotification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetKitchenResponseAPIModel;
import com.example.soh.APIModels.GetReportResponseAPIModel;
import com.example.soh.APIModels.GetTableResponseAPIModel;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Adapters.KitchenAdapter;
import com.example.soh.Models.Cashier;
import com.example.soh.Models.Kitchen;
import com.example.soh.Models.KitchenViewModel;
import com.example.soh.Models.Report;
import com.example.soh.Models.TableNumber;
import com.example.soh.MyInterface.OnKitchenItemClickListener;
import com.example.soh.R;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class KitchenActivity extends AppCompatActivity{

    private RecyclerView rcvKitchen;
    private List<Kitchen> mListKitchen;
    private GridLayoutManager gridLayoutManager;
    private KitchenViewModel kitchenViewModel;
    private List<Report> mListReport;
    private APIInterface apiInterface;

    private KitchenAdapter kitchenAdapter;

    private ExecutorService executorService;
    private int initialListSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //xử ly phân quyền
        Intent intent = getIntent();
        int userPermission = intent.getIntExtra("userPermission", -1);
        LinearLayout linearLayoutToHide = findViewById(R.id.llKitchen);
        LinearLayout linearLayoutToShow = findViewById(R.id.llLogout);
        if (userPermission == 3) {
            linearLayoutToHide.setVisibility(View.GONE);
            linearLayoutToShow.setVisibility(View.VISIBLE);
        }

        rcvKitchen = findViewById(R.id.rcvKitchen);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rcvKitchen.setLayoutManager(gridLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvKitchen.addItemDecoration(itemDecoration);

        executorService = Executors.newFixedThreadPool(5);

        kitchenViewModel = new ViewModelProvider(this).get(KitchenViewModel.class);

//         Thêm LiveData để theo dõi sự thay đổi trong danh sách món ăn
        kitchenViewModel.getUpdateKitchenListLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean update) {
                if (update != null && update) {
                    // Gọi GetFoodClient để lấy dữ liệu mới từ API khi có sự thay đổi
                    GetKitchenClient();

                    kitchenViewModel.getUpdateKitchenListLiveData().setValue(false);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        GetKitchenClient();
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

    private void InsertProductToCashierClientAsync(Kitchen kitchen, int position) {
        // Thay vì gọi trực tiếp phương thức InsertProductToCashierClient, sử dụng ExecutorService để chạy nó trong một luồng riêng biệt
        executorService.submit(() -> {
            InsertProductToReportClient(kitchen);
            InsertProductToCashierClient(kitchen, position);
        });
    }
    private void GetKitchenClient() {
        //show món
        Call<GetKitchenResponseAPIModel> call = apiInterface.getAllKitchenList();
        call.enqueue(new Callback<GetKitchenResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetKitchenResponseAPIModel> call, Response<GetKitchenResponseAPIModel> response) {
                if (kitchenAdapter == null) {
                    mListKitchen = response.body().getData();
                    Collections.sort(mListKitchen);

                    if (kitchenViewModel.getUpdateKitchenListLiveData() != null) {
                        kitchenViewModel.getUpdateKitchenListLiveData().setValue(true);
                    }
                    initialListSize = mListKitchen.size();
                    kitchenAdapter = new KitchenAdapter(mListKitchen, new OnKitchenItemClickListener() {
                        @Override
                        public void onKitchenItemClick(Kitchen kitchen) {
                            // 1. Xác định vị trí của item
                            final int position = mListKitchen.indexOf(kitchen);
                            // 2. Gọi hàm insert FoodToCashier để thêm món ăn vào Cashier và xóa món ăn khỏi Kitchen
                            InsertProductToCashierClientAsync(kitchen, position);
                            int getTableId = kitchen.getTableNum();
                            UpdateTableStatusClient(getTableId);
//                            showNotification();
                        }
                    });
                    rcvKitchen.setAdapter(kitchenAdapter);
                } else {
//                    // Nếu đã có kitchenAdapter, chỉ cần cập nhật dữ liệu
                    mListKitchen.clear();
                    mListKitchen.addAll(response.body().getData());
                    Collections.sort(mListKitchen);
                    kitchenViewModel.getUpdateKitchenListLiveData().setValue(true);
                    initialListSize = mListKitchen.size();
                    kitchenAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<GetKitchenResponseAPIModel> call, Throwable t) {
                showToast(KitchenActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }


    private void InsertProductToCashierClient(Kitchen kitchen, int position) {
        Cashier cashier = new Cashier();
        cashier.setIdCashier(kitchen.getIdKitchen());
        cashier.setProductName(kitchen.getProductName());
        cashier.setProductPrice(kitchen.getProductPrice());
        cashier.setProductQuantity(kitchen.getProductQuantity());
        cashier.setTableNum(kitchen.getTableNum());
        //gán dữ liệu cho cashier
        Call<ResponseAPIModel> call = apiInterface.insertProductForCashier(cashier);
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                if (response.isSuccessful()) {
                    // Sau khi thêm vào Cashier, tiến hành xóa món ăn khỏi Kitchen
                    DeleteProductClient(kitchen.getIdKitchen(), position);
                    showToast(KitchenActivity.this, "Đã xác nhận hoàn thành món!");
                }
                else {
                    showToast(KitchenActivity.this, "Xác nhận thất bại!");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(KitchenActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }
    private void GetReportListClient() {
        Call<GetReportResponseAPIModel> call = apiInterface.getReportList();
        call.enqueue(new Callback<GetReportResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetReportResponseAPIModel> call, Response<GetReportResponseAPIModel> response) {
                mListReport = response.body().getData();
            }

            @Override
            public void onFailure(Call<GetReportResponseAPIModel> call, Throwable t) {
                showToast(KitchenActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }
    private void InsertProductToReportClient(Kitchen kitchen) {
        Report report = new Report();
        report.setIdRep(kitchen.getIdKitchen());
        report.setProductName(kitchen.getProductName());
        report.setProductPrice(kitchen.getProductPrice());
        report.setProductQuantity(kitchen.getProductQuantity());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        report.setReportDate(currentDate);


//        boolean sanPhamTonTai = false;
//
//        for (Report report : mListReport) {
//            // Kiểm tra xem sản phẩm đã tồn tại trong danh sách dựa trên tên và giá.
//            if (report.getProductName().equals(kitchen.getProductName()) &&
//                    report.getProductPrice() == kitchen.getProductPrice()) {
//                // Sản phẩm tồn tại, tăng số lượng.
//                report.setProductQuantity(report.getProductQuantity() + kitchen.getProductQuantity());
//                sanPhamTonTai = true;
//                break;
//            }
//        }
//
//        if (!sanPhamTonTai) {
//            // Sản phẩm chưa tồn tại, tạo mục mới.
//            Report baoCaoMoi = new Report();
//            baoCaoMoi.setIdRep(kitchen.getIdKitchen());
//            baoCaoMoi.setProductName(kitchen.getProductName());
//            baoCaoMoi.setProductPrice(kitchen.getProductPrice());
//            baoCaoMoi.setProductQuantity(kitchen.getProductQuantity());
//            SimpleDateFormat dinhDangNgay = new SimpleDateFormat("dd-MM-yyyy");
//            String ngayHienTai = dinhDangNgay.format(Calendar.getInstance().getTime());
//            baoCaoMoi.setReportDate(ngayHienTai);
//
//            // Thêm sản phẩm mới vào danh sách.
//            mListReport.add(baoCaoMoi);
//        }
        //gán dữ liệu cho report
        Call<ResponseAPIModel> call = apiInterface.insertProductForReport(report);
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                if (response.isSuccessful()) {      }
                else {
                    showToast(KitchenActivity.this, "Thất bại!");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(KitchenActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }

    private void DeleteProductClient(UUID foodId, final int position) {
        //Xóa món ăn
        Call<ResponseAPIModel> call = apiInterface.deleteKitchen(String.valueOf(foodId));
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                if (response.isSuccessful()) {
                    kitchenViewModel.getUpdateKitchenListLiveData().setValue(true);
                    Toast.makeText(KitchenActivity.this, "Gửi thông báo đến nhân viên!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(KitchenActivity.this, "Gửi thông báo thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                Toast.makeText(KitchenActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    //------ xử lý update status table------

    private void GetTableClient(int tableSelectedNumber) {
        Call<GetTableResponseAPIModel> call = apiInterface.getTableNumberList(tableSelectedNumber);
        call.enqueue(new Callback<GetTableResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetTableResponseAPIModel> call, Response<GetTableResponseAPIModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TableNumber> mListTableNumber = response.body().getData();
                    if (!mListTableNumber.isEmpty()) {
                        // Chỉ lấy phần tử đầu tiên vì bạn chắc chắn rằng danh sách chỉ chứa một bàn
                        TableNumber tableToUpdate = mListTableNumber.get(0);
                        // Cập nhật trạng thái
                        tableToUpdate.setTableStatus(2);
                        UpdateTableClient(tableToUpdate);
                    } else {
                        showToast(KitchenActivity.this, "Không tìm thấy thông tin của bàn");
                    }
                } else {
                    Toast.makeText(KitchenActivity.this, "Không thể lấy thông tin của bàn", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetTableResponseAPIModel> call, Throwable t) {
                showToast(KitchenActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }

    private void UpdateTableClient(TableNumber tableToUpdate){
        Call<ResponseAPIModel> call = apiInterface.updateTable(tableToUpdate.getIdTable(), tableToUpdate);
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                ResponseAPIModel result = response.body();
                if (result != null && result.getStatus()) {
                    Log.d("OrderActivity", "TableId to update: " + tableToUpdate.getIdTable());
                    showToast(KitchenActivity.this,"Cập nhật thành công");
                } else {
                    showToast(KitchenActivity.this,"Cập nhật thất bại");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(KitchenActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }

    private void UpdateTableStatusClient(int tableSelectedNumber) {
        GetTableClient(tableSelectedNumber); // Lấy thông tin tableId từ GetTableClient
    }

    //Intent
    public void GoToHome(View view) {
        Intent t = new Intent(KitchenActivity.this, MainActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
    public void GoToCashier(View view) {
        Intent t = new Intent(KitchenActivity.this, CashierActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToAdmin(View view) {
        Intent t = new Intent(KitchenActivity.this, AdminActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToLogin(View view) {
        Intent t = new Intent(KitchenActivity.this, LoginActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
