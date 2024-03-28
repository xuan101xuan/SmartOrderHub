package com.example.soh.Adapters;


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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> mListProduct;
    private List<Product> mListSelectedProduct;
    private final OnProductItemClickListener onProductItemClickListener;

    // Constructor mặc định
    public ProductAdapter(List<Product> mListProduct) {
        this.mListProduct = mListProduct;
        this.mListSelectedProduct = new ArrayList<>();
        this.onProductItemClickListener = null; // Khởi tạo listener là null
    }
    public ProductAdapter(List<Product> mListProduct, OnProductItemClickListener listener) {
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
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.tvPrice.setText(decimalFormat.format((product.getProductPrice()))+ " VNĐ");

        // Chuyển đổi đường dẫn tương đối thành URL đầy đủ
        String imageUrl = utilsURL.BASE_URL + product.getImageUrl();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl) // Đường dẫn URL hình ảnh từ chuỗi String
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Lựa chọn cách lưu trữ cache
                .transition(DrawableTransitionOptions.withCrossFade()) // Hiệu ứng chuyển đổi
                .into(holder.imgFood);

        if (product.getProductStatus() == 1) {
            // Chỉ thiết lập sự kiện click nếu productStatus là 1
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProductItemClickListener != null) {
                        onProductItemClickListener.onProductItemClick(product);
                        mListSelectedProduct.add(product);
                    }
                }
            });
        } else {
            // Nếu productStatus là 0, không cho phép click và có thể thực hiện các thay đổi khác trong giao diện.
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
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
            tvHetMon = itemView.findViewById(R.id.tvHetMon);
            imgFood = itemView.findViewById(R.id.imgProduct);
        }
    }
}