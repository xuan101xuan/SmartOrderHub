package com.example.soh.Activity;

import static com.example.soh.Utils.utilsShow.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Models.Product;
import com.example.soh.Models.TableNumber;
import com.example.soh.R;
import com.example.soh.Utils.utilsInputFilter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTableActivity extends AppCompatActivity {
    EditText edtTableNum, edtStatus, edtFloor;
    private TableNumber selectedTableNumber;
    private APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_table);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        edtTableNum = findViewById(R.id.edtTableNum);
        edtStatus = findViewById(R.id.edtTableStatus);
        edtFloor = findViewById(R.id.edtFloor);

        //Phạm vi từ 0-2
        int minValue = 0;
        int maxValue = 2;
        edtStatus.setFilters(new InputFilter[]{utilsInputFilter.createInputFilter(minValue, maxValue)});

        //Phạm vi từ 1-2
        int minValue2 = 1;
        int maxValue2 = 2;
        edtFloor.setFilters(new InputFilter[]{utilsInputFilter.createInputFilter(minValue2, maxValue2)});

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("table")) {
            selectedTableNumber = (TableNumber) intent.getSerializableExtra("table");
            if (selectedTableNumber != null) {
                // Hiển thị thông tin sản phẩm trong EditText
                showTableDetails();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showTableDetails() {
        if (selectedTableNumber != null) {
            edtTableNum.setText(String.valueOf(selectedTableNumber.getTableNum()));
            edtStatus.setText(String.valueOf(selectedTableNumber.getTableStatus()));
            edtFloor.setText(String.valueOf(selectedTableNumber.getFloor()));
        }
    }
    private void updateTableClient(TableNumber updatedTable){
        Call<ResponseAPIModel> call = apiInterface.updateTable(updatedTable.getIdTable(), updatedTable);
        call.enqueue(new Callback<ResponseAPIModel>() {
            @Override
            public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                ResponseAPIModel result = response.body();
                if (result != null && result.getStatus()) {
                    showToast(UpdateTableActivity.this,"Cập nhật thông tin sản phẩm thành công");
                } else {
                    showToast(UpdateTableActivity.this,"Cập nhật thông tin sản phẩm thất bại");
                }
            }
            @Override
            public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                showToast(UpdateTableActivity.this,"Thất bại + " + t.getMessage());
            }
        });
    }
    private TableNumber getUpdatedTable() {
        Integer tableNum = Integer.parseInt(edtTableNum.getText().toString());
        Integer tableStatus = Integer.parseInt(edtStatus.getText().toString());
        Integer tableFloor = Integer.parseInt(edtFloor.getText().toString());

        // Tạo một đối tượng  mới với thông tin đã được cập nhật
        TableNumber updatedTable = new TableNumber();
        updatedTable.setIdTable(selectedTableNumber.getIdTable()); // Giữ nguyên ID của sản phẩm
        updatedTable.setTableNum(tableNum);
        updatedTable.setTableStatus(tableStatus);
        updatedTable.setFloor(tableFloor);

        return updatedTable;
    }


    //Intent
    public void CancelClick(View view) {
        finish();
    }

    public void UpdateTableClick(View view) {
        // Lấy thông tin sản phẩm đã được cập nhật từ EditText
        TableNumber updatedTable = getUpdatedTable();

        // Gọi phương thức cập nhật sản phẩm thông qua API
        updateTableClient(updatedTable);
        Intent t = new Intent(UpdateTableActivity.this, AdminShowTableActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

}
