package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetCashierResponseAPIModel;
import com.example.soh.APIModels.GetReportResponseAPIModel;
import com.example.soh.APIModels.GetTableResponseAPIModel;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Adapters.CashierAdapter;
import com.example.soh.Adapters.TableAdapter;
import com.example.soh.Models.Cashier;
import com.example.soh.Models.Kitchen;
import com.example.soh.Models.Report;
import com.example.soh.Models.TableNumber;
import com.example.soh.Models.TableViewModel;
import com.example.soh.MyInterface.OnTableItemClickListener;
import com.example.soh.R;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashierActivity extends AppCompatActivity {
    private RecyclerView rcvCashier, rcvTableShow;
    private TextView tvSoLuongMon, tvTamTinh, tvTongTien, tvTableNumber;
    private Button btnThanhToan;
    private List<Cashier> mListCashier;
    private List<Report> mListReport;
    private List<TableNumber> mListTableNumber;
    private GridLayoutManager gridLayoutManager;
    CashierAdapter cashierAdapter;
    TableAdapter tableAdapter;
    private TableViewModel tableViewModel;
    private ExecutorService executorService;
    APIInterface apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //rcv cashier
        rcvCashier = findViewById(R.id.rcvCashier);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rcvCashier.setLayoutManager(gridLayoutManager);

        //rcvTableShow
        rcvTableShow = findViewById(R.id.rcvTableShow);
        gridLayoutManager = new GridLayoutManager(this, 2);
        rcvTableShow.setLayoutManager(gridLayoutManager);

        cashierAdapter = new CashierAdapter(mListCashier);

        //xử ly phân quyền
        Intent intent = getIntent();
        int userPermission = intent.getIntExtra("userPermission", -1);
        LinearLayout linearLayoutToHide = findViewById(R.id.llKitchen);
        LinearLayout linearLayoutToShow = findViewById(R.id.llLogout);
        if (userPermission == 4) {
            linearLayoutToHide.setVisibility(View.GONE);
            linearLayoutToShow.setVisibility(View.VISIBLE);
        }

        tvTableNumber = findViewById(R.id.tvTableNumber);

        btnThanhToan = findViewById(R.id.btnThanhToan);
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // lấy số bàn từ EditText
                String getTableNumber = tvTableNumber.getText().toString();
                if (!getTableNumber.isEmpty()) {
                    int tableSelectedNumber = Integer.parseInt(getTableNumber);
                    deleteItemsInCashier(tableSelectedNumber);
                    UpdateTableStatusClient(tableSelectedNumber);
                    tvTableNumber.setText("0");
                    refreshCashierRecyclerView();
                    showToast(CashierActivity.this,"Hóa đơn của bàn số: " + tableSelectedNumber);
                }else {
                    // Xử lý nếu người dùng không nhập gì
                    showToast(CashierActivity.this,"Vui lòng nhập số bàn!");
                }

            }
        });

        tableViewModel = new ViewModelProvider(this).get(TableViewModel.class);

