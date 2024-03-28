package com.example.soh.Adapters;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soh.Models.Staff;
import com.example.soh.MyInterface.OnStaffItemClickListener;
import com.example.soh.R;

import java.util.ArrayList;
import java.util.List;

public class Admin_StaffAdapter extends RecyclerView.Adapter<Admin_StaffAdapter.StaffViewHolder> {

    private List<Staff> mListStaff;
    private List<Staff> mListSelectedStaff;
    private final OnStaffItemClickListener onStaffItemClickListener;

    // Constructor mặc định
    public Admin_StaffAdapter(List<Staff> mListStaff) {
        this.mListStaff = mListStaff;
        this.mListSelectedStaff = new ArrayList<>();
        this.onStaffItemClickListener = null; // Khởi tạo listener là null
    }
    public Admin_StaffAdapter(List<Staff> mListStaff, OnStaffItemClickListener listener) {
        this.mListStaff = mListStaff;
        this.mListSelectedStaff = new ArrayList<>();
        this.onStaffItemClickListener = listener;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        Staff staff = mListStaff.get(position);
        if (staff == null){
            return;
        }
        holder.tvName.setText(staff.getNameStaff());
        holder.tvPhone.setText(staff.getPhoneNum());
        holder.tvAddress.setText(staff.getAddress());
        holder.tvRole.setText(staff.getRole());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị AlertDialog khi mục được nhấp
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Lựa chọn");
                builder.setMessage("Hãy lựa chọn thao tác");

                // Nút "Đồng ý"
                builder.setPositiveButton("Xóa nhân viên", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onStaffItemClickListener != null) {
                            onStaffItemClickListener.onStaffItemClick(staff);
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
                // Nếu listener không null, thì gọi phương thức onStaffItemIntentClick
                if (onStaffItemClickListener != null) {
                    builder.setNeutralButton("Cập nhật thông tin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onStaffItemClickListener.onStaffItemIntentClick(staff);
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
        if (mListStaff != null){
            return mListStaff.size();
        }
        return 0;
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvPhone, tvAddress, tvRole;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStaffName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvRole = itemView.findViewById(R.id.tvRole);
        }
    }
}