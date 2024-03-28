package com.example.soh.Adapters;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.soh.Models.Kitchen;
import com.example.soh.MyInterface.OnKitchenItemClickListener;
import com.example.soh.R;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.KitchenViewHolder> {
    private List<Kitchen> mListFoodKitchen;
    private final OnKitchenItemClickListener onKitchenItemClickListener;


    // Constructor mặc định
    public KitchenAdapter(List<Kitchen> mListFoodKitchen) {
        this.mListFoodKitchen = mListFoodKitchen;
        this.onKitchenItemClickListener = null; // Khởi tạo listener là null
    }

    public KitchenAdapter(List<Kitchen> mListFoodKitchen, OnKitchenItemClickListener listener) {
        this.mListFoodKitchen = mListFoodKitchen;
        this.onKitchenItemClickListener = listener;
    }

    @NonNull
    @Override
    public KitchenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_kitchen, parent, false);
        return new KitchenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KitchenViewHolder holder, int position) {Kitchen kitchen = mListFoodKitchen.get(position);
        if (kitchen == null){
            return;
        }

        Glide.with(holder.itemView.getContext())
                .load(kitchen.getImageUrl()) // Đường dẫn URL hình ảnh từ chuỗi String
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Lựa chọn cách lưu trữ cache
                .transition(DrawableTransitionOptions.withCrossFade()) // Hiệu ứng chuyển đổi
                .into(holder.imgFood);
        holder.tvNameFood.setText(kitchen.getProductName());
        holder.tvQuantity.setText(String.format("Số lượng: " + kitchen.getProductQuantity())); // Hiển thị so lượng
        holder.tvTable.setText(String.format("Số bàn: " + kitchen.getTableNum()));
        holder.tvGhiChu.setText(String.format("Ghi chú: " + kitchen.getProductNote()));



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị AlertDialog khi mục được nhấp
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Xác nhận");
                builder.setMessage("Bạn có xác nhận hoàn thành món này?");

                // Nút "Đồng ý"
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onKitchenItemClickListener != null) {
                            onKitchenItemClickListener.onKitchenItemClick(kitchen);
                        }
                        dialog.dismiss();
                    }
                });

                // Nút "Hủy bỏ"
                builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện các tác vụ khi người dùng hủy bỏ
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mListFoodKitchen != null){
            return mListFoodKitchen.size();
        }
        return 0;
    }

    public class KitchenViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgFood;
        private TextView tvNameFood;
        private TextView tvQuantity;
        private TextView tvTable;
        private TextView tvGhiChu;


        public KitchenViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFood = itemView.findViewById(R.id.imgProduct);
            tvNameFood = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvSoLuong);
            tvTable = itemView.findViewById(R.id.tvTableNum);
            tvGhiChu = itemView.findViewById(R.id.tvGhiChu);
        }
    }
}
