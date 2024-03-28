package com.example.soh.Activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soh.API.APIClient;
import com.example.soh.API.APIInterface;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Models.TableNumber;
import com.example.soh.R;
import com.example.soh.Utils.utilsInputFilter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertTableActivity extends AppCompatActivity {
    EditText edtTableNum, edtStatus, edtFloor;
    TableNumber tableNumber;

    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_table);
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
    }


    public void InsertTableClient(View view) {

        try {

            tableNumber = new TableNumber();
            tableNumber.setTableNum(Integer.valueOf(edtTableNum.getText().toString()));
            tableNumber.setTableStatus(Integer.valueOf(edtStatus.getText().toString()));
            tableNumber.setFloor(Integer.valueOf(edtFloor.getText().toString()));

            //xử lý thêm sản phẩm qua api
            Call<ResponseAPIModel> call = apiInterface.insertTable(tableNumber);
            call.enqueue(new Callback<ResponseAPIModel>() {
                @Override
                public void onResponse(Call<ResponseAPIModel> call, Response<ResponseAPIModel> response) {
                    ResponseAPIModel result = response.body();
                    if (result != null && result.getStatus()) {
                        Toast.makeText(InsertTableActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMessage = result != null ? result.getMessage() : "Lỗi không xác định";
                        Toast.makeText(InsertTableActivity.this, "Thêm thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAPIModel> call, Throwable t) {
                    Toast.makeText(InsertTableActivity.this, "Thất bại + " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("InsertTableActivity", "Lỗi trong quá trình Insert: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
        }
    }



    public void CancelClick(View view) {
        finish();
    }

}
