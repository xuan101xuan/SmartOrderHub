package com.example.soh.Adapters;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.soh.Models.Product;
import com.example.soh.MyInterface.OnProductItemClickListener;
import com.example.soh.MyInterface.OnImageItemClickListener;
import com.example.soh.R;
import com.example.soh.Utils.utilsURL;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartListViewHolder> {
    private TextView tvQuantity;
    private List<Product> mListProduct;
    private List<Product> mListSelectedProduct;
    private final OnProductItemClickListener onProductItemClickListener;

    // Constructor mặc định
    public CartListAdapter(List<Product> mListProduct) {
        this.mListProduct = mListProduct;
        this.mListSelectedProduct = new ArrayList<>();
        this.onProductItemClickListener = null; // Khởi tạo listener là null
    }
    public CartListAdapter(List<Product> mListProduct, OnProductItemClickListener listener, TextView tvQuantity) {
        this.mListProduct = mListProduct;
        this.mListSelectedProduct = new ArrayList<>();
        this.onProductItemClickListener = listener;

        this.tvQuantity = tvQuantity; // Gán TextView
    }

    @NonNull
    @Override
    public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_cartlist, parent, false);
        return new CartListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListViewHolder holder, int position) {
        Product product = mListProduct.get(position);
        if (product == null){
            return;
        }
        holder.tvNameFood.setText(product.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.tvPrice.setText(decimalFormat.format((product.getProductPrice()))+ " VNĐ");
        holder.tvNumber.setText(product.getProductQuantity() + " ");
        // Chuyển đổi đường dẫn tương đối thành URL đầy đủ
        String imageUrl = utilsURL.BASE_URL + product.getImageUrl();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl) // Đường dẫn URL hình ảnh từ chuỗi String
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Lựa chọn cách lưu trữ cache
                .transition(DrawableTransitionOptions.withCrossFade()) // Hiệu ứng chuyển đổi
                .into(holder.imgFood);

        holder.setListener(new OnImageItemClickListener() {
            @Override
            public void onImageItemClick(View view, int pos, int giatri, TextView tvNumber) {
                int currentNumber = mListProduct.get(pos).getProductQuantity();



                //new - Cập nhật giá trị productNote trong selectedProduct
                String edtGhiChu = holder.edtGhiChu.getText().toString();
                //======================tạm bỏ========================
//                mListProduct.get(pos).setProductNote(edtGhiChu);

                if (giatri == 1){
                    // Giảm số lượng
                    if (currentNumber > 1) {
                        int newNumber = currentNumber - 1;
                        mListProduct.get(pos).setProductQuantity(newNumber);
                        holder.tvNumber.setText(String.valueOf(newNumber));
                        holder.setNewNumber(newNumber);// Lưu giữ giá trị mới
                        // Thêm log để theo dõi
                        Log.d("CartListAdapter", "Giảm số lượng - currentNumber: " + currentNumber + ", newNumber: " + newNumber);
                    } else {
                        // Số lượng đã là 1, bạn muốn xóa món ăn khỏi giỏ hàng
                        mListProduct.remove(pos);
                        notifyItemRemoved(pos);
                    }
                } else if (giatri == 2) {
                    // Tăng số lượng
                    int newNumber = currentNumber + 1;
                    mListProduct.get(pos).setProductQuantity(newNumber);
                    holder.tvNumber.setText(String.valueOf(newNumber));
                    holder.setNewNumber(newNumber); // Lưu giữ giá trị mới
                    // Thêm log để theo dõi
                    Log.d("CartListAdapter", "Tăng số lượng - currentNumber: " + currentNumber + ", newNumber: " + newNumber);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListProduct != null){
            return mListProduct.size();
        }
        return 0;
    }

    public static class CartListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvNameFood, tvPrice, tvNumber;
        private final ImageView imgFood,imgRemove, imgAdd;
        private final EditText edtGhiChu;
        OnImageItemClickListener listener;
        //new
        private int newNumber; // Thêm biến mới để lưu giữ giá trị mới

        public CartListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameFood = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgFood = itemView.findViewById(R.id.imgProduct);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            imgRemove = itemView.findViewById(R.id.imgRemove);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            edtGhiChu = itemView.findViewById(R.id.edtGhiChu);

            //event click
            imgRemove.setOnClickListener(this);
            imgAdd.setOnClickListener(this);
        }


        public void setListener(OnImageItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if (view == imgRemove){
                listener.onImageItemClick(view, getAdapterPosition(), 1, tvNumber);
                //1 tru
            } else if (view == imgAdd) {
                listener.onImageItemClick(view, getAdapterPosition(), 2, tvNumber);
                //2 cong
            }
        }
        //new
        public void setNewNumber(int newNumber) {
            this.newNumber = newNumber;
        }
    }

    //new
//    public interface OnQuantityChangeListener {
//        void onQuantityChanged(int position, int newQuantity);
//    }

}