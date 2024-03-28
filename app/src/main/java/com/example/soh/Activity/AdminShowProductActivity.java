package com.example.soh.Activity;

import static com.example.soh.Utils.utilsSetButton.setButtonStyle;
import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.GetProductResponseAPIModel;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Adapters.Admin_ProductAdapter;
import com.example.soh.Models.Product;
import com.example.soh.MyInterface.OnProductItemClickListener;
import com.example.soh.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminShowProductActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rcvAdmin;
    private List<Product> mListProduct;
    private Admin_ProductAdapter admin_productAdapter;
    private GridLayoutManager gridLayoutManager;
    private APIInterface apiInterface;
    private Button btnKhaiVi, btnMonChinh, btnNuocUong, btnTrangMieng;
    private int selectedValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        //rcv product
        rcvAdmin = findViewById(R.id.rcvCashier);
        gridLayoutManager = new GridLayoutManager(this, 4);
        rcvAdmin.setLayoutManager(gridLayoutManager);

        // ánh xạ
        btnKhaiVi = findViewById(R.id.btnKhaiVi);
        btnMonChinh = findViewById(R.id.btnMonChinh);
        btnNuocUong = findViewById(R.id.btnNuocUong);
        btnTrangMieng = findViewById(R.id.btnTrangMieng);

        //event onClick
        btnKhaiVi.setOnClickListener(this);
        btnMonChinh.setOnClickListener(this);
        btnNuocUong.setOnClickListener(this);
        btnTrangMieng.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getProductClient();
    }

    private void getProductClient() {
        Call<GetProductResponseAPIModel> call = apiInterface.getProductList(selectedValue);
        call.enqueue(new Callback<GetProductResponseAPIModel>() {
            @Override
            public void onResponse(Call<GetProductResponseAPIModel> call, Response<GetProductResponseAPIModel> response) {
                mListProduct = response.body().getData();
                admin_productAdapter = new Admin_ProductAdapter(mListProduct, new OnProductItemClickListener() {
                    @Override
                    public void onProductItemClick(Product product) {
                        final int position = mListProduct.indexOf(product);//1. Xác định vị trí của item
                        deleteProductClient(product, position);
                    }
                    @Override
                    public void onProductItemIntentClick(Product product) {
                        // Gọi hàm để xử lý khi sản phẩm được nhấp trong Activity
                        handleProductClick(product);
                    }
                });
                rcvAdmin.setAdapter(admin_productAdapter);
            }

            @Override
            public void onFailure(Call<GetProductResponseAPIModel> call, Throwable t) {
                showToast(AdminShowProductActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }
    private void handleProductClick(Product product) {
        // Tạo Intent và đính kèm thông tin sản phẩm
        Intent intent = new Intent(AdminShowProductActivity.this, UpdateProductActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void deleteProductClient(Product product, int position) {
        Call<ResponseAPIModel> call = apiInterface.deleteProduct(String.valueOf(product.getIdProduct()));
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                if (response.isSuccessful()) {
                    // Xóa món khỏi danh sách khi xóa thành công
                    mListProduct.remove(position);
                    admin_productAdapter.notifyItemRemoved(position);
                    getProductClient();
                    showToast(AdminShowProductActivity.this, "Xóa sản phẩm thành công!");
                }
                else {
                    showToast(AdminShowProductActivity.this, "Xóa sản phẩm thất bại!");
                }
            }

            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(AdminShowProductActivity.this, "Thất bại + " + t.getMessage());
            }
        });
    }

    private void refreshAdminRecyclerView() {
        admin_productAdapter = new Admin_ProductAdapter(mListProduct); // Sử dụng adapter đã có để hiển thị danh sách đã chọn.
        rcvAdmin.setAdapter(admin_productAdapter);
    }


    //-------phân loại hàng hóa--------------
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
                getProductClient();
                break;

            case "btnMonChinh":
                setButtonStyle(btnMonChinh, "#E8C5A3", 1.0f);
                setButtonStyle(btnKhaiVi, "#6750A3", 0.1f);
                setButtonStyle(btnNuocUong, "#6750A3", 0.1f);
                setButtonStyle(btnTrangMieng, "#6750A3", 0.1f);
                selectedValue = 2;
                getProductClient();
                break;

            case "btnNuocUong":
                setButtonStyle(btnNuocUong, "#E8C5A3", 1.0f);
                setButtonStyle(btnMonChinh, "#6750A3", 0.1f);
                setButtonStyle(btnKhaiVi, "#6750A3", 0.1f);
                setButtonStyle(btnTrangMieng, "#6750A3", 0.1f);
                selectedValue = 3;
                getProductClient();
                break;

            case "btnTrangMieng":
                setButtonStyle(btnTrangMieng, "#E8C5A3", 1.0f);
                setButtonStyle(btnMonChinh, "#6750A3", 0.1f);
                setButtonStyle(btnNuocUong, "#6750A3", 0.1f);
                setButtonStyle(btnKhaiVi, "#6750A3", 0.1f);
                selectedValue = 4;
                getProductClient();
                break;
        }
    }

    //-----Intent----------
    public void GoToAdmin(View view) {
        Intent t = new Intent(AdminShowProductActivity.this, AdminShowProductActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void BackToAdmin(View view) {
        Intent t = new Intent(AdminShowProductActivity.this, AdminActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToAddProduct(View view) {
        Intent t = new Intent(AdminShowProductActivity.this, InsertProductActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
