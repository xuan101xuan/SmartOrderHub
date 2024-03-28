package com.example.soh.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.Models.Cashier;
import com.example.soh.MyInterface.OnCashierItemClickListener;
import com.example.soh.R;

import java.text.DecimalFormat;
import java.util.List;

public class CashierAdapter extends RecyclerView.Adapter<CashierAdapter.CashierViewHolder> {
    private List<Cashier> mListFoodCashier;
//    private List<Kitchen> mListSelectedFood;
    private final OnCashierItemClickListener onCashierItemClickListener;


    // Constructor mặc định
    public CashierAdapter(List<Cashier> mListFoodCashier) {
        this.mListFoodCashier = mListFoodCashier;
//        this.mListSelectedFood = new ArrayList<>();
        this.onCashierItemClickListener = null; // Khởi tạo listener là null
    }

    public CashierAdapter(List<Cashier> mListFoodCashier, OnCashierItemClickListener listener) {
        this.mListFoodCashier = mListFoodCashier;
        this.onCashierItemClickListener = listener;
    }

    @NonNull
    @Override
    public CashierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_cashier, parent, false);
        return new CashierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CashierViewHolder holder, int position) {

        Cashier cashier = mListFoodCashier.get(position);
        if (cashier == null){
            return;
        }

        holder.tvNameFood.setText(cashier.getProductName());
        holder.tvQuantity.setText(String.format(" " + cashier.getProductQuantity()));
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.tvPrice.setText(decimalFormat.format((cashier.getProductPrice()))+ " VNĐ");



    }

    @Override
    public int getItemCount() {
        if (mListFoodCashier != null){
            return mListFoodCashier.size();
        }
        return 0;
    }

    public  class CashierViewHolder extends RecyclerView.ViewHolder{

        private TextView tvNameFood;
        private TextView tvPrice;
        private TextView tvQuantity;


        public CashierViewHolder(@NonNull View itemView) {
            super(itemView);

//            imgFood = itemView.findViewById(R.id.imgFood);
            tvNameFood = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvSoLuong);
            tvPrice = itemView.findViewById(R.id.tvGiaMon);
        }
    }

}
