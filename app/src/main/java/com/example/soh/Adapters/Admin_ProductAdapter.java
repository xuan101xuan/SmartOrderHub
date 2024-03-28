package com.example.soh.Adapters;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.soh.Models.Product;
import com.example.soh.MyInterface.OnProductItemClickListener;
import com.example.soh.R;
import com.example.soh.Utils.utilsURL;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Admin_ProductAdapter extends RecyclerView.Adapter<Admin_ProductAdapter.ProductViewHolder> {

    private List<Product> mListProduct;
    private List<Product> mListSelectedProduct;
    private final OnProductItemClickListener onProductItemClickListener;

    // Constructor mặc định
    public Admin_ProductAdapter(List<Product> mListProduct) {
        this.mListProduct = mListProduct;
        this.mListSelectedProduct = new ArrayList<>();
        this.onProductItemClickListener = null; // Khởi tạo listener là null
    }
    public Admin_ProductAdapter(List<Product> mListProduct, OnProductItemClickListener listener) {
        this.mListProduct = mListProduct;
        this.mListSelectedProduct = new ArrayList<>();
        this.onProductItemClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = mListProduct.get(position);
        if (product == null){
            return;
        }
        holder.tvNameFood.setText(product.getProductName());
//        holder.tvPrice.setText(String.format("%.3f VNĐ", product.getPrice()));
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.tvPrice.setText(decimalFormat.format((product.getProductPrice()))+ "VNĐ");
        // Chuyển đổi đường dẫn tương đối thành URL đầy đủ
        String imageUrl = utilsURL.BASE_URL + product.getImageUrl();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl) // Đường dẫn URL hình ảnh từ chuỗi String
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Lựa chọn cách lưu trữ cache
                .transition(DrawableTransitionOptions.withCrossFade()) // Hiệu ứng chuyển đổi
                .into(holder.imgFood);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị AlertDialog khi mục được nhấp
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Lựa chọn");
                builder.setMessage("Hãy lựa chọn thao tác");

                // Nút "Đồng ý"
                builder.setPositiveButton("Xóa sản phẩm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onProductItemClickListener != null) {
                            onProductItemClickListener.onProductItemClick(product);
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
                // Nếu listener không null, thì gọi phương thức onProductItemIntentClick
                if (onProductItemClickListener != null) {
                    builder.setNeutralButton("Cập nhật thông tin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onProductItemClickListener.onProductItemIntentClick(product);
                            dialog.dismiss();
                        }
                    });
                }
                builder.show();
            }
        });

        if (product.getProductStatus() == 0) {
            holder.tvHetMon.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (mListProduct != null){
            return mListProduct.size();
        }
        return 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNameFood, tvPrice, tvHetMon;
        private final ImageView imgFood;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameFood = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgFood = itemView.findViewById(R.id.imgProduct);
            tvHetMon = itemView.findViewById(R.id.tvHetMon);
        }
    }
}