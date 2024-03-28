package com.example.soh.Activity;

import static com.example.soh.Utils.utilsSetButton.setButtonStyle;
import static com.example.soh.Utils.utilsShow.showToast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetProductResponseAPIModel;
import com.example.soh.APIModels.GetKitchenResponseAPIModel;
import com.example.soh.APIModels.GetTableResponseAPIModel;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Adapters.CartListAdapter;
import com.example.soh.Adapters.ProductAdapter;
import com.example.soh.Models.Product;
import com.example.soh.Models.Kitchen;
import com.example.soh.Models.TableNumber;
import com.example.soh.MyInterface.OnProductItemClickListener;
import com.example.soh.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvTableId, tvSoLuongMon, tvTamTinh, tvTongTien;
    private RecyclerView rcvFood,rcvSelectedFood;
    private Button btnKhaiVi, btnMonChinh, btnNuocUong, btnTrangMieng, btnXoaTatCa, btnYeuCauDenBep, btnXacNhan;
    private GridLayoutManager gridLayoutManager;
    private List<Product> selectedProductList = new ArrayList<>();
    private List<Kitchen> mListKitchen = new ArrayList<>();

    private List<Product> mListProduct;
    private int selectedValue = 1;
    private int successfulRequestsCount = 0; // Số lượng yêu cầu thành công
    private int totalRequestsCount = mListKitchen.size(); // Tổng số lượng yêu cầu
    private static int createdNumberCounter = 1;

    APIInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // ánh xạ
        btnKhaiVi = findViewById(R.id.btnKhaiVi);
        btnMonChinh = findViewById(R.id.btnMonChinh);
        btnNuocUong = findViewById(R.id.btnNuocUong);
        btnTrangMieng = findViewById(R.id.btnTrangMieng);

        //rcv product
        rcvFood = findViewById(R.id.rcvFood);
        gridLayoutManager = new GridLayoutManager(this, 4);
        rcvFood.setLayoutManager(gridLayoutManager);

        //rcv product da chon
        rcvSelectedFood = findViewById(R.id.rcvSelectedFood);
        GridLayoutManager gridLayoutManagerSelectedFood = new GridLayoutManager(this, 1);
        rcvSelectedFood.setLayoutManager(gridLayoutManagerSelectedFood);


        //event onClick
        btnKhaiVi.setOnClickListener(this);
        btnMonChinh.setOnClickListener(this);
        btnNuocUong.setOnClickListener(this);
        btnTrangMieng.setOnClickListener(this);

        //Update theo số bàn khi click
        // Ánh xạ TextView
        tvTableId = findViewById(R.id.tvBanSo);
        int tableId = getIntent().getIntExtra("TableId", -1); // Nhận giá trị ID từ Intent
        tvTableId.setText("Bàn Số: " + tableId);// Hiển thị giá trị ID lên TextView

        //xử ly phân quyền
        Intent intent = getIntent();
        int userPermission = intent.getIntExtra("userPermission", -1);
        Log.d("OrderActivity", "Permiss: " + userPermission);
        LinearLayout linearLayoutToHide = findViewById(R.id.llOrder);
        LinearLayout linearLayoutToShow = findViewById(R.id.llLogout);
        if (userPermission == 2) {
            linearLayoutToHide.setVisibility(View.GONE);
            linearLayoutToShow.setVisibility(View.VISIBLE);
        }

        //Xóa tất cả
        btnXoaTatCa = findViewById(R.id.btnXoaTatCa);
        btnXoaTatCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Product selectedProduct : selectedProductList) {
                    selectedProduct.setProductQuantity(1);
                }
                selectedProductList.clear();// Xóa tất cả món khỏi danh sách selectedProductList
                refreshSelectedFoodRecyclerView();// Cập nhật giao diện RecyclerView rcvSelectedFood
                updateThongTinMonAn();// Cập nhật thông tin món ăn (số lượng, tạm tính, tổng tiền, v.v.) sau khi xóa
            }
        });

        //Thay đỏi status bàn
        btnXacNhan = findViewById(R.id.btnXacNhan);
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateTableStatusClient(tableId);
                finish();
            }
        });

        //Gui yeu cau den bếp
        btnYeuCauDenBep = findViewById(R.id.btnYeuCauDenBep);
        btnYeuCauDenBep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra xem đã chọn ít nhất một món ăn chưa
                if (selectedProductList.isEmpty()) {
                    showToast(OrderActivity.this, "Vui lòng chọn ít nhất một món ăn trước khi gửi yêu cầu đến bếp.");
                } else {
                    showConfirmationDialog();
                }
            }
        });
        getKitchenClient();
    }


    @Override
    protected void onStart() {
        super.onStart();

        GetProductClient();
    }



    private void GetProductClient(){
        Call<GetProductResponseAPIModel> call = apiInterface.getProductList(selectedValue);
        call.enqueue(new Callback<GetProductResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetProductResponseAPIModel> call, Response<GetProductResponseAPIModel> response) {
                mListProduct = response.body().getData();
                // Thêm log để kiểm tra dữ liệu ban đầu

                ProductAdapter productAdapter = new ProductAdapter(mListProduct, new OnProductItemClickListener() {
                    @Override
                    public void onProductItemClick(Product food) {
                        handleProductClick(food);
                    }
                    @Override
                    public void onProductItemIntentClick(Product product) {}
                });
                rcvFood.setAdapter(productAdapter);
            }

            @Override
            public void onFailure(Call<GetProductResponseAPIModel> call, Throwable t) {
                showToast(OrderActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }
    private void handleProductClick(Product food) {
        Log.d("OrderActivity", "Before quantity update: " + food.getProductQuantity());

        if (food != null) {
            // Kiểm tra xem sản phẩm đã tồn tại trong danh sách chưa
            boolean isProductExist = false;
            for (Product selectedProduct : selectedProductList) {
                if (selectedProduct.getIdProduct() != null && food.getIdProduct() != null && selectedProduct.getIdProduct().equals(food.getIdProduct())) {
//                    // Sản phẩm đã tồn tại, tăng quantity lên 1
                    selectedProduct.setProductQuantity(selectedProduct.getProductQuantity() + 1);
                    // Cập nhật thông tin của sản phẩm trong danh sách
                    selectedProductList.remove(selectedProduct);
                    selectedProductList.add(selectedProduct);
                    isProductExist = true;
                    break;
                }
            }

            if (!isProductExist) {
                // Sản phẩm chưa tồn tại, thêm vào danh sách với quantity là 1(mặc định model product=1)
                Product newProduct = new Product();
                newProduct.setProductName(food.getProductName());
                newProduct.setProductPrice(food.getProductPrice());
                newProduct.setImageUrl(food.getImageUrl());
                newProduct.setProductType(food.getProductType());
                newProduct.setProductStatus(food.getProductStatus());
                // Kiểm tra null trước khi sao chép
                if (food.getIdProduct() != null) {
                    newProduct.setIdProduct(food.getIdProduct());
                }
                newProduct.setProductQuantity(1);
                selectedProductList.add(newProduct);
            }
            Log.d("OrderActivity", "After quantity update: " + food.getProductQuantity());
            refreshSelectedFoodRecyclerView(); // Cập nhật giao diện rcvSelectedFood
            updateThongTinMonAn();
        }
    }

    private void prepareListProductForKitchen(int getTableId) {
        mListKitchen.clear(); // Xóa danh sách hiện tại (nếu có)
        for (Product selectedProduct : selectedProductList) {
            Kitchen kitchen = new Kitchen();
            kitchen.setProductName(selectedProduct.getProductName());
            kitchen.setProductPrice(selectedProduct.getProductPrice());
            //======================tạm bỏ========================
//            kitchen.setImageUrl(selectedProduct.getImageUrl());
            kitchen.setTableNum(getTableId); // Gán TableId theo số bàn đã chọn
            kitchen.setCreatedNumber(createdNumberCounter++); // Gán giá trị và tăng createdNumberCounter
            kitchen.setProductQuantity(selectedProduct.getProductQuantity());
            //======================tạm bỏ========================
//            kitchen.setProductNote(selectedProduct.getProductNote());
//            Log.e("ActiOrder", "Error: " + selectedProduct.getProductNote() );
            mListKitchen.add(kitchen);
        }
        // Cập nhật totalRequestsCount sau khi chuẩn bị danh sách món ăn cho bếp
        totalRequestsCount = mListKitchen.size();
    }
    private void sendRequestToKitchen(){
        successfulRequestsCount = 0; // Đảm bảo reset successfulRequestsCount thành 0
        int totalItems = mListKitchen.size();
        int maxThreads = Math.min(totalItems, 5);
        // Kiểm tra nếu maxThreads <= 0, sử dụng ít nhất một luồng
        int threadPoolSize = Math.max(maxThreads, 1);
        // Tạo ExecutorService với số lượng luồng tùy thuộc vào số item
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize );

        for (Kitchen kitchen : mListKitchen) {
            executorService.execute(() -> {
                Call<ResponseAPIModel> call = apiInterface.insertProductForKitchen(kitchen);
                call.enqueue(new Callback<ResponseAPIModel>() {
                    @Override
                    public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                        if (response.isSuccessful()) {
                            successfulRequestsCount++;
                            // Kiểm tra xem đã gửi thành công cho tất cả các yêu cầu chưa
                            if (successfulRequestsCount == totalRequestsCount) {
                                // Nếu đã gửi thành công cho tất cả các yêu cầu, hiển thị thông báo thành công
                                showToast(OrderActivity.this, "Tất cả yêu cầu đã được gửi đến bếp.");
                            }
                        } else {
                            showToast(OrderActivity.this, "Gửi yêu cầu thất bại.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                        showToast(OrderActivity.this, "Thất bại + " + t.getMessage());
                    }
                });
            });
        }
        executorService.shutdown(); // Đảm bảo đóng executor khi công việc hoàn tất
    }
    private void refreshSelectedFoodRecyclerView() {
        CartListAdapter cartListAdapter = new CartListAdapter(selectedProductList); // Sử dụng adapter đã có để hiển thị danh sách đã chọn.
        updateThongTinMonAn();
        rcvSelectedFood.setAdapter(cartListAdapter);
    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setTitle("Xác nhận!");
        builder.setMessage("Bạn có chắc chắn muốn gửi yêu cầu đến bếp không?");

        // Nút tích cực (Đồng ý)
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Xử lý khi người dùng ấn Đồng ý

                // Yêu cầu đã được gửi thành công
                int tableId = getIntent().getIntExtra("TableId", -1); // Nhận giá trị ID từ Intent
                int getTableId = tableId;// Chuyển đổi tableId từ String sang int
                // Lặp qua từng item trong RecyclerView để lấy giá trị của TextView
                prepareListProductForKitchen(getTableId);

                UpdateTableStatusClient(getTableId);
                showToast(OrderActivity.this, "Yêu cầu gửi món ăn thành công");
                sendRequestToKitchen();
                finish();
                dialogInterface.dismiss();
            }
        });

        // Nút tiêu cực (Hủy bỏ)
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Xử lý khi người dùng ấn Hủy bỏ
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    //-----------------lay danh so lon nhat trong kitchen-------------------\
    private void getKitchenClient() {
        Call<GetKitchenResponseAPIModel> call = apiInterface.getAllKitchenList();
        call.enqueue(new Callback<GetKitchenResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetKitchenResponseAPIModel> call, Response<GetKitchenResponseAPIModel> response) {
                if (response.isSuccessful()) {
                    List<Kitchen> kitchenList = response.body().getData();
                    handleKitchenClient(kitchenList);
                } else {
                    showToast(OrderActivity.this, "Không thể lấy danh sách kitchen từ API.");
                }
            }

            @Override
            public void onFailure(Call<GetKitchenResponseAPIModel> call, Throwable t) {
                showToast(OrderActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }

    private void handleKitchenClient(List<Kitchen> kitchenList) {
        if (kitchenList != null && !kitchenList.isEmpty()) {
            int maxCreatedNumber = findMaxCreatedNumber(kitchenList);
            createdNumberCounter = maxCreatedNumber + 1;
        } else {
            createdNumberCounter = 1;
        }
        int getTableId = getIntent().getIntExtra("TableId", -1);
        prepareListProductForKitchen(getTableId);
    }
    private int findMaxCreatedNumber(List<Kitchen> kitchenList) {
        int maxNumber = kitchenList.get(0).getCreatedNumber();

        for (Kitchen kitchen : kitchenList) {
            int currentNumber = kitchen.getCreatedNumber();
            if (currentNumber > maxNumber) {
                maxNumber = currentNumber;
            }
        }
        return maxNumber;
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
                        if (tableToUpdate.getTableStatus() == 2 || tableToUpdate.getTableStatus() == 0) {
                            // Nếu trạng thái là 2(cần lên món) hoặc 1(bàn trống), đặt thành 1(đang phục vụ) và cập nhật
                            tableToUpdate.setTableStatus(1);
                            UpdateTableClient(tableToUpdate);
                        } else {
                            // Nếu trạng thái không phải là 2(cần lên món)
                            showToast(OrderActivity.this, "Bàn không cần cập nhật");
                        }
                    } else {
                        showToast(OrderActivity.this, "Không tìm thấy thông tin của bàn");
                    }
                } else {
                    showToast(OrderActivity.this, "Không thể lấy thông tin của bàn");
                }
            }
            @Override
            public void onFailure(Call<GetTableResponseAPIModel> call, Throwable t) {
                showToast(OrderActivity.this,"Thất bại + " + t.getMessage());
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
                    showToast(OrderActivity.this,"Cập nhật thành công");
                } else {
                    showToast(OrderActivity.this,"Cập nhật thất bại");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(OrderActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }

    private void UpdateTableStatusClient(int tableSelectedNumber) {
        GetTableClient(tableSelectedNumber); // Lấy thông tin tableId từ GetTableClient
    }
        //----------------------

    @Override
    public void onClick(View view) {
        String buttonId = getResources().getResourceEntryName(view.getId()); // Lấy tên của ID

        switch (buttonId){
            case "btnKhaiVi":
                setButtonStyle(btnKhaiVi, "#E8C5A3", 1.0f);
                setButtonStyle(btnMonChinh, "#6750A3", 0.1f);
                setButtonStyle(btnNuocUong, "#6750A3", 0.1f);
                setButtonStyle(btnTrangMieng, "#6750A3", 0.1f);
                selectedValue = 1;
                GetProductClient();
                break;

            case "btnMonChinh":
                setButtonStyle(btnMonChinh, "#E8C5A3", 1.0f);
                setButtonStyle(btnKhaiVi, "#6750A3", 0.1f);
                setButtonStyle(btnNuocUong, "#6750A3", 0.1f);
                setButtonStyle(btnTrangMieng, "#6750A3", 0.1f);
                selectedValue = 2;
                GetProductClient();
                break;

            case "btnNuocUong":
                setButtonStyle(btnNuocUong, "#E8C5A3", 1.0f);
                setButtonStyle(btnMonChinh, "#6750A3", 0.1f);
                setButtonStyle(btnKhaiVi, "#6750A3", 0.1f);
                setButtonStyle(btnTrangMieng, "#6750A3", 0.1f);
                selectedValue = 3;
                GetProductClient();
                break;

            case "btnTrangMieng":
                setButtonStyle(btnTrangMieng, "#E8C5A3", 1.0f);
                setButtonStyle(btnMonChinh, "#6750A3", 0.1f);
                setButtonStyle(btnNuocUong, "#6750A3", 0.1f);
                setButtonStyle(btnKhaiVi, "#6750A3", 0.1f);
                selectedValue = 4;
                GetProductClient();
                break;
        }
    }


    private void updateThongTinMonAn() {
        //tinh so luong
        int soLuongMonAn = selectedProductList.size();
        tvSoLuongMon = findViewById(R.id.tvSoLuongMon); // ánh xạ TextView này trong phần khai báo
        tvSoLuongMon.setText("Số lượng món: " + soLuongMonAn);

        //tạm tính
        tvTamTinh = findViewById(R.id.tvTamTinhCashier); // ánh xạ TextView này trong phần khai báo
        double tongTien = tinhTongTien();
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tvTamTinh.setText("Tổng tiền: " +decimalFormat.format(tongTien)+ " VNĐ");

        //tổng tiền sau VAT
        tvTongTien = findViewById(R.id.tvTongTienCashier); // ánh xạ TextView này trong phần khai báo
        double tongTienVAT = tinhTongTienVAT(tongTien); // Tính tổng số tiền sau VAT
        tvTongTien.setText("Tổng tiền sau VAT: " + decimalFormat.format(tongTienVAT) + " VNĐ");


    }
    private double tinhTongTien() {
        double tongTien = 0;
        for (Product product : selectedProductList) {
            tongTien += product.getProductPrice() * product.getProductQuantity();
        }
        return tongTien;
    }

    private double tinhTongTienVAT(double tongTien) {
        // Tính số tiền VAT 8%
        double vat = tongTien * 0.08;
        return tongTien + vat;
    }



    //Intent
    public void GoToMain(View view) {
        Intent t = new Intent(this, MainActivity.class);
        Intent intentGet = getIntent();
        int userPermission = intentGet.getIntExtra("userPermission", -1);
        t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        t.putExtra("userPermission", userPermission);
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToKitchen(View view) {
        Intent t = new Intent(OrderActivity.this, KitchenActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToCashier(View view) {
        Intent t = new Intent(OrderActivity.this, CashierActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToAdmin(View view) {
        Intent t = new Intent(OrderActivity.this, AdminActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToLogin(View view) {
        Intent t = new Intent(OrderActivity.this, LoginActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}

