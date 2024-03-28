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
import com.example.soh.Models.Account;
import com.example.soh.MyInterface.OnAccountItemClickListener;
import com.example.soh.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Admin_AccountAdapter extends RecyclerView.Adapter<Admin_AccountAdapter.AccountViewHolder> {

    private List<Account> mListAccount;
    private List<Account> mListSelectedAccount;
    private final OnAccountItemClickListener onAccountItemClickListener;

    // Constructor mặc định
    public Admin_AccountAdapter(List<Account> mListAccount) {
        this.mListAccount = mListAccount;
        this.mListSelectedAccount = new ArrayList<>();
        this.onAccountItemClickListener = null; // Khởi tạo listener là null
    }
    public Admin_AccountAdapter(List<Account> mListAccount, OnAccountItemClickListener listener) {
        this.mListAccount = mListAccount;
        this.mListSelectedAccount = new ArrayList<>();
        this.onAccountItemClickListener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = mListAccount.get(position);
        if (account == null){
            return;
        }
        holder.tvUsername.setText(account.getAccountName());
        holder.tvPassword.setText(account.getPassword());
        holder.tvPermission.setText(String.format(" " + account.getPermission()));

        int permiss = account.getPermission();
        switch (permiss) {
            case 1:
                holder.tvPermission.setText("Admin");
                break;
            case 2:
                holder.tvPermission.setText("Order");
                break;
            case 3:
                holder.tvPermission.setText("Kitchen");
                break;
            case 4:
                holder.tvPermission.setText("Cashier");
                break;
            default:
                holder.tvPermission.setText("Không hợp lệ");
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị AlertDialog khi mục được nhấp
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Lựa chọn");
                builder.setMessage("Hãy lựa chọn thao tác");

                // Nút "Đồng ý"
                builder.setPositiveButton("Xóa tài khoản", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onAccountItemClickListener != null) {
                            onAccountItemClickListener.onAccountItemClick(account);
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
                // Nếu listener không null, thì gọi phương thức onAccountItemIntentClick
                if (onAccountItemClickListener != null) {
                    builder.setNeutralButton("Cập nhật thông tin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onAccountItemClickListener.onAccountItemIntentClick(account);
                            dialog.dismiss();
                        }
                    });
                }
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mListAccount != null){
            return mListAccount.size();
        }
        return 0;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUsername, tvPassword, tvPermission;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvPassword = itemView.findViewById(R.id.tvPassword);
            tvPermission = itemView.findViewById(R.id.tvPermission);
        }
    }
}