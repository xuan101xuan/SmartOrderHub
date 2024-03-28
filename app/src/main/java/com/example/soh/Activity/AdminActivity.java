package com.example.soh.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soh.R;


public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

    }

    //Intent
    public void GoToHome(View view) {
        Intent t = new Intent(AdminActivity.this, MainActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToKitchen(View view) {
        Intent t = new Intent(AdminActivity.this, KitchenActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToCashier(View view) {
        Intent t = new Intent(AdminActivity.this, CashierActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void GoToProduct(View view) {
        Intent t = new Intent(AdminActivity.this, AdminShowProductActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToTable(View view) {
        Intent t = new Intent(AdminActivity.this, AdminShowTableActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToStaff(View view) {
        Intent t = new Intent(AdminActivity.this, AdminShowStaffActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToAccount(View view) {
        Intent t = new Intent(AdminActivity.this, AdminShowAccountActivity.class );
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void GoToLogin(View view) {
        Intent t = new Intent(AdminActivity.this, LoginActivity.class );
        finish();
        startActivity(t);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