//         Thêm LiveData để theo dõi sự thay đổi trong danh sách bàn
        tableViewModel.getUpdateTableNumberListLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean update) {
                if (update != null && update) {
                    // Gọi GetTableClient để lấy dữ liệu mới từ API khi có sự thay đổi
                    GetTableListClient();
                }
            }
        });

        //xu ly tren 5 luong
        executorService = Executors.newFixedThreadPool(5);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Thực hiện công việc  ở đây
                GetTableListClient();
                GetReportListClient();
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
        tableViewModel.getUpdateTableNumberListLiveData().removeObservers(this);
    }
    private void GetCashierClient(int tableSelectedNumber){
        Call<GetCashierResponseAPIModel> call = apiInterface.getCashierList(tableSelectedNumber);
        call.enqueue(new Callback<GetCashierResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetCashierResponseAPIModel> call, Response<GetCashierResponseAPIModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mListCashier = response.body().getData();
                    if (!mListCashier.isEmpty()) {
                        setupCashierRecyclerView();
                        updateThongTinMonAn();
                    } else {
                        refreshCashierRecyclerView();
                        showToast(CashierActivity.this, "Bàn hiện chưa có món");
                    }
                } else {
                    showToast(CashierActivity.this,"Không thể lấy thông tin của bàn");
                }
            }
            @Override
            public void onFailure(Call<GetCashierResponseAPIModel> call, Throwable t) {
                showToast(CashierActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }



    // Hàm mới để gọi API và lấy danh sách bàn với status = 1
    private void GetTableListClient() {
        Call<GetTableResponseAPIModel> call = apiInterface.getTableStatusList(1); // Chỉ lấy bàn có status = 1
        call.enqueue(new Callback<GetTableResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetTableResponseAPIModel> call, Response<GetTableResponseAPIModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
                if (tableAdapter == null) {
                    mListTableNumber = response.body().getData();
                    Collections.sort(mListTableNumber);
                    // Cập nhật dữ liệu cho RecyclerView của bàn
                    if (tableViewModel.getUpdateTableNumberListLiveData() != null) {
                        tableViewModel.getUpdateTableNumberListLiveData().setValue(true);
                    }

                    tableAdapter = new TableAdapter(mListTableNumber, new OnTableItemClickListener()  {
                        @Override
                        public void onTableItemClick(TableNumber tableNumber) {
                            // Xử lý số bàn, ví dụ: cập nhật EditText
                            if (tableNumber != null) {
                                Log.d("TableItemClick", "Clicked on tableNum: " + tableNumber.getTableNum());
                                // Xử lý số bàn, ví dụ: cập nhật EditText
                                tvTableNumber.setText(String.valueOf(tableNumber.getTableNum()));
                                int getTableNumber = Integer.parseInt(String.valueOf(tableNumber.getTableNum()));
                                GetCashierClient(getTableNumber);
                                if (cashierAdapter != null) {
                                    cashierAdapter.notifyDataSetChanged();
                                }
                            }
                            Log.d("TableItemClick", "Clicked on tableNum: " + tableNumber.getTableNum());
                        }
                    });
                    rcvTableShow.setAdapter(tableAdapter);
                } else {
                    mListTableNumber.clear(); // Xóa toàn bộ dữ liệu cũ
                    mListTableNumber.addAll(response.body().getData()); // Thêm dữ liệu mới
                    Collections.sort(mListTableNumber);

                    if (tableViewModel.getUpdateTableNumberListLiveData() != null) {
                        tableViewModel.getUpdateTableNumberListLiveData().setValue(true);
                    }
                    tableAdapter.notifyDataSetChanged(); // Thông báo cập nhật
                }
            }

            @Override
            public void onFailure(Call<GetTableResponseAPIModel> call, Throwable t) {
                showToast(CashierActivity.this, "Thất bại + " + t.getMessage());
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
                showToast(CashierActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }

    //------lam mới rcv------------
    private void setupCashierRecyclerView() {
        cashierAdapter = new CashierAdapter(mListCashier, foodForCashier -> {
            int position = mListCashier.indexOf(foodForCashier);
            mListCashier.remove(position);
        });
        rcvCashier.setAdapter(cashierAdapter);
    }
    private void refreshCashierRecyclerView() {
        cashierAdapter = new CashierAdapter(mListCashier); // Sử dụng adapter đã có để hiển thị danh sách đã chọn.
        updateThongTinMonAn();
        rcvCashier.setAdapter(cashierAdapter);
//        cashierAdapter.notifyDataSetChanged();
    }



    //------ xử lý update status table------

    private void GetTableClient(int tableSelectedNumber) {
        //show món
        Log.d("CashierActivity", "TableSelectedNumber: " + tableSelectedNumber);
        Call<GetTableResponseAPIModel> call = apiInterface.getTableNumberList(tableSelectedNumber);
        call.enqueue(new Callback<GetTableResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetTableResponseAPIModel> call, Response<GetTableResponseAPIModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TableNumber> mListTableNumber = response.body().getData();
                    if (!mListTableNumber.isEmpty()) {
                        TableNumber tableToUpdate = mListTableNumber.get(0); // Lấy thông tin của bàn từ danh sách
                        tableToUpdate.setTableStatus(0); // Cập nhật trạng thái
                        UpdateTableClient(tableToUpdate);
                    } else {
                        showToast(CashierActivity.this, "Không tìm thấy thông tin của bàn");
                    }
                } else {
                    Toast.makeText(CashierActivity.this, "Không thể lấy thông tin của bàn", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetTableResponseAPIModel> call, Throwable t) {
                showToast(CashierActivity.this,"Thất bại + " + t.getMessage());
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
                    Log.d("CashierActivity", "TableId to update: " + tableToUpdate.getIdTable());
                    showToast(CashierActivity.this,"Cập nhật trạng thái bàn thành công");
                } else {
                    showToast(CashierActivity.this,"Cập nhật trạng thái bàn thất bại");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(CashierActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }

    private void UpdateTableStatusClient(int tableSelectedNumber) {
        GetTableClient(tableSelectedNumber); // Lấy thông tin tableId từ GetTableClient
    }

    //-----------thêm vào excel-------------
    private void exportToExcel(List<Report> mListReport) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        try {
            // Đọc tệp Excel hiện tại nếu có
            File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDirectory, "report_date_" + currentDate +".xls");

            HSSFWorkbook workbook;
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                workbook = new HSSFWorkbook(inputStream);
                inputStream.close();
            } else {
                // Tạo một tệp mới nếu không tìm thấy tệp hiện tại
                workbook = new HSSFWorkbook();
            }

            // Lấy hoặc tạo sheet "Kitchen Items"
            HSSFSheet sheet = workbook.getSheet("Report SOH");
            if (sheet == null) {
                sheet = workbook.createSheet("Report SOH");
            }

            // Danh sách để theo dõi các món đã thêm vào
            List<String> addedProducts = new ArrayList<>();

            // Thêm các món mới vào danh sách trong Excel
            for (Report newItem : mListReport) {
                String productName = newItem.getProductName();
                int newQuantity = newItem.getProductQuantity();

                // Kiểm tra xem món đã được thêm vào hay chưa
                if (addedProducts.contains(productName)) {
                    // Nếu đã tồn tại, tìm dòng tương ứng và so sánh với API
                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                        HSSFRow row = sheet.getRow(i);
                        HSSFCell cellName = row.getCell(0);
                        HSSFCell cellQuantity = row.getCell(2);

                        if (cellName.getStringCellValue().equals(productName)) {
                            // Lấy số lượng hiện tại từ Excel
                            int existingQuantity = (int) cellQuantity.getNumericCellValue();

                            // So sánh với API và cập nhật số lượng nếu cần
                            if (newQuantity < existingQuantity) {
                                cellQuantity.setCellValue(newQuantity);
                            }
                            break;
                        }
                    }
                } else {
                    // Nếu chưa tồn tại, thêm vào sheet và danh sách
                    addedProducts.add(productName);

                    HSSFRow newRow = sheet.createRow(sheet.getLastRowNum() + 1);

                    HSSFCell cellName = newRow.createCell(0);
                    cellName.setCellValue(productName);

                    HSSFCell cellPrice = newRow.createCell(1);
                    cellPrice.setCellValue(newItem.getProductPrice());

                    HSSFCell cellQuantity = newRow.createCell(2);
                    cellQuantity.setCellValue(newQuantity);
                }
            }

            // Lưu lại tệp Excel
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            showToast(CashierActivity.this, "updated in Excel");
        } catch (IOException e) {
            e.printStackTrace();
            showToast(CashierActivity.this, "Error updating items in Excel");
        }
    }

                //------ xóa món ăn có trong bàn------

    private void deleteItemsInCashier(int tableSelectedNumber) {
        int totalItems = mListCashier.size();
        int maxThreads = Math.min(totalItems, 5);
        // Kiểm tra nếu maxThreads <= 0, sử dụng ít nhất một luồng
        int threadPoolSize = Math.max(maxThreads, 1);
        // Tạo ExecutorService với số lượng luồng tùy thuộc vào số item
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize );

        final AtomicInteger itemsDeleted = new AtomicInteger(0);
        // Đặt giá trị của MAX_THREADS bằng với số lượng item có trong danh sách

        for (Cashier cashier : mListCashier) {
            executorService.execute(() -> {
                deleteCashierItem(String.valueOf(cashier.getIdCashier()), tableSelectedNumber, totalItems, itemsDeleted);
            });
        }
        executorService.shutdown();
        showToast(CashierActivity.this, "Tất cả món đã được thanh toán.");
    }

    private void deleteCashierItem(String idCashier, int tableSelectedNumber, int totalItems, AtomicInteger itemsDeleted) {
        Call<ResponseAPIModel> call = apiInterface.deleteCashier(idCashier);
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                if (response.isSuccessful()) {
                    itemsDeleted.incrementAndGet();

                    if (itemsDeleted.get() == totalItems) {
                        // Tất cả các mục đã được xóa, gọi hàm để làm mới danh sách
                        GetCashierClient(tableSelectedNumber);
                    }
                    Log.d("CashierActivity", "Xóa thành công ID " + idCashier);
                } else {
                    showToast(CashierActivity.this, "Lỗi khi thanh toán");
                }
            }

            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(CashierActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }


    private void updateThongTinMonAn() {
        //tinh so luong
        int soLuongMonAn = mListCashier.size();
        tvSoLuongMon = findViewById(R.id.tvSoLuongMonCashier); // ánh xạ TextView này trong phần khai báo
        tvSoLuongMon.setText("Số lượng món: " + soLuongMonAn);

        //tạm tính
        tvTamTinh = findViewById(R.id.tvTamTinhCashier); // ánh xạ TextView này trong phần khai báo
        double tongTien = tinhTongTien();
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tvTamTinh.setText("Tạm tính: " +decimalFormat.format(tongTien)+ " VNĐ");

        //tổng tiền sau VAT
        tvTongTien = findViewById(R.id.tvTongTienCashier); // ánh xạ TextView này trong phần khai báo
        double tongTienVAT = tinhTongTienVAT(tongTien); // Tính tổng số tiền sau VAT
        tvTongTien.setText("Tổng tiền sau VAT: " + decimalFormat.format(tongTienVAT) + " VNĐ");
    }
    private double tinhTongTien() {
        double tongTien = 0;
        for (Cashier cashier : mListCashier) {
            tongTien += cashier.getProductPrice() * cashier.getProductQuantity();
        }
        return tongTien;
    }

    private double tinhTongTienVAT(double tongTien) {
        // Tính số tiền VAT 8%
        double vat = tongTien * 0.08;
        return tongTien + vat;
    }

    //Intent
    public void GoToHome(View view) {
        Intent t = new Intent(CashierActivity.this, MainActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToKitchen(View view) {
        Intent t = new Intent(CashierActivity.this, KitchenActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToAdmin(View view) {
        Intent t = new Intent(CashierActivity.this, AdminActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToLogin(View view) {
        Intent t = new Intent(CashierActivity.this, LoginActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void ExportExcel(View view){
        exportToExcel(mListReport);
    }
}
