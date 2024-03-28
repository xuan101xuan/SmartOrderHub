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

import com.example.soh.Models.TableNumber;
import com.example.soh.MyInterface.OnTableItemClickListener;
import com.example.soh.MyInterface.OnTableNumberItemClickListener;
import com.example.soh.R;

import java.util.List;

public class Admin_TableAdapter extends RecyclerView.Adapter<Admin_TableAdapter.TableViewHolder> {
    private List<TableNumber> mListTableNumber;
    private final OnTableNumberItemClickListener onTableNumberItemClickListener;


    // Constructor mặc định
    public Admin_TableAdapter(List<TableNumber> mListTableNumber) {
        this.mListTableNumber = mListTableNumber;
        this.onTableNumberItemClickListener = null; // Khởi tạo listener là null
    }

    public Admin_TableAdapter(List<TableNumber> mListTableNumber, OnTableNumberItemClickListener listener) {
        this.mListTableNumber = mListTableNumber;
        this.onTableNumberItemClickListener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {

        TableNumber tableNumber = mListTableNumber.get(position);

        if (mListTableNumber == null){
            return;
        }

        holder.tvTableNum.setText(String.format("Số bàn: %d", tableNumber.getTableNum()));

        int tableStatus = tableNumber.getTableStatus();
        switch (tableStatus) {
            case 0:
                holder.tvStatus.setText("Đang trống");
                holder.imgTable.setImageResource(R.drawable.ic_table);
                break;
            case 1:
                holder.tvStatus.setText("Đang phục vụ");
                holder.imgTable.setImageResource(R.drawable.ic_table2);
                break;
            case 2:
                holder.tvStatus.setText("Cần lên món");
                holder.imgTable.setImageResource(R.drawable.ic_table3);
                break;
            default:
                holder.tvStatus.setText("Lỗi hiển thị");
                holder.imgTable.setImageResource(R.drawable.ic_table4);
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
                builder.setPositiveButton("Xóa số bàn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onTableNumberItemClickListener != null) {
                            onTableNumberItemClickListener.onTableNumberItemClick(tableNumber);
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
                if (onTableNumberItemClickListener != null) {
                    builder.setNeutralButton("Cập nhật thông tin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onTableNumberItemClickListener.onTableNumberItemIntentClick(tableNumber);
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
        if (mListTableNumber != null){
            return mListTableNumber.size();
        }
        return 0;
    }

    public  class TableViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgTable;
        private TextView tvStatus;
        private TextView tvTableNum;


        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            imgTable = itemView.findViewById(R.id.imgProduct);
            tvTableNum = itemView.findViewById(R.id.tvProductName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
    
}
